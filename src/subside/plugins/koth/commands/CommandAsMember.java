package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandAsMember extends AbstractCommand {

    public CommandAsMember(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        List<String> list = getPlugin().getConfigHandler().getGlobal().getHelpCommand();
        List<String> list2 = new ArrayList<>();
        for (String hlp : list) {
           MessageBuilder mB = new MessageBuilder(hlp);
           try {
               mB.koth(getPlugin().getKothHandler().getRunningKoth().getKoth());
               mB.time(getPlugin().getKothHandler().getRunningKoth().getTimeObject());
           }
           catch (Exception e) {
               mB.koth(getPlugin().getKothHandler(), "None").time("00:00").capper("None");
           }
           list2.addAll(mB.buildArray());
       }
       sender.sendMessage(list2.toArray(new String[list2.size()]));
   }

    @Override
    public IPerm getPermission() {
        return Perm.HELP;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"asmember"};
    }

    @Override
    public String getUsage() {
        return "/koth asmember";
    }

    @Override
    public String getDescription() {
        return "Shows the help menu as a normal player";
    }

}
