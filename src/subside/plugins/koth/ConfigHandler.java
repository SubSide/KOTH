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
	private @Getter Hooks hooks;
	
	public ConfigHandler(FileConfiguration cfg){
		cfgHandler = this;
        
		global = new Global(cfg.getConfigurationSection("global"));
		loot = new Loot(cfg.getConfigurationSection("loot"));
		koth = new Koth(cfg.getConfigurationSection("koth"));
		scoreboard = new Scoreboard(cfg.getConfigurationSection("scoreboard"));
		hooks = new Hooks(cfg.getConfigurationSection("hooks"));
	}
	
	public class Global {
	    private @Getter String timeZone = "Europe/Amsterdam";
	    private @Getter int minuteOffset = 0;
	    private @Getter boolean usePlayerMoveEvent = false;
	    private @Getter int preBroadcast = 0;
	    private @Getter List<String> helpCommand = null;
	    private @Getter boolean useFancyPlayerName = false;
	    
	    public Global(ConfigurationSection section){
	        timeZone = section.getString("schedule-timezone");
            minuteOffset = section.getInt("minuteoffset");
	        usePlayerMoveEvent = section.getBoolean("use-playermoveevent");
	        preBroadcast = section.getInt("pre-broadcast");
	        helpCommand = section.getStringList("helpcommand");
	        useFancyPlayerName = section.getBoolean("fancyplayername");
	    }
	}
	
	public class Hooks {
	    private @Getter boolean vanishNoPacket = true;
	    private @Getter boolean factions = true;
	    private @Getter boolean kingdoms = true;
	    private @Getter Featherboard featherboard;
	    
	    public Hooks(ConfigurationSection section){
            vanishNoPacket = section.getBoolean("vanishnopacket");
            factions = section.getBoolean("factions");
            kingdoms = section.getBoolean("kingdoms");
            featherboard = new Featherboard(section.getConfigurationSection("featherboard"));
	    }
	    
	    public class Featherboard {
	        private @Getter boolean enabled = false;
	        private @Getter int range = 100;
	        private @Getter int rangeMargin = 5;
	        private @Getter String board = "KoTH";
	        
	        public Featherboard(ConfigurationSection section){
	            enabled = section.getBoolean("enabled");
	            range = section.getInt("range");
	            rangeMargin = section.getInt("rangemargin");
	            board = section.getString("board");
	        }
	        
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
}
