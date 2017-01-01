package subside.plugins.koth.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lombok.Getter;

public class JSONLoader {
    private @Getter String fileName;
    private JavaPlugin plugin;
    
    public JSONLoader(JavaPlugin plugin, String fileName){
        this.plugin = plugin;
        this.fileName = fileName;
    }
    
    public Object load() {
        try {
            // Check if file exists, if it doesn't just return null
            if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + fileName).exists()) {
                return null;
            }
            
            // Parse and return the JSON
            return new JSONParser().parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + fileName));
        } catch (IOException | ParseException e) {
            plugin.getLogger().log(Level.SEVERE, "Looks like the JSON file \"" + fileName + "\" got corrupted!", e);
        }
        
        // Otherwise return null
        return null;
    }

    public void save(Object object) {
        OutputStreamWriter osw = null;
        try {
            File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + fileName);
            if (!file.exists()) {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            }
            
            // Write the objects to the file
            // JSONObject and JSONArray don't have a shared interface/abstract class
            // So I need to do them separately. Sadly.
            FileOutputStream fileStream = new FileOutputStream(file);
            osw = new OutputStreamWriter(fileStream, "UTF-8");
            if(object instanceof JSONObject){
                osw.write(getGson(((JSONObject)object).toJSONString()));
            } else if(object instanceof JSONArray){
                osw.write(getGson(((JSONArray)object).toJSONString()));
            }
            
            // Flush the output to the file and close it
            osw.flush();
            osw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private String getGson(String str){
        try {
            return new com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new com.google.gson.JsonParser().parse(str));
        } catch(NoClassDefFoundError e){
            return new org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new org.bukkit.craftbukkit.libs.com.google.gson.JsonParser().parse(str));
        } 
    }
}
