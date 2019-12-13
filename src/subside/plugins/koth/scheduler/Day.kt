package subside.plugins.koth.scheduler

import subside.plugins.koth.KothManager
import subside.plugins.koth.KothPlugin
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.Utils
import java.lang.IllegalStateException
import java.util.*
import java.util.logging.Level

enum class Day(val identifier: String) {
    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    SATURDAY("saturday"),
    SUNDAY("sunday");

    fun getText(): String {
        return when (this) {
            MONDAY -> Lang.KOTH_DAY_MONDAY[0]
            TUESDAY -> Lang.KOTH_DAY_TUESDAY[0]
            WEDNESDAY -> Lang.KOTH_DAY_WEDNESDAY[0]
            THURSDAY -> Lang.KOTH_DAY_THURSDAY[0]
            FRIDAY -> Lang.KOTH_DAY_FRIDAY[0]
            SATURDAY -> Lang.KOTH_DAY_SATURDAY[0]
            SUNDAY -> Lang.KOTH_DAY_SUNDAY[0]
        }
    }

    fun getDayStart(kothManager: KothManager): Long {
        return getStartOfWeek(kothManager) + this.ordinal * 24 * 60 * 60 * 1000
    }

    companion object {

        fun getCurrentDay(): Day? {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_WEEK)

            return when (day) {
                Calendar.MONDAY -> Day.MONDAY
                Calendar.TUESDAY -> Day.TUESDAY
                Calendar.WEDNESDAY -> Day.WEDNESDAY
                Calendar.THURSDAY -> Day.THURSDAY
                Calendar.FRIDAY -> Day.FRIDAY
                Calendar.SATURDAY -> Day.SATURDAY
                Calendar.SUNDAY -> Day.SUNDAY
                else -> throw IllegalStateException("Day $day doesn't exist")
            }
        }

        fun getTime(time: String): Long {
            var hours = 0
            var minutes = 0
            if (time.contains(":")) {
                val timz = time.split(":")
                try {
                    hours = Integer.parseInt(timz[0])
                    minutes = Integer.parseInt(timz[1].replace("[a-zA-Z]".toRegex(), ""))
                } catch (e: Exception) {
                }

            } else {
                try {
                    hours = Integer.parseInt(time.replace("[a-zA-Z]".toRegex(), ""))
                } catch (e: Exception) {
                }

            }

            if (time.toLowerCase().endsWith("pm") && hours % 12 != 0) {
                hours += 12
            }
            return (hours * 60 * 60 * 1000 + minutes * 60 * 1000).toLong()
        }

        private fun getStartOfWeek(kothManager: KothManager): Long {
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone(kothManager.configHandler.global.timeZone)
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.add(Calendar.MINUTE, kothManager.configHandler.global.startWeekMinuteOffset)

            if (kothManager.configHandler.global.debug) {
                kothManager.plugin.get()!!.logger.log(
                    Level.INFO,
                    "Schedule start of week has been set to: " +
                            Utils.parseDate(calendar.timeInMillis, kothManager.plugin.get()!!) +
                            " (${calendar.timeInMillis})"
                )
            }
            return calendar.timeInMillis
        }

        fun getDay(str: String): Day? {
            when (str.toLowerCase()) {
                Lang.KOTH_DAY_MONDAY[0] -> MONDAY
                Lang.KOTH_DAY_TUESDAY[0] ->TUESDAY
                Lang.KOTH_DAY_WEDNESDAY[0] -> WEDNESDAY
                Lang.KOTH_DAY_THURSDAY[0] ->THURSDAY
                Lang.KOTH_DAY_FRIDAY[0] -> FRIDAY
                Lang.KOTH_DAY_SATURDAY[0] -> SATURDAY
                Lang.KOTH_DAY_SUNDAY[0] -> SUNDAY
            }
            return null
        }
    }
}