package subside.plugins.koth.hooks;

import org.bukkit.entity.Player;

import subside.plugins.koth.KothPlugin;

public abstract class AbstractHook {
    protected HookManager hookManager;
    
    public AbstractHook(HookManager hookManager){
        this.hookManager = hookManager;
    }
    
    public void initialize(){}
    public boolean canCap(Player player){ return true; }
    public void tick(){}
    public void onDisable(){}
    public abstract boolean isEnabled();
    
    public KothPlugin getPlugin(){
        return hookManager.getPlugin();
    }
}
