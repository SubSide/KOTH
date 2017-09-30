package subside.plugins.koth.modules;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import subside.plugins.koth.KothPlugin;

import java.util.*;

public class ConfigHandler extends AbstractModule {
    
	private @Getter Global global;
	private @Getter Loot loot;
	private @Getter Koth koth;
	private @Getter Hooks hooks;
	private @Getter Database database;
	
	public ConfigHandler(KothPlugin plugin){
	    super(plugin);
	    onLoad();
	}
	
	@Override
	public void onLoad(){
        // reload the configs
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        
        // Now load all sub-classes
	    FileConfiguration cfg = plugin.getConfig();
        global = new Global(cfg.getConfigurationSection("global"));
        loot = new Loot(cfg.getConfigurationSection("loot"));
        koth = new Koth(cfg.getConfigurationSection("koth"));
        hooks = new Hooks(cfg.getConfigurationSection("hooks"));
        database = new Database(cfg.getConfigurationSection("database"));
	}
	
	public class Global {
	    private @Getter boolean useCache = false;
	    private @Getter boolean currentDayOnly = false;
	    private @Getter String timeZone = "Europe/Amsterdam";
        private @Getter int minuteOffset = 0;
        private @Getter int startWeekMinuteOffset = 0;
        private @Getter int scheduleMinuteOffset = 0;
        private @Getter String dateFormat = "dd/MM/yyyy";
	    private @Getter int noCapBroadcastInterval = 30;
	    private @Getter List<String> helpCommand = null;
        private @Getter boolean useFancyPlayerName = false;
        private @Getter boolean multipleKothsAtOnce = true;
        private @Getter boolean worldFilter = false;
	    private @Getter boolean debug = false;

        private @Getter boolean preBroadcast = false;
        private @Getter Map<Integer, String> preBroadcastMessages;
        private @Getter List<Integer> preBroadcastTimes;
	    
	    public Global(ConfigurationSection section){
	        useCache = section.getBoolean("use-cache");
	        currentDayOnly = section.getBoolean("schedule-show-current-day-only");
	        timeZone = section.getString("schedule-timezone");
            minuteOffset = section.getInt("minuteoffset");
            startWeekMinuteOffset = section.getInt("startweekminuteoffset");
            scheduleMinuteOffset = section.getInt("scheduleminuteoffset");
            dateFormat = section.getString("date-format");
	        noCapBroadcastInterval = section.getInt("nocap-broadcast-interval");
	        helpCommand = section.getStringList("helpcommand");
            useFancyPlayerName = section.getBoolean("fancyplayername");
            multipleKothsAtOnce = section.getBoolean("multiplekothsatonce");
            worldFilter = section.getBoolean("world-filter");
	        debug = section.getBoolean("debug");

            preBroadcast = section.getBoolean("pre-broadcast");


            // Broadcast stuff
            preBroadcastMessages = new HashMap<>();
            for(String broadcast : section.getStringList("pre-broadcast-messages")){
                String[] expl = broadcast.split(":", 2);
                try {
                    preBroadcastMessages.put(Integer.parseInt(expl[0]), expl[1]);
                } catch(NumberFormatException e){
                    plugin.getLogger().warning("pre-broadcast-messages: "+ expl[0] + " could not be converted to a number!");
                }
            }

            preBroadcastTimes = new ArrayList<>(preBroadcastMessages.keySet());
            // Sorting here is VERY important
            Collections.sort(preBroadcastTimes);
	    }
	}
	
	public class Hooks {
	    private @Getter boolean essentialsVanish = true;
	    private @Getter boolean vanishNoPacket = true;
	    private @Getter boolean factions = true;
	    private @Getter boolean kingdoms = true;
        private @Getter boolean feudalKingdoms = true;
	    private @Getter boolean gangs = true;
        private @Getter boolean mcMMO = true;
        private @Getter boolean pvpManager = true;
        
	    private @Getter Featherboard featherboard;
        private @Getter BossBar bossBar;
	    
