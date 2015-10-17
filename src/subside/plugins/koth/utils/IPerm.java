package subside.plugins.koth.utils;

import org.bukkit.command.CommandSender;

public interface IPerm {
    public abstract boolean has(CommandSender sender);
}
