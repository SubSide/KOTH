package subside.plugins.koth.hooks.plugins;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.hooks.AbstractHook;

public class VanishHook extends AbstractHook {
    private @Getter @Setter boolean enabled = false;
    private VanishManager vanishManager;
    
    public VanishHook(JavaPlugin plugin){
        super(plugin); // First call the constructor of the parent class
        
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("VanishNoPacket")){
            if(ConfigHandler.getInstance().getHooks().isVanishNoPacket()){
                enabled = true;
            }
            vanishManager = ((VanishPlugin)Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket")).getManager();
        }
        plugin.getLogger().log(Level.INFO, "Vanish hook: "+(enabled?"Enabled":"Disabled"));
    }
    
    @Override
    public boolean canCap(Player player) {
        return !vanishManager.isVanished(player);
    }

}
