package subside.plugins.koth.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.ConfigHandler;

public class VanishHook implements IHook {
    private @Getter @Setter boolean enabled = false;
    private VanishManager vanishManager;
    
    public VanishHook(){
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("VanishNoPacket")){
            if(ConfigHandler.getCfgHandler().getHooks().isVanishNoPacket()){
                enabled = true;
            }
            vanishManager = ((VanishPlugin)Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket")).getManager();
        }
        
    }
    
    @Override
    public boolean canCap(Player player) {
        return vanishManager.isVanished(player);
    }

}
