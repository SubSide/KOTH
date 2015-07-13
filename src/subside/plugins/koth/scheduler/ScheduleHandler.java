package subside.plugins.koth.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Koth;
import subside.plugins.koth.Utils;
import subside.plugins.koth.area.Area;

public class ScheduleHandler {
	static ArrayList<Schedule> schedules = new ArrayList<Schedule>();
	static long startOfWeek;

	public static Schedule getNextEvent() {
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

	public static Schedule getNextEvent(Area area) {
		Schedule ret = null;
		for (Schedule sched : schedules) {
			if (sched.getArea().equalsIgnoreCase(area.getName())) {
				if (ret == null) {
					ret = sched;
				} else if (sched.getNextEvent() < ret.getNextEvent()) {
					ret = sched;
				}
			}
		}
		return ret;
	}

	public static String removeId(int id) {
		Schedule sched = schedules.get(id);
		if (sched != null) {
			schedules.remove(id);
			save();
			return sched.getArea();
		} else {
			return null;
		}
	}

	public static List<Schedule> getSchedules() {
		return schedules;
	}

	public static void tick() {
		for (Schedule schedule : schedules) {
			schedule.tick();
		}
	}

	public static void createSchedule(String area, int runTime, Day day, String time, int maxRunTime) {
		long eventTime = day.getDayStart() + getTime(time);

		if (eventTime < System.currentTimeMillis()) {
			eventTime += 7 * 24 * 60 * 60 * 1000;
		}
		schedules.add(new Schedule(eventTime, area, runTime, day, time, maxRunTime));
		save();
	}

	public static void setupStartWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getTimeZone()));
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);

		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

		startOfWeek = calendar.getTimeInMillis();
	}

	public static void load() {
		setupStartWeek();
		try {
			if (!new File(Koth.getPlugin().getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json").exists()) {
				save();
				return;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(Koth.getPlugin().getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json"));

			schedules.clear();

			JSONObject object = (JSONObject) obj;

			for (Day day : Day.values()) {
				if (object.containsKey(day.getDay())) {
					readDay((JSONObject) object.get(day.getDay()), day);
				}
			}
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public static void readDay(JSONObject obj, Day day) {
		Set<?> set = obj.keySet();
		for (Object ob : set) {
			JSONObject sched = (JSONObject) obj.get(ob);
			long eventTime = day.getDayStart() + getTime((String) sched.get("time"));

			if (eventTime < System.currentTimeMillis()) {
				eventTime += 7 * 24 * 60 * 60 * 1000;
			}
			
			int maxRunTime = -1;
			if(sched.containsKey("maxruntime")){
			    maxRunTime = Integer.parseInt(sched.get("maxruntime")+"");
			}

			schedules.add(new Schedule(eventTime, (String) sched.get("area"), Integer.parseInt(sched.get("runtime") + ""), day, (String) sched.get("time"), maxRunTime));
		}
	}

	@SuppressWarnings("unchecked")
	public static void save() {
		Koth plugin = Koth.getPlugin();
		try {

			if (!new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json").exists()) {
				plugin.getDataFolder().mkdirs();
				new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json").createNewFile();
			}

			JSONObject obj = new JSONObject();
			for (Day day : Day.values()) {
				JSONObject dayObj = new JSONObject();
				int x = 1;
				boolean shouldAdd = false;
				for (Schedule sched : schedules) {
					if (sched.getDay() == day) {
						JSONObject sch = new JSONObject();
						sch.put("time", sched.getTime());
						sch.put("area", sched.getArea());
						sch.put("runtime", sched.getRunTime());
						dayObj.put(x, sch);
						shouldAdd = true;
						x++;
					}
				}

				if (shouldAdd) {
					obj.put(day.getDay(), dayObj);
				}

			}
			FileOutputStream fileStream = new FileOutputStream(new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "schedule.json"));
			OutputStreamWriter file = new OutputStreamWriter(fileStream, "UTF-8");
			try {
				file.write(Utils.getGson(obj.toJSONString()));
			}
			catch (IOException e) {
				e.printStackTrace();

			}
			finally {
				file.flush();
				file.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static long getTime(String time) {
		int hours = 0;
		int minutes = 0;
		if (time.contains(":")) {
			String[] timz = time.split(":");
			try {
				hours = Integer.parseInt(timz[0]);
				minutes = Integer.parseInt(timz[1].replaceAll("[a-zA-Z]", ""));
			}
			catch (Exception e) {}
		} else {
			try {
				hours = Integer.parseInt(time.replaceAll("[a-zA-Z]", ""));
			}
			catch (Exception e) {}
		}

		if (time.endsWith("PM") && hours % 12 != 0) {
			hours += 12;
		}
		return hours * 60 * 60 * 1000 + minutes * 60 * 1000;
	}

	public enum Day {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

		public String getDay() {
			switch (this) {
				case MONDAY:
					return "Monday";
				case TUESDAY:
					return "Tuesday";
				case WEDNESDAY:
					return "Wednesday";
				case THURSDAY:
					return "Thursday";
				case FRIDAY:
					return "Friday";
				case SATURDAY:
					return "Saturday";
				case SUNDAY:
					return "Sunday";
			}
			return null;
		}

		public static Day getDay(String str) {
			if (str.equalsIgnoreCase("monday")) {
				return MONDAY;
			} else if (str.equalsIgnoreCase("tuesday")) {
				return TUESDAY;
			} else if (str.equalsIgnoreCase("wednesday")) {
				return WEDNESDAY;
			} else if (str.equalsIgnoreCase("thursday")) {
				return THURSDAY;
			} else if (str.equalsIgnoreCase("friday")) {
				return FRIDAY;
			} else if (str.equalsIgnoreCase("saturday")) {
				return SATURDAY;
			} else if (str.equalsIgnoreCase("sunday")) {
				return SUNDAY;
			}
			return null;
		}

		public long getDayStart() {
			return startOfWeek + (this.ordinal() * 24 * 60 * 60 * 1000);
		}
	}
}
