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
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.utils.Utils;

public class ScheduleLoader {
	
	public static void load() {
		KothPlugin plugin = KothPlugin.getPlugin();
		try {
			ScheduleHandler.getInstance().getSchedules().clear();
			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json").exists()) {
				save();
				return;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json"));
			if(obj instanceof JSONArray){
				JSONArray koths = (JSONArray) obj;
				
				Iterator<?> it = koths.iterator();
				while(it.hasNext()){
					try {
						ScheduleHandler.getInstance().getSchedules().add(Schedule.load((JSONObject)it.next()));
					} catch(Exception e){
					    KothPlugin.getPlugin().getLogger().severe("////////////////");
					    KothPlugin.getPlugin().getLogger().severe("Error loading Schedule!");
					    KothPlugin.getPlugin().getLogger().severe("////////////////");
						e.printStackTrace();
					}
				}
			}

		}
		catch (Exception e) {
		    KothPlugin.getPlugin().getLogger().warning("///// KOTH FILE NOT FOUND, EMPTY OR NOT CORRECTLY SET UP ////");

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

			JSONArray obj = new JSONArray();
			for (Schedule schedule : ScheduleHandler.getInstance().getSchedules()) {
				obj.add(schedule.save());
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
