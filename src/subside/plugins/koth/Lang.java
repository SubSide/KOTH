package subside.plugins.koth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import subside.plugins.koth.utils.Utils;

public class Lang {

    public static String[] KOTH_PLAYING_CAP_CHANNELING = new String[]{"&aChanneling started for %capper%, stay on the point for just %time% seconds!"};
    public static String[] KOTH_PLAYING_CAP_CHANNELING_CAPPER = new String[]{"&aYou started channeling the KoTH, just stay on the point for %time% seconds!"};
    public static String[] KOTH_PLAYING_CAP_START = new String[]{"&a%capper% has started to cap %koth%!"};
    public static String[] KOTH_PLAYING_CAP_START_CAPPER = new String[]{"&aYou have started capping %koth%!"};
    public static String[] KOTH_PLAYING_CAPTIME = new String[]{"&a%capper% is capping the koth! %ml%:%sl% left!"};
    public static String[] KOTH_PLAYING_CAPTIME_CAPPER = new String[]{"&aYou are capping the koth! %ml%:%sl% left!"};
    public static String[] KOTH_PLAYING_LEFT = new String[]{"&a%capper% left the koth!"};
    public static String[] KOTH_PLAYING_LEFT_CAPPER = new String[]{"&aYou left the koth!"};
    public static String[] KOTH_PLAYING_WON = new String[]{"&aThe koth %koth% ended! %capper% won!"};
    public static String[] KOTH_PLAYING_WON_CAPPER = new String[]{"&aThe koth %koth% ended! You won!"};
    public static String[] KOTH_PLAYING_WON_DROPPING_ITEMS = new String[]{"&aCouldn't place all items in your inventory! It's on the floor!"};
    public static String[] KOTH_PLAYING_NOT_CAPPING = new String[]{"&aThere is nobody capping the KoTH right now! (X: %x%, Z:%z%)" };
    public static String[] KOTH_PLAYING_STARTING = new String[]{"&aThe koth %koth% has begun!"};
    public static String[] KOTH_PLAYING_MINIMUM_PLAYERS_NOT_MET = new String[] { "&aThe minimum required of players is not met, KoTH will not continue." };
	public static String[] KOTH_PLAYING_LOOT_CHEST = new String[]{"&1&l%koth%s &8&lloot"};
    public static String[] KOTH_PLAYING_TIME_UP = new String[]{"&aAfter %mt% minute noone capped the KoTH! Event is over!"};
    
    public static String[] HOOKS_PLACEHOLDERAPI_NOONECAPPING = new String[]{ "No One" };
    public static String[] HOOKS_PLACEHOLDERAPI_TIMETILL = new String[]{ "%hh%:%mm%:%ss%" };
    
    public static String[] KOTH_PLAYING_PRE_BROADCAST = new String[]{"&aThe koth %koth% will start in 30 minutes!"};
    public static String[] KOTH_PLAYING_PLAYER_JOINING = new String[]{"&aThere is currently a KoTH running! How about a fight!?" };

    public static String[] KOTH_ERROR_ALREADYRUNNING = new String[]{"&aThe koth %koth% is already running!"};
    public static String[] KOTH_ERROR_ANOTHERALREADYRUNNING = new String[]{"&aThere is already a KoTH running!"};
    public static String[] KOTH_ERROR_ALREADYEXISTS = new String[]{"&aThe koth %koth% already exists!"};
    public static String[] KOTH_ERROR_NONE_RUNNING = new String[]{"&aThe currently no koth running!"};
    public static String[] KOTH_ERROR_NOTEXIST = new String[]{"&aThe koth %koth% doesn't exist!"};
	public static String[] KOTH_ERROR_NO_COMPATIBLE_CAPPER = new String[]{"This is not a compatible player!" };
    public static String[] KOTH_ERROR_NOT_COMPATIBLE = new String[]{"&aThe current koth is not compatible!"};

    public static String[] AREA_ERROR_ALREADYEXISTS = new String[]{"&aThe area %area% already exists!"};
    public static String[] AREA_ERROR_NOTEXIST = new String[]{"&aThe area %area% doesn't exist!"};
    public static String[] LOOT_ERROR_ALREADYEXISTS = new String[]{"&aThe loot %loot% already exists!"};
    public static String[] LOOT_ERROR_NOTEXIST = new String[]{"&aThe loot %loot% doesn't exist!"};
	

