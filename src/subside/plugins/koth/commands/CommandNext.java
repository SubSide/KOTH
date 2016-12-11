package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.Lang;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandNext implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        Schedule schedule = ScheduleHandler.getInstance().getNextEvent();
        if(schedule != null)
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_NEXT_MESSAGE).koth(schedule.getKoth()).timeTillNext(schedule));

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

}
