package subside.plugins.koth.adapter;

import java.io.IOException;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import subside.plugins.koth.Lang;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Utils;

public class Loot {

    private @Getter Inventory inventory;
    private @Getter String name;
    
    public Loot(String name){
        inventory = Bukkit.createInventory(null, 54, getTitle(name));
        this.name = name;
    }
    
    public static String getTitle(String name){
        String title = new MessageBuilder(Lang.COMMAND_LOOT_CHEST_TITLE).loot(name).build()[0];
        if (title.length() > 32) title = title.substring(0, 32);
        return title;
    }
    
    public static String getKothLootTitle(String koth){
        String title = new MessageBuilder(Lang.KOTH_PLAYING_LOOT_CHEST).koth(koth).build()[0];
        if (title.length() > 32) title = title.substring(0, 32);
        return title;
    }
    
    public Inventory getInventory(String koth){
        Inventory inv = Bukkit.createInventory(null, 54, getKothLootTitle(koth));
        inv.setContents(inventory.getContents());
        return inv;
    }
    
    public static Loot load(JSONObject obj) throws IOException{
        Loot loot = new Loot((String)obj.get("name"));
        JSONObject lootItems = (JSONObject)obj.get("items");
        for(Object key : lootItems.keySet()){
            try {
                loot.inventory.setItem(Integer.parseInt((String)key), Utils.itemFrom64((String)lootItems.get(key)));
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return loot;
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject save(){
        JSONObject obj = new JSONObject();
        obj.put("name", this.name); // name
        if(inventory.getSize() > 0){
            JSONObject lootItems = new JSONObject();
            for (int x = 0; x < 54; x++) {
                ItemStack item = inventory.getItem(x);
                if (item != null) {
                    lootItems.put(x, Utils.itemTo64(item));
                }
            }
            obj.put("items", lootItems); // items
        }
        return obj;
    }
}
