package subside.plugins.koth.modules

import subside.plugins.koth.KothManager
import subside.plugins.koth.KothPlugin

interface Module {
    fun onLoad(kothManager: KothManager) {}
    fun onEnable(kothManager: KothManager) {}
    fun onDisable(kothManager: KothManager) {}
}
