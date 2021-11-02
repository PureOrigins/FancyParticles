package it.pureorigins.fancyparticles.particles

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d

enum class PositionOffset {
    EYE, FEET, HEAD;

    fun getPosition(player: ServerPlayerEntity): Vec3d? = when (this) {
        EYE -> player.eyePos
        FEET -> player.pos
        HEAD -> player.pos.add(0.0, player.height.toDouble(), 0.0)
    }
}