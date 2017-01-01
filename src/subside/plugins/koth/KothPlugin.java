package subside.plugins.koth;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import subside.plugins.koth.captureentities.CaptureTypeRegistry;
import subside.plugins.koth.commands.CommandHandler;
import subside.plugins.koth.datatable.DataTable;
import subside.plugins.koth.events.KothPluginInitializationEvent;
import subside.plugins.koth.gamemodes.GamemodeRegistry;
import subside.plugins.koth.hooks.HookManager;
import subside.plugins.koth.loot.LootHandler;
import subside.plugins.koth.modules.AbstractModule;
import subside.plugins.koth.modules.CacheHandler;
import subside.plugins.koth.modules.ConfigHandler;
import subside.plugins.koth.modules.EventListener;
import subside.plugins.koth.modules.KothHandler;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.scheduler.MapRotation;
import subside.plugins.koth.scheduler.ScheduleHandler;

public class KothPlugin extends JavaPlugin {
    
    // Modules
    private @Getter ConfigHandler configHandler;
	private @Getter CommandHandler commandHandler;
	private @Getter LootHandler lootHandler;
    private @Getter GamemodeRegistry gamemodeRegistry;
    private @Getter CaptureTypeRegistry captureTypeRegistry;
    private @Getter KothHandler kothHandler;
    private @Getter HookManager hookManager;
    private @Getter ScheduleHandler scheduleHandler;
    private @Getter MapRotation mapRotation;
	private @Getter DataTable dataTable;
	private @Getter CacheHandler cacheHandler;
	
	private List<AbstractModule> activeModules;
	
	
	// Loaded on server startup (Not to be confused with enable)
	//
	// I added this to onLoad so other plugins can hook easier into the plugin
	// if they want to register their own entities and such.
	@Override
	public void onLoad(){
	    // Load and set up all modules
	    setupModules();
	    
        // Trigger loading event on the modules
        trigger(LoadingState.LOAD);
	}
	
	public void setupModules(){
	    // Clear the module list
	    activeModules = new ArrayList<>();
	    
	    // Add Lang
	    activeModules.add(new Lang(this));
	    
	    // Add ConfigHandler
	    configHandler = new ConfigHandler(this);
	    activeModules.add(configHandler);
	    
	    // Add CommandHandler
	    commandHandler = new CommandHandler(this);
	    activeModules.add(commandHandler);
	    
	    // Add LootHandler
	    lootHandler = new LootHandler(this);
	    activeModules.add(lootHandler);
	    
	    // Add GamemodeRegistry
	    gamemodeRegistry = new GamemodeRegistry(this);
	    activeModules.add(gamemodeRegistry);
	    
	    // Add CaptureTypeRegistry
	    captureTypeRegistry = new CaptureTypeRegistry(this);
	    activeModules.add(captureTypeRegistry);
	    
        // Add KothHandler
	    kothHandler = new KothHandler(this);
	    activeModules.add(kothHandler);
	    
	    // Add HookManager
	    hookManager = new HookManager(this);
	    activeModules.add(hookManager);
        
        // Add EventListener
        activeModules.add(new EventListener(this));
        
        // Add ScheduleHandler
        scheduleHandler = new ScheduleHandler(this);
        activeModules.add(scheduleHandler);
        
        
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
        trigger(LoadingState.ENABLE);
	}
	
	@Override
	public void onDisable() {
		trigger(LoadingState.DISABLE);
	}
	
	public void trigger(LoadingState state){
	    for(AbstractModule module : activeModules){
	        switch(state){
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

        getServer().getPluginManager().callEvent(new KothPluginInitializationEvent(state));
	}
    
	public enum LoadingState {
	    LOAD, ENABLE, DISABLE;
	}
}
