package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandNext extends AbstractCommand {

    public CommandNext(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Schedule schedule = getPlugin().getScheduleHandler().getNextEvent();
        if(schedule != null)
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_NEXT_MESSAGE).koth(getPlugin().getKothHandler(), schedule.getKoth()).timeTillNext(schedule));

        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_NEXT_NO_NEXT_FOUND));
    }

    @Override
    public Perm getPermission() {
        return Perm.NEXT;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"next"};
    }
    
    @Override
    public String getUsage() {
        return "/koth next";
    }

    @Override
    public String getDescription() {
        return "info about the next upcoming KoTH";
    }

}
