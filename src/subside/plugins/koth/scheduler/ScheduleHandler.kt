package subside.plugins.koth.scheduler

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import subside.plugins.koth.KothManager
import subside.plugins.koth.areas.Koth
import subside.plugins.koth.events.KothEndEvent
import subside.plugins.koth.gamemodes.RunningKoth
import subside.plugins.koth.gamemodes.StartParams
import subside.plugins.koth.modules.Module
import subside.plugins.koth.utils.FileLoader

class ScheduleHandler(kothManager: KothManager) : Module, Listener {
    val schedules: MutableList<Schedule> = ArrayList()
    val mapRotation: MapRotation = MapRotation(kothManager.configHandler.koth.mapRotation)

    fun getNextEvent(): Schedule? = schedules.minBy { it.getNextEvent() }

    fun getNextEvent(koth: Koth): Schedule? =
        schedules
            .filter { it.koth == koth.name }
            .minBy { it.getNextEvent() }

    fun tick(kothManager: KothManager) {
        schedules.forEach {
            it.tick(kothManager)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onKothEnd(event: KothEndEvent) {
        if (!kothManager.configHandler.koth.startNewOnEnd)
            return

        if (event.reason == RunningKoth.EndReason.FORCED)
            return

        Bukkit.getScheduler().runTaskLater(kothManager.plugin.get()!!, {
            if (kothManager.kothHandler.runningKoths.isNotEmpty())
                return@runTaskLater

            val params = StartParams(mapRotation.getNext())
            try {
                kothManager.kothHandler.startKoth(params)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 20)
    }

    override fun onEnable(kothManager: KothManager) {
        val plugin = kothManager.plugin.get() ?: return
        plugin.server.pluginManager.registerEvents(this, plugin)

        schedules.clear()
        schedules.addAll(FileLoader("schedule.json").loadList(plugin,Schedule::class.java))
    }

    override fun onDisable(kothManager: KothManager) {
        // Remove all previous event handlers
        HandlerList.unregisterAll(this)
        save(kothManager)
    }

    fun save(kothManager: KothManager) {
        FileLoader("schedule.json").save(kothManager.plugin.get()!!, schedules)
    }
}