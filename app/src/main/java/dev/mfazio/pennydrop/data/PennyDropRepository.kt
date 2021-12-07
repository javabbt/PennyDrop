package dev.mfazio.pennydrop.data

import dev.mfazio.pennydrop.types.Player

class PennyDropRepository(private val pennyDropDao: PennyDropDao) {

    fun getCompletedGameStatusesWithPlayers() = pennyDropDao.getCompletedGameStatusesWithPlayers()

    fun getCurrentGameWithPlayers() = pennyDropDao.getCurrentGameWithPlayers()

    fun getCurrentGameStatuses() = pennyDropDao.getCurrentGameStatuses()

    suspend fun startGame(players: List<Player>, pennyCount: Int? = null) =
        pennyDropDao.startGame(players, pennyCount)

    suspend fun updateGameAndStatuses(
        game: Game,
        statuses: List<GameStatus>
    ) = pennyDropDao.updateGameAndStatuses(game, statuses)

    companion object {
        @Volatile
        private var instance: PennyDropRepository? = null

        fun getInstance(pennyDropDao: PennyDropDao) =
            this.instance ?: synchronized(this) {
                instance ?: PennyDropRepository(pennyDropDao).also {
                    instance = it
                }
            }
    }
}