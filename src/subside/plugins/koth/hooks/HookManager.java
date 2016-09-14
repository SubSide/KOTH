package subside.plugins.koth.hooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.hooks.plugins.FeatherboardHook;
import subside.plugins.koth.hooks.plugins.PlaceholderAPIHook;
import subside.plugins.koth.hooks.plugins.VanishHook;

public class HookManager {
    private static @Getter HookManager hookManager;
    private @Getter List<AbstractHook> hooks;
    
    public HookManager(){
        hookManager = this;
        hooks = new ArrayList<>();
        init();
    }
    
    private void init(){
        registerHook(new VanishHook());
        registerHook(new FeatherboardHook());
        
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new PlaceholderAPIHook(KothPlugin.getPlugin()).hook();
        }
    }
    
    public void registerHook(AbstractHook hook){
        hooks.add(hook);
        if(hook instanceof Listener){
            Bukkit.getServer().getPluginManager().registerEvents((Listener)hook, KothPlugin.getPlugin());
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
