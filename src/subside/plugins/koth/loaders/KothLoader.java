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
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.utils.Utils;

public class KothLoader {
	
	public static void load() {
		KothPlugin plugin = KothPlugin.getPlugin();
		try {
			KothHandler.getAvailableKoths().clear();
			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "koths.json").exists()) {
				save();
				return;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "koths.json"));
			if(obj instanceof JSONArray){
				JSONArray koths = (JSONArray) obj;
				
				Iterator<?> it = koths.iterator();
				while(it.hasNext()){
					try {
						KothHandler.getAvailableKoths().add(Koth.load((JSONObject)it.next()));
					} catch(Exception e){
					    KothPlugin.getPlugin().getLogger().severe("////////////////");
					    KothPlugin.getPlugin().getLogger().severe("Error loading koth!");
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

			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "koths.json").exists()) {
				plugin.getDataFolder().mkdirs();
				new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "koths.json").createNewFile();
			}

			JSONArray obj = new JSONArray();
			for (Koth koth : KothHandler.getAvailableKoths()) {
				obj.add(koth.save());
			}
			FileOutputStream fileStream = new FileOutputStream(new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "koths.json"));
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
