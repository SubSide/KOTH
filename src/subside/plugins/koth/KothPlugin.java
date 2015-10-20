package subside.plugins.koth;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.commands.CommandHandler;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.loaders.ScheduleLoader;
import subside.plugins.koth.scoreboard.SBManager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

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
        KothHandler.stopAllKoths();
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
                KothHandler.update();
            }
        }, 20, 20);

        if (ConfigHandler.getCfgHandler().isUsePlayerMoveEvent()) {
            getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        }
	}

	@Override
	public void onDisable() {
		SBManager.getManager().clearAll();
		
        for(Player player : Bukkit.getOnlinePlayers()){
            String title = player.getOpenInventory().getTitle();
            for(Loot loot : KothHandler.getLoots()){
                if(loot.getInventory().getTitle().equalsIgnoreCase(title)){
                    player.closeInventory();
                }
            }
            
            for(Koth koth : KothHandler.getAvailableKoths()){
                if(Loot.getKothLootTitle(koth.getName()).equalsIgnoreCase(title)){
                    player.closeInventory();
                }
            }
        }
	}
}
