package subside.plugins.koth.commands.control

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.gamemodes.StartParams
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import subside.plugins.koth.utils.Utils

class CommandStart : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth start <name> [time] [maxRunTime] [lootAmount] [entitytype]")
                    .send(sender)
            return
        }

        val params = StartParams(args[0])
        try {
            if (args.size > 1) params.captureTime = Utils.convertTime(args[1])
            if (args.size > 2) params.maxRunTime = Integer.parseInt(args[2])
            if (args.size > 3) params.lootAmount = Integer.parseInt(args[3])
            if (args.size > 4) params.entityType = args[4]

            kothManager.kothHandler.startKoth(params)
        } catch (e: NumberFormatException) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth start <name> [time] [maxRunTime] [lootAmount]")
                    .send(sender)
        } catch(e: Exception) {
            MessageBuilder(e.message).send(sender)
        }


    }

    override val permission = Perm.Control.START
    override val commands: Array<String> = arrayOf("start")
    override val usage: String = "/koth start <koth>"
    override val description: String = "Starts a certain KoTH"
}