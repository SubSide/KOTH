package subside.plugins.koth.scheduler;

import java.util.List;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.utils.JSONSerializable;

public class MapRotation implements JSONSerializable<MapRotation> {
    
    public List<String> rotation;
    public @Getter @Setter int index;
    
    public MapRotation(List<String> rotation){
        this.rotation = rotation;
    }
    
    public String getNext(){
        return this.rotation.get(index++%rotation.size());
    }

    @Override
    public MapRotation load(JSONObject obj) {
        this.index = (int)(long)obj.get("index");
        
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
