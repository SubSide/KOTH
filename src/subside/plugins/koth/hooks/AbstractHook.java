package subside.plugins.koth.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractHook {
    protected JavaPlugin plugin;
    
    public AbstractHook(JavaPlugin plugin){
        this.plugin = plugin;
    }
    
    public boolean canCap(Player player){ return true; }
    public void tick(){}
    public abstract boolean isEnabled();
}
