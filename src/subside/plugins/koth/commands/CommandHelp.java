package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandHelp implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (Perm.Admin.HELP.has(sender)) {
            List<String> list = new ArrayList<>();
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth create <koth>").commandInfo("creates a new koth").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit").commandInfo("edits an existing koth").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth remove <koth>").commandInfo("removes an existing koth").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth list").commandInfo("Shows all available koths").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot (set) <koth>").commandInfo("Shows the available loot for a koth").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth start <koth>").commandInfo("Starts a koth at a certain koth").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth stop [koth]").commandInfo("Stops a (specific) koth").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth end [koth]").commandInfo("Gracefully ends a (specific) koth").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule (?)").commandInfo("Shows/Schedules a koth at a certain time").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth reload").commandInfo("Reloads the plugin").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth asmember").commandInfo("Shows the help menu as a normal player").buildArray());
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info").commandInfo("Shows info about this plugin").buildArray());
            sender.sendMessage(list.toArray(new String[list.size()]));
        } else {
            List<String> list = ConfigHandler.getCfgHandler().getHelpCommand();
            List<String> list2 = new ArrayList<>();
            for (String hlp : list) {
                list2.addAll(new MessageBuilder(hlp).koth(KothHandler.getRunningKoth().get().getKoth().getName()).time(KothHandler.getRunningKoth().get().getTimeObject()).player(KothHandler.getRunningKoth().get().getCappingPlayer()).buildArray());
            }
            sender.sendMessage(list2.toArray(new String[list2.size()]));
        }

    }

    @Override
    public Perm getPermission() {
        return Perm.HELP;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"help"};
    }

}
