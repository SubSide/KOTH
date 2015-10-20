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
        if (!Perm.Admin.HELP.has(sender)) {
            asMember(sender, args);
        }

        List<String> list = new ArrayList<>();
        list.add("");
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH Basic Commands").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth list").commandInfo("Shows all available koths").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth asmember").commandInfo("Shows the help menu as a normal player").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth version").commandInfo("Shows the version of this plugin").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth reload").commandInfo("Reloads the plugin").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth tp <koth> [area]").commandInfo("teleport to an koth (area)").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info").commandInfo("info about various things (helpful!)").buildArray());
        list.add("");
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH Control Commands").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth start <koth>").commandInfo("Starts a certain koth").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth stop [koth]").commandInfo("Stops a (specific) koth").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth end [koth]").commandInfo("Gracefully ends a (specific) koth").buildArray());
        list.add("");
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH Editor Commands").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth create <koth>").commandInfo("creates a new koth").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth remove <koth>").commandInfo("removes an existing koth").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit").commandInfo("shows the editor menu").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot").commandInfo("Shows the loot menu").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule").commandInfo("Shows the schedule menu").buildArray());
        list.add("");
        sender.sendMessage(list.toArray(new String[list.size()]));

    }

    public static void asMember(CommandSender sender, String[] args) {
        List<String> list = ConfigHandler.getCfgHandler().getHelpCommand();
        List<String> list2 = new ArrayList<>();
        for (String hlp : list) {
            MessageBuilder mB = new MessageBuilder(hlp);
            try {
                mB.koth(KothHandler.getRunningKoth().get().getKoth());
                mB.time(KothHandler.getRunningKoth().get().getTimeObject());
                mB.player(KothHandler.getRunningKoth().get().getCappingPlayer());
            }
            catch (Exception e) {
                mB.koth("None").time("00:00").player("None");
            }
            list2.addAll(mB.buildArray());
        }
        sender.sendMessage(list2.toArray(new String[list2.size()]));
    }

    @Override
    public Perm getPermission() {
        return Perm.HELP;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "help"
        };
    }

}
