package subside.plugins.koth.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import subside.plugins.koth.Lang;

public class Utils {
	public static void msg(CommandSender sender, String msg){
		new MessageBuilder(Lang.COMMAND_GLOBAL_PREFIX+msg).buildAndSend(sender);
	}
	
	public static String getGson(String str){
		try {
			return new com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new com.google.gson.JsonParser().parse(str));
		} catch(NoClassDefFoundError e){
			return new org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new org.bukkit.craftbukkit.libs.com.google.gson.JsonParser().parse(str));
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
    public static void sendMsg(CommandSender player, Object... args){
	    for(Object obj : args){
	        if(obj instanceof String){
                player.sendMessage((String)obj);
            } else if(obj instanceof String[]){
	            player.sendMessage((String[])obj);
	        } else if(obj instanceof List<?>){
	            for(String str : ((List<String>)obj)){
	                player.sendMessage(str);
	            }
	        }
	    }
	}


	public static String itemTo64(ItemStack stack) throws IllegalStateException {
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
    
    public static ItemStack itemFrom64(String data) throws IOException {
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
    public static JSONObject createLocObject(Location loc) {
        JSONObject obj = new JSONObject();
        obj.put("x", loc.getBlockX());
        obj.put("y", loc.getBlockY());
        obj.put("z", loc.getBlockZ());
        obj.put("world", loc.getWorld().getName());
        return obj;
    }
    
    public static int convertTime(String time){
        int t = 0;
        if(time.contains(":")){
            String[] split = time.split(":");
            if(split.length > 2){
                try {
                    t = Integer.parseInt(split[0])*60*60+Integer.parseInt(split[1])*60+Integer.parseInt(split[2]);
                } catch(Exception e){
                }
            } else {
                try {
                    t = Integer.parseInt(split[0])*60+Integer.parseInt(split[1]);
                } catch(Exception e){
                }
            }
        } else {
            try {
                t = Integer.parseInt(time)*60;
            } catch(Exception e){
                
            }
        }
        
        return t;
    }
    
    public static Location getLocFromObject(JSONObject loc) {
        return new Location(Bukkit.getWorld((String)loc.get("world")), (int)(long)loc.get("x"), (int)(long)loc.get("y"), (int)(long)loc.get("z"));
    }
}
