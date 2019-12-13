package subside.plugins.koth.utils

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import subside.plugins.koth.KothManager
import subside.plugins.koth.KothPlugin
import subside.plugins.koth.modules.Lang
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    const val KOTH_IGNORE_KEY = "KOTH_IGNORING"

    /** Returns if the player has enabled the ignore feature in KoTH
     *
     * @param player the player to check
     * @return true if the player wants to ignore all messages
     */
    fun isIgnoring(player: Player): Boolean {
        return player.hasMetadata(KOTH_IGNORE_KEY)
    }

    /** Toggles the ignore state of the player
     *
     * @param player the player to toggle ignore for
     * @return true if the player is now ignoring the messages
     */
    fun toggleIgnoring(plugin: KothPlugin, player: Player): Boolean {
        return if (player.hasMetadata(KOTH_IGNORE_KEY)) {
            player.removeMetadata(KOTH_IGNORE_KEY, plugin)
            false
        } else {
            player.setMetadata(KOTH_IGNORE_KEY, FixedMetadataValue(plugin, true))
            true
        }
    }


    fun convertTime(time: String): Int {
        var t = 0
        if (time.contains(":")) {
            val split = time.split(":")
            if (split.size > 2) {
                try {
                    t = Integer.parseInt(split[0]) * 60 * 60 +
                            Integer.parseInt(split[1]) * 60 +
                            Integer.parseInt(split[2])
                } catch (e: Exception) { }

            } else {
                try {
                    t = Integer.parseInt(split[0]) * 60 +
                            Integer.parseInt(split[1])
                } catch (e: Exception) { }
            }
        } else {
            try {
                t = Integer.parseInt(time) * 60
            } catch (e: Exception) { }
        }

        return t
    }

    fun parseDate(millis: Long, kothManager: KothManager): String {
        val sdf = SimpleDateFormat()
        sdf.applyPattern(kothManager.configHandler.global.dateFormat)
        return sdf.format(Date(millis))
    }

    fun parseCurrentDate(kothManager: KothManager): String {
        return parseDate(
            System.currentTimeMillis() + kothManager.configHandler.global.minuteOffset * 60 * 1000,
            kothManager
        )
    }
}