package subside.plugins.koth.scheduler;

import lombok.Getter;
import lombok.Setter;

import org.json.simple.JSONObject;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.utils.MessageBuilder;

public class Schedule {
	private long nextEventMillis;
	private @Getter @Setter String koth;
	private @Getter @Setter int runTime = 15;
	private @Getter @Setter Day day;
	private @Getter @Setter String time;
	private @Getter @Setter int maxRunTime = -1;
	private @Getter @Setter int lootAmount = -1;
	private @Getter boolean isBroadcasted = false;

	public Schedule(String koth, Day day, String time) {
		this.koth = koth;
		this.day = day;
		this.time = time;
		calculateNextEvent();
		
	}
	
	public void calculateNextEvent(){
	    long eventTime = day.getDayStart() + Day.getTime(time);

        if (eventTime < System.currentTimeMillis()) {
            eventTime += 7 * 24 * 60 * 60 * 1000;
        }
        nextEventMillis = eventTime;
	}

	public void tick() {
	    if(isBroadcasted && System.currentTimeMillis()+1000*60*30 > nextEventMillis){
	        isBroadcasted = true;
	        if(ConfigHandler.getCfgHandler().getPreBroadcast() != 0){
	            new MessageBuilder(Lang.KOTH_PLAYING_PRE_BROADCAST).maxTime(maxRunTime).length(runTime).lootAmount(lootAmount).koth(koth).buildAndBroadcast();
	        }
	    }
	    
	    if (System.currentTimeMillis() > nextEventMillis) {
			setNextEventTime();
			isBroadcasted = false;
			KothHandler.startKoth(koth, runTime*60, maxRunTime, lootAmount, true);
		}
	}

	private void setNextEventTime() {
		nextEventMillis += 1000 * 60 * 60 * 24 * 7;
	}
	
	public long getNextEvent(){
		return nextEventMillis;
	}
	
	
	public static Schedule load(JSONObject obj){
	    String tKoth = (String)obj.get("koth"); // koth
	    Day tDay = Day.getDay((String)obj.get("day")); // day
	    String tTime = (String)obj.get("time"); // time
	    Schedule schedule = new Schedule(tKoth, tDay, tTime);
	    if(obj.containsKey("runTime")){
	        schedule.setRunTime((int)obj.get("runTime")); // runTime
	    }
	    
	    if(obj.containsKey("maxRunTime")){
	        schedule.setMaxRunTime((int)obj.get("maxRunTime")); // maxRunTime
	    }
	    
	    if(obj.containsKey("lootAmount")){
	        schedule.setLootAmount((int)obj.get("lootAmount")); // lootAmount
	    }
	    
	    return schedule;
	    
	}
	
	@SuppressWarnings("unchecked")
    public JSONObject save(){
	    JSONObject obj = new JSONObject();
	    obj.put("koth", this.koth); // koth
	    obj.put("day", this.day.getDay()); // day
	    obj.put("time", this.time); // time
	    
	    if(runTime != -1){
	        obj.put("runTime", this.runTime); // runTime
	    }
	    
	    if(maxRunTime != -1){
	        obj.put("maxRunTime", this.maxRunTime); // maxRunTime
	    }
	    
	    if(lootAmount != -1){
	        obj.put("lootAmount", this.lootAmount); // lootAmount
	    }
	    
	    return obj;
	}
}
