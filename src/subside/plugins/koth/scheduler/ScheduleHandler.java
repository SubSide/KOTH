package subside.plugins.koth.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.modules.AbstractModule;
import subside.plugins.koth.utils.JSONLoader;
import subside.plugins.koth.utils.MessageBuilder;

public class ScheduleHandler extends AbstractModule {
    private @Getter List<Schedule> schedules;
    private @Getter MapRotation mapRotation;
    
    public ScheduleHandler(KothPlugin plugin){
        super(plugin);
        schedules = new ArrayList<>();
        mapRotation = new MapRotation(plugin.getConfigHandler().getKoth().getMapRotation());
    }

    public Schedule getNextEvent() {
        Schedule ret = null;
        for (Schedule sched : schedules) {
            if (ret == null) {
                ret = sched;
            } else if (sched.getNextEvent() < ret.getNextEvent()) {
                ret = sched;
            }
        }
        return ret;
    }

    public Schedule getNextEvent(Koth koth) {
        Schedule ret = null;
        for (Schedule sched : schedules) {
            if (sched.getKoth().equalsIgnoreCase(koth.getName())) {
                if (ret == null) {
                    ret = sched;
                } else if (sched.getNextEvent() < ret.getNextEvent()) {
                    ret = sched;
                }
            }
        }
        return ret;
    }

    public String removeId(int id) {
        if (schedules.get(id) == null) {
            return null;
        }
        String koth = schedules.get(id).getKoth();

        schedules.remove(id);
        saveSchedules();
        return koth;
    }

    public void tick() {
        for (Schedule schedule : schedules) {
            schedule.tick();
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        schedules = new ArrayList<>();
        
        Object object = new JSONLoader(plugin, "schedule.json").load();
        
        if(object == null || !(object instanceof JSONObject))
            return;
        
            
        JSONObject obj = (JSONObject)object;
        Set<Object> dayz = ((JSONObject) obj).keySet();
        for (Object dai : dayz) {
            Day day = Day.getDay((String) dai);
            
            JSONArray koths = (JSONArray) obj.get(dai);

            Iterator<?> it = koths.iterator();
            while (it.hasNext()) {
                try {
                    Schedule schedule = new Schedule(this);
                    JSONObject schedObj = (JSONObject) it.next();
                    schedObj.put("day", day.getDay());
                    schedule.load((JSONObject) schedObj);
                    
                    schedules.add(schedule);
                }
                catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "////////////////\nError loading Schedule!\n////////////////", e);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        saveSchedules();
    }

    @SuppressWarnings("unchecked")
    public void saveSchedules(){
        JSONObject obj = new JSONObject();
        
        for (Schedule schedule : schedules) {
            JSONArray obj2 = new JSONArray();
            if(obj.containsKey(schedule.getDay().getDay())){
                obj2 = (JSONArray)obj.get(schedule.getDay().getDay());
            }
            obj2.add(schedule.save());
            obj.put(schedule.getDay().getDay(), obj2);
        }
        new JSONLoader(plugin, "schedule.json").save(obj);
    }
}
