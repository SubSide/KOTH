package subside.plugins.koth.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.adapter.KothConquest;
import subside.plugins.koth.adapter.KothConquest.FactionScore;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.RunningKoth;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;

public class CommandChange implements ICommand {

	@Override
	public void run(CommandSender sender, String[] args) {
		if (args.length > 0) {
			String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
			if (args[0].equalsIgnoreCase("factionpoints")) {
				factionPoints(sender, newArgs);
			}
			// TODO add time and such
		} else {
			// TODO show help menu
		}
	}

	public void factionPoints(CommandSender sender, String[] args) {
		if (args.length < 3) {
			// TODO add error messages
			return;
		}

		if (args[0].equalsIgnoreCase("set")) {
			RunningKoth rKoth = KothHandler.getInstance().getRunningKoth();
			if (rKoth instanceof KothConquest) {
				KothConquest kothCQ = (KothConquest) rKoth;
				for (FactionScore fScore : kothCQ.getFScores()) {
					if (fScore.getFaction().getName().equalsIgnoreCase(args[1])) {
						fScore.setScore(Integer.parseInt(args[2])); // TODO check integer
					}
				}
			}
		}
	}

	@Override
	public IPerm getPermission() {
		return Perm.Admin.CHANGE;
	}

	@Override
	public String[] getCommands() {
		return new String[] { "change" };
	}

}
