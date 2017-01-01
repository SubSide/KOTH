package subside.plugins.koth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.loaders.JSONLoader;
import subside.plugins.koth.scheduler.MapRotation;
import subside.plugins.koth.utils.Utils;

public class CacheHandler {
    private JavaPlugin plugin;
    public CacheHandler(JavaPlugin plugin){
        this.plugin = plugin;
    }
    
    public void load() {
        JSONObject jsonObj = (JSONObject)new JSONLoader(plugin, "cache.json").load();
        if(jsonObj == null)
            return;
        
        // Loading
        MapRotation.getInstance().load((JSONObject)jsonObj.get("mapRotation"));
        
        if(plugin.getServer().getOnlinePlayers().size() > 0){
            JSONArray runningKoths = (JSONArray)jsonObj.get("runningKoths");
            for(Object rObject : runningKoths){
                JSONObject rObj = (JSONObject)rObject;
                RunningKoth rKoth = KothHandler.getInstance().getGamemodeRegistry().createGame((String)rObj.get("kothType"));
                rKoth.load((JSONObject)rObj);
                KothHandler.getInstance().addRunningKoth(rKoth);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void save(JavaPlugin plugin) {
            JSONObject obj = new JSONObject();
            
            // Saving
            obj.put("mapRotation", MapRotation.getInstance().save());
            
            JSONArray runningKoths = new JSONArray();
            for(RunningKoth koth : KothHandler.getInstance().getRunningKoths()){
                JSONObject rObj = koth.save();
                rObj.put("kothType", koth.getType());
                runningKoths.add(rObj);
            }
            
            obj.put("runningKoths", runningKoths);
            
            new JSONLoader(plugin, "cache.json").save(obj);
    }
}
