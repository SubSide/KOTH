package subside.plugins.koth;

import java.util.List;

import lombok.Getter;

import org.bukkit.configuration.file.FileConfiguration;

import subside.plugins.koth.scoreboard.SBManager;

public class ConfigHandler {
	private @Getter boolean usePlayerMoveEvent = false;
	private @Getter boolean useScoreboard = true;
	private @Getter int lootAmount = 5;
	private @Getter String timeZone = "Europe/Amsterdam";
	private @Getter boolean randomizeLoot = true;
	private @Getter boolean randomizeAmountLoot = false;
	private @Getter boolean useItemsMultipleTimes = true;
	private @Getter boolean instantLoot = false;
	private @Getter long removeLootAfterSeconds = 0;
	private @Getter boolean dropLootOnRemoval = false;
	private @Getter int minimumPlayersNeeded = 0;
	private @Getter List<String> helpCommand = null;
    private @Getter boolean useOldScoreboard = false;
	private @Getter int knockTime = 0;
	private @Getter int preBroadcast = 0;
	private @Getter static ConfigHandler cfgHandler;
	
	
	public ConfigHandler(FileConfiguration cfg){
		cfgHandler = this;
		usePlayerMoveEvent = cfg.getBoolean("use-playermoveevent");
		useScoreboard = cfg.getBoolean("use-scoreboard");
		timeZone = cfg.getString("schedule-timezone");
		lootAmount = cfg.getInt("amount-of-loot");
		knockTime = cfg.getInt("knockTime");
		randomizeLoot = cfg.getBoolean("randomize-loot");
		instantLoot = cfg.getBoolean("instant-loot");
		randomizeAmountLoot = cfg.getBoolean("randomize-amount-of-loot");
		useItemsMultipleTimes = cfg.getBoolean("can-use-same-items");
		removeLootAfterSeconds = cfg.getInt("remove-lootchest-after");
		minimumPlayersNeeded = cfg.getInt("minimum-players-needed");
		dropLootOnRemoval = cfg.getBoolean("drop-loot-on-removal");
		helpCommand = cfg.getStringList("helpcommand");
		preBroadcast = cfg.getInt("pre-broadcast");
		useOldScoreboard = cfg.getBoolean("use-old-scoreboard");
	
		if(useScoreboard){
			SBManager.getManager().load(useOldScoreboard, cfg.getString("scoreboard.title"), cfg.getStringList("scoreboard.contents").toArray(new String[cfg.getStringList("scoreboard.contents").size()]));
		}
	}
}
