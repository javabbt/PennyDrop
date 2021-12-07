package dev.mfazio.pennydrop.types

import androidx.databinding.ObservableBoolean
import dev.mfazio.pennydrop.game.AI

data class NewPlayer(
    var playerName: String = "",
    val isHuman: ObservableBoolean = ObservableBoolean(true),
    val canBeRemoved: Boolean = true,
    val canBeToggled: Boolean = true,
    var isIncluded: ObservableBoolean = ObservableBoolean(!canBeRemoved),
    var selectedAIPosition: Int = -1
) {
    fun selectedAI() = if (!isHuman.get()) {
        AI.basicAI.getOrNull(selectedAIPosition)
    } else {
        null
    }

    fun toPlayer() = Player(
        playerName = if (this.isHuman.get()) this.playerName else (this.selectedAI()?.name ?: "AI"),
        isHuman = this.isHuman.get(),
        selectedAI = this.selectedAI()
    )

    override fun toString() = listOf(
        "name" to this.playerName,
        "isIncluded" to this.isIncluded.get(),
        "isHuman" to this.isHuman.get(),
        "canBeRemoved" to this.canBeRemoved,
        "canBeToggled" to this.canBeToggled,
        "selectedAI" to (this.selectedAI()?.name ?: "N/A")
    ).joinToString(", ", "NewPlayer(", ")") { (property, value) ->
        "$property=$value"
    }
}