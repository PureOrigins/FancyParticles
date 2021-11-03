package it.pureorigins.fancyparticles.particles

import it.pureorigins.fancyparticles.PositionOffset.HEAD
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

class SandParticle(delay:Long, period:Long) : Particle(delay, period, HEAD) {
    constructor():this(0L, 2L)

    override fun isActive(player: PlayerEntity): Boolean = true
    override fun getPosition(iteration: Int) = Vec3d(cos(0.1 * iteration), 0.0, sin(0.1 * iteration))
    override fun getOffset(iteration: Int): Vec3d = Vec3d.ZERO
    override fun getParticle(iteration: Int): ParticleEffect = DustParticleEffect.DEFAULT
    override fun getSpeed(iteration: Int) = 0
    override fun getCount(iteration: Int) = 1
}