package it.pureorigins.fancyparticles.particles

import it.pureorigins.fancyparticles.PositionOffset.FEET
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.Vec3d

class RainParticle(delay:Long, period:Long) : Particle(delay, period, FEET) {
    constructor() : this(30L, 4L)

    override fun isActive(player: PlayerEntity): Boolean = true
    override fun getPosition(iteration: Int): Vec3d = Vec3d(0.0, 2.4, 0.0)
    override fun getOffset(iteration: Int): Vec3d = Vec3d(0.3, 0.0, 0.3)
    override fun getParticle(iteration: Int): ParticleEffect = ParticleTypes.FALLING_WATER
    override fun getSpeed(iteration: Int) = 0
    override fun getCount(iteration: Int) = 5
}