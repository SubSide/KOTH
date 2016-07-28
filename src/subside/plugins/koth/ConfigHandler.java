package subside.plugins.koth;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;

public class ConfigHandler {
    private @Getter static ConfigHandler cfgHandler;
    
	private @Getter Global global;
	private @Getter Loot loot;
	private @Getter Koth koth;
	private @Getter Scoreboard scoreboard;
	private @Getter Factions factions;
	private @Getter Hooks hooks;
	
	public ConfigHandler(FileConfiguration cfg){
		cfgHandler = this;
        
		global = new Global(cfg.getConfigurationSection("global"));
		loot = new Loot(cfg.getConfigurationSection("loot"));
		koth = new Koth(cfg.getConfigurationSection("koth"));
		scoreboard = new Scoreboard(cfg.getConfigurationSection("scoreboard"));
		factions = new Factions(cfg.getConfigurationSection("factions"));
		hooks = new Hooks(cfg.getConfigurationSection("hooks"));
	}
	
	public class Global {
	    private @Getter String timeZone = "Europe/Amsterdam";
	    private @Getter boolean usePlayerMoveEvent = false;
	    private @Getter int preBroadcast = 0;
	    private @Getter List<String> helpCommand = null;
	    private @Getter boolean useFancyPlayerName = false;
	    
	    public Global(ConfigurationSection section){
	        timeZone = section.getString("schedule-timezone");
	        usePlayerMoveEvent = section.getBoolean("use-playermoveevent");
	        preBroadcast = section.getInt("pre-broadcast");
	        helpCommand = section.getStringList("helpcommand");
	        useFancyPlayerName = section.getBoolean("fancyplayername");
	    }
	}
	
	public class Hooks {
	    private @Getter boolean vanishNoPacket = false;
	    
	    public Hooks(ConfigurationSection section){
	        vanishNoPacket = section.getBoolean("vanishnopacket");
	    }
	}
	
	public class Loot {

	    private @Getter String defaultLoot = "";
	    private @Getter boolean randomizeLoot = true;
	    private @Getter int lootAmount = 5;
	    private @Getter boolean randomizeStackSize = false;
	    private @Getter boolean useItemsMultipleTimes = true;
	    private @Getter long removeLootAfterSeconds = 0;
	    private @Getter boolean dropLootOnRemoval = false;
	    private @Getter boolean instantLoot = false;
	    
	    public Loot(ConfigurationSection section){
	        defaultLoot = section.getString("default");
	        randomizeLoot = section.getBoolean("randomize");
	        lootAmount = section.getInt("default-amount");
	        randomizeStackSize = section.getBoolean("randomize-stacksize");
	        useItemsMultipleTimes = section.getBoolean("can-use-same-items");
	        removeLootAfterSeconds = section.getInt("remove-after");
	        dropLootOnRemoval = section.getBoolean("drop-on-removal");
	        instantLoot = section.getBoolean("give-instantly");
	    }
	}
	
	public class Koth {
	    private @Getter int knockTime = 0;
	    private @Getter int minimumPlayersNeeded = 0;
	    
	    public Koth(ConfigurationSection section){
	        knockTime = section.getInt("knockTime");
	        minimumPlayersNeeded = section.getInt("minimum-players");
	    }
	}
	
	public class Scoreboard {
	    private @Getter boolean useScoreboard = true;
	    private @Getter boolean useOldScoreboard = false;
	    private ConfigurationSection section;
	    
	    public Scoreboard(ConfigurationSection section){
	        useScoreboard = section.getBoolean("use-scoreboard");
	        useOldScoreboard = section.getBoolean("use-old-scoreboard");
	        this.section = section;
	    }
	    
	    public ConfigurationSection getSection(){
	        return section;
	    }
	}
	
	public class Factions {
	    private @Getter boolean useFactions = false;
	    private @Getter boolean shouldCapAllAreas = false;
	    
	    public Factions(ConfigurationSection section){
	        useFactions = section.getBoolean("enable");
	        shouldCapAllAreas = section.getBoolean("shouldCapAllAreas");
	    }
	}
}
