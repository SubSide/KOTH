package subside.plugins.koth.commands.basic.datatable

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.captureentities.Capper
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import subside.plugins.koth.utils.Utils
import java.sql.SQLException

class CommandDatatable : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            Utils.sendMessage(sender, true, "Invalid syntax")
            return
        }

        val newArgs = offsetArgs(args, 1)
        when (args[0].toLowerCase()) {
            "debug" -> debug(kothManager, sender, newArgs)
            "query" -> query(sender, newArgs)
            "clear" -> clear(sender, newArgs)
            "close" -> close(kothManager, sender, newArgs)
        }
    }

    private fun debug(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.size < 2) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth datatable debug (0|1|2) <maxrows> [time] [capturetype] [gamemode] [koth]").buildAndSend(sender)
            return
        }

        if (args[0] == "2") {
            Utils.sendMessage(sender, true, "Player results returned:")

            val wins: Int = kothManager.dataTable.getPlayerStats(Bukkit.getPlayer(args[1]), 0)
            Utils.sendMessage(sender, true, args[1] + " has " + wins + " wins.")
            return
        }

        val rows: Int = Integer.parseInt(args[1])
        val time: Int = if (args.size > 2) Integer.parseInt(args[2]) else 0
        val captureType = if (args.size > 3 && args[3] != "0") args[3] else null
        val gameMode = if (args.size > 4 && args[4] != "0") args[4] else null
        val koth = if (args.size > 5 && args[5] != "0") args[5] else null

        if (args[0] == "0") {
            Utils.sendMessage(sender, true, "Global results returned:")

            val list: List<Map.Entry<Capper<*>, Int>> = kothManager.dataTable.getTop(rows, time, captureType, gameMode, koth)
            for ((key, value) in list) {
                Utils.sendMessage(sender, true, key.name + " : " + value)
            }
        } else {

            Utils.sendMessage(sender, true, "Player results returned:")

            val list: List<Map.Entry<OfflinePlayer, Int>> = kothManager.dataTable.getPlayerTop(rows, time, captureType, gameMode, koth)
            for ((key, value) in list) {
                Utils.sendMessage(sender, true, key.name + " : " + value)
            }
        }
    }

    private fun query(sender: CommandSender, args: Array<String>) {

    }

    private fun clear(sender: CommandSender, args: Array<String>) {

    }

    private fun close(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        try {
            kothManager.dataTable.databaseProvider.connection.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        Utils.sendMessage(sender, true, "Database connection has been temporarily closed")
    }

    override val permission = Perm.Admin.ADMIN
    override val commands = arrayOf("datatable")
    override val usage = "/koth datatable"
    override val description = "Manage/manipulate the datatable (experimental)"

}