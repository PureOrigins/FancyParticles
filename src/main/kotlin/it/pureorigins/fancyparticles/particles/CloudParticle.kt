package it.pureorigins.fancyparticles.particles

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.Vec3d

object CloudParticle : Particle {
    override val delay = 30L
    override val period = 4L
    override val positionOffset = PositionOffset.FEET

    override fun isActive(player: PlayerEntity): Boolean = true
    override fun getPosition(iteration: Int): Vec3d = Vec3d(0.0, 2.5, 0.0)
    override fun getOffset(iteration: Int): Vec3d = Vec3d(0.35, 0.1, 0.35)
    override fun getParticle(iteration: Int): ParticleEffect = ParticleTypes.CLOUD
    override fun getSpeed(iteration: Int) = 0
    override fun getCount(iteration: Int) = 10
}