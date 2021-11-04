package it.pureorigins.fancyparticles.particles

import it.pureorigins.fancyparticles.PositionOffset.FEET
import it.pureorigins.fancyparticles.PositionOffset.HEAD
import it.pureorigins.fancyparticles.particles.shapes.OrbitalParticle
import it.pureorigins.fancyparticles.particles.shapes.ParallelepipedParticle
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.particle.ParticleTypes.*

object ParticleEffects {
    val effects = HashMap<String, ParticleEffect>()

    private fun register(id: String, particleEffect: ParticleEffect): ParticleEffect {
        effects[id] = particleEffect
        return particleEffect
    }

    private fun register(id: String, vararg particlePart: ParticlePart): ParticleEffect {
        return register(id, ParticleEffect(listOf(*particlePart)))
    }

    val MUSIC = register(
        "music",
        ParallelepipedParticle() madeOf NOTE period 13L at HEAD,
        ParallelepipedParticle() madeOf NOTE delay 5L period 17L at HEAD atY 0.05,
        ParallelepipedParticle() madeOf NOTE period 41L at HEAD atY 0.2
    )
    val CLOUD = register(
        "cloud",
        ParallelepipedParticle(x = 0.3, z = 0.3) madeOf FALLING_WATER delay 30L period 4L at FEET atY 2.5,
        ParallelepipedParticle(0.35, 0.1, 0.35) madeOf ParticleTypes.CLOUD period 4L at FEET atY 2.4

    )
    val SAND_SPIRAL = register(
        "sand_spiral", OrbitalParticle() madeOf DustParticleEffect.DEFAULT period 2 at HEAD
    )

    val LOVE = register(
        "love",
        ParallelepipedParticle() madeOf HEART period 40 at HEAD,
        ParallelepipedParticle() madeOf HEART delay 4 period 40 at HEAD,
        ParallelepipedParticle() madeOf HEART delay 12 period 40 at HEAD
    )

    val KNOWLEDGE = register("knowledge", ParallelepipedParticle() madeOf ENCHANT at HEAD)

    val ASHEN = register("ashen", ParallelepipedParticle(0.1, 0.05, 0.1, 5) madeOf SMOKE period 3 at HEAD)

    val SHINING =
        register("shining", ParallelepipedParticle(0.25, 0.5, 0.25) madeOf HAPPY_VILLAGER period 10 at FEET atY 0.9)
}
