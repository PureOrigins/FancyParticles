package it.pureorigins.fancyparticles.particles.shapes

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes.NOTE
import net.minecraft.util.math.Vec3d

object NoteParticleShape : ParticleShape {

    override fun isActive(player: PlayerEntity): Boolean = true
    override fun getPosition(iteration: Int): Vec3d = Vec3d.ZERO
    override fun getOffset(iteration: Int): Vec3d = Vec3d(0.25,0.0, 0.25)
    override fun getParticle(iteration: Int): ParticleEffect = NOTE
    override fun getSpeed(iteration: Int) = 0
    override fun getCount(iteration: Int) = 1
}