    public static String[] COMMAND_GLOBAL_PREFIX = new String[]{"&2[KOTH] &a"};
	public static String[] COMMAND_GLOBAL_ONLYFROMINGAME = new String[]{"&aThis command can only be executed from ingame!"};
    public static String[] COMMAND_GLOBAL_USAGE = new String[]{"&2Usage: &a"};
    public static String[] COMMAND_GLOBAL_NO_PERMISSION = new String[]{"&cYou have no permission to use this command!"};
    public static String[] COMMAND_GLOBAL_WESELECT = new String[]{"&aYou need to select an koth with worldedit!"};
    public static String[] COMMAND_GLOBAL_HELP_TITLE = new String[]{"&8========> &2%title% &8<========"};
    public static String[] COMMAND_GLOBAL_HELP_INFO = new String[]{"&a%command% &7%command_info%"};

    public static String[] COMMAND_IGNORE_START = new String[]{ "&aChat messages from &2KoTH &aare now ignored." };
    public static String[] COMMAND_IGNORE_STOP = new String[]{ "&aChat messages from &2KoTH &aare now shown." };

    public static String[] COMMAND_RELOAD_RELOAD = new String[]{"&aKoth &2>> &aReload complete"};

    public static String[] COMMAND_TELEPORT_TELEPORTING = new String[]{"&aTeleporting to %koth%!"};
    public static String[] COMMAND_TELEPORT_TELEPORTING_AREA = new String[]{"&aTeleporting to %koth%, area %area%!"};
    public static String[] COMMAND_TELEPORT_NOAREAS = new String[]{"&aThis koth doesn't have any areas!"};

    public static String[] COMMAND_LISTS_EDITOR_AREA_TITLE = new String[]{"&8========> &2KoTH area list &8<========"};
    public static String[] COMMAND_LISTS_EDITOR_AREA_ENTRY = new String[]{"&a%area% &7- (%x%, %z%)"};
    public static String[] COMMAND_LISTS_LIST_TITLE = new String[]{"&8========> &2Available KoTHs &8<========"};
    public static String[] COMMAND_LISTS_LIST_ENTRY = new String[]{"&7- &a%koth%"};
    public static String[] COMMAND_LISTS_LOOT_TITLE = new String[]{"&8========> &2Available Loot chests &8<========"};
    public static String[] COMMAND_LISTS_LOOT_ENTRY = new String[]{"&7- &a%loot%"};
    public static String[] COMMAND_LISTS_LOOT_CMD_TITLE = new String[] { "&8========> &2Loot command list &8<========" };
    public static String[] COMMAND_LISTS_LOOT_CMD_ENTRY = new String[]{"&a(#%id%) &7- &a%command%"};

    public static String[] COMMAND_LOOT_CREATE = new String[]{ "&aLoot succesfully created!"};
    public static String[] COMMAND_LOOT_RENAME = new String[]{ "&aLoot succesfully renamed!"};
    public static String[] COMMAND_LOOT_OPENING = new String[]{ "&aOpening %loot%."};
    public static String[] COMMAND_LOOT_REMOVE = new String[]{ "&aSuccesfully removed the loot!"};
    public static String[] COMMAND_LOOT_CHEST_TITLE = new String[]{ "&1%loot%s &8&lloot"};
    public static String[] COMMAND_LOOT_CMD_CREATED = new String[]{ "&aLoot command succesfully added!" };
    public static String[] COMMAND_LOOT_CMD_REMOVED = new String[]{ "&aLoot command succesfully removed!" };
    public static String[] COMMAND_LOOT_CMD_NOTANUMBER = new String[]{ "&aThis is not a valid ID!" };
    public static String[] COMMAND_LOOT_CMD_INGAME_DISABLED = new String[]{ "&aCommands can't be managed from ingame! Must be through config!" };
    public static String[] COMMAND_LOOT_CMD_OPONLY = new String[]{ "&aYou must be an OP to manage commands!" };
    public static String[] COMMAND_LOOT_CMD_CONFIG_NOT_ENABLED = new String[]{ "&cNote: commands are disabled in the config!" };
    

    public static String[] COMMAND_CHANGE_POINTS_SET = new String[]{ "&aPoints of the faction %entry% succesfully set!" };
    public static String[] COMMAND_CHANGE_POINTS_NOTANUMBER = new String[]{ "&aThis is not a number!" };
    public static String[] COMMAND_CHANGE_POINTS_FACTION_NOT_FOUND = new String[]{ "&aCan't find a faction with this name!" };
    

    public static String[] COMMAND_INFO_TITLE_KOTH = new String[]{"&8========> &2%koth% koth info &8<========"};
    public static String[] COMMAND_INFO_TITLE_LOOT = new String[]{"&8========> &2%loot% loot info &8<========"};
    public static String[] COMMAND_INFO_TITLE_SCHEDULE = new String[]{"&8========> &2#%id% schedule info &8<========"};
    public static String[] COMMAND_INFO_COLORS = new String[]{"&2", "&a"};
    

