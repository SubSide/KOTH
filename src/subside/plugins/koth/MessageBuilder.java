package subside.plugins.koth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import subside.plugins.koth.area.Area;
import subside.plugins.koth.area.KothHandler;

public class MessageBuilder {
	String message;
	public MessageBuilder(String msg){
		this.message = "";
		if(msg != null)
			this.message = msg;
	}
	
	public MessageBuilder area(String area){
		message = message.replaceAll("%area%", area.replaceAll("\\\\\\\\", "%5C").replaceAll("([^\\\\])_", "$1 ").replaceAll("\\\\_", "_").replaceAll("%5C", "\\\\\\\\"));
		Area ar = KothHandler.getArea(area);
		if (ar != null) {
			Location loc = ar.getMiddle();
			message = message
					.replaceAll("%x%", ""+loc.getBlockX())
					.replaceAll("%y%", ""+loc.getBlockY())
					.replaceAll("%z%", ""+loc.getBlockZ());
			message = message.replaceAll("%world%", loc.getWorld().getName());
		}
		return this;
	}
	
	public MessageBuilder player(String player){
		message = message.replaceAll("%player%", player);
		return this;
	}
	
	public MessageBuilder day(String day){
		message = message.replaceAll("%day%", day);
		return this;
	}
	
	public MessageBuilder time(String time){
		message = message.replaceAll("%time%", time);
		return this;
	}
	
	public MessageBuilder length(int length){
		message = message.replaceAll("%length%", ""+length);
		return this;
	}
	
	public MessageBuilder id(int id){
		message = message.replaceAll("%id%", ""+id);
		return this;
	}
	
	public MessageBuilder date(String date){
		message = message.replaceAll("%date%", date);
		return this;
	}
	
	public MessageBuilder time(int captureTime, int timeCapped){
		int secondsCapped = timeCapped % 60;
		int minutesCapped = timeCapped / 60;
		int secondsLeft = (captureTime - timeCapped) % 60;
		int minutesLeft = (captureTime - timeCapped) / 60;

		message = message.replaceAll("%minutes%", String.format("%02d", minutesCapped));
		message = message.replaceAll("%seconds%", String.format("%02d", secondsCapped));
		message = message.replaceAll("%minutes_left%", String.format("%02d", minutesLeft));
		message = message.replaceAll("%seconds_left%", String.format("%02d", secondsLeft));
		
		return this;
	}
	
	public MessageBuilder command(String command){
		message = message.replaceAll("%command%", command);
		return this;
	}
	
	public MessageBuilder commandInfo(String commandInfo){
		message = message.replaceAll("%command_info%", commandInfo);
		return this;
	}
	
	public void buildAndBroadcast(){
		String msg = build();
		if(!msg.trim().equalsIgnoreCase("")){
			Bukkit.broadcastMessage(msg);
		}
	}
	
	public void buildAndSend(CommandSender player){
		String msg = build();
		if(!msg.trim().equalsIgnoreCase("")){
			player.sendMessage(msg);
		}
	}
	
	public String build(){
		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
