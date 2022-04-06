package it.pureorigins.fancyparticles

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object Events : Listener {
    @EventHandler
    fun onLeave(event: PlayerJoinEvent) {
        plugin.clearTasks(event.player)
    }

    @EventHandler
    fun onJoin(event: PlayerQuitEvent) {
        plugin.getCurrentParticle(event.player.uniqueId)?.second?.let {
            plugin.scheduleParticle(event.player, it.particleEffect)
        }
    }
}
