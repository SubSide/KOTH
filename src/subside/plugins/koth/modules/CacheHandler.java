package subside.plugins.koth.modules;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.utils.JSONLoader;

public class CacheHandler extends AbstractModule {
    
    public CacheHandler(KothPlugin plugin){
        super(plugin);
    }
    
    @Override
    public void onEnable(){
        Bukkit.getScheduler().runTask(plugin, this::lateEnable);
    }
    
    private void lateEnable() {
        JSONObject jsonObj = (JSONObject)new JSONLoader(plugin, "cache.json").load();
        if(jsonObj == null)
            return;
        
        // Loading
        plugin.getScheduleHandler().getMapRotation().load((JSONObject)jsonObj.get("mapRotation"));
        
        if(plugin.getServer().getOnlinePlayers().size() > 0){
            JSONArray runningKoths = (JSONArray)jsonObj.get("runningKoths");
            for(Object rObject : runningKoths){
                JSONObject rObj = (JSONObject)rObject;
                RunningKoth rKoth = plugin.getGamemodeRegistry().createGame((String)rObj.get("kothType"));
                rKoth.load(rObj);
                plugin.getKothHandler().addRunningKoth(rKoth);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDisable() {
            JSONObject obj = new JSONObject();
            
            // Saving
            obj.put("mapRotation", plugin.getScheduleHandler().getMapRotation().save());
            
            JSONArray runningKoths = new JSONArray();
            for(RunningKoth koth : plugin.getKothHandler().getRunningKoths()){
                JSONObject rObj = koth.save();
                rObj.put("kothType", koth.getType());
                runningKoths.add(rObj);
            }
            
            obj.put("runningKoths", runningKoths);
            
            new JSONLoader(plugin, "cache.json").save(obj);
    }
}
