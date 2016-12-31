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

import lombok.Getter;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.scheduler.MapRotation;
import subside.plugins.koth.utils.Utils;

public class CacheHandler {
    public void load(JavaPlugin plugin) {
        try {

            if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "cache.json").exists()) {
                save(plugin);
                return;
            }
            JSONParser parser = new JSONParser();
            //Object obj = parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json"));
            Object obj = parser.parse(new InputStreamReader(new FileInputStream(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "cache.json"), "UTF-8"));
            JSONObject jsonObj = (JSONObject) obj;
            
            // Loading
            MapRotation.getInstance().load((JSONObject)jsonObj.get("mapRotation"));
            
            if(KothPlugin.getPlugin().getServer().getOnlinePlayers().size() > 0){
                JSONArray runningKoths = (JSONArray)jsonObj.get("runningKoths");
                for(Object rObject : runningKoths){
                    JSONObject rObj = (JSONObject)rObject;
                    RunningKoth rKoth = KothHandler.getInstance().getGamemodeRegistry().createGame((String)rObj.get("kothType"));
                    rKoth.load((JSONObject)rObj);
                    KothHandler.getInstance().addRunningKoth(rKoth);
                }
            }
            //
            
            save(plugin);
        }
        catch (Exception e) {
            KothPlugin.getPlugin().getLogger().warning("///// LANG FILE NOT FOUND OR NOT CORRECTLY SET UP ////");
            KothPlugin.getPlugin().getLogger().warning("Will use default variables instead.");
            KothPlugin.getPlugin().getLogger().warning("You could try to delete the lang.json in the plugin folder.");

            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void save(JavaPlugin plugin) {
        try {

            if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "cache.json").exists()) {
                plugin.getDataFolder().mkdirs();
                new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "cache.json").createNewFile();
            }

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
            //

            FileOutputStream fileStream = new FileOutputStream(new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "cache.json"));
            OutputStreamWriter file = new OutputStreamWriter(fileStream, "UTF-8");
            try {
                file.write(Utils.getGson(obj.toJSONString()));
            }
            catch (IOException e) {
                e.printStackTrace();

            }
            finally {
                file.flush();
                file.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
