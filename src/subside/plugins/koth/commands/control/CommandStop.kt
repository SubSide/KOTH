package subside.plugins.koth.commands.control

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.gamemodes.RunningKoth
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm

class CommandStop : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            kothManager.kothHandler.endAllKoths(RunningKoth.EndReason.FORCED)
            MessageBuilder(Lang.COMMAND_TERMINATE_ALL_KOTHS).buildAndSend(sender)
        } else {
            kothManager.kothHandler.endKoth(args[0], RunningKoth.EndReason.FORCED)
            MessageBuilder(Lang.COMMAND_TERMINATE_SPECIFIC_KOTH)
                    .koth(kothManager.kothHandler, args[0])
                    .buildAndSend(sender)
        }
    }

    override val permission: IPerm = Perm.Control.STOP
    override val commands: Array<String> = arrayOf("stop")
    override val usage: String = "/koth stop [koth]"
    override val description: String = "Stops a (specific) koth"
}