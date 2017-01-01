package subside.plugins.koth;

public abstract class AbstractModule {
    protected KothPlugin plugin;
    
    public AbstractModule(KothPlugin plugin){
        this.plugin = plugin;
    }
    
    public void onLoad(){};
    public void onEnable(){};
    public void onDisable(){};
}
