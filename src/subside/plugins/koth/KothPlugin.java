package subside.plugins.koth;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import subside.plugins.koth.captureentities.CaptureTypeRegistry;
import subside.plugins.koth.commands.CommandHandler;
import subside.plugins.koth.datatable.DataTable;
import subside.plugins.koth.events.KothPluginInitializationEvent;
import subside.plugins.koth.gamemodes.GamemodeRegistry;
import subside.plugins.koth.hooks.HookManager;
import subside.plugins.koth.loot.LootHandler;
import subside.plugins.koth.modules.*;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.utils.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

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
	private @Getter DataTable dataTable;
	private @Getter CacheHandler cacheHandler;
	private @Getter VersionChecker versionChecker;
	
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

        // Add VersionChecker
		versionChecker = new VersionChecker(this);
		activeModules.add(versionChecker);
        
        
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
        
        // Trigger SETUP event for eventual hooking
        trigger(LoadingState.SETUP);
	}
	
	@Override
	public void onEnable() {
        trigger(LoadingState.ENABLE);
        
        // Some nasty code to be able to use %ttn% everywhere and to access the config handler
        MessageBuilder.setPlugin(this);
	}

	@Override
	public void onDisable() {
		trigger(LoadingState.DISABLE);

		// Some code to clean up static references (The only one)
		MessageBuilder.setPlugin(null);
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
                default:
                    break;
	        }
	    }

        getServer().getPluginManager().callEvent(new KothPluginInitializationEvent(this, state));
	}
    
	public enum LoadingState {
	    SETUP, LOAD, ENABLE, DISABLE
	}
}
