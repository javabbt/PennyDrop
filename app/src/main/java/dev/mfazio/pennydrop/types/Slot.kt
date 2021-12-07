package dev.mfazio.pennydrop.types

import dev.mfazio.pennydrop.data.Game

data class Slot(
    val number: Int,
    val canBeFilled: Boolean = true,
    var isFilled: Boolean = false,
    var lastRolled: Boolean = false
) {
    companion object {
        fun mapFromGame(game: Game?) =
            (1..6).map { slotNum ->
                Slot(
                    number = slotNum,
                    canBeFilled = slotNum != 6,
                    isFilled = game?.filledSlots?.contains(slotNum) ?: false,
                    lastRolled = game?.lastRoll == slotNum
                )
            }
    }
}

fun List<Slot>.clear() = this.forEach { slot ->
    slot.isFilled = false
    slot.lastRolled = false
}

fun List<Slot>.fullSlots(): Int = this.count { it.canBeFilled && it.isFilled }