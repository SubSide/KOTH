package subside.plugins.koth;

import org.bukkit.command.CommandSender;

public enum Perm {
	SCHEDULE("schedule"), LIST("list"), ADMIN("admin"), LOOT("loot"), INFO("info");
	
	private String perm;
	
	Perm(String perm){
		this.perm = "koth."+perm;
	}
	
	public boolean has(CommandSender sender){
		return sender.hasPermission(perm);
	}
}
