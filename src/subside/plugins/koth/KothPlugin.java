package subside.plugins.koth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.commands.CommandHandler;
import subside.plugins.koth.faction.EventListenerNormal;
import subside.plugins.koth.faction.EventListenerUUID;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.loaders.ScheduleLoader;
import subside.plugins.koth.scoreboard.SBManager;

public class KothPlugin extends JavaPlugin {
	private @Getter static KothPlugin plugin;
	private @Getter static WorldEditPlugin worldEdit;

	@Override
	public void onEnable() {
		plugin = this;

		worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		getCommand("koth").setExecutor(new CommandHandler(this));
		init();
		
	}

    @SuppressWarnings("deprecation")
	public void init(){
        KothHandler.getInstance().stopAllKoths();
        this.saveDefaultConfig();
        this.reloadConfig();
	    new ConfigHandler(this.getConfig());
        Lang.load(this);
        KothLoader.load();
        LootLoader.load();
        ScheduleLoader.load();
        
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(SBManager.getManager(), this);
        
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                KothHandler.getInstance().update();
            }
        }, 20, 20);

        if (ConfigHandler.getCfgHandler().getGlobal().isUsePlayerMoveEvent()) {
            getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        }
        
        if(ConfigHandler.getCfgHandler().getFactions().isUseFactions()){
            hook2Factions();
        }
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
	

    
    private void hook2Factions() {
        if(!Bukkit.getPluginManager().isPluginEnabled("Factions")){
            this.getLogger().severe("Factions plugin not found! Could not hook in to the plugin!");
            return;
        }
        boolean shouldCapAll= getConfig().getBoolean("shouldCapAllAreas");
        try {
            Class.forName("com.massivecraft.factions.entity.FactionColl");
            this.getServer().getPluginManager().registerEvents(new EventListenerNormal(shouldCapAll), this);
        } catch(ClassNotFoundException e){
            this.getServer().getPluginManager().registerEvents(new EventListenerUUID(shouldCapAll), this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
