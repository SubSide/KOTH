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
        inventory = Bukkit.createInventory(null, 54, name);
    }
    
    public Inventory getInventory(String koth){
        String title = new MessageBuilder(Lang.KOTH_PLAYING_LOOT_CHEST).koth(koth).build()[0];
        if (title.length() > 32) title = title.substring(0, 32);
        Inventory inv = Bukkit.createInventory(null, 54, title);
        inv.setContents(inventory.getContents());
        return inv;
    }
    
    public static Loot load(JSONObject obj) throws IOException{
        Loot loot = new Loot((String)obj.get("name"));
        JSONObject lootItems = (JSONObject)obj.get("items");
        for(Object key : lootItems.keySet()){
            loot.inventory.setItem((int)key, Utils.itemFrom64((String)lootItems.get(key)));
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