    public static String[] COMMAND_MODE_LIST_TITLE = new String[]{"&8========> &2Available Gamemodes &8<========"};
    public static String[] COMMAND_MODE_LIST_ENTRY = new String[]{"&7- &7%entry%"};
    public static String[] COMMAND_MODE_CHANGED = new String[]{"&aYou succesfully changed the gamemode to: %entry%"};
    public static String[] COMMAND_MODE_NOT_EXIST = new String[]{"&aNo such gamemode exists!"};

    public static String[] COMMAND_ENTITY_LIST_TITLE = new String[]{"&8========> &2Available Capture Entites &8<========"};
    public static String[] COMMAND_ENTITY_LIST_ENTRY = new String[]{"&7- &7%entry%"};
    public static String[] COMMAND_ENTITY_CHANGED = new String[]{"&aYou succesfully changed the gamemode to: %entry%"};
    public static String[] COMMAND_ENTITY_NOT_EXIST = new String[]{"&aNo such capture entity exists!"};
    		

    public static String[] COMMAND_EDITOR_AREA_ADDED = new String[]{"&aYou succesfully added the area!"};
    public static String[] COMMAND_EDITOR_AREA_EDITED = new String[]{"&aYou succesfully edited the area!"};
    public static String[] COMMAND_EDITOR_AREA_DELETED = new String[]{"&aYou succesfully deleted the area!"};
    public static String[] COMMAND_EDITOR_LOOT_SETNOBLOCK = new String[]{"&aYou need to look at the block where the chest should spawn!"};
    public static String[] COMMAND_EDITOR_LOOT_POSITION_SET = new String[]{"&aLoot position succesfully set!"};
    public static String[] COMMAND_EDITOR_LOOT_LINK = new String[]{"&aChanged the link of the loot!"};
    public static String[] COMMAND_EDITOR_NAME_CHANGE = new String[]{"&aChanged the name of the koth!"};
	
	
	public static String[] COMMAND_KOTH_CREATED = new String[]{"&aYou successfully created the koth %koth%!"};
	public static String[] COMMAND_KOTH_REMOVED = new String[]{"&aYou've successfully removed the koth %koth%!"};
	public static String[] COMMAND_KOTH_ALREADYEXISTS = new String[]{"&aThe koth %koth% already exists!"};

	public static String[] COMMAND_SCHEDULE_CREATED = new String[]{"&aYou have created a schedule for %koth% on %day% at %time% (Capture time: %ct% minutes)!"};
	public static String[] COMMAND_SCHEDULE_NOVALIDDAY = new String[]{"&aThis is not a valid day! (monday, tuesday etc)"};
	public static String[] COMMAND_SCHEDULE_REMOVED = new String[]{"&aThe schedule for %koth% is removed."};
    public static String[] COMMAND_SCHEDULE_NOTEXIST = new String[]{"&aThis schedule doesn't exist! (Check /koth schedule list for numbers)"};
    public static String[] COMMAND_SCHEDULE_CLEARED = new String[]{"&aThe schedule list has been cleared!"};
	public static String[] COMMAND_SCHEDULE_REMOVENOID = new String[]{"&aThe ID must be a number! (Shown in /koth schedule list)"};
	public static String[] COMMAND_SCHEDULE_EMPTY = new String[]{"&aThe server owner did not schedule any Koths!"};
    public static String[] COMMAND_SCHEDULE_NOTANUMBER = new String[]{"&aThis is not a valid number!"};

