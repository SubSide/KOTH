package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.utils.IPerm;

public interface ICommand {
    public void run(CommandSender sender, String[] args);

    public IPerm getPermission();
    public String[] getCommands();
}