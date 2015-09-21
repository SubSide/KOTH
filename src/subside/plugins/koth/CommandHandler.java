package subside.plugins.koth;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
			if (args.length > 0) {
				String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
				if (args[0].equalsIgnoreCase("create") && Perm.ADMIN.has(sender)) {
				    if(sender instanceof Player){
				        create((Player)sender, newArgs);
				    } else {
		                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_ONLYFROMINGAME).build());
				    }
				} else if (args[0].equalsIgnoreCase("remove") && Perm.ADMIN.has(sender)) {
					remove(sender, newArgs);
				} else if (args[0].equalsIgnoreCase("start") && Perm.ADMIN.has(sender)) {
					start(sender, newArgs);
				} else if (args[0].equalsIgnoreCase("stop") && Perm.ADMIN.has(sender)) {
					stop(sender, newArgs);
                } else if (args[0].equalsIgnoreCase("end") && Perm.ADMIN.has(sender)) {
                    end(sender, newArgs);
                } else if (args[0].equalsIgnoreCase("reload") && Perm.ADMIN.has(sender)) {
                    reload(sender, newArgs);
                } else if (args[0].equalsIgnoreCase("asmember") && Perm.ADMIN.has(sender)) {
                    help2(sender, newArgs);
				} else if (args[0].equalsIgnoreCase("schedule") && Perm.SCHEDULE.has(sender)) {
					schedule(sender, newArgs);
                } else if (args[0].equalsIgnoreCase("loot") && Perm.LOOT.has(sender)) {
                    if(sender instanceof Player){
                        loot((Player)sender, newArgs);
                    } else {
                        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_ONLYFROMINGAME).build());
                    }
				} else if (args[0].equalsIgnoreCase("list") && Perm.LIST.has(sender)) {
					list(sender, newArgs);
                } else if ((args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("version")) && Perm.INFO.has(sender)) {
                    info(sender, newArgs);
				} else if(Perm.HELP.has(sender)) {
					help(sender, newArgs);
				} else {
				    new MessageBuilder(Lang.COMMAND_NO_PERMISSION).buildAndSend(sender);
				}
			} else if(Perm.HELP.has(sender)) {
				help(sender, args);
			} else {
                new MessageBuilder(Lang.COMMAND_NO_PERMISSION).buildAndSend(sender);
            }

		}
		catch (CommandMessageException | AreaAlreadyRunningException | AreaNotExistException e) {
			Utils.msg(sender, e.getMessage());
		}
		return false;
	}

	public void reload(CommandSender sender, String[] args) {
	    Koth.getPlugin().init();
	    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_RELOAD).build());
	    
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
		} else if (ConfigHandler.getCfgHandler().isSingleLootChest()) {
			player.openInventory(SingleLootChest.getInventory());
			new MessageBuilder(Lang.COMMAND_LOOT_EXPLANATION).area("global").buildAndSend(player);
			return;
		} else {
			WeakReference<RunningKoth> koth = KothHandler.getRunningKoth();
			if (koth.get() != null) {
			    ar = koth.get().getArea().getName();
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
			msgb.area(area.getName()).buildAndSend(player);
			player.openInventory(area.getInventory());
		} else {
			throw new CommandMessageException(new MessageBuilder(Lang.AREA_NOTEXIST).area(ar).build());
		}

	}

	public void start(CommandSender sender, String[] args) {
		if (args.length > 0) {
		    String area = args[0];
		    int runTime = 15;
		    int maxRunTime = -1;
		    int amount = ConfigHandler.getCfgHandler().getLootAmount();
		    if(args.length > 1){
		        try {
		            runTime = Integer.parseInt(args[1]);
		            
		            if(args.length > 2){
		                maxRunTime = Integer.parseInt(args[2]);
		                
		                if(args.length > 3){
		                    amount = Integer.parseInt(args[3]);
		                }
		            }
		        } catch(NumberFormatException e){
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth start <name> [time] [maxRunTime] [lootAmount]").build());
		        }
		    }
		    KothHandler.startKoth(area, runTime*60, maxRunTime, amount, false);
		} else {
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth start <name> [time] [maxRunTime] [lootAmount]").build());
		}
	}

	public void create(Player player, String[] args) {
		if (args.length > 0) {
			if (KothHandler.getArea(args[0]) == null) {
				Selection sel = Koth.getWorldEdit().getSelection(player);
				if (sel != null) {
					KothHandler.createArea(args[0], sel.getMinimumPoint(), sel.getMaximumPoint());
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_CREATED).area(args[0]).build());
				} else {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_SELECT).area(args[0]).build());
				}
			} else {
				throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_ALREADYEXISTS).area(args[0]).build());
			}
		} else {
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth create <name>").build());
		}
	}

	public void list(CommandSender sender, String[] args) {
		new MessageBuilder(Lang.COMMAND_LIST_MESSAGE).buildAndSend(sender);
		for (Area areas : KothHandler.getAvailableAreas()) {
			new MessageBuilder(Lang.COMMAND_LIST_ENTRY).area(areas.getName()).buildAndSend(sender);
		}
	}

	public void stop(CommandSender sender, String[] args) {
		if (args.length > 0) {
			KothHandler.stopKoth(args[0]);
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_SPECIFIC_KOTH).area(args[0]).build());
		} else {
			KothHandler.stopAllKoths();
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_ALL_KOTHS).build());
		}
	}

	public void end(CommandSender sender, String[] args) {
		if (args.length > 0) {
			KothHandler.endKoth(args[0]);
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_SPECIFIC_KOTH).area(args[0]).build());
		} else {
			KothHandler.endAllKoths();
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_ALL_KOTHS).build());
		}
	}

	public void schedule(CommandSender sender, String[] args) {
		if (args.length > 0 && Perm.ADMIN.has(sender)) {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length > 3) {
					Area area = KothHandler.getArea(args[1]);
					if (area != null) {
					    Day day = Day.getDay(args[2].toUpperCase());
					    if(day != null){
							    
					        String time = args[3];
					        int runTime = 15;
					        int maxRunTime = -1;
					        int lootAmount = ConfigHandler.getCfgHandler().getLootAmount();
					        try {
					            if (args.length > 4) {
					                runTime = Integer.parseInt(args[4]);
					            
					                if(args.length > 5){
					                    maxRunTime = Integer.parseInt(args[5]);
					                
					                    if(args.length > 6){
					                        lootAmount = Integer.parseInt(args[6]);
					                    }
					                }
					            }
					        }
					        catch (Exception e) {
					            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_RUNTIMEERROR).build());
							}

							ScheduleHandler.createSchedule(area.getName(), runTime, day, time, maxRunTime, lootAmount);
							throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_CREATED).area(area.getName()).lootAmount(lootAmount).day(day.getDay()).time(time).length(runTime).build());

						} else {
							throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOVALIDDAY).build());
						}
					} else {
						throw new CommandMessageException(new MessageBuilder(Lang.AREA_NOTEXIST).area(args[1]).build());
					}
				} else {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth schedule create <area> <day> <time> [runtime] [maxruntime] [lootamount]").build());
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
				List<Schedule> schedules = ScheduleHandler.getSchedules();
				List<String> list = new ArrayList<String>();
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getTimeZone()));
				list.add(" ");
				list.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_CURRENTDATETIME).date(sdf.format(new Date())).build());
				for (Day day : Day.values()) {
					ArrayList<String> subList = new ArrayList<String>();
					for (Schedule sched : schedules) {
						if (sched.getDay() == day) {
							subList.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_ENTRY).id(schedules.indexOf(sched)).day(day.getDay()).maxTime(sched.getMaxRunTime()*60).area(sched.getArea()).time(sched.getTime()).length(sched.getRunTime()).build());
						}
					}
					if (subList.size() > 0) {
						list.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_DAY).day(day.getDay()).build());
						list.addAll(subList);
					}
				}
				sender.sendMessage(list.toArray(new String[list.size()]));
				if (schedules.size() < 1) {
					throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_EMPTY).build());
				}
			} else {
			    sender.sendMessage(new String[] {
						new MessageBuilder(Lang.COMMAND_SCHEDULE_HELP_TITLE).build(),
						new MessageBuilder(Lang.COMMAND_SCHEDULE_HELP_INFO).command("/koth schedule create").commandInfo("schedule a koth").build(),
						new MessageBuilder(Lang.COMMAND_SCHEDULE_HELP_INFO).command("/koth schedule remove <ID>").commandInfo("removes an existing schedule").build(),
						new MessageBuilder(Lang.COMMAND_SCHEDULE_HELP_INFO).command("/koth schedule list").commandInfo("shows the ID's of the schedule").build()
				});
			}
		} else {
		    List<Schedule> schedules = ScheduleHandler.getSchedules();
			List<String> list = new ArrayList<String>();
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getTimeZone()));
			list.add(" ");
			list.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_CURRENTDATETIME).date(sdf.format(new Date())).build());
			for (Day day : Day.values()) {
				ArrayList<String> subList = new ArrayList<String>();
				for (Schedule sched : schedules) {
					if (sched.getDay() == day) {
						subList.add(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_ENTRY).day(day.getDay()).lootAmount(sched.getLootAmount()).area(sched.getArea()).time(sched.getTime()).length(sched.getRunTime()).build());
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
			sender.sendMessage(list.toArray(new String[list.size()]));
		}
	}

	public void remove(CommandSender sender, String[] args) {
		if (args.length > 0) {
			KothHandler.removeArea(args[0]);
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_AREA_REMOVED).area(args[0]).build());
		} else {
			throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_USAGE + "/koth remove <name>").build());
		}
	}

	public void help(CommandSender sender, String[] args) {
		if (Perm.ADMIN.has(sender)) {
	        List<String> list = new ArrayList<String>();
			list.add(new MessageBuilder(Lang.COMMAND_HELP_TITLE).build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth create <area>").commandInfo("creates a new koth").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth remove <area>").commandInfo("removes an existing koth").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth list").commandInfo("Shows all available koths").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth loot (set) <area>").commandInfo("Shows the available loot for an area").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth start <area>").commandInfo("Starts a koth at a certain area").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth stop [area]").commandInfo("Stops a (specific) koth").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth end [area]").commandInfo("Gracefully ends a (specific) koth").build());
            list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth schedule (?)").commandInfo("Shows/Schedules a koth at a certain time").build());
            list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth reload").commandInfo("Reloads the plugin").build());
            list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth asmember").commandInfo("Shows the help menu as a normal player").build());
			list.add(new MessageBuilder(Lang.COMMAND_HELP_INFO).command("/koth info").commandInfo("Shows info about this plugin").build());
			sender.sendMessage(list.toArray(new String[list.size()]));
		} else {
		    help2(sender, args);
		}

	}
	
	@SuppressWarnings("deprecation")
    public void help2(CommandSender sender, String[] args){
        List<String> list = ConfigHandler.getCfgHandler().getHelpCommand();
        List<String> list2 = new ArrayList<String>();
        for(String hlp : list){
            list2.add(new MessageBuilder(hlp).area(KothAdapter.getAdapter().getName()).time(KothAdapter.getAdapter().getLengthInSeconds(), KothAdapter.getAdapter().getTotalSecondsCapped()).player(KothAdapter.getAdapter().getCapper()).build());
        }
        sender.sendMessage(list2.toArray(new String[list2.size()]));
	}

	public void info(CommandSender sender, String[] args) {
		List<String> list = new ArrayList<String>();
		list.add(" ");
		list.add(new MessageBuilder("&8========> &2INFO &8<========").build());
		list.add(new MessageBuilder("&2Author: &aSubSide").build());
		list.add(new MessageBuilder("&2Version: &a" + Koth.getPlugin().getDescription().getVersion()).build());
		list.add(new MessageBuilder("&2Site: &ahttps://www.spigotmc.org/resources/koth-king-of-the-hill.7689/").build());
		sender.sendMessage(list.toArray(new String[list.size()]));
	}
}
