package it.pureorigins.fancyparticles.particles

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3i

fun interface ParticleActivation {
    fun isActive(player: ServerPlayerEntity): Boolean

    companion object {
        val ALWAYS_ACTIVE = ParticleActivation { true }
        val WHEN_MOVING = ParticleActivation { it.movementDirection.vector.equals(Vec3i.ZERO) }
        val WHEN_STANDING = ParticleActivation { !WHEN_MOVING.isActive(it) }
    }
}