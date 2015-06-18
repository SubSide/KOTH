package subside.plugins.koth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Lang {

	public static String PREFIX = "&2[KOTH] &a";

    public static String KOTH_WON = "&aThe koth %area% ended! %player% won!";
    public static String KOTH_WON_CAPPER = "&aThe koth %area% ended! You won!";
	public static String KOTH_STARTING = "&aThe koth %area% has begun!";
    public static String KOTH_PLAYERCAP = "&a%player% has started to cap %area%!";
    public static String KOTH_PLAYERCAP_CAPPER = "&aYou have started capping %area%!";
    public static String KOTH_CAPTIME = "&a%player% is capping the koth! %minutes_left%:%seconds_left% left!";
    public static String KOTH_CAPTIME_CAPPER = "&aYou are capping the koth! %minutes_left%:%seconds_left% left!";
    public static String KOTH_LEFT = "&a%player% left the koth!";
    public static String KOTH_LEFT_CAPPER = "&aYou left the koth!";
	public static String KOTH_LOOT_CHEST = "&1&l%area%s &8&lloot";
	
	public static String AREA_ALREADYRUNNING = "&aThe area %area% is already running!";
	public static String AREA_ALREADYEXISTS = "&aThe area %area% already exists!";
	public static String AREA_NOTEXIST = "&aThe area %area% doesn't exist!";
	

	public static String COMMAND_ONLYFROMINGAME = "&aThis command can only be executed from ingame!";
	public static String COMMAND_USAGE = "Usage: ";

	public static String COMMAND_HELP_TITLE = "&8========> &2Koth &8<========";
	public static String COMMAND_HELP_INFO = "&a%command% &7%command_info%";
	
	public static String COMMAND_SCHEDULE_HELP_TITLE = "&8========> &2Koth scheduler &8<========";
	public static String COMMAND_SCHEDULE_HELP_INFO = "&a%command% &7%command_info%";
	
	
	public static String COMMAND_AREA_TRIGGERED = "&aYou've started the area %area%!";
	public static String COMMAND_AREA_CREATED = "&aYou successfully created the area %area%!";
	public static String COMMAND_AREA_REMOVED = "&aYou've successfully removed the area %area%!";
	public static String COMMAND_AREA_ALREADYEXISTS = "&aThe area %area% already exists!";
	public static String COMMAND_AREA_SELECT = "&aYou need to select an area with worldedit!";

	public static String COMMAND_SCHEDULE_CREATED = "&aYou have created a schedule for %area% on %day% at %time% (Length: %length% minutes)!";
	public static String COMMAND_SCHEDULE_NOVALIDDAY = "&aThis is not a valid day! (monday, tuesday etc)";
	public static String COMMAND_SCHEDULE_RUNTIMEERROR = "&aCould not change the run time into an integer!";
	public static String COMMAND_SCHEDULE_REMOVED = "&aThe schedule for %area% is removed.";
	public static String COMMAND_SCHEDULE_NOTEXIST = "&aThis schedule doesn't exist! (Check /koth schedule list for numbers)";
	public static String COMMAND_SCHEDULE_REMOVENOID = "&aThe ID must be a number! (Shown in /koth schedule list)";
	public static String COMMAND_SCHEDULE_EMPTY = "&aThe server owner did not schedule any Koths!";

	public static String COMMAND_SCHEDULE_LIST_CURRENTDATETIME = "&aCurrent date: %date%";
	public static String COMMAND_SCHEDULE_LIST_DAY = "&8========> &2%day% &8<========";
	public static String COMMAND_SCHEDULE_LIST_ENTRY = "&a%area% at %time%";
	public static String COMMAND_SCHEDULE_ADMIN_LIST_CURRENTDATETIME = "&aCurrent date: %date%";
	public static String COMMAND_SCHEDULE_ADMIN_LIST_DAY = "&8========> &2%day% &8<========";
	public static String COMMAND_SCHEDULE_ADMIN_LIST_ENTRY = "&a(#%id%) %area% at %time% with length %length%";
	public static String COMMAND_SCHEDULE_ADMIN_EMPTY = "&aThe schedule list is currently empty!";
	
	public static String COMMAND_LOOT_EXPLANATION = "&aThis is the loot chest for %area%!";
	public static String COMMAND_LOOT_NOARGSEXPLANATION = "&aThis is the loot chest for the next running koth (%area%)!";
	public static String COMMAND_LOOT_SETNOAREA = "&aYou need to specify an area to set the loot chest!";
	public static String COMMAND_LOOT_SETNOBLOCK = "&aYou need to look at the block where the chest should spawn!";
	public static String COMMAND_LOOT_CHESTSET= "&aYou've set the loot chest for the area %area%!";
	
	public static String COMMAND_TERMINATE_SPECIFIC_KOTH = "&aYou have terminated %area%!";
	public static String COMMAND_TERMINATE_ALL_KOTHS = "&aYou have terminated all areas!";
	
	public static String COMMAND_LIST_MESSAGE = "&2List of areas:";
	public static String COMMAND_LIST_ENTRY = "&a- %area%";

	public static void load(JavaPlugin plugin) {
		try {

			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json").exists()) {
				save(plugin);
				return;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json"));

			JSONObject jsonObject = (JSONObject) obj;

			Field[] fields = Lang.class.getFields();
			for (Field field : fields) {
				try {
					if (Modifier.isStatic(field.getModifiers())) {
						if(jsonObject.containsKey(field.getName())){
							field.set(null, jsonObject.get(field.getName()));
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			save(plugin);
		}
		catch (Exception e) {
			System.out.println("///// LANG FILE NOT FOUND OR NOT CORRECTLY SET UP ////");
			System.out.println("Will use default variables instead.");
			System.out.println("You could try to delete the lang.json in the plugin folder.");

			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void save(JavaPlugin plugin) {
		try {

			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json").exists()) {
				plugin.getDataFolder().mkdirs();
				new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json").createNewFile();
			}

			JSONObject obj = new JSONObject();

			Field[] fields = Lang.class.getFields();
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())) {
					obj.put(field.getName(), field.get(null));
				}
			}

			FileOutputStream fileStream = new FileOutputStream(new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json"));
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
		catch (Exception e) {}
	}
}
