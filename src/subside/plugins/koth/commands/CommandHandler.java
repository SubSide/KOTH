package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.Getter;
import subside.plugins.koth.AbstractModule;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandHandler extends AbstractModule implements CommandExecutor {

    private @Getter List<CommandCategory> categories;
    private CommandAsMember fallback;

    public CommandHandler(KothPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad(){
        this.categories = new ArrayList<>();

        CommandCategory basic = registerCategory("basic", "KoTH Basic Commands");
        fallback = new CommandAsMember(basic); // This one should be used for fallback
        
        basic.addCommand(new CommandList(basic));
        basic.addCommand(fallback);
        basic.addCommand(new CommandVersion(basic));
        basic.addCommand(new CommandReload(basic));
        basic.addCommand(new CommandTp(basic));
        basic.addCommand(new CommandInfo(basic));
        basic.addCommand(new CommandNext(basic));
        basic.addCommand(new CommandIgnore(basic));
        
        CommandCategory control = registerCategory("control", "KoTH Control Commands");
        control.addCommand(new CommandStart(control));
        control.addCommand(new CommandStop(control));
        control.addCommand(new CommandEnd(control));
        control.addCommand(new CommandMode(control));
        control.addCommand(new CommandChange(control));
        
        
        CommandCategory editor = registerCategory("editor", "Koth Editor Commands");
        editor.addCommand(new CommandCreate(editor));
        editor.addCommand(new CommandRemove(editor));
        editor.addCommand(new CommandEdit(editor));
        editor.addCommand(new CommandLoot(editor));
        editor.addCommand(new CommandSchedule(editor));
    }
    
    @Override
    public void onEnable(){
        // Register the class to the command
        plugin.getCommand("koth").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        try {
            if(args.length < 1){
                if(fallback.getPermission().has(sender)){
                    fallback.run(sender, args);
                } else {
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_NO_PERMISSION);
                }
                return true;
            }
            
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            for (CommandCategory category : categories) {
                for(AbstractCommand command : category.getCommands()) {
                    for (String com : command.getCommands()) {
                        if (!com.equalsIgnoreCase(args[0])) {
                            continue;
                        }
        
                        if (command.getPermission().has(sender)) {
                            command.run(sender, newArgs);
                            return true;
                        }
        
                    }
                }
            }
    
            if(fallback.getPermission().has(sender)){
                fallback.run(sender, newArgs);
            } else {
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_NO_PERMISSION);
            }
        } catch(CommandMessageException e){
            Utils.sendMessage(sender, true, (Object[])e.getMsg());
        }

        return true;
    }
    
    public CommandCategory registerCategory(String categoryName, String categoryInfo){
        CommandCategory category = new CommandCategory(this, categoryName, categoryInfo);
        categories.add(category);
        
        return category;
    }
    
    public CommandCategory getCategory(String categoryName){
        for(CommandCategory category : categories){
            if(category.getCategoryName().equalsIgnoreCase(categoryName))
                return category;
        }
        
        return null;
    }
    
    public void buildHelp(CommandSender sender){
        if (!Perm.Admin.HELP.has(sender)) {
            fallback.run(sender, new String[]{});
            return;
        }

        List<String> list = new ArrayList<>();
        list.add("");
        for(CommandCategory category : categories){
            list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title(category.getCategoryInfo()).buildArray());
            for(AbstractCommand command : category.getCommands()){
                list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command(command.getUsage()).commandInfo(command.getDescription()).buildArray());
            }
            list.add("");
        }
        sender.sendMessage(list.toArray(new String[list.size()]));
    }
    
    public void helpAsMember(CommandSender sender){
        List<String> list = plugin.getConfigHandler().getGlobal().getHelpCommand();
        List<String> list2 = new ArrayList<>();
        for (String hlp : list) {
            MessageBuilder mB = new MessageBuilder(hlp);
            try {
                mB.koth(plugin.getKothHandler().getRunningKoth().getKoth());
                mB.time(plugin.getKothHandler().getRunningKoth().getTimeObject());
            }
            catch (Exception e) {
                mB.koth("None").time("00:00").capper("None");
            }
            list2.addAll(mB.buildArray());
        }
        sender.sendMessage(list2.toArray(new String[list2.size()]));
    }
    
    public class CommandCategory {
        private @Getter String categoryName;
        private @Getter String categoryInfo;
        private @Getter List<AbstractCommand> commands;
        private @Getter CommandHandler commandHandler;
        
        public CommandCategory(CommandHandler commandHandler, String categoryName, String categoryInfo){
            commands = new ArrayList<>();

            this.commandHandler = commandHandler;
            this.categoryName = categoryName;
            this.categoryInfo = categoryInfo;
        }
        
        public void addCommand(AbstractCommand command){
            commands.add(command);
        }
    }

}
