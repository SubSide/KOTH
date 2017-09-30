package subside.plugins.koth.hooks;

import org.bukkit.entity.Player;

import subside.plugins.koth.KothPlugin;

public abstract class AbstractHook {
    protected HookManager hookManager;
    
    public AbstractHook(HookManager hookManager){
        this.hookManager = hookManager;
    }
    
    /**
     * Initialize everything, this is important for hooks that require instances.
     */
    public void initialize(){}
    
    /**
     * Check if this hook allows (or disallows) a certain player for capping
     * @param player the player to check
     * @return true if the player can cap
     */
    public boolean canCap(Player player){ return true; }
    
    /**
     * Something that is executed every tick.
     */
    public void tick(){}
    
    /**
     * Overridable for if something should happen when the KoTH plugin gets disabled
     */
    public void onDisable(){}
    
    /**
     * Checks if the Hook should still be enabled.
     * @return true if it should be enabled
     */
    public abstract boolean isEnabled();
    
    /**
     * Convinience method. Returns the KothPlugin object.
     * @return the Koth Plugin
     */
    public KothPlugin getPlugin(){
        return hookManager.getPlugin();
    }
}
