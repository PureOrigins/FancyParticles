package it.pureorigins.fancyparticles

import com.mojang.brigadier.CommandDispatcher
import it.pureorigins.fancyparticles.particles.NamedParticleEffect
import it.pureorigins.fancyparticles.particles.ParticleEffect
import it.pureorigins.fancyparticles.particles.ParticleEffects
import it.pureorigins.framework.configuration.*
import kotlinx.serialization.Serializable
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object FancyParticles : ModInitializer {

    private val playerTasks = HashMap<UUID, ArrayList<ScheduledFuture<*>>>()
    fun getParticle(id: Int): NamedParticleEffect? = transaction(database) { ParticlesTable.getById(id) }
    fun getParticle(name: String): Pair<Int, NamedParticleEffect>? =
        transaction(database) { ParticlesTable.getByName(name) }

    fun getPlayersCount(): Long = transaction(database) { PlayersTable.count() }
    fun getCurrentParticle(playerUniqueId: UUID): Pair<Int, NamedParticleEffect>? =
        transaction(database) { PlayersTable.getCurrentParticle(playerUniqueId) }
    fun setCurrentParticle(playerUniqueId: UUID, titleId: Int?): Boolean =
        transaction(database) { PlayersTable.setCurrentParticle(playerUniqueId, titleId) }


    lateinit var scheduler: ScheduledExecutorService
    internal lateinit var database: Database private set
    override fun onInitialize() {
        val config = json.readFileAs(configFile("fancyparticles.json"), Config())
        scheduler = Executors.newScheduledThreadPool(4)
        database = Database.connect(config.database.url, user = config.database.username, password = config.database.password)
        transaction(database) {
            createMissingTablesAndColumns(PlayersTable, ParticlesTable)
        }

        //Remove all particle tasks when player disconnects
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ -> clearTasks(handler.player) }

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            getCurrentParticle(handler.player.uuid)?.second?.let { scheduleParticle(it.particleEffect, handler.player) }
        }


        //Test commands
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { d: CommandDispatcher<ServerCommandSource?>, _: Boolean ->
            for (e in ParticleEffects.effects)
                d.register(literal(e.key) { success { scheduleParticle(e.value, source.player) } })
        })
    }

    private fun scheduleParticle(particleEffect: ParticleEffect, player: ServerPlayerEntity) {
        clearTasks(player)
        playerTasks[player.uuid] = ArrayList()
        for (part in particleEffect.particleParts) {
            val task = ParticleTask(part, player)
            val t = scheduler.scheduleAtFixedRate(task, part.delay * 50, part.period * 50, TimeUnit.MILLISECONDS)
            playerTasks[player.uuid]?.add(t)
        }
    }

    private fun clearTasks(player: ServerPlayerEntity) {
        if (playerTasks.containsKey(player.uuid))
            for (t in playerTasks[player.uuid]!!) t.cancel(false)
        playerTasks.remove(player.uuid)
    }

    @Serializable
    data class Config(
        val database: Database = Database(),
        val commands: ParticleCommands.Config = ParticleCommands.Config()
    ) {
        @Serializable
        data class Database(
            val url: String = "",
            val username: String = "",
            val password: String = ""
        )
    }
}

