package subside.plugins.koth.loot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.JSONSerializable;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Utils;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class Loot implements JSONSerializable<Loot> {

    private @Getter Inventory inventory;
    private @Getter String name;
    private @Getter List<String> commands;
    
    private @Getter LootHandler lootHandler;
    
    public Loot(LootHandler lootHandler){
        this.lootHandler = lootHandler;
        commands = new ArrayList<>();
        inventory = Bukkit.createInventory(null, 54, "Loot chest!");
    }
    
    public Loot(LootHandler lootHandler, String name){
        this(lootHandler);
        setName(name);
    }
    
    public Loot(LootHandler lootHandler, String name, List<String> commands){
        this(lootHandler, name);
        this.commands = commands;
    }
    
    public void setName(String title){
        this.name = title;
        Inventory newInv = Bukkit.createInventory(null, 54, createTitle(name));
        if(this.inventory != null){
            for(int i = 0; i < this.inventory.getContents().length; i++){
                newInv.setItem(i, this.inventory.getContents()[i]);
            }
        }
        
        this.inventory = newInv;
    }

    /** Get the title by the loot name
     * 
     * @param name      The name of the loot
     * @return          The marked-up title
     */
    public static String createTitle(String name){
        name = name == null ? "" : name;
        String title = new MessageBuilder(Lang.COMMAND_LOOT_CHEST_TITLE).loot(name).build()[0];
        if (title.length() > 32) title = title.substring(0, 32);
        return title;
    }
    
    public void triggerCommands(Koth koth, Capper<?> capper){
        if(!lootHandler.getPlugin().getConfigHandler().getLoot().isCmdEnabled()){
            return;
        }
        
        if(capper == null){
            return;
        }
        
        for(String command : commands){
            List<Player> players = new ArrayList<>(capper.getAvailablePlayers(koth));
            if(command.contains("%player%")){
                for(Player player : players){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", player.getName()));
                }
            } else if(command.contains("%faction%")){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%faction%", capper.getName()));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }
  
    public Loot load(JSONObject obj) {
        this.name = (String)obj.get("name");
        this.setName(this.name);
        
        JSONObject lootItems = (JSONObject)obj.get("items");
        for(Object key : lootItems.keySet()){
            try {
                this.inventory.setItem(Integer.parseInt((String)key), Utils.itemFrom64((String)lootItems.get(key)));
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        
        if(obj.containsKey("commands")){
            JSONArray commands = (JSONArray)obj.get("commands");
            Iterator<?> it = commands.iterator();
            while(it.hasNext()){
                try {
                    this.commands.add((String)it.next());
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        
        
        return this;
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
        
        if(commands.size() > 0){
            JSONArray commandz = new JSONArray();
            commandz.addAll(commands);
            
            obj.put("commands", commandz); // commands
        }
        
        return obj;
    }
}
