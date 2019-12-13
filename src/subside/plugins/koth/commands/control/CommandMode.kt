package subside.plugins.koth.commands.control

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import java.util.*

class CommandMode : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            showHelp(sender)
            return
        }

        val newArgs = offsetArgs(args, 1)
        when(args[0].toLowerCase()) {
            "gamemode" -> gameMode(kothManager, sender, newArgs)
            "captureType" -> captureType(kothManager, sender, newArgs)
            else -> showHelp(sender)
        }
    }

    private fun captureType(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_ENTITY_LIST_TITLE).send(sender)

            kothManager.captureTypeRegistry.captureTypes.forEach { (key, _) ->
                MessageBuilder(Lang.COMMAND_ENTITY_LIST_ENTRY).entry(key).send(sender)
            }
            return
        }

        val captureType = kothManager.captureTypeRegistry.captureTypes.get(args[0])
        if (captureType == null) {
            MessageBuilder(Lang.COMMAND_ENTITY_NOT_EXIST)
                    .entry(args[0])
                    .send(sender)
            return
        }

        kothManager.captureTypeRegistry.preferedClass = captureType
        MessageBuilder(Lang.COMMAND_ENTITY_CHANGED)
                .entry(args[0])
                .send(sender)
    }

    private fun gameMode(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_MODE_LIST_TITLE).send(sender)

            kothManager.gamemodeRegistry.gamemodes.forEach { (key, _) ->
                MessageBuilder(Lang.COMMAND_MODE_LIST_ENTRY).entry(key).send(sender)
            }
            return
        }

        val gamemode = kothManager.gamemodeRegistry.gamemodes.get(args[0].toLowerCase())
        if (gamemode == null) {
            MessageBuilder(Lang.COMMAND_MODE_NOT_EXIST)
                    .entry(args[0])
                    .send(sender)
            return
        }

        kothManager.gamemodeRegistry.currentMode = gamemode
        MessageBuilder(Lang.COMMAND_MODE_CHANGED)
                .entry(args[0])
                .send(sender)
    }

    private fun showHelp(sender: CommandSender) {
        val list = ArrayList<String>()
        list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH mode commands").buildArray())
        list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth mode capturetype").commandInfo("Switch between capute types").buildArray())
        list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth mode gamemode").commandInfo("Switch between gamemodes").buildArray())
        sender.sendMessage(list.toTypedArray())
    }

    override val permission: IPerm = Perm.Admin.MODE
    override val commands: Array<String> = arrayOf("mode")
    override val usage: String = "/koth mode [mode]"
    override val description: String = "change the gamemode"
}