	    public Hooks(ConfigurationSection section){
	        essentialsVanish = section.getBoolean("essentialsvanish");
            vanishNoPacket = section.getBoolean("vanishnopacket");
            factions = section.getBoolean("factions");
            kingdoms = section.getBoolean("kingdoms");
            feudalKingdoms = section.getBoolean("feudalkingdoms");
            pvpManager = section.getBoolean("pvpmanager");
            gangs = section.getBoolean("gangs");
            mcMMO = section.getBoolean("mcmmo");
            featherboard = new Featherboard(section.getConfigurationSection("featherboard"));
            bossBar = new BossBar(section.getConfigurationSection("bossbar"));
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
        
        public class BossBar {
            private @Getter boolean enabled = false;
            private @Getter int range = 100;
            private @Getter int rangeMargin = 5;
            private @Getter String text = "&a%koth% currently captured by: %capper%";
            private @Getter String barColor = "BLUE";
            private @Getter int barsegments = 10;
            private @Getter boolean countingDown = false;
            private @Getter Flags flags;
            
            public BossBar(ConfigurationSection section){
                enabled = section.getBoolean("enabled");
                range = section.getInt("range");
                rangeMargin = section.getInt("rangemargin");
                text = section.getString("text");
                barColor = section.getString("barcolor");
                barsegments = section.getInt("barsegments");
                countingDown = section.getBoolean("countingDown");
                flags = new Flags(section.getConfigurationSection("flags"));
            }
            
            public class Flags {
                private @Getter boolean createfog = false;
                private @Getter boolean darkensky = false;
                private @Getter boolean playmusic = false;
                
                public Flags(ConfigurationSection section){
                    createfog = section.getBoolean("createfog");
                    darkensky = section.getBoolean("darkensky");
                    playmusic = section.getBoolean("playmusic");
                }
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
	    private @Getter boolean rewardEveryone = false;

        private @Getter boolean cmdEnabled = false;
        private @Getter boolean cmdIngame = false;
        private @Getter boolean cmdNeedOp = true;
	    
	    
	    public Loot(ConfigurationSection section){
	        defaultLoot = section.getString("default");
	        randomizeLoot = section.getBoolean("randomize");
	        lootAmount = section.getInt("default-amount");
	        randomizeStackSize = section.getBoolean("randomize-stacksize");
	        useItemsMultipleTimes = section.getBoolean("can-use-same-items");
	        removeLootAfterSeconds = section.getInt("remove-after");
	        dropLootOnRemoval = section.getBoolean("drop-on-removal");
	        instantLoot = section.getBoolean("give-instantly");
	        rewardEveryone = section.getBoolean("reward-everyone");

            cmdEnabled = section.getBoolean("commands.enabled");
            cmdNeedOp = section.getBoolean("commands.needop");
            cmdIngame = section.getBoolean("commands.changeingame");
	    }
	}
    
    public class Koth {
        private @Getter int captureCooldown = 0;
        private @Getter int channelTime = 0;
        private @Getter int knockTime = 0;
        private @Getter boolean contestFreeze = false;
        private @Getter boolean removeChestAtStart = true;
        private @Getter boolean ffaChestTimeLimit = false;
        private @Getter int broadcastInterval = 30;
        private @Getter int minimumPlayersNeeded = 0;
        private @Getter boolean startNewOnEnd = false;
        private @Getter String defaultCaptureType = "Player";
        private @Getter List<String> mapRotation = new ArrayList<>();

        private @Getter CapDecrementation capDecrementation;
        
        public Koth(ConfigurationSection section){
            removeChestAtStart = section.getBoolean("remove-chest-at-start");
            ffaChestTimeLimit = section.getBoolean("ffa-on-time-limit");
            contestFreeze = section.getBoolean("contest-freeze");
            channelTime = section.getInt("channel-time");
            knockTime = section.getInt("knock-time");
            broadcastInterval = section.getInt("broadcast-interval");
            captureCooldown = section.getInt("capture-cooldown");
            minimumPlayersNeeded = section.getInt("minimum-players");
            startNewOnEnd = section.getBoolean("start-new-on-end");
            defaultCaptureType = section.getString("default-capturetype");
            mapRotation = section.getStringList("map-rotation");

            capDecrementation = new CapDecrementation(section.getConfigurationSection("captime-decrementation"));
        }

        public class CapDecrementation {
            private @Getter boolean enabled = false;
            private @Getter int everyXSeconds = 600;
            private @Getter int decreaseBy = 60;
            private @Getter int minimum = 300;

            public CapDecrementation(ConfigurationSection section){
                enabled = section.getBoolean("enabled");
                everyXSeconds = section.getInt("every-x-seconds");
                decreaseBy = section.getInt("decrease-by");
                minimum = section.getInt("minimum");
            }
        }
    }
    
    public class Database {
        private @Getter boolean enabled = false;
        private @Getter String storagetype = "sqlite";
        private @Getter String database = "KoTH";
        private @Getter String host = "localhost";
        private @Getter int port = 3306;
        private @Getter String username = "root";
        private @Getter String password = "";
        private @Getter Modules modules;
        
        public Database(ConfigurationSection section){
            enabled = section.getBoolean("enabled");
            storagetype = section.getString("storagetype");
            database = section.getString("database");
            host = section.getString("host");
            port = section.getInt("port");
            username = section.getString("username");
            password = section.getString("password");
            modules = new Modules(section.getConfigurationSection("modules"));
        }

        public class Modules {
            private @Getter boolean saveKothWins = true;
            private @Getter boolean savePlayerIgnores = true;

            public Modules(ConfigurationSection section){
                saveKothWins = section.getBoolean("saveKothWins");
                savePlayerIgnores = section.getBoolean("savePlayerIgnores");
            }
        }
    }
}
