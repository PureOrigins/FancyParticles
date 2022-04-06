package it.pureorigins.pureparticles

import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.BaseComponent
import net.minecraft.network.chat.ContextAwareComponent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.Entity
import java.util.*

data class ParticleComponent(val particleName: String) : BaseComponent(), ContextAwareComponent {
    override fun resolve(source: CommandSourceStack?, sender: Entity?, depth: Int): MutableComponent {
        val particleWithId = plugin.getParticle(particleName)
        return particleWithId?.second?.title ?: TextComponent("")
    }
    
    override fun getContents() = particleName
    override fun plainCopy() = ParticleComponent(particleName)
    override fun toString() = "ParticleComponent{particleName='$particleName', siblings=$siblings, style=$style}"
    
    override fun equals(other: Any?) = when {
        this === other -> true
        other !is ParticleComponent -> false
        else -> particleName == other.particleName && super.equals(other)
    }
    
    override fun hashCode() = Objects.hash(particleName, super.hashCode())
}
