package subside.plugins.koth.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.modules.Lang;

public class Utils {
    static String KOTH_IGNORE_KEY = "KOTH_IGNORING";
    
    
    /** Returns if the player has enabled the ignore feature in KoTH
     * 
     * @param player the player to check
     * @return true if the player wants to ignore all messages
     */
    public static boolean isIgnoring(Player player){
        return player.hasMetadata(KOTH_IGNORE_KEY);
    }
    
    /** Toggles the ignore state of the player
     * 
     * @param player the player to toggle ignore for
     * @return true if the player is now ignoring the messages
     */
    public static boolean toggleIgnoring(KothPlugin plugin, Player player){
        if(player.hasMetadata(KOTH_IGNORE_KEY)){
            player.removeMetadata(KOTH_IGNORE_KEY, plugin);
            return false;
        } else {
            player.setMetadata(KOTH_IGNORE_KEY, new FixedMetadataValue(plugin, true));
            return true;
        }
    }
    
    
	public static void msg(CommandSender sender, String msg){
		new MessageBuilder(Lang.COMMAND_GLOBAL_PREFIX[0]+msg).buildAndSend(sender);
	}

    @SuppressWarnings("unchecked")
	public static void sendMessage(CommandSender player, boolean priority, Object ...args){
	    if(player instanceof Player && Utils.isIgnoring((Player)player) && !priority)
            return;
        
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
	
    public static void sendMsg(CommandSender player, Object... args){
	    sendMessage(player, false, args);
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
    
    public static String parseDate(long millis, KothPlugin plugin){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(plugin.getConfigHandler().getGlobal().getDateFormat());
        return sdf.format(new Date(millis));
    }
    
    public static String parseCurrentDate(KothPlugin plugin){
        return parseDate(System.currentTimeMillis() + plugin.getConfigHandler().getGlobal().getMinuteOffset()*60*1000, plugin);
    }
    
    public static Location getLocFromObject(JSONObject loc) {
        return new Location(Bukkit.getWorld((String)loc.get("world")), (int)(long)loc.get("x"), (int)(long)loc.get("y"), (int)(long)loc.get("z"));
    }
}
