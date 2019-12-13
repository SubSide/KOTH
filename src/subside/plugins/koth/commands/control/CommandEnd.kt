package subside.plugins.koth.commands.control

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.gamemodes.RunningKoth
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm

class CommandEnd : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            kothManager.kothHandler.endAllKoths(RunningKoth.EndReason.GRACEFUL)
            MessageBuilder(Lang.COMMAND_TERMINATE_ALL_KOTHS).send(sender)
        } else {
            kothManager.kothHandler.endKoth(args[0], RunningKoth.EndReason.GRACEFUL)
            MessageBuilder(Lang.COMMAND_TERMINATE_SPECIFIC_KOTH)
                    .koth(kothManager.kothHandler, args[0])
                    .send(sender)
        }
    }

    override val permission: IPerm = Perm.Control.END
    override val commands: Array<String> = arrayOf("end")
    override val usage: String = "/koth end [koth]"
    override val description: String = "Gracefully ends a (specific) KoTH"
}