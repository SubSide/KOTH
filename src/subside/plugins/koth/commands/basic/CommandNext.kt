package subside.plugins.koth.commands.basic

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm

class CommandNext : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        val schedule = kothManager.scheduleHandler.nextEvent

        if (schedule == null) {
            MessageBuilder(Lang.COMMAND_NEXT_NO_NEXT_FOUND).send(sender)
            return
        }

        MessageBuilder(Lang.COMMAND_NEXT_MESSAGE)
                .koth(kothManager.kothHandler, schedule.koth)
                .timeTillNext(schedule)
                .send(sender)
    }

    override val permission = Perm.NEXT
    override val commands = arrayOf("next")
    override val usage = "/koth next"
    override val description = "Info about the next upcoming KoTH"

}