	public static String[] COMMAND_SCHEDULE_LIST_CURRENTDATETIME = new String[]{"&aCurrent date: %date%"};
	public static String[] COMMAND_SCHEDULE_LIST_DAY = new String[]{"&8========> &2%day% &8<========"};
	public static String[] COMMAND_SCHEDULE_LIST_ENTRY = new String[]{"&a%koth% at %time%"};
	public static String[] COMMAND_SCHEDULE_LIST_BOTTOM = new String[]{""};
	public static String[] COMMAND_SCHEDULE_ADMIN_LIST_CURRENTDATETIME = new String[]{"&aCurrent date: %date%"};
	public static String[] COMMAND_SCHEDULE_ADMIN_LIST_DAY = new String[]{"&8========> &2%day% &8<========"};
	public static String[] COMMAND_SCHEDULE_ADMIN_LIST_ENTRY = new String[]{"&a(#%id%) %koth% at %time% with a capture time of %ct%"};
	public static String[] COMMAND_SCHEDULE_ADMIN_EMPTY = new String[]{"&aThe schedule list is currently empty!"};

    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_KOTH = new String[]{"&aYou changed the koth for #%id% to %koth"};
    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_NOVALIDDAY = new String[]{"&aThis is not a valid day! (monday, tuesday etc)"};
    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_CAPTURETIME = new String[]{"&aYou changed the runtime for #%id%!"};
    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_DAY = new String[]{"&aYou changed the day for #%id%!"};
    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_TIME = new String[]{"&aYou changed the time for #%id%!"};
    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_ENTITYTYPE = new String[]{"&aYou changed the entitytype for #%id%!"};
    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_MAXRUNTIME = new String[]{"&aYou changed the max runtime for #%id%!"};
    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_LOOTAMOUNT = new String[]{"&aYou changed the loot amount for #%id%!"};
    public static String[] COMMAND_SCHEDULE_EDITOR_CHANGE_LOOT = new String[]{"&aYou changed the loot for #%id%!"};
	
	public static String[] COMMAND_TERMINATE_SPECIFIC_KOTH = new String[]{"&aYou have terminated %koth%!"};
	public static String[] COMMAND_TERMINATE_ALL_KOTHS = new String[]{"&aYou have terminated all koths!"};

    public static String[] COMMAND_NEXT_MESSAGE = new String[]{ "&aThe next KoTH \"%koth%\" will start in: %ttn%" };
    public static String[] COMMAND_NEXT_NO_NEXT_FOUND = new String[]{ "&aThere are no scheduled KoTH's!" };

	@SuppressWarnings("unchecked")
    public static void load(JavaPlugin plugin) {
		try {

			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json").exists()) {
				save(plugin);
				return;
			}
			JSONParser parser = new JSONParser();
			//Object obj = parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json"));
			Object obj = parser.parse(new InputStreamReader(new FileInputStream(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json"), "UTF-8"));
			JSONObject jsonObject2 = (JSONObject) obj;

			Field[] fields = Lang.class.getFields();
			for (Field field : fields) {
				try {
					if (!Modifier.isStatic(field.getModifiers()))
					    continue;
					
					// This is just to check if it is able to find the JSON Object in the file //
				    String[] fieldName = field.getName().split("_", 3);
				    if(!jsonObject2.containsKey(fieldName[0]))
				        continue;
				    
				    JSONObject jsonObject3 = (JSONObject)jsonObject2.get(fieldName[0]);
				    if(!jsonObject3.containsKey(fieldName[1]))
			            continue;
				    
			        JSONObject jsonObject = (JSONObject)jsonObject3.get(fieldName[1]);
					if(!jsonObject.containsKey(fieldName[2]))
			            continue;
			        
				    Object strObj = jsonObject.get(fieldName[2]);
				    // //
				    
				    
				    if(strObj instanceof String){
                        field.set(null, new String[]{(String)strObj});
				    } else {
				        JSONArray strArray = (JSONArray)strObj;
                        field.set(null, strArray.toArray(new String[strArray.size()]));
				    }
					
			        
				    
					
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
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
	public static void save(JavaPlugin plugin) {
		try {

			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json").exists()) {
				plugin.getDataFolder().mkdirs();
				new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "lang.json").createNewFile();
			}

			JSONObject obj = new JSONObject();

			Field[] fields = Lang.class.getFields();
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers())) {
				    continue;
				}
				
			    String[] fieldName = field.getName().split("_", 3);
			    JSONObject obj2 = new JSONObject();
			    if(obj.containsKey(fieldName[0])){
			        obj2 = (JSONObject)obj.get(fieldName[0]);
			    }
			    
			    JSONObject obj3 = new JSONObject();
			    if(obj2.containsKey(fieldName[1])){
			        obj3 = (JSONObject)obj2.get(fieldName[1]);
			    }
			    
			    
			    String[] strObj = (String[])field.get(null);
			    if(strObj.length > 1){
			        JSONArray obj4 = new JSONArray();
			        String[] fieldObj = (String[])field.get(null);
			        for(String str : fieldObj){
			            obj4.add(str);
			        }
                    obj3.put(fieldName[2], obj4);
			    } else if(strObj.length == 1) {
			        obj3.put(fieldName[2], strObj[0]);
			    } else {
			        obj3.put(fieldName[2], new String[]{});
			    }
			    
			    obj2.put(fieldName[1], obj3);
			    obj.put(fieldName[0], obj2);
				
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
		catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
