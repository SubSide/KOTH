package subside.plugins.koth.hooks;

import org.bukkit.entity.Player;

public abstract class AbstractHook {
    public boolean canCap(Player player){ return true; }
    public void tick(){}
    public abstract boolean isEnabled();
}
