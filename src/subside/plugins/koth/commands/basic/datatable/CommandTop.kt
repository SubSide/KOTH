package subside.plugins.koth.commands.basic.datatable

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import subside.plugins.koth.utils.Utils
import java.lang.NumberFormatException
import java.sql.SQLException
import java.util.*
import kotlin.collections.ArrayList

class CommandTop : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (!kothManager.configHandler.database.isEnabled) return

        val page = try {
            if (args.isNotEmpty()) {
                Integer.parseInt(args[0])
            } else 1
        } catch (e: NumberFormatException) {
            MessageBuilder(Lang.COMMAND_TOP_NOTANUMBER).send(sender)
            return
        }

        Bukkit.getScheduler().runTaskAsynchronously(kothManager.plugin.get()!!) {
            try {
                val result = kothManager.dataTable.sqlBuilder
                        .limit(10)
                        .offset((page -1) * 10).execute()

                val list = ArrayList<String>()
                list.addAll(MessageBuilder(Lang.COMMAND_TOP_TITLE).buildArray())

                var i = (page - 1) * 10 + 1
                while(result.next()) {
                    list.addAll(
                            MessageBuilder(Lang.COMMAND_TOP_ENTRY)
                                    .id(i++)
                                    .times(result.getInt("result").toString())
                                    .capper(Bukkit.getOfflinePlayer(UUID.fromString(result.getString("player_uuid"))).getName())
                                    .buildArray()
                    )
                }

                list.addAll(MessageBuilder(Lang.COMMAND_TOP_PAGE).times(page.toString()).buildArray())

                Utils.sendMsg(sender, list)
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }
    }


    override val permission = Perm.TOP
    override val commands = arrayOf("top")
    override val usage = "/koth top [page]"
    override val description = "Shows the top list"

}