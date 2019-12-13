package subside.plugins.koth.commands.editor

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import subside.plugins.koth.KothManager
import subside.plugins.koth.areas.Area
import subside.plugins.koth.areas.Koth
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Utils

class CommandEdit : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            MessageBuilder(Lang.COMMAND_GLOBAL_ONLYFROMINGAME).buildAndSend(sender)
            return
        }

        if (args.size < 2) {
            sendHelp(sender)
            return
        }

        val koth = kothManager.kothHandler.getKoth(args[0])
        if (koth == null) {
            MessageBuilder(Lang.KOTH_ERROR_NOTEXIST)
                    .koth(kothManager.kothHandler, args[0])
                    .buildAndSend(sender)
        }

        val newArgs = offsetArgs(args, 1)
        when (args[1]) {
            "area" -> area(kothManager, sender, newArgs, koth)
            "loot" -> loot(kothManager, sender, newArgs, koth)
            "rename" -> rename(kothManager, sender, newArgs, koth)
            else -> sendHelp(sender)
        }


    }

    private fun rename(kothManager: KothManager, sender: CommandSender, args: Array<String>, koth: Koth) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth edit <koth> rename <name>")
                    .buildAndSend(sender)
            return
        }

        koth.name = args[0]
        kothManager.kothHandler.saveKoths()

        MessageBuilder(Lang.COMMAND_EDITOR_NAME_CHANGE).buildAndSend(sender)
    }

    private fun area(kothManager: KothManager, sender: Player, args: Array<String>, koth: Koth) {
        fun showHelp() {
            Utils.sendMessage(sender, true,
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Area commands").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area create <name>").commandInfo("create an area").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area edit <area>").commandInfo("re-sets an area").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area list").commandInfo("shows the area list").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area remove <area>").commandInfo("removes an area").build())
        }

        if (args.isEmpty()) {
            showHelp()
            return
        }

        val selection = (kothManager.plugin.get()!!.server.pluginManager.getPlugin("WorldEdit") as WorldEditPlugin)
                .getSelection(sender)

        when (args[0]) {
            "create" -> {
                if (args.size < 2) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth edit <koth> area create <name>")
                            .buildAndSend(sender)
                    return
                }

                if (selection == null) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_WESELECT).buildAndSend(sender)
                    return
                }

                if (koth.areas.any { it.name == args[1] }) {
                    MessageBuilder(Lang.AREA_ERROR_ALREADYEXISTS)
                            .area(args[1])
                            .buildAndSend(sender)
                    return
                }
                val area = Area(args[1], selection.minimumPoint, selection.maximumPoint)
                koth.areas.add(area)
                kothManager.kothHandler.saveKoths()

                MessageBuilder(Lang.COMMAND_EDITOR_AREA_ADDED).buildAndSend(sender)
            }
            "list" -> {
                MessageBuilder(Lang.COMMAND_LISTS_EDITOR_AREA_TITLE).buildAndSend(sender)
                koth.areas.forEach {
                    MessageBuilder(Lang.COMMAND_LISTS_EDITOR_AREA_ENTRY).area(it).buildAndSend(sender)
                }
            }
            "edit" -> {
                if (args.size < 2) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth edit <koth> area edit <name>")
                            .buildAndSend(sender)
                    return
                }

                if (selection == null) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_WESELECT).buildAndSend(sender)
                    return
                }

                val area = koth.areas.find { it.name == args[1] }
                if (area == null) {
                    MessageBuilder(Lang.AREA_ERROR_NOTEXIST).area(args[1]).buildAndSend(sender)
                    return
                }

                area.setArea(selection.minimumPoint, selection.maximumPoint)
                kothManager.kothHandler.saveKoths()

                MessageBuilder(Lang.COMMAND_EDITOR_AREA_EDITED).buildAndSend(sender)
            }
            "remove" -> {
                if (args.size < 2) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth edit <koth> area edit <name>")
                            .buildAndSend(sender)
                    return
                }

                val area = koth.areas.find { it.name == args[1] }
                if (area == null) {
                    MessageBuilder(Lang.AREA_ERROR_NOTEXIST).area(args[1]).buildAndSend(sender)
                    return
                }

                koth.areas.remove(area)
                kothManager.kothHandler.saveKoths()
                MessageBuilder(Lang.COMMAND_EDITOR_AREA_DELETED).buildAndSend(sender)
            }
        }

        showHelp()
    }

    private fun loot(kothManager: KothManager, sender: Player, args: Array<String>, koh: Koth) {
        // TODO
    }

    fun sendHelp(sender: CommandSender) {
        Utils.sendMessage(sender, true,
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area").commandInfo("Area commands").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot").commandInfo("Loot commands").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> rename <name>").commandInfo("Rename a koth").build())
    }
}