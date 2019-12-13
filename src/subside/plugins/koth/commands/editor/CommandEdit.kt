package subside.plugins.koth.commands.editor

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import subside.plugins.koth.KothManager
import subside.plugins.koth.areas.Area
import subside.plugins.koth.areas.Koth
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import subside.plugins.koth.utils.Utils
import java.util.*

class CommandEdit : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            MessageBuilder(Lang.COMMAND_GLOBAL_ONLYFROMINGAME).send(sender)
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
                    .send(sender)
        }

        val newArgs = offsetArgs(args, 1)
        when (args[1].toLowerCase()) {
            "area" -> area(kothManager, sender, newArgs, koth)
            "loot" -> loot(kothManager, sender, newArgs, koth)
            "rename" -> rename(kothManager, sender, newArgs, koth)
            else -> sendHelp(sender)
        }


    }

    private fun rename(kothManager: KothManager, sender: CommandSender, args: Array<String>, koth: Koth) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth edit <koth> rename <name>")
                    .send(sender)
            return
        }

        koth.name = args[0]
        kothManager.kothHandler.saveKoths()

        MessageBuilder(Lang.COMMAND_EDITOR_NAME_CHANGE).send(sender)
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

        when (args[0].toLowerCase()) {
            "create" -> {
                if (args.size < 2) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth edit <koth> area create <name>")
                            .send(sender)
                    return
                }

                if (selection == null) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_WESELECT).send(sender)
                    return
                }

                if (koth.areas.any { it.name == args[1] }) {
                    MessageBuilder(Lang.AREA_ERROR_ALREADYEXISTS)
                            .area(args[1])
                            .send(sender)
                    return
                }
                val area = Area(args[1], selection.minimumPoint, selection.maximumPoint)
                koth.areas.add(area)
                kothManager.kothHandler.saveKoths()

                MessageBuilder(Lang.COMMAND_EDITOR_AREA_ADDED).send(sender)
            }
            "list" -> {
                MessageBuilder(Lang.COMMAND_LISTS_EDITOR_AREA_TITLE).send(sender)
                koth.areas.forEach {
                    MessageBuilder(Lang.COMMAND_LISTS_EDITOR_AREA_ENTRY).area(it).send(sender)
                }
            }
            "edit" -> {
                if (args.size < 2) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth edit <koth> area edit <name>")
                            .send(sender)
                    return
                }

                if (selection == null) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_WESELECT).send(sender)
                    return
                }

                val area = koth.areas.find { it.name == args[1] }
                if (area == null) {
                    MessageBuilder(Lang.AREA_ERROR_NOTEXIST).area(args[1]).send(sender)
                    return
                }

                area.setArea(selection.minimumPoint, selection.maximumPoint)
                kothManager.kothHandler.saveKoths()

                MessageBuilder(Lang.COMMAND_EDITOR_AREA_EDITED).send(sender)
            }
            "remove" -> {
                if (args.size < 2) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth edit <koth> area edit <name>")
                            .send(sender)
                    return
                }

                val area = koth.areas.find { it.name == args[1] }
                if (area == null) {
                    MessageBuilder(Lang.AREA_ERROR_NOTEXIST).area(args[1]).send(sender)
                    return
                }

                koth.areas.remove(area)
                kothManager.kothHandler.saveKoths()
                MessageBuilder(Lang.COMMAND_EDITOR_AREA_DELETED).send(sender)
            }
            else -> showHelp()
        }
    }

    private fun loot(kothManager: KothManager, sender: Player, args: Array<String>, koth: Koth) {
        fun showHelp() {
            Utils.sendMessage(sender, true,
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("loot commands").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot setpos").commandInfo("sets the position to the block looking at").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot link <loot>").commandInfo("links a loot chest").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot second (none|north|east|south|west)").commandInfo("set where second chest will spawn").build()
            )
        }

        if (args.isEmpty()) {
            showHelp()
            return
        }

        when (args[0]) {
            "setpos" -> {
                val block: Block? = try {
                    val method = try {
                        LivingEntity::class.java.getDeclaredMethod("getTargetBlock", Set::class.java, Int::class.javaPrimitiveType)
                    } catch (e: NoSuchMethodException) {
                        LivingEntity::class.java.getDeclaredMethod("getTargetBlock", HashSet::class.java, Int::class.javaPrimitiveType)
                    }

                    method.invoke(sender, null, 8) as Block
                } catch (e: Exception) {
                    sender.sendMessage("Cannot use the getTargetBlock function!")
                    return
                }

                if (block == null) {
                    MessageBuilder(Lang.COMMAND_EDITOR_LOOT_SETNOBLOCK).send(sender)
                    return
                }

                koth.loot.lootPos = block.location
                kothManager.kothHandler.saveKoths()
                MessageBuilder(Lang.COMMAND_EDITOR_LOOT_POSITION_SET).send(sender)
            }
            "link" -> {
                if (args.size < 2) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth edit <koth> loot link <loot>")
                            .send(sender)
                    return
                }
                koth.loot.lootName = args[1]
                kothManager.kothHandler.saveKoths()
                MessageBuilder(Lang.COMMAND_EDITOR_LOOT_LINK).send(sender)
            }
            "second" -> {
                if (args.size < 2) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth edit <koth> loot second (none|north|east|south|west)")
                            .send(sender)
                    return
                }

                val direction = Koth.LootDirection.values().find { it.text == args[1].toLowerCase() }
                if (direction == null) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth edit <koth> loot second (none|north|east|south|west)")
                            .send(sender)
                    return
                }
                koth.loot.direction = direction
                kothManager.kothHandler.saveKoths()
                MessageBuilder(Lang.COMMAND_EDITOR_LOOT_SECOND_CHEST).send(sender)
            }
            else -> showHelp()
        }
    }

    fun sendHelp(sender: CommandSender) {
        Utils.sendMessage(sender, true,
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area").commandInfo("Area commands").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot").commandInfo("Loot commands").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> rename <name>").commandInfo("Rename a koth").build())
    }

    override val permission: IPerm = Perm.Admin.EDIT
    override val commands: Array<String> = arrayOf("edit")
    override val usage: String = "/koth edit <koth>"
    override val description: String = "Edits an existing KoTH"
}