package subside.plugins.koth.hooks;

import org.bukkit.entity.Player;

public interface IHook {
    public boolean canCap(Player player);
    public boolean isEnabled();
}
