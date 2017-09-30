package subside.plugins.koth.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
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
        OutputStreamWriter osw;
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
            try {
                Object gsonBuilder = Class.forName("org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder").getDeclaredConstructor().newInstance();
                Object jsonParser = Class.forName("org.bukkit.craftbukkit.libs.com.google.gson.JsonParser").getDeclaredConstructor().newInstance();
                Class jsonElementClass = Class.forName("org.bukkit.craftbukkit.libs.com.google.gson.JsonElement");

                gsonBuilder = gsonBuilder.getClass().getDeclaredMethod("setPrettyPrinting").invoke(gsonBuilder);
                gsonBuilder = gsonBuilder.getClass().getDeclaredMethod("disableHtmlEscaping").invoke(gsonBuilder);
                gsonBuilder = gsonBuilder.getClass().getDeclaredMethod("create").invoke(gsonBuilder);

                jsonParser = jsonParser.getClass().getDeclaredMethod("parse", String.class).invoke(jsonParser, str);

                return (String)gsonBuilder.getClass().getDeclaredMethod("toJson", jsonElementClass).invoke(gsonBuilder, jsonParser);
            } catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException f){
                plugin.getLogger().severe("Couldn't find GsonBuilder/JsonParser class!");
                f.printStackTrace();
            }
        }
        return "";
    }
}
