package subside.plugins.koth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import subside.plugins.koth.adapter.Area;
import subside.plugins.koth.adapter.KothHandler;

public class KothLoader {
	
	public static void load() {
		KothPlugin plugin = KothPlugin.getPlugin();
		try {
			KothHandler.getAvailableAreas().clear();
			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "koths.json").exists()) {
				save();
				return;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "koths.json"));
			if(obj instanceof JSONArray){
				JSONArray areas = (JSONArray) obj;
				
				Iterator<?> it = areas.iterator();
				while(it.hasNext()){
					JSONObject ar = (JSONObject)it.next();
					try {
						Area area = new Area((String)ar.get("name"), getLocFromObject((JSONObject)ar.get("min")), getLocFromObject((JSONObject)ar.get("max")));
						try {
							if(ar.get("loot") != null){
								Inventory inv = Bukkit.createInventory(null, 54, new MessageBuilder(Lang.KOTH_LOOT_CHEST).area((String)ar.get("name")).build());
								JSONObject loot = (JSONObject)ar.get("loot");
								if(loot.containsKey("pos")){
									area.setLootPos(getLocFromObject((JSONObject)loot.get("pos")));
								}
								if(loot.containsKey("items")){
									JSONObject lootItems = (JSONObject)loot.get("items");
									Set<?> keys = lootItems.keySet();
									for(Object key : keys){
										try {
											inv.setItem(Integer.parseInt(key+""), itemFrom64((String)lootItems.get(key)));
										} catch(Exception e){
											e.printStackTrace();
										}
									}
									area.setInventory(inv);
								}
							}
							if(ar.get("lastWinner") != null){
								area.setLastWinner((String)ar.get("lastWinner"));
							}
						} catch(Exception e){
							e.printStackTrace();
						}
						
						
						KothHandler.getAvailableAreas().add(area);
					} catch(Exception e){
					    KothPlugin.getPlugin().getLogger().severe("////////////////");
					    KothPlugin.getPlugin().getLogger().severe("Error loading koth: "+ar.get("name"));
					    KothPlugin.getPlugin().getLogger().severe("////////////////");
						e.printStackTrace();
					}
				}
			}
			
			if(ConfigHandler.getCfgHandler().isSingleLootChest()){
				try {
					SingleLootChest.load();
				} catch(Exception e){
					e.printStackTrace();
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
			for (Area area : KothHandler.getAvailableAreas()) {
				JSONObject ar = new JSONObject();
				ar.put("name", area.getName());
				ar.put("min", createLocObject(area.getMin()));
				ar.put("max", createLocObject(area.getMax()));
				if (area.getLootPos() != null || area.getInventory().getSize() > 0) {
					JSONObject loot = new JSONObject();
					if(area.getLootPos() != null){
						loot.put("pos", createLocObject(area.getLootPos()));
					}
					if(area.getInventory().getSize() > 0){
						JSONObject lootItems = new JSONObject();
						for (int x = 0; x < 54; x++) {
							ItemStack item = area.getInventory().getItem(x);
							if (item != null) {
								lootItems.put(x, itemTo64(item));
							}
						}
						loot.put("items", lootItems);
					}
					
					ar.put("loot", loot);
				}
				
				if(area.getLastWinner() != null && !area.getLastWinner().equals("")){
					ar.put("lastWinner", area.getLastWinner());
				}
				
				
				obj.add(ar);
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
			

			
			if(ConfigHandler.getCfgHandler().isSingleLootChest()){
				try {
					SingleLootChest.save();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String itemTo64(ItemStack stack) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(stack);

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to save item stack.", e);
		}
	}
	
	private static ItemStack itemFrom64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			try {
				return (ItemStack) dataInput.readObject();
			} finally {
				dataInput.close();
			}
		}
		catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
	

	@SuppressWarnings("unchecked")
	private static JSONObject createLocObject(Location loc) {
		JSONObject obj = new JSONObject();
		obj.put("x", loc.getBlockX());
		obj.put("y", loc.getBlockY());
		obj.put("z", loc.getBlockZ());
		obj.put("world", loc.getWorld().getName());
		return obj;
	}
	
	private static Location getLocFromObject(JSONObject loc) {
		return new Location(Bukkit.getWorld((String)loc.get("world")), (int)(long)loc.get("x"), (int)(long)loc.get("y"), (int)(long)loc.get("z"));
	}
}
