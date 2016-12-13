package subside.plugins.koth.adapter;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class MapRotation {
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
}
