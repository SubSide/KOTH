package subside.plugins.koth;

import lombok.Getter;

public abstract class AbstractModule {
    protected @Getter KothPlugin plugin;
    
    public AbstractModule(KothPlugin plugin){
        this.plugin = plugin;
    }
    
    public void onLoad(){};
    public void onEnable(){};
    public void onDisable(){};
}
