package it.pureorigins.fancyparticles

import org.bukkit.entity.Player
import org.bukkit.util.Vector

enum class PositionReference {
    EYE, FEET, HEAD;

    fun getPosition(player: Player): Vector = when (this) {
        EYE -> player.eyeLocation.toVector()
        FEET -> player.location.toVector()
        HEAD -> player.location.toVector().add(Vector(0.0, player.height, 0.0))
    }
}