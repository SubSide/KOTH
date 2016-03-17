package subside.plugins.koth;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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
import subside.plugins.koth.adapter.captypes.CappingFaction;
import subside.plugins.koth.adapter.captypes.CappingFactionUUID;
import subside.plugins.koth.adapter.captypes.CappingPlayer;
import subside.plugins.koth.commands.CommandHandler;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.loaders.ScheduleLoader;
import subside.plugins.koth.scoreboard.SBManager;

public class KothPlugin extends JavaPlugin {
	private @Getter static KothPlugin plugin;
	private @Getter static WorldEditPlugin worldEdit;
	
	@Override
	public void onLoad(){
        plugin = this;
        new KothHandler();
        this.saveDefaultConfig();
        this.reloadConfig();
        new ConfigHandler(this.getConfig());
        Lang.load(this);
        register();
	}
	
	@Override
	public void onEnable() {
		worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		getCommand("koth").setExecutor(new CommandHandler(this));
        init();
	}

    
    public void register(){
        // Registering the Gamemodes
        GamemodeRegistry gR = KothHandler.getInstance().getGamemodeRegistry();
        gR.register("classic", KothClassic.class);
        
        if(ConfigHandler.getCfgHandler().getFactions().isUseFactions()){
            gR.register("conquest", KothConquest.class);
        }
        
        // Registering the capture entities
        CapEntityRegistry cER = KothHandler.getInstance().getCapEntityRegistry();
        cER.registerCaptureType("player", CappingPlayer.class);
        if(ConfigHandler.getCfgHandler().getFactions().isUseFactions()){
            cER.registerCaptureType("faction", CappingFaction.class);
            cER.registerCaptureType("factionuuid", CappingFactionUUID.class);
        }
    }

    @SuppressWarnings("deprecation")
	public void init(){
        
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(SBManager.getManager(), this);
        
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                KothHandler.getInstance().update();
            }
        }, 20, 20);
        

        if(ConfigHandler.getCfgHandler().getScoreboard().isUseScoreboard()){
            ConfigurationSection section = ConfigHandler.getCfgHandler().getScoreboard().getSection();
            SBManager.getManager().load(ConfigHandler.getCfgHandler().getScoreboard().isUseOldScoreboard(), section.getString("scoreboard.title"), section.getStringList("scoreboard.contents").toArray(new String[section.getStringList("scoreboard.contents").size()]));
        }
        
        // LOADING

        KothHandler.getInstance().stopAllKoths();
        KothLoader.load();
        LootLoader.load();
        ScheduleLoader.load();
    }
    
	@Override
	public void onDisable() {
		SBManager.getManager().clearAll();
		
        for(Player player : Bukkit.getOnlinePlayers()){
            String title = player.getOpenInventory().getTitle();
            for(Loot loot : KothHandler.getInstance().getLoots()){
                if(loot.getInventory().getTitle().equalsIgnoreCase(title)){
                    player.closeInventory();
                }
            }
            
//            for(Koth koth : KothHandler.getInstance().getAvailableKoths()){
//                if(Loot.getKothLootTitle(koth.getName()).equalsIgnoreCase(title)){
//                    player.closeInventory();
//                }
//            }
        }
	}
    
}
