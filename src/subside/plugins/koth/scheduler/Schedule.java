package subside.plugins.koth.scheduler;

import lombok.Getter;
import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.scheduler.ScheduleHandler.Day;

public class Schedule {
	private long nextEventMillis;
	private @Getter String area;
	private @Getter int runTime;
	private @Getter Day day;
	private @Getter String time;
	private @Getter int maxRunTime;
	private @Getter int lootAmount;
	private @Getter boolean isBroadcasted = false;

	public Schedule(long nextEvent, String area, int runTime, Day day, String time, int maxRunTime, int lootAmount) {
		this.nextEventMillis = nextEvent;
		this.area = area;
		this.runTime = runTime;
		this.day = day;
		this.time = time;
		this.maxRunTime = maxRunTime;
		this.lootAmount = lootAmount;
		
	}

	public void tick() {
	    if(isBroadcasted && System.currentTimeMillis()+1000*60*30 > nextEventMillis){
	        isBroadcasted = true;
	        if(ConfigHandler.getCfgHandler().getPreBroadcast() != 0){
	            new MessageBuilder(Lang.KOTH_PRE_BROADCAST).maxTime(maxRunTime).length(runTime).lootAmount(lootAmount).area(area).buildAndBroadcast();
	        }
	    }
	    
	    if (System.currentTimeMillis() > nextEventMillis) {
			setNextEventTime();
			isBroadcasted = false;
			//TODO
			KothHandler.startKoth(area, runTime*60, maxRunTime, lootAmount, true);
		}
	}

	private void setNextEventTime() {
		nextEventMillis += 1000 * 60 * 60 * 24 * 7;
	}
	
	public long getNextEvent(){
		return nextEventMillis;
	}
}
