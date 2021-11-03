package it.pureorigins.fancyparticles.particles

import it.pureorigins.fancyparticles.PositionOffset.FEET
import it.pureorigins.fancyparticles.PositionOffset.HEAD
import it.pureorigins.fancyparticles.particles.shapes.*
import net.minecraft.util.math.Vec3d

object ParticleEffects {
    private val map = HashMap<String, ParticleEffect>()

    private fun register(id: String, particleEffect: ParticleEffect): ParticleEffect {
        map[id] = particleEffect
        return particleEffect
    }

    val MUSIC = register(
        "music", ParticleEffect(
            NoteParticleShape period 13L at HEAD,
            NoteParticleShape delay 5L period 17L at HEAD at Vec3d(0.0, 0.05, 0.0),
            NoteParticleShape period 41L at HEAD at Vec3d(0.0, 0.2, 0.0)
        )
    )
    val CLOUD = register(
        "cloud",
        ParticleEffect(
            RainParticleShape delay 30L period 4L at FEET at Vec3d(0.0, 2.5, 0.0),
            CloudParticleShape period 4L at FEET at Vec3d(0.0, 2.4, 0.0)
        )
    )
    val SAND_SPIRAL = register("sand_spiral", ParticleEffect(SandParticleShape period 2 at HEAD))

    val LOVE = register(
        "love",
        ParticleEffect(
            LoveParticleShape period 40 at HEAD,
            LoveParticleShape delay 4 period 40 at HEAD,
            LoveParticleShape delay 12 period 40 at HEAD
        )
    )

}
