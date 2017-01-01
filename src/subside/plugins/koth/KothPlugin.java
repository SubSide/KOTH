package subside.plugins.koth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;
import subside.plugins.koth.capture.Capper;
import subside.plugins.koth.capture.CappingFactionNormal;
import subside.plugins.koth.capture.CappingFactionUUID;
import subside.plugins.koth.capture.CappingGroup;
import subside.plugins.koth.capture.CappingKingdom;
import subside.plugins.koth.capture.CappingPlayer;
import subside.plugins.koth.KothHandler.CapEntityRegistry;
import subside.plugins.koth.KothHandler.GamemodeRegistry;
import subside.plugins.koth.commands.CommandHandler;
import subside.plugins.koth.datatable.DataTable;
import subside.plugins.koth.gamemodes.KothClassic;
import subside.plugins.koth.gamemodes.KothConquest;
import subside.plugins.koth.hooks.HookManager;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.loaders.ScheduleLoader;

public class KothPlugin extends JavaPlugin {
    
    // Modules
	private @Getter CommandHandler commandHandler;
	private @Getter DataTable dataTable;
	private @Getter CacheHandler cacheHandler;
	
	
	// Loaded on server startup (Not to be confused with enable)
	//
	// I added this to onLoad so other plugins can hook easier into the plugin
	// if they want to register their own entities and such.
	@Override
	public void onLoad(){
        
        // Initialize the KoTH main class
        new KothHandler();
        
        // load configs
        this.saveDefaultConfig();
        this.reloadConfig();
        new ConfigHandler(this.getConfig());
        
        // Load the lang.json
        Lang.load(this);
        
        // Trigger loading event on the modules
        trigger(LoadingType.LOAD);
	}
	
	@Override
	public void onEnable() {
		commandHandler = new CommandHandler(this);
		getCommand("koth").setExecutor(commandHandler);
        init();
	}

    @SuppressWarnings("deprecation")
	public void init(){
        // Remove all previous event handlers
        HandlerList.unregisterAll(this);
        // Remove all previous schedulings
        Bukkit.getScheduler().cancelTasks(this);
        
        // reload configs
        this.reloadConfig();
        new ConfigHandler(this.getConfig());
        
        // reload the lang.json
        Lang.load(this);
        
        // Register all events
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        
        
        // Add a repeating ASYNC scheduler for the KothHandler
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                KothHandler.getInstance().update();
            }
        }, 20, 20);
        
        // Load all the hooks
        new HookManager();
        
        // All the standard loading
        KothHandler.getInstance().stopAllKoths();
        KothLoader.load();
        LootLoader.load();
        ScheduleLoader.load();
        
        // Database connection
        if(ConfigHandler.getInstance().getDatabase().isEnabled()){
            dataTable = new DataTable(this);
        }
        
        // Cache loading
        if(ConfigHandler.getInstance().getGlobal().isUseCache()){
            Bukkit.getScheduler().runTask(this, new BukkitRunnable(){
                @Override
                public void run(){
                    new CacheHandler();
                    CacheHandler.getInstance().load(KothPlugin.getPlugin());
                }
            });
        }
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
        
        // Cache saving
        if(ConfigHandler.getInstance().getGlobal().isUseCache()){
            CacheHandler.getInstance().save(this);
        }
	}
	
	public void trigger(LoadingType event){
	    AbstractModule[] modules = { commandHandler, dataTable, cacheHandler, gamemodeRegistry captureTypeRegistry };
	    for(AbstractModule module : modules){
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
