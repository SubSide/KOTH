package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandVersion implements AbstractCommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        list.add(" ");
        list.addAll(new MessageBuilder("&8========> &2INFO &8<========").buildArray());
        list.addAll(new MessageBuilder("&2Author: &aSubSide").buildArray());
        list.addAll(new MessageBuilder("&2Version: &a" + KothPlugin.getPlugin().getDescription().getVersion()).buildArray());
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

}
