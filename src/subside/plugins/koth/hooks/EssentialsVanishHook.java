package subside.plugins.koth.hooks;

import java.util.logging.Level;

import com.earth2me.essentials.Essentials;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class EssentialsVanishHook extends AbstractHook {
    private @Getter @Setter boolean enabled = false;
    private IEssentials essentials;

    public EssentialsVanishHook(HookManager hookManager){
        super(hookManager); // First call the constructor of the parent class

        if(Bukkit.getServer().getPluginManager().isPluginEnabled("Essentials")){
            if(getPlugin().getConfigHandler().getHooks().isEssentialsVanish()){
                enabled = true;
                essentials = (Essentials)Bukkit.getServer().getPluginManager().getPlugin("Essentials");
            }
        }
        getPlugin().getLogger().log(Level.INFO, "Essentials Vanish hook: "+(enabled?"Enabled":"Disabled"));
    }

    @Override
    public boolean canCap(Player player) {
        return !essentials.getUser(player).isVanished();
    }

}
