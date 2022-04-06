package it.pureorigins.pureparticles.particles.shapes

import org.bukkit.util.Vector

interface ParticleShape {
    fun getPosition(iteration: Int): Vector
    fun getOffset(iteration: Int): Vector
    fun getSpeed(iteration: Int): Double
    fun getCount(iteration: Int): Int
}