package subside.plugins.koth.commands.basic

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm

class CommandTp : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            MessageBuilder(Lang.COMMAND_GLOBAL_ONLYFROMINGAME).buildAndSend(sender)
            return
        }

        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth tp <koth> [area]").buildAndSend(sender)
            return
        }

        val koth = kothManager.kothHandler.getKoth(args[0])
        if (koth == null) {
            MessageBuilder(Lang.KOTH_ERROR_NOTEXIST).koth(kothManager.kothHandler, args[0]).buildAndSend(sender)
            return
        }

        var loc = koth.middle
        if (loc == null) {
            MessageBuilder(Lang.COMMAND_TELEPORT_NOAREAS).koth(koth).buildAndSend(sender)
            return
        }

        if (args.size > 1) {
            val area = koth.areas.find { it.name == args[1] }?.middle
            if (area == null) {
                MessageBuilder(Lang.AREA_ERROR_NOTEXIST).area(args[1]).buildAndSend(sender)
                return
            }

            loc = area

            MessageBuilder(Lang.COMMAND_TELEPORT_TELEPORTING_AREA).koth(koth).area(args[1]).buildAndSend(sender)
        } else {
            MessageBuilder(Lang.COMMAND_TELEPORT_TELEPORTING).koth(koth).buildAndSend(sender)
        }

        loc = loc.world.getHighestBlockAt(loc).location
                .add(0.5, 0.5, 0.5)
                .setDirection(sender.location.direction)

        sender.teleport(loc)
    }

    override val permission = Perm.Admin.TP
    override val commands = arrayOf("tp")
    override val usage = "/koth tp <koth> [area]"
    override val description = "teleport to a koth (area)"
}