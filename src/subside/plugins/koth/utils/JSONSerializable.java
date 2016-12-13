package subside.plugins.koth.utils;

import org.json.simple.JSONObject;

public interface JSONSerializable<E> {
    public E load(JSONObject obj);
    public JSONObject save();
}
