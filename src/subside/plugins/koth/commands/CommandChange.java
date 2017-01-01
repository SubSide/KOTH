package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.gamemodes.KothConquest;
import subside.plugins.koth.gamemodes.KothConquest.FactionScore;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandChange extends AbstractCommand {

    public CommandChange(CommandCategory category) {
        super(category);
    }


    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 1) {
            help(sender);
            return;
        }
        
        RunningKoth rKoth = getPlugin().getKothHandler().getRunningKoth();
        if (rKoth == null) {
            throw new CommandMessageException(new MessageBuilder(Lang.KOTH_ERROR_NONE_RUNNING));
        }
        
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        if (args[0].equalsIgnoreCase("points")) {
            points(sender, newArgs, rKoth);
        } else if (args[0].equalsIgnoreCase("time")) {
            time(sender, newArgs, rKoth);
        } else {
            help(sender);
        }
        // TODO add time and such
    }

    
    public void help(CommandSender sender){
        List<String> list = new ArrayList<>();
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Running game manager").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change time").commandInfo("Command to change the time").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points").commandInfo("Manage the points").buildArray());
        sender.sendMessage(list.toArray(new String[list.size()]));
    }
    
    
    public void time(CommandSender sender, String[] args, RunningKoth rKoth) {
        if(args.length < 1){
            // TODO
            return;
        }
        // TODO
    }

    public void points(CommandSender sender, String[] args, RunningKoth rKoth) {
        if (args.length < 1) {
            List<String> list = new ArrayList<>();
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Change points").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points set <faction> <points>").commandInfo("Set the points of a faction").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points add <faction> <points>").commandInfo("Add points to a faction").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points del <faction> <points>").commandInfo("remove points from a faction").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points reset <faction>").commandInfo("reset points of a faction").buildArray());
            sender.sendMessage(list.toArray(new String[list.size()]));    
            return;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth change points set <faction> <points>");
            }
            if (rKoth instanceof KothConquest) {
                KothConquest kothCQ = (KothConquest) rKoth;
                for (FactionScore fScore : kothCQ.getFScores()) {
                    if (fScore.getFaction().getName().equalsIgnoreCase(args[1])) {
                        try {
                            fScore.setPoints(Integer.parseInt(args[2]));
                            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_SET).entry(fScore.getFaction().getName()));
                        } catch(Exception e){
                            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_NOTANUMBER));
                        }
                    }
                }

                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_FACTION_NOT_FOUND));
            } else {
                throw new CommandMessageException(Lang.KOTH_ERROR_NOT_COMPATIBLE);
            }
        } else if(args[0].equalsIgnoreCase("add")){
            if (args.length < 3) {
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth change points add <faction> <points>");
            }
            if (rKoth instanceof KothConquest) {
                KothConquest kothCQ = (KothConquest) rKoth;
                for (FactionScore fScore : kothCQ.getFScores()) {
                    if (fScore.getFaction().getName().equalsIgnoreCase(args[1])) {
                        try {
                            fScore.setPoints(fScore.getPoints()+Integer.parseInt(args[2]));
                            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_SET).entry(fScore.getFaction().getName()));
                        } catch(Exception e){
                            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_NOTANUMBER));
                        }
                    }
                }

                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_FACTION_NOT_FOUND));
            } else {
                throw new CommandMessageException(Lang.KOTH_ERROR_NOT_COMPATIBLE);
            }
        } else if(args[0].equalsIgnoreCase("del")){
            if (args.length < 3) {
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth change points set <faction> <points>");
            }
            if (rKoth instanceof KothConquest) {
                KothConquest kothCQ = (KothConquest) rKoth;
                for (FactionScore fScore : kothCQ.getFScores()) {
                    if (fScore.getFaction().getName().equalsIgnoreCase(args[1])) {
                        try {
                            fScore.setPoints(fScore.getPoints()-Integer.parseInt(args[2]));
                            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_SET).entry(fScore.getFaction().getName()));
                        } catch(Exception e){
                            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_NOTANUMBER));
                        }
                    }
                }

                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_FACTION_NOT_FOUND));
            } else {
                throw new CommandMessageException(Lang.KOTH_ERROR_NOT_COMPATIBLE);
            }
        } else if(args[0].equalsIgnoreCase("reset")){
            if (args.length < 3) {
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth change points set <faction> <points>");
            }
            if (rKoth instanceof KothConquest) {
                KothConquest kothCQ = (KothConquest) rKoth;
                for (FactionScore fScore : kothCQ.getFScores()) {
                    if (fScore.getFaction().getName().equalsIgnoreCase(args[1])) {
                        try {
                            fScore.setPoints(0);
                            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_SET).entry(fScore.getFaction().getName()));
                        } catch(Exception e){
                            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_NOTANUMBER));
                        }
                    }
                }

                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_CHANGE_POINTS_FACTION_NOT_FOUND));
            } else {
                throw new CommandMessageException(Lang.KOTH_ERROR_NOT_COMPATIBLE);
            }
        }
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.CHANGE;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
                "change"
        };
    }

    @Override
    public String getUsage() {
        return "/koth change";
    }


    @Override
    public String getDescription() {
        return "Gives control over a running koth";
    }

}
