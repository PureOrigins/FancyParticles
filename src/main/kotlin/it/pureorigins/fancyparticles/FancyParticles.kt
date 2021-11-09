package it.pureorigins.fancyparticles

import it.pureorigins.fancyparticles.particles.NamedParticleEffect
import it.pureorigins.fancyparticles.particles.ParticleEffect
import it.pureorigins.fancyparticles.particles.ParticleEffects
import it.pureorigins.framework.configuration.*
import kotlinx.serialization.Serializable
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
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
    fun getAllNames(): Set<String> = transaction(database) { ParticlesTable.getAllNames() }
    fun addParticle(playerUniqueId: UUID, particleId: Int): Boolean = transaction(database) { PlayerParticlesTable.add(playerUniqueId, particleId) }
    fun removeParticle(playerUniqueId: UUID, particleId: Int): Boolean = transaction(database) { PlayerParticlesTable.remove(playerUniqueId, particleId) }
    fun getPlayerParticles(playerUniqueId: UUID): Map<Int, NamedParticleEffect> =
        transaction(database) { PlayerParticlesTable.getParticles(playerUniqueId) }

    fun getPlayersCount(): Long = transaction(database) { PlayersTable.count() }
    fun getCurrentParticle(playerUniqueId: UUID): Pair<Int, NamedParticleEffect>? =
        transaction(database) { PlayersTable.getCurrentParticle(playerUniqueId) }

    fun setCurrentParticle(playerUniqueId: UUID, particleId: Int?): Boolean =
        transaction(database) { PlayersTable.setCurrentParticle(playerUniqueId, particleId) }


    lateinit var scheduler: ScheduledExecutorService
    lateinit var server: MinecraftServer
    private lateinit var database: Database private set
    override fun onInitialize() {
        val (db, commands) = json.readFileAs(configFile("fancyparticles.json"), Config())
        scheduler = Executors.newScheduledThreadPool(4)
        require(db.url.isNotEmpty()) { "Database url is empty" }
        database = Database.connect(db.url, user = db.username, password = db.password)
        transaction(database) { createMissingTablesAndColumns(PlayersTable, ParticlesTable, PlayerParticlesTable) }
        ServerLifecycleEvents.SERVER_STARTED.register { server = it }
        CommandRegistrationCallback.EVENT.register { d, _ ->
            d.register(ParticleCommands(commands).command)
        }

        //Remove all particle tasks when player disconnects
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ -> clearTasks(handler.player) }

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            getCurrentParticle(handler.player.uuid)?.second?.let { scheduleParticle(it.particleEffect, handler.player) }
        }


        //Test commands
        CommandRegistrationCallback.EVENT.register { d, _ ->
            for (e in ParticleEffects.effects)
                d.register(literal(e.key) { success { scheduleParticle(e.value, source.player) } })
        }
    }

    fun scheduleParticle(particleEffect: ParticleEffect, player: ServerPlayerEntity) {
        clearTasks(player)
        playerTasks[player.uuid] = ArrayList()
        for (part in particleEffect.particleParts) {
            val task = ParticleTask(part, player)
            val t = scheduler.scheduleAtFixedRate(task, part.delay * 50, part.period * 50, TimeUnit.MILLISECONDS)
            playerTasks[player.uuid]?.add(t)
        }
    }

    public fun clearTasks(player: ServerPlayerEntity) {
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
            //Default db
            val url: String = "jdbc:sqlite:fancyparticles.db",
            val username: String = "",
            val password: String = ""
        )
    }
}

