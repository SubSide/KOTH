package subside.plugins.koth.hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;

import lombok.Getter;
import lombok.Setter;

public class VanishHook extends AbstractHook {
    private @Getter @Setter boolean enabled = false;
    private VanishManager vanishManager;
    
    public VanishHook(HookManager hookManager){
        super(hookManager); // First call the constructor of the parent class
        
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("VanishNoPacket")){
            if(getPlugin().getConfigHandler().getHooks().isVanishNoPacket()){
                enabled = true;
                vanishManager = ((VanishPlugin)Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket")).getManager();
            }
        }
        getPlugin().getLogger().log(Level.INFO, "VanishNoPacket hook: "+(enabled?"Enabled":"Disabled"));
    }
    
    @Override
    public boolean canCap(Player player) {
        return !vanishManager.isVanished(player);
    }

}
