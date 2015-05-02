package subside.plugins.koth;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.area.Area;
import subside.plugins.koth.area.KothHandler;
import subside.plugins.koth.area.RunningKoth;
import subside.plugins.koth.area.SingleLootChest;
import subside.plugins.koth.exceptions.AreaAlreadyRunningException;
import subside.plugins.koth.exceptions.AreaNotExistException;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.scheduler.ScheduleHandler.Day;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandHandler implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_ONLYFROMINGAME).build());
			}
			Player player = (Player) sender;
			if (args.length > 0) {
				String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
				if (args[0].equalsIgnoreCase("create") && Perm.ADMIN.has(sender)) {
					create(player, newArgs);
				} else if (args[0].equalsIgnoreCase("remove") && Perm.ADMIN.has(sender)) {
					remove(player, newArgs);
				} else if (args[0].equalsIgnoreCase("loot") && Perm.LOOT.has(sender)) {
					loot(player, newArgs);
				} else if (args[0].equalsIgnoreCase("start") && Perm.ADMIN.has(sender)) {
					start(player, newArgs);
				} else if (args[0].equalsIgnoreCase("stop") && Perm.ADMIN.has(sender)) {
					stop(player, newArgs);
				} else if (args[0].equalsIgnoreCase("end") && Perm.ADMIN.has(sender)) {
					end(player, newArgs);
				} else if (args[0].equalsIgnoreCase("schedule") && Perm.SCHEDULE.has(sender)) {
					schedule(player, newArgs);
				} else if (args[0].equalsIgnoreCase("list") && Perm.LIST.has(sender)) {
					list(player, newArgs);
				} else if (args[0].equalsIgnoreCase("info")) {
					info(player, newArgs);
				} else {
					help(player, newArgs);
				}
			} else {
				help(player, args);
			}

		}
		catch (CommandMessageException | AreaAlreadyRunningException | AreaNotExistException e) {
			Utils.msg(sender, e.getMessage());
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void loot(Player player, String[] args) {

		String ar = "";
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("set") && Perm.ADMIN.has(player)) {
				if (args.length > 1) {
					Area area = KothHandler.getArea(args[1]);
					if (area != null) {
						Block block = player.getTargetBlock(null, 5);
						if (block != null) {
							area.setLootPos(block.getLocation());
							KothLoader.save();
							throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_CHESTSET).area(args[1]).build());
						} else {
							throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_SETNOBLOCK).area(args[1]).build());
						}
					} else {
						throw new AreaNotExistException(args[0]);
					}
				} else {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_SETNOAREA).build());
				}
			}
			ar = args[0];
		} else if (ConfigHandler.getCfgHandler().getSingleLootChest()) {
			player.openInventory(SingleLootChest.getInventory());
			new MessageBuilder(Lang.COMMAND_LOOT_EXPLANATION).area("global").buildAndSend(player);
			return;
		} else {
			WeakReference<RunningKoth> koth = KothHandler.getRunningKoth();
			if (koth != null) {
				if (koth.get() != null) {
					ar = koth.get().getArea().getName();
				}
			}
			if (ar.equalsIgnoreCase("")) {
				Schedule schedule = ScheduleHandler.getNextEvent();
				if (schedule != null) {
					ar = schedule.getArea();
				}
			}
		}
		Area area = KothHandler.getArea(ar);

		if (area != null) {
			MessageBuilder msgb;
			if (args.length > 0) {
				msgb = new MessageBuilder(Lang.COMMAND_LOOT_EXPLANATION);
			} else {
				msgb = new MessageBuilder(Lang.COMMAND_LOOT_NOARGSEXPLANATION);
			}
			player.sendMessage(msgb.area(area.getName()).build());
			player.openInventory(area.getInventory());
		} else {
			throw new CommandMessageException(new MessageBuilder(Lang.AREA_NOTEXIST).area(ar).build());
		}

	}

	public void start(Player player, String[] args) {
		if (args.length > 0) {
			if (args.length > 1) {
				try {
					KothHandler.startKoth(args[0], Integer.parseInt(args[1]));
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_TRIGGERED).world(KothHandler.getArea(args[0]).getMin().getWorld().getName()).area(args[0]).build());
				}
				catch (NumberFormatException e) {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth start <name> [time]").build());
				}
			} else {
				KothHandler.startKoth(args[0]);
				throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_TRIGGERED).world(KothHandler.getArea(args[0]).getMin().getWorld().getName()).area(args[0]).build());
			}
		} else {
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth start <name> [time]").build());
		}
	}

	public void create(Player player, String[] args) {
		if (args.length > 0) {
			if (KothHandler.getArea(args[0]) == null) {
				Selection sel = Koth.getWorldEdit().getSelection(player);
				if (sel != null) {
					KothHandler.createArea(args[0], sel.getMinimumPoint(), sel.getMaximumPoint());
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_CREATED).world(KothHandler.getArea(args[0]).getMin().getWorld().getName()).area(args[0]).build());
				} else {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_SELECT).area(args[0]).build());
				}
			} else {
				throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_ALREADYEXISTS).world(KothHandler.getArea(args[0]).getMin().getWorld().getName()).area(args[0]).build());
			}
		} else {
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth create <name>").build());
		}
	}

	public void list(Player player, String[] args) {
		player.sendMessage(new MessageBuilder(Lang.COMMAND_LIST_MESSAGE).build());
		for (Area areas : KothHandler.getAvailableAreas()) {
			int posX = 0;
			int posY = 0;
			int posZ = 0;
			if (areas != null) {
				posX = Math.round((areas.getMin().getBlockX() + areas.getMax().getBlockX()) / 2);
				posY = Math.round((areas.getMin().getBlockY() + areas.getMax().getBlockY()) / 2);
				posZ = Math.round((areas.getMin().getBlockZ() + areas.getMax().getBlockZ()) / 2);
			}
			player.sendMessage(new MessageBuilder(Lang.COMMAND_LIST_ENTRY).area(areas.getName()).x(posX).y(posY).z(posZ).build());
		}
	}

	public void stop(Player player, String[] args) {
		if (args.length > 0) {
			KothHandler.stopKoth(args[0]);
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_SPECIFIC_KOTH).area(args[0]).build());
		} else {
			KothHandler.stopAllKoths();
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_ALL_KOTHS).build());
		}
	}

	public void end(Player player, String[] args) {
		if (args.length > 0) {
			KothHandler.endKoth(args[0]);
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_SPECIFIC_KOTH).area(args[0]).build());
		} else {
			KothHandler.endAllKoths();
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_ALL_KOTHS).build());
		}
	}

	public void schedule(Player player, String[] args) {
		if (args.length > 0 && Perm.ADMIN.has(player)) {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length > 3) {
					Area area = KothHandler.getArea(args[1]);
					if (area != null) {
						try {
							Day day = Day.getDay(args[2].toUpperCase());
							String time = args[3];
							int runTime = 15;
							try {
								if (args.length > 4) {
									runTime = Integer.parseInt(args[4]);
								}
							}
							catch (Exception e) {
								throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_RUNTIMEERROR).build());
							}

							ScheduleHandler.createSchedule(area.getName(), runTime, day, time);
							throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_CREATED).area(area.getName()).day(day.getDay()).time(time).length(runTime).build());

						}
						catch (IllegalArgumentException e) {
							throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOVALIDDAY).build());
						}
					} else {
						throw new CommandMessageException(new MessageBuilder(Lang.AREA_NOTEXIST).area(args[1]).build());
					}
				} else {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth schedule create <area> <day> <time> [runtime]").build());
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length > 1) {
					try {
						String area = ScheduleHandler.removeId(Integer.parseInt(args[1]));
						if (area != null) {
							throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_REMOVED).area(area).build());
						} else {
							throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTEXIST).build());
						}
					}
					catch (NumberFormatException e) {
						throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_REMOVENOID).build());
					}
					catch (IndexOutOfBoundsException e) {
						throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTEXIST).build());
					}
				} else {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth schedule remove <ID>").build());
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				ArrayList<Schedule> schedules = ScheduleHandler.getSchedules();
				ArrayList<String> list = new ArrayList<String>();
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getTimeZone()));
				list.add(" ");
				list.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_CURRENTDATETIME).date(sdf.format(new Date())).build());
				for (Day day : Day.values()) {
					ArrayList<String> subList = new ArrayList<String>();
					for (Schedule sched : schedules) {
						if (sched.getDay() == day) {
							Area area = KothHandler.getArea(sched.getArea());
							int posX = 0;
							int posY = 0;
							int posZ = 0;
							if (area != null) {
								posX = Math.round((area.getMin().getBlockX() + area.getMax().getBlockX()) / 2);
								posY = Math.round((area.getMin().getBlockY() + area.getMax().getBlockY()) / 2);
								posZ = Math.round((area.getMin().getBlockZ() + area.getMax().getBlockZ()) / 2);
							}
							subList.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_ENTRY).id(schedules.indexOf(sched)).day(day.getDay()).area(sched.getArea()).time(sched.getTime()).x(posX).y(posY).z(posZ).length(sched.getRunTime()).build());
						}
					}
					if (subList.size() > 0) {
						list.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_DAY).day(day.getDay()).build());
						list.addAll(subList);
					}
				}
				player.sendMessage(list.toArray(new String[list.size()]));
				if (schedules.size() < 1) {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_EMPTY).build());
				}
			} else {
				player.sendMessage(new String[] {
						new MessageBuilder(Lang.COMMAND_SCHEDULE_HELP_TITLE).build(),
						new MessageBuilder(Lang.COMMAND_SCHEDULE_HELP_INFO).command("/koth schedule create").commandInfo("schedule a koth").build(),
						new MessageBuilder(Lang.COMMAND_SCHEDULE_HELP_INFO).command("/koth schedule remove <ID>").commandInfo("removes an existing schedule").build(),
						new MessageBuilder(Lang.COMMAND_SCHEDULE_HELP_INFO).command("/koth schedule list").commandInfo("shows the ID's of the schedule").build()
				});
			}
		} else {
			ArrayList<Schedule> schedules = ScheduleHandler.getSchedules();
			ArrayList<String> list = new ArrayList<String>();
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getTimeZone()));
			list.add(" ");
			list.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_CURRENTDATETIME).date(sdf.format(new Date())).build());
			for (Day day : Day.values()) {
				ArrayList<String> subList = new ArrayList<String>();
				for (Schedule sched : schedules) {
					if (sched.getDay() == day) {
						Area area = KothHandler.getArea(sched.getArea());
						int posX = 0;
						int posY = 0;
						int posZ = 0;
						if (area != null) {
							posX = Math.round((area.getMin().getBlockX() + area.getMax().getBlockX()) / 2);
							posY = Math.round((area.getMin().getBlockY() + area.getMax().getBlockY()) / 2);
							posZ = Math.round((area.getMin().getBlockZ() + area.getMax().getBlockZ()) / 2);
						}
						subList.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_ENTRY).day(day.getDay()).area(sched.getArea()).time(sched.getTime()).x(posX).y(posY).z(posZ).length(sched.getRunTime()).build());
					}
				}
				if (subList.size() > 0) {
					list.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_DAY).day(day.getDay()).build());
					list.addAll(subList);
				}
			}
			if (schedules.size() < 1) {
				throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EMPTY).build());
			}
			player.sendMessage(list.toArray(new String[list.size()]));
		}
	}

	public void remove(Player player, String[] args) {
		if (args.length > 0) {
			KothHandler.removeArea(args[0]);
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_REMOVED).area(args[0]).build());
		} else {
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth remove <name>").build());
		}
	}

	public void help(Player player, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		if (Perm.ADMIN.has(player)) {
			list.add(new MessageBuilder(Lang.COMMAND_HELP_TITLE).build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth create <area>").commandInfo("creates a new koth").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth remove <area>").commandInfo("removes an existing koth").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth list").commandInfo("Shows all available koths").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth loot (set) <area>").commandInfo("Shows the available loot for an area").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth start <area>").commandInfo("Starts a koth at a certain area").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth stop [area]").commandInfo("Stops a (specific) koth").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth end [area]").commandInfo("Gracefully ends a (specific) koth").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth schedule (?)").commandInfo("Shows/Schedules a koth at a certain time").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth info").commandInfo("Shows info about this plugin").build());
		} else {
			list.add(new MessageBuilder(Lang.COMMAND_HELP_TITLE).build());
			if (Perm.LIST.has(player)) list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth list").commandInfo("Shows all available koths").build());
			if (Perm.LOOT.has(player)) list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth loot").commandInfo("Shows the loot for the upcoming koth").build());
			if (Perm.SCHEDULE.has(player)) list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth schedule").commandInfo("Shows the schedule for koths").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth info").commandInfo("Shows info about this plugin").build());
		}

		player.sendMessage(list.toArray(new String[list.size()]));
	}

	public void info(Player player, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(" ");
		list.add(new MessageBuilder("&8========> &2INFO &8<========").build());
		list.add(new MessageBuilder("&2Author: &aSubSide").build());
		list.add(new MessageBuilder("&2Version: &a" + Koth.getPlugin().getDescription().getVersion()).build());
		list.add(new MessageBuilder("&2Website: &ahttp://mcplugins.co/").build());
		player.sendMessage(list.toArray(new String[list.size()]));
	}
}
