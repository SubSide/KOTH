package subside.plugins.koth

import subside.plugins.koth.captureentities.CaptureTypeRegistry
import subside.plugins.koth.commands.CommandHandler
import subside.plugins.koth.datatable.DataTable
import subside.plugins.koth.gamemodes.GamemodeRegistry
import subside.plugins.koth.hooks.HookManager
import subside.plugins.koth.loot.LootHandler
import subside.plugins.koth.modules.CacheHandler
import subside.plugins.koth.modules.ConfigHandler
import subside.plugins.koth.modules.KothHandler
import subside.plugins.koth.modules.VersionChecker
import subside.plugins.koth.scheduler.ScheduleHandler
import java.lang.ref.WeakReference

class KothManager {
     val configHandler: ConfigHandler
     val commandHandler: CommandHandler
     val lootHandler: LootHandler
     val gamemodeRegistry: GamemodeRegistry
     val captureTypeRegistry: CaptureTypeRegistry
     val kothHandler: KothHandler
     val hookManager: HookManager
     val scheduleHandler: ScheduleHandler
     val dataTable: DataTable?
     val cacheHandler: CacheHandler
     val versionChecker: VersionChecker

     val plugin: WeakReference<KothPlugin>
}