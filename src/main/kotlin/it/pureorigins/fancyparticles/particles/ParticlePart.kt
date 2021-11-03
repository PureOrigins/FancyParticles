package it.pureorigins.fancyparticles.particles

import it.pureorigins.fancyparticles.PositionOffset
import it.pureorigins.fancyparticles.particles.shapes.ParticleShape
import net.minecraft.util.math.Vec3d

data class ParticlePart(
    val shape: ParticleShape,
    val delay: Long = 0,
    val period: Long = 1,
    val position: PositionOffset = PositionOffset.FEET,
    val offset: Vec3d = Vec3d.ZERO
)

infix fun ParticleShape.delay(delay: Long) = ParticlePart(this, delay = delay)
infix fun ParticlePart.delay(delay: Long) = copy(delay = delay)
infix fun ParticleShape.period(period: Long) = ParticlePart(this, period = period)
infix fun ParticlePart.period(period: Long) = copy(period = period)
infix fun ParticleShape.at(position: PositionOffset) = ParticlePart(this, position = position)
infix fun ParticlePart.at(position: PositionOffset) = copy(position = position)
infix fun ParticleShape.at(offset: Vec3d) = ParticlePart(this, offset = offset)
infix fun ParticlePart.at(offset: Vec3d) = copy(offset = offset)

