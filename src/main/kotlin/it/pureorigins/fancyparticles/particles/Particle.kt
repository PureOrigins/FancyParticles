package it.pureorigins.fancyparticles.particles

import it.pureorigins.fancyparticles.PositionOffset
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.util.math.Vec3d

abstract class Particle(val delay: Long, val period: Long, val positionOffset: PositionOffset) {

    abstract fun isActive(player: PlayerEntity): Boolean // quando switcha a true partono le particelle e il numero di iteration incrementa ogni period
    abstract fun getPosition(iteration: Int): Vec3d
    abstract fun getOffset(iteration: Int): Vec3d
    abstract fun getParticle(iteration: Int): ParticleEffect
    abstract fun getSpeed(iteration: Int): Int
    abstract fun getCount(iteration: Int): Int
}