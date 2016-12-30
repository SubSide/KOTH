package subside.plugins.koth.commands;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.captypes.Capper;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandDatatable implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        
        if(args.length < 1){
            Utils.sendMessage(sender, true, "Invalid syntax");
            return;
        }
        
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        if (args[0].equalsIgnoreCase("debug")) {
            debug(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("query")){
            query(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("clear")){
            clear(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("close")){
            close(sender, newArgs);
        }
    }
    
    public void debug(CommandSender sender, String[] args){
        if(args.length < 1)
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth debug <maxrows> [time] [capturetype] [gamemode] [koth]");
        
        int rows = Integer.parseInt(args[0]);
        int time = 0;
        String captureType = null;
        String gameMode = null;
        String koth = null;
        
        if(args.length > 1){
            time = Integer.parseInt(args[1]);
        }
        
        if(args.length > 2){
            captureType = (args[2] != "0") ? args[2] : null;
        }
        
        if(args.length > 3){
            gameMode = (args[3] != "0") ? args[3] : null;
        }
        
        if(args.length > 4){
            koth = (args[4] != "0") ? args[4] : null;
        }
        
        List<Entry<Capper, Integer>> list = KothPlugin.getPlugin().getDataTable().getTop(rows, time, captureType, gameMode, koth);
        
        Utils.sendMessage(sender, true, "Info returned:");
        
        for(Entry<Capper, Integer> entry : list){
            Utils.sendMessage(sender, true, entry.getKey().getName() + " : " + entry.getValue());
        }
    }
    
    public void query(CommandSender sender, String[] args){
        
    }
    
    public void clear(CommandSender sender, String[] args){
        
    }
    
    public void close(CommandSender sender, String[] args){
        try {
            KothPlugin.getPlugin().getDataTable().getDatabaseProvider().getConnection().close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        Utils.sendMessage(sender, true, "Database connection has been temporarily closed");
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.ADMIN;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"datatable"};
    }

}
