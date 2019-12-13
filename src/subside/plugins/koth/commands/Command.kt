package subside.plugins.koth.commands

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.utils.IPerm
import java.util.*

interface Command {
    fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>)

    val permission: IPerm
    val commands: Array<String>

    val usage: String
    val description: String

    fun offsetArgs(args: Array<String>, offset: Int) =
            args.copyOfRange(offset, args.size)
}