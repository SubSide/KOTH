package subside.plugins.koth.areas

import org.bukkit.OfflinePlayer

interface Cappable {
    /**
     * @param player the player to check
     * @return true if the given player is in the area
     */
    fun isInArea(offlinePlayer: OfflinePlayer?): Boolean
}