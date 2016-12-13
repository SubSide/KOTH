package subside.plugins.koth.loaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.scheduler.Day;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.utils.Utils;

public class ScheduleLoader {

    @SuppressWarnings("unchecked")
    public static void load() {
        KothPlugin plugin = KothPlugin.getPlugin();
        try {
            ScheduleHandler.getInstance().getSchedules().clear();
            if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json").exists()) {
                save();
                return;
            }
            JSONParser parser = new JSONParser();
            JSONObject obj2 = (JSONObject)parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json"));
            Set<Object> dayz = ((JSONObject) obj2).keySet();
            for (Object dai : dayz) {
                Day day = Day.getDay((String) dai);
                
                JSONArray koths = (JSONArray) obj2.get(dai);

                Iterator<?> it = koths.iterator();
                while (it.hasNext()) {
                    try {
                        Schedule schedule = new Schedule(null, null, null);
                        JSONObject schedObj = (JSONObject) it.next();
                        schedObj.put("day", day.getDay());
                        schedule.load((JSONObject) it.next());
                        
                        ScheduleHandler.getInstance().getSchedules().add(schedule);
                    }
                    catch (Exception e) {
                        KothPlugin.getPlugin().getLogger().severe("////////////////");
                        KothPlugin.getPlugin().getLogger().severe("Error loading Schedule!");
                        KothPlugin.getPlugin().getLogger().severe("////////////////");
                        e.printStackTrace();
                    }
                }

            }

        }
        catch (Exception e) {
            KothPlugin.getPlugin().getLogger().warning("///// SCHEDULE FILE NOT FOUND, EMPTY OR NOT CORRECTLY SET UP ////");

            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void save() {
        KothPlugin plugin = KothPlugin.getPlugin();
        try {

            if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json").exists()) {
                plugin.getDataFolder().mkdirs();
                new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json").createNewFile();
            }
            JSONObject obj = new JSONObject();
            
            for (Schedule schedule : ScheduleHandler.getInstance().getSchedules()) {
                JSONArray obj2 = new JSONArray();
                if(obj.containsKey(schedule.getDay().getDay())){
                    obj2 = (JSONArray)obj.get(schedule.getDay().getDay());
                }
                obj2.add(schedule.save());
                obj.put(schedule.getDay().getDay(), obj2);
            }
            FileOutputStream fileStream = new FileOutputStream(new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json"));
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
