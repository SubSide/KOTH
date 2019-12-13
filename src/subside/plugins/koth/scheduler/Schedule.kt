package subside.plugins.koth.scheduler

import org.bukkit.configuration.serialization.ConfigurationSerializable
import subside.plugins.koth.KothManager
import subside.plugins.koth.utils.MessageBuilder
import java.util.logging.Level

class Schedule(
    var koth: String,
    var day: Day,
    var time: String
) : ConfigurationSerializable {
    var captureTime = 15
    var maxRunTime = -1
    var lootAmount = -1
    var lootChest: String? = null
    var entityType: String? = null

    private var nextEventMillis: Long = 0
    private var preBroadcastIndex: Int = 0
    private var hasSetup = false

    fun tick(kothManager: KothManager) {
        if (!hasSetup) {
            calculateNextEvent(kothManager)
            hasSetup = true
        }

        // Broadcast stuff
        val configGlobal = kothManager.configHandler.global
        // If we have anything to broadcast
        if (configGlobal.preBroadcast && preBroadcastIndex >= 0) {
            // We calculate the next broadcast time
            val time = configGlobal.preBroadcastTimes[preBroadcastIndex]
            val nextBroadcast = nextEventMillis - time * 1000
            // If we the next broadcast time already past we should broadcast it
            if (System.currentTimeMillis() > nextBroadcast) {
                MessageBuilder(configGlobal.preBroadcastMessages[time])
                    .maxTime(maxRunTime)
                    .captureTime(captureTime)
                    .lootAmount(lootAmount)
                    .koth(kothManager.kothHandler, koth)
                    .broadcast()
                preBroadcastIndex--
            }
        }

        // Code for when a KoTH should start
        if (System.currentTimeMillis() > nextEventMillis) {
            // We reset the broadcast index
            preBroadcastIndex = kothManager.configHandler.global.preBroadcastTimes.size - 1

            // And we set the next event time
            setNextEventTime()
            try {
                kothManager.kothHandler.startKoth(this)
            } catch (e: Exception) {
                kothManager.plugin.get()!!.logger.warning("KoTH is already running")
            }
        }
    }

    private fun setNextEventTime() {
        nextEventMillis += WEEK
    }

    /**
     * Simple getter for nextEventMillis
     * @return When the next event should run (in milliseconds)
     */
    fun getNextEvent(): Long {
        return nextEventMillis
    }


    private fun calculateNextEvent(kothManager: KothManager) {
        var eventTime = day.getDayStart(kothManager) + Day.getTime(time) - WEEK
        eventTime += kothManager.configHandler.global.scheduleMinuteOffset * 60 * 1000

        while (eventTime < System.currentTimeMillis()) {
            eventTime += WEEK
        }

        if (kothManager.configHandler.global.debug) {
            kothManager.plugin.get()!!.logger.log(Level.WARNING, "Schedule created for: $day $time $nextEventMillis")
        }

        // Broadcast stuff
        val times = kothManager.configHandler.global.preBroadcastTimes
        preBroadcastIndex = times.size - 1
        while (preBroadcastIndex >= 0 && nextEventMillis - times[preBroadcastIndex] * 1000 < System.currentTimeMillis()) {
            preBroadcastIndex--
        }
    }

    override fun serialize(): Map<String, Any?> {
        return mapOf(
            "koth" to koth,
            "day" to day.identifier,
            "time" to time,
            "captureTime" to captureTime,
            "maxRunTime" to maxRunTime,
            "lootAmount" to lootAmount,
            "lootChest" to lootChest,
            "entityType" to entityType
        )
    }

    companion object {
        const val WEEK: Long = 7 * 24 * 60 * 60 * 1000

        @JvmStatic fun deserialize(args: Map<String, Any>): Schedule {
            return Schedule(
                args["koth"] as String,
                Day.values().find { it.identifier == args["day"] } ?: Day.MONDAY,
                args["time"] as String
            ).also {
                it.captureTime = args.getOrDefault("captureTime", 15) as Int
                it.maxRunTime = args.getOrDefault("maxRunTime", -1) as Int
                it.lootAmount = args.getOrDefault("lootAmount", -1) as Int
                it.lootChest = args.getOrDefault("lootChest", null) as String?
                it.entityType = args.getOrDefault("entityType", null) as String?
            }
        }
    }
}