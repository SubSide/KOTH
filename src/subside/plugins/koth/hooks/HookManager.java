package subside.plugins.koth.hooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
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
        registerHook(new BossbarHook(this));
        registerHook(new PvPManagerHook(this));
        registerHook(new EssentialsVanishHook(this));
        
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new PlaceholderAPIHook(plugin).register();
        }
    }
    
    @Override
    public void onDisable(){
        for(AbstractHook hook : hooks){
            try {
                hook.onDisable();
            } catch(Exception e){}
            if(hook instanceof Listener){
                HandlerList.unregisterAll((Listener)hook);
            }
        }
    }
    
    public void registerHook(AbstractHook hook){
        if(!hook.isEnabled()){
            hook.onDisable();
            return;
        }
        
        hook.initialize();
        
        hooks.add(hook);
        
        // Register events if they might contain events.
        if(hook instanceof Listener){
            Bukkit.getServer().getPluginManager().registerEvents((Listener)hook, plugin);
        }
    }
    
    public boolean canCap(Player player){
        for(AbstractHook hook : hooks){
            if(!hook.canCap(player)) return false;
        }
        
        return true;
    }
    
    public void tick(){
        for(AbstractHook hook : hooks){
            hook.tick();
        }
    }
}
