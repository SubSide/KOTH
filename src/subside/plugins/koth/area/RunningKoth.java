package subside.plugins.koth.area;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Koth;
import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;

public class RunningKoth {
	private Area area;
	private int captureTime;

	private String cappingPlayer;
	private int timeCapped;
	private int timeKnocked;
	private boolean knocked;

	public RunningKoth(Area area, int time) {
		this.area = area;
		this.captureTime = time;
		this.timeCapped = 0;
		this.cappingPlayer = null;
		area.removeLootChest();
		area.setLastWinner(null);	
		int seconds = captureTime % 60;
		int minutes = captureTime / 60;
		new MessageBuilder(Lang.KOTH_STARTING).minutes(minutes).seconds(seconds).area(area.getName()).world(area.getMin().getWorld().getName()).buildAndBroadcast();

	}

	@SuppressWarnings("deprecation")
	public void checkPlayerCapping() {
		if(cappingPlayer != null){
			if (!area.isInArea(Bukkit.getOfflinePlayer(cappingPlayer))) {
				int secondsCapped = timeCapped % 60;
				int minutesCapped = timeCapped / 60;
				int secondsLeft = (captureTime - timeCapped) % 60;
				int minutesLeft = (captureTime - timeCapped) / 60;
				new MessageBuilder(Lang.KOTH_LEFT).minutes(minutesCapped).seconds(secondsCapped).minutesLeft(minutesLeft).secondsLeft(secondsLeft).player(cappingPlayer).area(area.getName()).world(area.getMin().getWorld().getName()).buildAndBroadcast();
	
				cappingPlayer = null;
				timeCapped = 0;
				if(ConfigHandler.getCfgHandler().getKnockTime() > 0){
					timeKnocked = 0;
					knocked = true;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void update() {
		if (!ConfigHandler.getCfgHandler().getUsePlayerMoveEvent()){
			checkPlayerCapping();
		} else {
			if(!Bukkit.getOfflinePlayer(cappingPlayer).isOnline()){
				cappingPlayer = null;
			}
		}
		if(knocked && timeKnocked < ConfigHandler.getCfgHandler().getKnockTime()){
			timeKnocked++;
			return;
		} else if(knocked){
			knocked = false;
		}
		
		if (cappingPlayer != null) {
			Player player = Bukkit.getOfflinePlayer(cappingPlayer).getPlayer();
			if (++timeCapped < captureTime) {
				if (timeCapped % 30 == 0) {
					int secondsCapped = timeCapped % 60;
					int minutesCapped = timeCapped / 60;
					int secondsLeft = (captureTime - timeCapped) % 60;
					int minutesLeft = (captureTime - timeCapped) / 60;
					new MessageBuilder(Lang.KOTH_CAPTIME).minutes(minutesCapped).seconds(secondsCapped).minutesLeft(minutesLeft).secondsLeft(secondsLeft).player(cappingPlayer).area(area.getName()).world(area.getMin().getWorld().getName()).buildAndBroadcast();
				}
			} else {
				new MessageBuilder(Lang.KOTH_WON).player(cappingPlayer).world(player.getWorld().getName()).area(area.getName()).buildAndBroadcast();
				area.setLastWinner(player.getName());
				Bukkit.getScheduler().runTask(Koth.getPlugin(), new Runnable(){
					public void run(){
						area.createLootChest();
					}
				});
				
				Bukkit.getScheduler().runTask(Koth.getPlugin(), new Runnable(){
					public void run(){
						KothHandler.stopKoth(getArea().getName());
					}
				});
			}
		} else {
			ArrayList<Player> insideArea = new ArrayList<Player>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (area.isInArea(player)) {
					insideArea.add(player);
				}
			}
			if (insideArea.size() > 0) {
				cappingPlayer = insideArea.get(new Random().nextInt(insideArea.size())).getName();
				int seconds = captureTime % 60;
				int minutes = captureTime / 60;
				new MessageBuilder(Lang.KOTH_PLAYERCAP).player(cappingPlayer).area(area.getName()).world(area.getMin().getWorld().getName()).minutes(minutes).seconds(seconds).buildAndBroadcast();
			}

		}
	}
	
	public void quickEnd(){
		timeCapped = 9999999;
	}
	
	public String getCappingPlayer(){
		return cappingPlayer;
	}

	public Area getArea() {
		return area;
	}
	
	public int getCaptureTime(){
		return captureTime;
	}
	
	public int getTimeCapped(){
		return timeCapped;
	}
}
