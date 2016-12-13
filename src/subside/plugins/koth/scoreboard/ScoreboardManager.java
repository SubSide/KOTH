package subside.plugins.koth.scoreboard;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;
import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.adapter.RunningKoth;

public class ScoreboardManager implements Listener {
    private static @Getter ScoreboardManager instance;
    
    private Map<String, Class<? extends AbstractScoreboard>> scoreboardTypes = new HashMap<>();
    private AbstractScoreboard currentScoreboard;
    
    
    public ScoreboardManager(){
        instance = this;
    }
    
    public void registerScoreboard(String name, Class<? extends AbstractScoreboard> clazz){
        scoreboardTypes.put(name.toLowerCase(), clazz);
    }
    
    public AbstractScoreboard loadScoreboard(String name, RunningKoth koth){
        if(currentScoreboard != null || !ConfigHandler.getInstance().getScoreboard().isUseScoreboard()){
            return null;
        }
        try {
            currentScoreboard = scoreboardTypes.get(name).getConstructor().newInstance();
            
            ConfigurationSection section = ConfigHandler.getInstance().getScoreboard().getSection().getConfigurationSection(name.toLowerCase());
            currentScoreboard.load(koth, section.getString("title"), section.getStringList("contents").toArray(new String[section.getStringList("contents").size()]));
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return currentScoreboard;
    }
    
    public void update(){
        if(currentScoreboard == null){
            return;
        }
        currentScoreboard.updateScoreboard();
    }
    
    public void destroy(){
        if(currentScoreboard == null){
            return;
        }
        currentScoreboard.destroy();
        currentScoreboard = null;
    }
    
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerQuit(PlayerQuitEvent event){
        if(currentScoreboard == null){
            return;
        }
        currentScoreboard.playerQuit(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerKick(PlayerKickEvent event){
        if(currentScoreboard == null){
            return;
        }
        currentScoreboard.playerQuit(event.getPlayer());
    }
}
