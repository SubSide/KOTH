package subside.plugins.koth.utils;

import org.bukkit.command.CommandSender;

public enum Perm implements IPerm {
	LIST("list"), LOOT("loot"), NEXT("next"), SCHEDULE("schedule"), VERSION("version"), HELP("help"), IGNORE("ignore"), TOP("top");
	
	private String perm;
	
	Perm(String perm){
		this.perm = "koth."+perm;
	}
	
	public boolean has(CommandSender sender){
		return sender.hasPermission(perm);
	}
	
	
	public enum ALLOW implements IPerm {
	    ALLOW;
        public boolean has(CommandSender sender){
            return true;
        }
	}
	
	public enum Admin implements IPerm {
	    TP("tp"), ADMIN("admin"), CHANGE("change"), CREATE("create"), INFO("info"), MODE("mode"), EDIT("edit"), REMOVE("remove"), BYPASS("bypass"), HELP("help"), LOOT("loot"), RELOAD("reload"), SCHEDULE("schedule");
	    
	    private String perm;
	    Admin(String perm){
	        this.perm = "koth.admin."+perm;
	    }
	    
	    public boolean has(CommandSender sender){
	        return sender.hasPermission(perm);
	    }
	}
	

    public enum Control implements IPerm {
        END("end"), START("start"), STOP("stop");
        
        private String perm;
        Control(String perm){
            this.perm = "koth.control."+perm;
        }
        
        public boolean has(CommandSender sender){
            return sender.hasPermission(perm);
        }
    }
}
