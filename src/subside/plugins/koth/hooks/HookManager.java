package subside.plugins.koth.hooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.modules.AbstractModule;

public class HookManager extends AbstractModule {
    private @Getter List<AbstractHook> hooks;
    
    public HookManager(KothPlugin plugin){
        super(plugin);
        hooks = new ArrayList<>();
    }
    
    @Override
    public void onEnable(){
        registerHook(new VanishHook(this));
        registerHook(new FeatherboardHook(this));
        registerHook(new PvPManagerHook(this));
        
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new PlaceholderAPIHook(plugin).hook();
        }
    }
    
    @Override
    public void onDisable(){
        for(AbstractHook hook : hooks){
            hook.onDisable();
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
