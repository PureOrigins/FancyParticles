package it.pureorigins.fancyparticles.particles

object ParticleEffects {
    private val map = HashMap<String, ParticleEffect>()

    private fun register(id:String, particleEffect: ParticleEffect):ParticleEffect{
        map[id] = particleEffect
        return particleEffect
    }

    val STORM = register("storm", ParticleEffect(SandParticle(), FireParticle()))
    val CLOUD = register("cloud", ParticleEffect(RainParticle(), CloudParticle()))
    val SAND_SPIRAL = register("sand_spiral", ParticleEffect(SandParticle()))

}
