package it.pureorigins.fancyparticles.particles

import it.pureorigins.fancyparticles.PositionOffset.EYE
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes.NOTE
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

class NoteParticle(delay: Long, period: Long) : Particle(delay, period, EYE) {
    constructor() : this(0L, 1L)

    override fun isActive(player: PlayerEntity): Boolean = true
    override fun getPosition(iteration: Int) = Vec3d(cos(0.1 * iteration), 0.4, sin(0.1 * iteration))
    override fun getOffset(iteration: Int): Vec3d = Vec3d.ZERO
    override fun getParticle(iteration: Int): ParticleEffect = NOTE
    override fun getSpeed(iteration: Int) = 0
    override fun getCount(iteration: Int) = 10
}