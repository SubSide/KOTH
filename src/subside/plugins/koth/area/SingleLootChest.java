package subside.plugins.koth.area;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import subside.plugins.koth.Koth;
import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;

public class SingleLootChest {
	private static Inventory inventory;

	public static void load() {
		inventory = Bukkit.createInventory(null, 54, new MessageBuilder(Lang.KOTH_LOOT_CHEST).area("Global").build());
		Koth plugin = Koth.getPlugin();
		try {
			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "singleloot.json").exists()) {
				save();
				return;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "singleloot.json"));

			JSONObject loot = (JSONObject) obj;

			Set<?> keys = loot.keySet();
			for (Object key : keys) {
				try {
					inventory.setItem(Integer.parseInt(key + ""), itemFrom64((String) loot.get(key)));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		catch (Exception e) {
			System.out.println("///// LOOT CHEST NOT FOUND, EMPTY OR NOT CORRECTLY SET UP ////");

			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void save() {
		Koth plugin = Koth.getPlugin();
		try {

			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "singleloot.json").exists()) {
				plugin.getDataFolder().mkdirs();
				new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "singleloot.json").createNewFile();
			}

			JSONObject obj = new JSONObject();
			for (int x = 0; x < 54; x++) {
				ItemStack item = inventory.getItem(x);
				if (item != null) {
					obj.put(x, itemTo64(item));
				}
			}

			FileOutputStream fileStream = new FileOutputStream(new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "singleloot.json"));
			OutputStreamWriter file = new OutputStreamWriter(fileStream, "UTF-8");
			try {
				file.write(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new JsonParser().parse(obj.toJSONString())));
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

	public static Inventory getInventory(){
		return inventory;
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
			}
			finally {
				dataInput.close();
			}
		}
		catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
}
