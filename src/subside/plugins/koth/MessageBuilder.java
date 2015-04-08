package subside.plugins.koth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageBuilder {
	String message;
	public MessageBuilder(String msg){
		this.message = "";
		if(msg != null)
			this.message = msg;
	}
	
	public MessageBuilder area(String area){
		message = message.replaceAll("%area%", area);
		return this;
	}
	
	public MessageBuilder player(String player){
		message = message.replaceAll("%player%", player);
		return this;
	}
	
	public MessageBuilder world(String world){
		message = message.replaceAll("%world%", world);
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
	
	public MessageBuilder minutes(int minutes){
		message = message.replaceAll("%minutes%", String.format("%02d", minutes));
		return this;
	}
	
	public MessageBuilder seconds(int seconds){
		message = message.replaceAll("%seconds%", String.format("%02d", seconds));
		return this;
	}
	
	public MessageBuilder minutesLeft(int left){
		message = message.replaceAll("%minutes_left%", String.format("%02d", left));
		return this;
	}
	
	public MessageBuilder secondsLeft(int left){
		message = message.replaceAll("%seconds_left%", String.format("%02d", left));
		return this;
	}

	
	public MessageBuilder x(int x){
		message = message.replaceAll("%x%", ""+x);
		return this;
	}

	
	public MessageBuilder y(int y){
		message = message.replaceAll("%y%", ""+y);
		return this;
	}
	
	public MessageBuilder z(int z){
		message = message.replaceAll("%z%", ""+z);
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
