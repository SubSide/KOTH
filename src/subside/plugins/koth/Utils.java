package subside.plugins.koth;

import org.bukkit.command.CommandSender;

public class Utils {
	public static void msg(CommandSender sender, String msg){
		new MessageBuilder(Lang.PREFIX+msg).buildAndSend(sender);
	}
	
	public static String getGson(String str){
		try {
			return new com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new com.google.gson.JsonParser().parse(str));
		} catch(NoClassDefFoundError e){
			return new org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new org.bukkit.craftbukkit.libs.com.google.gson.JsonParser().parse(str));
		}
		
	}
}
