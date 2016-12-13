package subside.plugins.koth.loaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.utils.Utils;

public class LootLoader {
    
    @SuppressWarnings("deprecation")
    public static void load() {
        KothPlugin plugin = KothPlugin.getPlugin();
        try {
            KothHandler.getInstance().getLoots().clear();
            if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "loot.json").exists()) {
                save();
                return;
            }
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "loot.json"));
            if(obj instanceof JSONArray){
                JSONArray koths = (JSONArray) obj;
                
                Iterator<?> it = koths.iterator();
                while(it.hasNext()){
                    try {
                        Loot loot = new Loot(null);
                        loot.load((JSONObject)it.next());
                        KothHandler.getInstance().getLoots().add(loot);
                    } catch(Exception e){
                        KothPlugin.getPlugin().getLogger().severe("////////////////");
                        KothPlugin.getPlugin().getLogger().severe("Error loading loot!");
                        KothPlugin.getPlugin().getLogger().severe("////////////////");
                        e.printStackTrace();
                    }
                }
            }

        }
        catch (Exception e) {
            KothPlugin.getPlugin().getLogger().warning("///// LOOT FILE NOT FOUND, EMPTY OR NOT CORRECTLY SET UP ////");

            e.printStackTrace();
        }
    }

    @SuppressWarnings({
            "unchecked", "deprecation"
    })
    public static void save() {
        KothPlugin plugin = KothPlugin.getPlugin();
        try {

            if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "loot.json").exists()) {
                plugin.getDataFolder().mkdirs();
                new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "loot.json").createNewFile();
            }

            JSONArray obj = new JSONArray();
            for (Loot loot : KothHandler.getInstance().getLoots()) {
                obj.add(loot.save());
            }
            FileOutputStream fileStream = new FileOutputStream(new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "loot.json"));
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
