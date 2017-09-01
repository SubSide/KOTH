package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandVersion extends AbstractCommand {

    public CommandVersion(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        list.add(" ");
        list.addAll(new MessageBuilder("&8========> &2INFO &8<========").buildArray());
        list.addAll(new MessageBuilder("&2Author: &aSubSide").buildArray());

        String version = "&a" + getPlugin().getDescription().getVersion()
                + (getPlugin().getVersionChecker().getNewVersion() != null ? " &7(outdated)" : "");
        list.addAll(new MessageBuilder("&2Version: &a"+ version).buildArray());
        list.addAll(new MessageBuilder("&2Site: &ahttp://bit.ly/1Pyxu2N").buildArray());
        sender.sendMessage(list.toArray(new String[list.size()]));
    }

    @Override
    public Perm getPermission() {
        return Perm.VERSION;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"version"};
    }

    @Override
    public String getUsage() {
        return "/koth version";
    }

    @Override
    public String getDescription() {
        return "Shows the version of this plugin";
    }

}
