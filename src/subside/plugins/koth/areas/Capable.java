package subside.plugins.koth.areas;

import org.bukkit.OfflinePlayer;

public interface Capable {
    /**
     * @param player the player to check
     * @return true if the given player is in the area
     */
    public boolean isInArea(OfflinePlayer player);
}
