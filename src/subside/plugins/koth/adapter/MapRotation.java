package subside.plugins.koth.adapter;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.utils.JSONSerializable;

public class MapRotation implements JSONSerializable<MapRotation> {
    public static @Getter MapRotation instance;
    public ArrayList<String> rotation;
    public @Getter @Setter int index;
    
    public MapRotation(ArrayList<String> rotation){
        instance = this;
        this.rotation = rotation;
    }
    
    public String getNext(){
        return this.rotation.get(index++%rotation.size());
    }

    @Override
    public MapRotation load(JSONObject obj) {
        this.index = (int)obj.get("index");
        
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject save() {
        JSONObject obj = new JSONObject();
        obj.put("index", this.index);
        
        return obj;
    }
    
}
