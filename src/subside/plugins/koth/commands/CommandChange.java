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
        if (args.length < 1) {
            // TODO show help menu
            return;
        }
        
        RunningKoth rKoth = KothHandler.getInstance().getRunningKoth();
        if (rKoth == null) {
            // TODO no KoTH running
            return;
        }
        
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        if (args[0].equalsIgnoreCase("points")) {
            points(sender, newArgs, rKoth);
        } else if (args[0].equalsIgnoreCase("time")) {
            time(sender, newArgs, rKoth);
        }
        // TODO add time and such
    }

    public void time(CommandSender sender, String[] args, RunningKoth rKoth) {
        if(args.length < 1){
            // TODO
            return;
        }
        // TODO
    }

    public void points(CommandSender sender, String[] args, RunningKoth rKoth) {
        if (args.length < 1) {
            // TODO
            return;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                // TODO show help menu
                return;
            }
            if (rKoth instanceof KothConquest) {
                KothConquest kothCQ = (KothConquest) rKoth;
                for (FactionScore fScore : kothCQ.getFScores()) {
                    if (fScore.getFaction().getName().equalsIgnoreCase(args[1])) {
                        fScore.setPoints(Integer.parseInt(args[2])); // TODO check integer
                    }
                }
            }
            return;
        }
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.CHANGE;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
                "change"
        };
    }

}
