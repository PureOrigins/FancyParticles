package it.pureorigins.fancyparticles.particles.shapes

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.util.math.Vec3d

interface ParticleShape {


    fun isActive(player: PlayerEntity): Boolean // quando switcha a true partono le particelle e il numero di iteration incrementa ogni period
    fun getPosition(iteration: Int): Vec3d
    fun getOffset(iteration: Int): Vec3d
    fun getParticle(iteration: Int): ParticleEffect
    fun getSpeed(iteration: Int): Int
    fun getCount(iteration: Int): Int
}