package subside.plugins.koth.hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.NoChance.PvPManager.PvPManager;
import me.NoChance.PvPManager.Managers.PlayerHandler;

public class PvPManagerHook extends AbstractHook {
    private @Getter @Setter boolean enabled = false;
    private PlayerHandler playerHandler;
    
    public PvPManagerHook(HookManager hookManager){
        super(hookManager); // First call the constructor of the parent class
        
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PvPManager")){
            if(getPlugin().getConfigHandler().getHooks().isPvpManager()){
                enabled = true;
            }
            playerHandler = ((PvPManager)Bukkit.getServer().getPluginManager().getPlugin("PvPManager")).getPlayerHandler();
        }
        getPlugin().getLogger().log(Level.INFO, "PvPManager hook: "+(enabled?"Enabled":"Disabled"));
    }
    
    @Override
    public boolean canCap(Player player) {
        return !playerHandler.get(player).isNewbie();
    }

}