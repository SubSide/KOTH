package subside.plugins.koth.hooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import subside.plugins.koth.hooks.plugins.FeatherboardHook;
import subside.plugins.koth.hooks.plugins.PlaceholderAPIHook;
import subside.plugins.koth.hooks.plugins.VanishHook;

public class HookManager {
    private @Getter List<AbstractHook> hooks;
    
    private JavaPlugin plugin;
    
    public HookManager(JavaPlugin plugin){
        this.plugin = plugin;
        hooks = new ArrayList<>();
        init();
    }
    
    private void init(){
        registerHook(new VanishHook(plugin));
        registerHook(new FeatherboardHook(plugin));
        
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new PlaceholderAPIHook(plugin).hook();
        }
    }
    
    public void registerHook(AbstractHook hook){
        hooks.add(hook);
        if(hook instanceof Listener){
            Bukkit.getServer().getPluginManager().registerEvents((Listener)hook, plugin);
        }
    }
    
    public boolean canCap(Player player){
        for(AbstractHook hook : hooks){
            if(!hook.isEnabled()) continue;
            if(!hook.canCap(player)) return false;
        }
        
        return true;
    }
    
    public void tick(){
        for(AbstractHook hook : hooks){
            if(!hook.isEnabled()) continue;
            hook.tick();
        }
    }
}
