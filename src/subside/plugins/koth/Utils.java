package subside.plugins.koth;

import org.bukkit.command.CommandSender;

public class Utils {
	public static void msg(CommandSender sender, String msg){
		new MessageBuilder(Lang.PREFIX+msg).buildAndSend(sender);
	}
	
	public static boolean hasPerms(CommandSender sender){
		return sender.hasPermission("koth.admin");
	}
}
