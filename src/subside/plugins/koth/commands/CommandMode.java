package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.captureentities.CaptureTypeRegistry;
import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.gamemodes.GamemodeRegistry;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandMode extends AbstractCommand {

    public CommandMode(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if(args.length > 0){
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            if(args[0].equalsIgnoreCase("gamemode")){
                gameMode(sender, newArgs);
                return;
            } else if(args[0].equalsIgnoreCase("captureType")) {
                captureType(sender, newArgs);
                return;
            }
        }
        
        List<String> list = new ArrayList<>();
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH mode commands").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth mode capturetype").commandInfo("Switch between capute types").buildArray());
        list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth mode gamemode").commandInfo("Switch between gamemodes").buildArray());
        sender.sendMessage(list.toArray(new String[list.size()]));
    }
    
    public void captureType(CommandSender sender, String[] args){
        CaptureTypeRegistry cER = getPlugin().getCaptureTypeRegistry();
        if(args.length > 0){
            if(cER.getCaptureTypes().containsKey(args[0].toLowerCase())){
                cER.setPreferedClass(cER.getCaptureTypes().get(args[0].toLowerCase()));

                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_ENTITY_CHANGED).entry(args[0]));
            } else {
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_ENTITY_NOT_EXIST).entry(args[0]));
            }
        } else {
            new MessageBuilder(Lang.COMMAND_ENTITY_LIST_TITLE).buildAndSend(sender);

            for (String capEntity : cER.getCaptureTypes().keySet()) {
                new MessageBuilder(Lang.COMMAND_ENTITY_LIST_ENTRY).entry(capEntity).buildAndSend(sender);
            }
        }
    }

    public void gameMode(CommandSender sender, String[] args) {
        GamemodeRegistry gR = getPlugin().getGamemodeRegistry();
        if (args.length > 0) {
            if (gR.getGamemodes().containsKey(args[0].toLowerCase())) {
                gR.setCurrentMode(args[0]);
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_MODE_CHANGED).entry(args[0]));
            } else {
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_MODE_NOT_EXIST).entry(args[0]));
            }
        } else {
            new MessageBuilder(Lang.COMMAND_MODE_LIST_TITLE).buildAndSend(sender);

            for (String gamemode : gR.getGamemodes().keySet()) {
                new MessageBuilder(Lang.COMMAND_MODE_LIST_ENTRY).entry(gamemode).buildAndSend(sender);
            }
        }
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.MODE;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
                "mode"
        };
    }
    
    @Override
    public String getUsage() {
        return "/koth mode [mode]";
    }

    @Override
    public String getDescription() {
        return "change the gamemode";
    }

}
