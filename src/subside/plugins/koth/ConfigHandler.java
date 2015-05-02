package subside.plugins.koth;

import org.bukkit.configuration.file.FileConfiguration;

import subside.plugins.koth.scoreboard.ScoreboardHandler;

public class ConfigHandler {
	private boolean usePlayerMoveEvent = false;
	private boolean useScoreboard = true;
	private int lootAmount = 5;
	private String timeZone = "Europe/Amsterdam";
	private boolean singleLootChest = false;
	private boolean randomizeLoot = true;
	private int knockTime = 0;
	private static ConfigHandler cfgHandler;
	
	
	public ConfigHandler(FileConfiguration cfg){
		cfgHandler = this;
		usePlayerMoveEvent = cfg.getBoolean("use-playermoveevent");
		useScoreboard = cfg.getBoolean("use-scoreboard");
		timeZone = cfg.getString("schedule-timezone");
		lootAmount = cfg.getInt("amount-of-loot");
		knockTime = cfg.getInt("knockTime");
		singleLootChest = cfg.getBoolean("one-for-all");
		randomizeLoot = cfg.getBoolean("randomize-loot");
		
		if(useScoreboard){
			ScoreboardHandler.load(cfg.getString("scoreboard.title"), cfg.getStringList("scoreboard.contents").toArray(new String[cfg.getStringList("scoreboard.contents").size()]));
		}
	}
	
	public static ConfigHandler getCfgHandler(){
		return cfgHandler;
	}
	
	public boolean getSingleLootChest(){
		return singleLootChest;
	}
	
	public int getLootAmount(){
		return lootAmount;
	}
	
	public String getTimeZone(){
		return timeZone;
	}
	
	public int getKnockTime(){
		return knockTime;
	}
	
	public boolean getRandomizeLoot(){
		return randomizeLoot;
	}
	
	public boolean getUsePlayerMoveEvent(){
		return usePlayerMoveEvent;
	}
	
	public boolean getUseScoreboard(){
		return useScoreboard;
	}
}
