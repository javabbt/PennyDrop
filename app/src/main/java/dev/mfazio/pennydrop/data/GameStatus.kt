package dev.mfazio.pennydrop.data

import androidx.room.Entity
import androidx.room.ForeignKey
import dev.mfazio.pennydrop.types.Player

@Entity(
    tableName = "game_statuses",
    primaryKeys = ["gameId", "playerId"],
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"]
        ),
        ForeignKey(
            entity = Player::class,
            parentColumns = ["playerId"],
            childColumns = ["playerId"]
        )
    ]
)
data class GameStatus(
    val gameId: Long,
    val playerId: Long,
    val gamePlayerNumber: Int,
    val isRolling: Boolean = false,
    val pennies: Int = Player.defaultPennyCount
)