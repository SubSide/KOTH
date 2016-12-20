package subside.plugins.koth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;
import subside.plugins.koth.adapter.KothClassic;
import subside.plugins.koth.adapter.KothConquest;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.KothHandler.CapEntityRegistry;
import subside.plugins.koth.adapter.KothHandler.GamemodeRegistry;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.adapter.captypes.Capper;
import subside.plugins.koth.adapter.captypes.CappingFactionNormal;
import subside.plugins.koth.adapter.captypes.CappingFactionUUID;
import subside.plugins.koth.adapter.captypes.CappingGroup;
import subside.plugins.koth.adapter.captypes.CappingKingdom;
import subside.plugins.koth.adapter.captypes.CappingPlayer;
import subside.plugins.koth.commands.CommandHandler;
import subside.plugins.koth.hooks.HookManager;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.loaders.ScheduleLoader;
import subside.plugins.koth.scoreboard.ConquestScoreboard;
import subside.plugins.koth.scoreboard.DefaultScoreboard;
import subside.plugins.koth.scoreboard.OldScoreboard;
import subside.plugins.koth.scoreboard.ScoreboardManager;

public class KothPlugin extends JavaPlugin {
	private @Getter static KothPlugin plugin;
	private @Getter static WorldEditPlugin worldEdit;
	
	
	// Loaded on server startup (Not to be confused with enable)
	//
	// I added this to onLoad so other plugins can hook easier into the plugin
	// if they want to register their own entities and such.
	@Override
	public void onLoad(){
        plugin = this;
        
        // Initialize the KoTH main class
        new KothHandler();
        
        // load configs
        this.saveDefaultConfig();
        this.reloadConfig();
        new ConfigHandler(this.getConfig());
        
        // Load the lang.json
        Lang.load(this);
        
        // Register the gamemodes, entities, and scoreboards
        register();
	}
	
	@Override
	public void onEnable() {
		worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		getCommand("koth").setExecutor(new CommandHandler(this));
        init();
	}

    
    public void register(){
        // Registering the Gamemodes //
        GamemodeRegistry gR = KothHandler.getInstance().getGamemodeRegistry();
        
        gR.getGamemodes().clear();
        gR.register("classic", KothClassic.class);
        
        if(ConfigHandler.getInstance().getHooks().isFactions()){
            // Add conquest if factions is enabled in the config
            gR.register("conquest", KothConquest.class);
        }
        
        // Registering the capture entities //
        CapEntityRegistry cER = KothHandler.getInstance().getCapEntityRegistry();
        cER.getCaptureTypes().clear();
        cER.getCaptureClasses().clear();
        
        // Add the player entity

        cER.registerCaptureClass("capperclass", Capper.class);
        
        cER.registerCaptureType("player", CappingPlayer.class);
        cER.setPreferedClass(CappingPlayer.class);
        boolean hasGroupPlugin = false;
        if(ConfigHandler.getInstance().getHooks().isFactions() && getServer().getPluginManager().getPlugin("Factions") != null){
            try {
                // If this class is not found it means that Factions is not in the server
                Class.forName("com.massivecraft.factions.entity.FactionColl");
                cER.registerCaptureType("faction", CappingFactionNormal.class);
                cER.setPreferedClass(CappingFactionNormal.class);
                hasGroupPlugin = true;
            } catch(ClassNotFoundException e){
                // So if the class is not found, we add FactionsUUID instead
                cER.registerCaptureType("factionuuid", CappingFactionUUID.class);
                cER.setPreferedClass(CappingFactionUUID.class);
                hasGroupPlugin = true;
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        
        if(ConfigHandler.getInstance().getHooks().isKingdoms() && getServer().getPluginManager().getPlugin("Kingdoms") != null){
            cER.registerCaptureType("kingdoms", CappingKingdom.class);
            cER.setPreferedClass(CappingKingdom.class);
            hasGroupPlugin = true;
        }
        
        // Make sure when you register your own group-like capturetype, to register the CappingGroup in the capentityregistry
        if(hasGroupPlugin){
            cER.registerCaptureClass("groupclass", CappingGroup.class);
        }
        
        if(cER.getCaptureTypeClass(ConfigHandler.getInstance().getKoth().getDefaultCaptureType()) != null)
            cER.setPreferedClass(cER.getCaptureTypeClass(ConfigHandler.getInstance().getKoth().getDefaultCaptureType()));
        

        // Registering the scoreboards //
        new ScoreboardManager();
        if(ConfigHandler.getInstance().getScoreboard().isUseScoreboard()){
            if(ConfigHandler.getInstance().getHooks().isFactions()){
                ScoreboardManager.getInstance().registerScoreboard("conquest", ConquestScoreboard.class);
            }
            if(ConfigHandler.getInstance().getScoreboard().isUseOldScoreboard()){
                ScoreboardManager.getInstance().registerScoreboard("default", OldScoreboard.class);
            } else {
                ScoreboardManager.getInstance().registerScoreboard("default", DefaultScoreboard.class);
            }
        }
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
        // Register scoreboard events (PlayerJoin, playerQuit for setting and removing the scoreboard)
        if(ConfigHandler.getInstance().getScoreboard().isUseScoreboard()){
            getServer().getPluginManager().registerEvents(ScoreboardManager.getInstance(), this);
        }
        
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
        
        // Cache loading
        if(ConfigHandler.getInstance().getGlobal().isUseCache()){
            new CacheHandler();
            CacheHandler.getInstance().load(this);
        }
    }
    
	@Override
	public void onDisable() {
	    // Remove the scoreboard from all the players
		ScoreboardManager.getInstance().destroy();
		
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
    
}
