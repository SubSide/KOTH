package subside.plugins.koth.loot

import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import subside.plugins.koth.KothManager
import subside.plugins.koth.areas.Koth
import subside.plugins.koth.captureentities.Capper
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.MessageBuilder

class Loot(
    var name: String,
    val commands: MutableList<String>,
    val inventory: Inventory
): ConfigurationSerializable {

    fun triggerCommands(kothManager: KothManager, koth: Koth, capper: Capper<*>?) {
        if (kothManager.configHandler.loot.isCmdEnabled) return
        if (capper == null) return

        commands.forEach { command ->
            when {
                command.contains("%player%") -> {
                    capper.getAvailablePlayers(koth).forEach {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", it.name))
                    }
                }
                command.contains("%faction%") -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%faction%", capper.name))
                }
                else -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
            }
        }
    }




    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "name" to name,
            "commands" to commands,
            "inventory" to inventory.map { it.serialize() }
        )
    }

    companion object {
        @JvmStatic fun deserialize(args: Map<String, Any>): Loot {
            return Loot(
                args["name"] as String,
                (args["commands"] as List<String>).toMutableList(),
                createInventory(
                    args["name"] as String,
                    (args["inventory"] as? List<Map<String, Any>>)?.map { ItemStack.deserialize(it) }
                        ?: emptyList()
                )
            )
        }

        private fun createInventory(name: String, items: List<ItemStack>): Inventory {
            val inv = Bukkit.createInventory(null, 54, createTitle(name))
            inv.contents = items.toTypedArray()
            return inv
        }

        private fun createTitle(name: String): String {
            var title = MessageBuilder(Lang.COMMAND_LOOT_CHEST_TITLE).loot(name).build()[0]
            if (title.length > 32) title = title.substring(0, 32)
            return title
        }
    }
}