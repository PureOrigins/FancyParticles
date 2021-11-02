package it.pureorigins.fancyparticles.particles

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.Vec3d

object RainParticle : Particle {
    override val delay = 30L
    override val period = 4L
    override val positionOffset = PositionOffset.FEET

    override fun isActive(player: PlayerEntity): Boolean = true
    override fun getPosition(iteration: Int): Vec3d = Vec3d(0.0, 2.4, 0.0)
    override fun getOffset(iteration: Int): Vec3d = Vec3d(0.3, 0.0, 0.3)
    override fun getParticle(iteration: Int): ParticleEffect = ParticleTypes.FALLING_WATER
    override fun getSpeed(iteration: Int) = 0
    override fun getCount(iteration: Int) = 5
}