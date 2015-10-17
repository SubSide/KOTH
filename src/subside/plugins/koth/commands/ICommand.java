package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.utils.IPerm;

public interface ICommand {
    public abstract void run(CommandSender sender, String[] args);

    public abstract IPerm getPermission();

    public abstract String[] getCommands();
}