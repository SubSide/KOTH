package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.modules.AbstractModule;
import subside.plugins.koth.modules.Lang;
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

        // Basic commands
        CommandCategory basic = registerCategory("basic", "KoTH Basic Commands");
        fallback = new CommandAsMember(basic); // This one should be used for fallback
        
        basic.addCommand(new CommandList(basic));
        basic.addCommand(fallback); // CommandAsMember
        basic.addCommand(new CommandVersion(basic));
        basic.addCommand(new CommandReload(basic));
        basic.addCommand(new CommandTp(basic));
        basic.addCommand(new CommandInfo(basic));
        basic.addCommand(new CommandNext(basic));
        basic.addCommand(new CommandIgnore(basic));
        
        if(getPlugin().getDataTable() != null){
            basic.addCommand(new CommandDatatable(basic));
            basic.addCommand(new CommandTop(basic));
        }
        
        // Control commands
        CommandCategory control = registerCategory("control", "KoTH Control Commands");
        control.addCommand(new CommandStart(control));
        control.addCommand(new CommandStop(control));
        control.addCommand(new CommandEnd(control));
        control.addCommand(new CommandMode(control));
        control.addCommand(new CommandChange(control));
        
        
        // Editor commands
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
                buildHelp(sender);
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
            buildHelp(sender);
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
            if(!fallback.getPermission().has(sender)){
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_NO_PERMISSION);
            }
            fallback.run(sender, new String[]{});
            return;
        }

        List<String> list = new ArrayList<>();
        list.add("");
        for(CommandCategory category : categories){
            List<String> categoryHelp = new ArrayList<>();
            for(AbstractCommand command : category.getCommands()){
                if(command.getPermission().has(sender)){
                    categoryHelp.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command(command.getUsage()).commandInfo(command.getDescription()).buildArray());
                }
            }
            
            if(categoryHelp.size() > 0){
                list.addAll(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title(category.getCategoryInfo()).buildArray());
                list.addAll(categoryHelp);
            }
            
            list.add("");
        }
        sender.sendMessage(list.toArray(new String[list.size()]));
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
