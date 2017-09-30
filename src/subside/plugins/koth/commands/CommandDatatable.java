package subside.plugins.koth.commands;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandDatatable extends AbstractCommand {

    public CommandDatatable(CommandCategory category) {
        super(category);
    }

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
        if(args.length < 2)
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth datatable debug (0|1|2) <maxrows> [time] [capturetype] [gamemode] [koth]");
        
        if(args[0].equalsIgnoreCase("2")){
            Utils.sendMessage(sender, true, "Player results returned:");
            
            int wins = getPlugin().getDataTable().getPlayerStats(Bukkit.getPlayer(args[1]), 0);
            Utils.sendMessage(sender, true, args[1]+ " has " + wins + " wins.");
            return;
        }
        
        int rows = Integer.parseInt(args[1]);
        int time = 0;
        String captureType = null;
        String gameMode = null;
        String koth = null;
        
        if(args.length > 2){
            time = Integer.parseInt(args[2]);
        }
        
        if(args.length > 3){
            captureType = (args[3].equalsIgnoreCase("0")) ? args[3] : null;
        }
        
        if(args.length > 4){
            gameMode = (args[4].equalsIgnoreCase("0")) ? args[4] : null;
        }
        
        if(args.length > 5){
            koth = (args[5].equalsIgnoreCase("0")) ? args[5] : null;
        }
        
        if(args[0].equalsIgnoreCase("0")){
            Utils.sendMessage(sender, true, "Global results returned:");
    
            List<Entry<Capper<?>, Integer>> list = getPlugin().getDataTable().getTop(rows, time, captureType, gameMode, koth);
            for(Entry<Capper<?>, Integer> entry : list){
                Utils.sendMessage(sender, true, entry.getKey().getName() + " : " + entry.getValue());
            }
        } else {
            Utils.sendMessage(sender, true, "Player results returned:");
            
            List<Entry<OfflinePlayer, Integer>> list = getPlugin().getDataTable().getPlayerTop(rows, time, captureType, gameMode, koth);
            for(Entry<OfflinePlayer, Integer> entry : list){
                Utils.sendMessage(sender, true, entry.getKey().getName() + " : " + entry.getValue());
            }
        }
    }
    
    public void query(CommandSender sender, String[] args){
        
    }
    
    public void clear(CommandSender sender, String[] args){
        
    }
    
    public void close(CommandSender sender, String[] args){
        try {
            getPlugin().getDataTable().getDatabaseProvider().getConnection().close();
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

    @Override
    public String getUsage() {
        return "/koth datatable";
    }

    @Override
    public String getDescription() {
        return "Manage/manipulate the datatable";
    }

}
