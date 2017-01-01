package subside.plugins.koth;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import subside.plugins.koth.commands.CommandHandler;
import subside.plugins.koth.datatable.DataTable;
import subside.plugins.koth.hooks.HookManager;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.loaders.ScheduleLoader;
import subside.plugins.koth.loot.Loot;

public class KothPlugin extends JavaPlugin {
    
    // Modules
    private @Getter ConfigHandler configHandler;
	private @Getter CommandHandler commandHandler;
    private @Getter KothHandler kothHandler;
    private @Getter HookManager hookManager;
	private @Getter DataTable dataTable;
	private @Getter CacheHandler cacheHandler;
	private @Getter EventListener eventListener;
	
	private @Getter List<AbstractModule> activeModules;
	
	
	// Loaded on server startup (Not to be confused with enable)
	//
	// I added this to onLoad so other plugins can hook easier into the plugin
	// if they want to register their own entities and such.
	@Override
	public void onLoad(){
        // Load the lang.json
        Lang.load(this);
        
        // Trigger loading event on the modules
        trigger(LoadingType.LOAD);
	}
	
	public void setupModules(){
	    // Clear the module list
	    activeModules = new ArrayList<>();;
	    
	    // Add ConfigHandler
	    configHandler = new ConfigHandler(this);
	    activeModules.add(configHandler);
	    
	    // Add CommandHandler
	    commandHandler = new CommandHandler(this);
	    activeModules.add(commandHandler);
        
        // Add KothHandler
	    kothHandler = new KothHandler(this);
	    activeModules.add(kothHandler);
	    
	    // Add HookManager
	    hookManager = new HookManager(this);
	    activeModules.add(hookManager);
        
        // Add EventListener
        eventListener = new EventListener(this);
        activeModules.add(eventListener);
        
        
        /* Now add all the dynamic modules */
        // Add DataTable
        if(configHandler.getDatabase().isEnabled()){
            dataTable = new DataTable(this);
            activeModules.add(dataTable);
        }
        
        // Add CacheHandler
        if(configHandler.getGlobal().isUseCache()){
            cacheHandler = new CacheHandler(this);
            activeModules.add(cacheHandler);
        }
	}
	
	@Override
	public void onEnable() {
        init();
        trigger(LoadingType.ENABLE);
	}

	@SuppressWarnings("deprecation")
    public void init(){
        
        // reload the lang.json
        Lang.load(this);
        
        // All the standard loading
        KothHandler.getInstance().stopAllKoths();
        KothLoader.load();
        LootLoader.load();
        ScheduleLoader.load();
    }
    
	@Override
	public void onDisable() {
		// Make sure that nobody is viewing a loot chest
		// This is important because otherwise people could take stuff out of the viewing loot chest
        for(Player player : Bukkit.getOnlinePlayers()){
            String title = player.getOpenInventory().getTitle();
            for(Loot loot : KothHandler.getInstance().getLoots()){
                if(loot.getInventory().getTitle().equalsIgnoreCase(title)){
                    player.closeInventory();
                    break; // No need to close the players inventory more than once!
                }
            }
        }
	}
	
	public void trigger(LoadingType event){
	    for(AbstractModule module : activeModules){
	        switch(event){
	            case LOAD:
	                module.onLoad();
	                break;
	            case ENABLE:
	                module.onEnable();
	                break;
	            case DISABLE:
	                module.onDisable();
	                break;
	        }
	    }
	}
    
	enum LoadingType {
	    LOAD, ENABLE, DISABLE;
	}
}
