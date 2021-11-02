package it.pureorigins.fancyparticles.particles

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes.FLAME
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

object FireParticle : Particle {
    override val delay = 0L
    override val period = 1L
    override val positionOffset = PositionOffset.EYE

    override fun isActive(player: PlayerEntity): Boolean = true
    override fun getPosition(iteration: Int) = Vec3d(cos(0.1 * iteration), 0.4, sin(0.1 * iteration))
    override fun getOffset(iteration: Int): Vec3d = Vec3d.ZERO
    override fun getParticle(iteration: Int): ParticleEffect = FLAME
    override fun getSpeed(iteration: Int) = 0
    override fun getCount(iteration: Int) = 10
}