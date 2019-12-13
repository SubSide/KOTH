package subside.plugins.koth.modules;

import lombok.Getter;
import subside.plugins.koth.KothManager;
import subside.plugins.koth.KothPlugin;

public abstract class AbstractModule {
    protected @Getter KothPlugin plugin;
    
    public AbstractModule(KothPlugin plugin){
        this.plugin = plugin;
    }
    
    public void onLoad(KothManager kothManager){}
    public void onEnable(KothManager kothManager){}
    public void onDisable(KothManager kothManager){}
}
