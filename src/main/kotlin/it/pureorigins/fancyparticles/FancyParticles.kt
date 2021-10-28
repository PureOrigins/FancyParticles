package it.pureorigins.fancyparticles

import com.mojang.brigadier.CommandDispatcher
import it.pureorigins.fancyparticles.particles.FireParticle
import it.pureorigins.fancyparticles.particles.Particle
import it.pureorigins.framework.configuration.literal
import it.pureorigins.framework.configuration.success
import kotlinx.serialization.Serializable
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.jetbrains.exposed.sql.Database
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object FancyParticles : ModInitializer {

    fun getParticles(): List<Particle> =
        TODO()


    lateinit var scheduler: ScheduledExecutorService
    internal lateinit var database: Database private set
    override fun onInitialize() {
        scheduler = Executors.newScheduledThreadPool(4)

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { d: CommandDispatcher<ServerCommandSource?>, _: Boolean ->
            d.register(literal("fancy") { success { scheduleParticle(FireParticle, source.player) } })
        })
    }

    private fun scheduleParticle(particle: Particle, player: ServerPlayerEntity) {
        val task = ParticleTask(particle, player)
        scheduler.scheduleAtFixedRate(task, particle.delay * 50, particle.period * 50, TimeUnit.MILLISECONDS)
    }

    @Serializable
    data class Config(
        val database: Database = Database(),
        val titles: ParticleCommands.Config = ParticleCommands.Config()
    ) {
        @Serializable
        data class Database(
            val url: String = "",
            val username: String = "",
            val password: String = ""
        )
    }
}

