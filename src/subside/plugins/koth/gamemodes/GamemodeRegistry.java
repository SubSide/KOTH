package subside.plugins.koth.gamemodes;

import java.util.HashMap;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.modules.AbstractModule;

public class GamemodeRegistry extends AbstractModule {
    private @Getter HashMap<String, Class<? extends RunningKoth>> gamemodes;
    private @Getter @Setter String currentMode;
    
    public GamemodeRegistry(KothPlugin plugin){
        super(plugin);
        
        gamemodes = new HashMap<>();
        currentMode = "classic";
    }
    
    @Override
    public void onLoad(){
        this.getGamemodes().clear();
        this.register("classic", KothClassic.class);
    }
    
    public void register(String name, Class<? extends RunningKoth> clazz){
        gamemodes.put(name, clazz);
    }
    
    public RunningKoth createGame(){
        return createGame(currentMode);
    }
    
    public RunningKoth createGame(String gamemode){
        if(gamemodes.containsKey(gamemode)){
            try {
                return gamemodes.get(gamemode).getConstructor(GamemodeRegistry.class).newInstance(this);
            }
            catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An error occured while attempting to create a RunningKoth object!", e);
            }
        }
        return null;
    }
}
