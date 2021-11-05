package it.pureorigins.fancyparticles.particles.shapes

import net.minecraft.util.math.Vec3d

interface ParticleShape {
    fun getPosition(iteration: Int): Vec3d
    fun getOffset(iteration: Int): Vec3d
    fun getSpeed(iteration: Int): Double
    fun getCount(iteration: Int): Int
}