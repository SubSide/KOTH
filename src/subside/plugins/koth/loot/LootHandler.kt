package subside.plugins.koth.loot

import org.bukkit.Bukkit
import subside.plugins.koth.KothManager
import subside.plugins.koth.KothPlugin
import subside.plugins.koth.modules.Module
import subside.plugins.koth.utils.FileLoader

class LootHandler : Module {
    val loots: MutableList<Loot> = ArrayList()

    override fun onEnable(kothManager: KothManager) {
        loots.clear()
        loots.addAll(
            FileLoader("loot.json")
                .loadList(kothManager.plugin.get()!!, Loot::class.java)
        )
    }

    override fun onDisable(kothManager: KothManager) {
        // Make sure that nobody is viewing a loot chest
        // This is important because otherwise people could take stuff out of the viewing loot chest
        Bukkit.getOnlinePlayers().forEach { player ->
            val openInv = player.openInventory?.title
            if (loots.any { loot -> loot.inventory.title.equals(openInv, ignoreCase = true) }) {
                player.closeInventory()
                return@forEach
            }
        }

        save(kothManager.plugin.get()!!) // Save the loot
    }

    fun save(plugin: KothPlugin) {
        FileLoader("loot.json").save(plugin, loots)
    }
}