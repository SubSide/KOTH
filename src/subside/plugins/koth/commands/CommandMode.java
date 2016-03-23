package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandMode implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
    	if(args.length > 0){
    		if(KothHandler.getInstance().getGamemodeRegistry().getGamemodes().containsKey(args[0])){
    			KothHandler.getInstance().getGamemodeRegistry().setCurrentMode(args[0]);
        		new MessageBuilder(Lang.COMMAND_MODE_CHANGED).gamemode(args[0]).buildAndSend(sender);
    		} else {
        		new MessageBuilder(Lang.COMMAND_MODE_NOT_EXIST).gamemode(args[0]).buildAndSend(sender);
    		}
    	} else {
    		new MessageBuilder(Lang.COMMAND_MODE_LIST_TITLE).buildAndSend(sender);

    		for(String gamemode : KothHandler.getInstance().getGamemodeRegistry().getGamemodes().keySet()){
	    		new MessageBuilder(Lang.COMMAND_MODE_LIST_ENTRY).gamemode(gamemode).buildAndSend(sender);
    		}
    	}
        
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.MODE;
    }

    @Override
    public String[] getCommands() {
        return new String[]{
        		"mode"
        };
    }

}
