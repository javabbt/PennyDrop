package dev.mfazio.pennydrop.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import dev.mfazio.pennydrop.data.*
import dev.mfazio.pennydrop.game.GameHandler
import dev.mfazio.pennydrop.game.TurnEnd
import dev.mfazio.pennydrop.game.TurnResult
import dev.mfazio.pennydrop.types.Player
import dev.mfazio.pennydrop.types.Slot
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private var clearText = false

    private val prefs =
        PreferenceManager.getDefaultSharedPreferences(application)

    private val repository: PennyDropRepository

    val currentGame = MediatorLiveData<GameWithPlayers>()

    val currentGameStatuses: LiveData<List<GameStatus>>
    val currentPlayer: LiveData<Player>
    val currentStandingsText: LiveData<String>

    val slots: LiveData<List<Slot>>

    val canRoll: LiveData<Boolean>
    val canPass: LiveData<Boolean>

    init {
        this.repository =
            PennyDropDatabase
                .getDatabase(application, viewModelScope)
                .pennyDropDao()
                .let { dao ->
                    PennyDropRepository.getInstance(dao)
                }

        this.currentGameStatuses = this.repository.getCurrentGameStatuses()

        this.currentGame.addSource(
            this.repository.getCurrentGameWithPlayers()
        ) { gameWithPlayers ->
            updateCurrentGame(gameWithPlayers, this.currentGameStatuses.value)
        }

        this.currentGame.addSource(this.currentGameStatuses) { gameStatuses ->
            updateCurrentGame(this.currentGame.value, gameStatuses)
        }

        this.currentPlayer = Transformations.map(this.currentGame) { gameWithPlayers ->
            gameWithPlayers?.players?.firstOrNull { it.isRolling }
        }

        this.currentStandingsText = Transformations.map(this.currentGame) { gameWithPlayers ->
            gameWithPlayers?.players?.let { players ->
                this.generateCurrentStandings(players)
            }
        }

        this.slots = Transformations.map(this.currentGame) { gameWithPlayers ->
            Slot.mapFromGame(gameWithPlayers?.game)
        }

        this.canRoll = Transformations.map(this.currentPlayer) { player ->
            player?.isHuman == true && currentGame.value?.game?.canRoll == true
        }

        this.canPass = Transformations.map(this.currentPlayer) { player ->
            player?.isHuman == true && currentGame.value?.game?.canPass == true
        }
    }

    private fun updateCurrentGame(
        gameWithPlayers: GameWithPlayers?,
        gameStatuses: List<GameStatus>?
    ) {
        this.currentGame.value = gameWithPlayers?.updateStatuses(gameStatuses)
    }

    suspend fun startGame(playersForNewGame: List<Player>) {
        repository.startGame(
            playersForNewGame,
            prefs?.getInt("pennyCount", Player.defaultPennyCount)
        )
    }

    fun roll() {
        val game = this.currentGame.value?.game
        val players = this.currentGame.value?.players
        val currentPlayer = this.currentPlayer.value
        val slots = this.slots.value

        if (game != null && players != null && currentPlayer != null && slots != null && game.canRoll) {
            updateFromGameHandler(GameHandler.roll(players, currentPlayer, slots))
        }
    }

    fun pass() {
        val game = this.currentGame.value?.game
        val players = this.currentGame.value?.players
        val currentPlayer = this.currentPlayer.value

        if (game != null && players != null && currentPlayer != null && game.canPass) {
            updateFromGameHandler(GameHandler.pass(players, currentPlayer))
        }
    }

    private fun updateFromGameHandler(result: TurnResult) {
        val game = currentGame.value?.let { currentGameWithPlayers ->
            currentGameWithPlayers.game.copy(
                gameState = if (result.isGameOver) GameState.Finished else GameState.Started,
                lastRoll = result.lastRoll,
                filledSlots = updateFilledSlots(result, currentGameWithPlayers.game.filledSlots),
                //Note: This generates with old values since the game's yet to be updated.
                currentTurnText = generateTurnText(result),
                canPass = result.canPass,
                canRoll = result.canRoll,
                endTime = if (result.isGameOver) OffsetDateTime.now() else null
            )
        } ?: return

        val statuses = currentGameStatuses.value?.map { status ->
            when (status.playerId) {
                result.previousPlayer?.playerId -> {
                    status.copy(
                        isRolling = false,
                        pennies = status.pennies + (result.coinChangeCount ?: 0)
                    )
                }
                result.currentPlayer?.playerId -> {
                    status.copy(
                        isRolling = !result.isGameOver,
                        pennies = status.pennies +
                            if (!result.playerChanged) {
                                result.coinChangeCount ?: 0
                            } else 0
                    )
                }
                else -> status
            }
        } ?: emptyList()

        viewModelScope.launch {
            repository.updateGameAndStatuses(game, statuses)
            if (result.currentPlayer?.isHuman == false) {
                playAITurn()
            }
        }
    }

    private fun updateFilledSlots(result: TurnResult, filledSlots: List<Int>) =
        when {
            result.clearSlots -> emptyList()
            result.lastRoll != null && result.lastRoll != 6 ->
                filledSlots + result.lastRoll
            else -> filledSlots
        }

    private fun generateCurrentStandings(
        players: List<Player>,
        headerText: String = "Current Standings:"
    ) = players.sortedBy { it.pennies }.joinToString(
        separator = "\n",
        prefix = "$headerText\n"
    ) {
        "\t${it.playerName} - ${it.pennies} pennies"
    }

    private fun generateTurnText(result: TurnResult): String {
        val currentText = if (clearText) "" else currentGame.value?.game?.currentTurnText ?: ""
        clearText = result.turnEnd != null

        val currentPlayerName = result.currentPlayer?.playerName ?: "???"

        return when {
            result.isGameOver -> generateGameOverText()
            result.turnEnd == TurnEnd.Bust -> "${ohNoPhrases.shuffled().first()} ${result.previousPlayer?.playerName} rolled a ${result.lastRoll}.  They collected ${result.coinChangeCount} pennies for a total of ${result.previousPlayer?.pennies}.\n$currentText"
            result.turnEnd == TurnEnd.Pass -> "${result.previousPlayer?.playerName} passed.  They currently have ${result.previousPlayer?.pennies} pennies.\n$currentText"
            result.lastRoll != null -> "$currentPlayerName rolled a ${result.lastRoll}.\n$currentText"
            else -> ""
        }
    }

    private fun generateGameOverText(): String {
        val statuses = this.currentGameStatuses.value
        val players = this.currentGame.value?.players?.map { player ->
            player.apply {
                this.pennies = statuses?.firstOrNull { it.playerId == playerId }?.pennies
                    ?: Player.defaultPennyCount
            }
        }

        val winningPlayer = players
            ?.firstOrNull { !it.penniesLeft() || it.isRolling }
            ?.apply { this.pennies = 0 }

        if (players == null || winningPlayer == null) return "N/A"

        val sortedPlayersString = players
            .sortedBy { it.pennies }
            .joinToString("\n") {
                "\t${it.playerName} - ${it.pennies} pennies."
            }

        return """Game Over!
                  |${winningPlayer.playerName} is the winner!
                  |
                  |Final Scores:
                  |$sortedPlayersString
                  """.trimMargin()
    }

    private suspend fun playAITurn() {
        delay(if (prefs.getBoolean("fastAI", false)) 100 else 1000)

        val game = currentGame.value?.game
        val players = currentGame.value?.players
        val currentPlayer = currentPlayer.value
        val slots = slots.value

        if (game != null && players != null &&
            currentPlayer != null && slots != null
        ) {
            GameHandler
                .playAITurn(players, currentPlayer, slots, game.canPass)
                ?.let { result ->
                    updateFromGameHandler(result)
                }
        }
    }

    // I added this at the request of my wife, who wanted to see a bunch of "oh no!" phrases added in.
    // They make me smile.
    private val ohNoPhrases = listOf(
        "Oh no!",
        "Bummer!",
        "Dang.",
        "Whoops.",
        "Ah, fiddlesticks.",
        "Oh, kitty cats.",
        "Piffle.",
        "Well, crud.",
        "Ah, cinnamon bits.",
        "Ooh, bad luck.",
        "Shucks!",
        "Woopsie daisy.",
        "Nooooooo!",
        "Aw, rats and bats.",
        "Blood and thunder!",
        "Gee whillikins.",
        "Well that's disappointing.",
        "I find your lack of luck disturbing.",
        "That stunk, huh?",
        "Uff da."
    )
}