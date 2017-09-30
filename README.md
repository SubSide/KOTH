# King of The Hill

> King of the Hill (also known as King of the Mountain or King of the Castle) is a children's game, the object of which is to stay on top of a large hill or pile (or any other designated area) as the "King of the Hill". Other players attempt to knock the current King off the pile and take their place, thus becoming the new King of the Hill.  
\- [Wikipedia](https://en.wikipedia.org/wiki/King_of_the_Hill_(game))

<br />I made a King of The Hill plugin for Minecraft that does exactly this. Players or teams of players have to try to get on top of a mountain area and need to remain on this area for a period of time to win. When they win they get rewarded with loot. Which time such an event should happen, the area where the players should stand in, the amount of time they have to capture it, which loot and how much they get, messages to display when the an event starts, get's capped, someone wins and much more, can all be done through the use of this plugin.
<br /><br />
## Features
\- Fully Configurable through ingame and the configs  
\- Stable but flexible. Doesn't just break while being very modular.  
\- Schedule KoTH's for a certain time of the day  
\- Lots of support for other plugins  
\- An API for developers  
\- And lots more  
<br />
## Documentation
A lot of the plugin is documented here:  
http://dev.thomasvdbulk.nl/KoTH/
<br /><br />
## Current supported plugins
#### Group plugins:
\- Factions  
\- FactionsOne  
\- FactionsUUID  
\- Feudal Kingdoms  
\- GangsPlus  
\- Kingdoms  
\- LegacyFactions (Special thanks to @MarkehMe for adding that)  
\- McMMO (party system)
  
Is the group plugin you use on your server not supported?   
Don't hesitate to ask me to add support for it :)


#### Other supported plugins:
\- BossBarAPI (For showing a bossbar containing the KoTH information)  
\- Essentials (for blocking people in vanish)  
\- Featherboard (Both Placeholder support and scoreboard switching support)  
\- PlaceholderAPI (For placeholder support over a huge amount of plugins)  
\- PvPManager (for blocking people with Newbie protection)  
\- VanishNoPacket (for blocking people in vanish)  
  
Same rule applies, want support for a plugin?  
Don't hesitate to ask :)<br /><br />  

## Want to contribute?
Getting the code working in your IDE can be challenging because of the amount of dependencies. A lot of dependencies are not open-source and some are even paid, so it is possible you won't be able to get everything working. All the information shown here might overwhelm you, but it looks more than it is, most of it is just heavily explained to reduce confusion. Here are some things to keep in mind when you fork/clone/download this repository:

### Dependencies:
Although my plugin is completely modular in a way that dependencies aren't intertwined with the rest of the code. Most of the time, removing a dependency is as simple as removing its class and 1 line somewhere else in the code. But with some dependencies this is not the case. Those are listed here:

#### Java 1.8+
To use the Java Lambda feature, Java 1.8 or higher is required.

#### Spigot 1.9+
Although you might not find this worthy to place here, I feel like I want to cover everything.  
  
1.9+ is needed for BossBarAPI support. As mentioned above, I have a separate plugin that adds support to the **plugin** BossBarAPI, but I **also** support the integrated API by spigot in this plugin itself. Since this was introduced in 1.9 you need 1.9 or higher for this support.  
I'm always trying to use the latest build, so I do recommend you doing that too.  
  

#### Lombok
_Tip: Lombok is the only required dependency that is not automatically imported by Maven!_  
  
My plugin HEAVILY abuses Lombok. Lombok is a plugin that allows you to create getters and setters simply by prefixing a variable with \@Getter and \@Setter. This makes my code a lot cleaner and allows for easy encapsulation. This has been my absolute friend in this project. Both Eclipse and IntelliJ has support for Lombok. If you are using a different IDE I recommend just searching a bit on the internet, high propability there is support for it somewhere.

**For installing maven on Eclipse follow these steps:**  
You can download lombok, and see how to install it for Eclipse here:  
https://projectlombok.org/

**For installing maven on IntelliJ follow these steps:**  

Installing Lombok:  
\- Preferences -> Plugins  
\- Search for "Lombok Plugin"  
\- Click Browse repositories...  
\- Choose Lombok Plugin  
\- Install  
\- Restart IntelliJ  
  
Then to enable annotation preprocessing:  
\- Preferences (Ctrl + Alt + S)  
\- Build, Execution, Deployment  
\- Compiler  
\- Annotation Processors  
\- Enable annotation processing  

#### Worldedit
Worldedit is used to select an area for the KoTH. Since almost all servers already have Worldedit I didn't feel it was a big deal using this as a dependency. Most people are familiar with it and know how to use it. So why not use it?  
Worldedit is only used for selecting the area. After you do /koth create it is saved as a Location and has nothing to do anymore with Worldedit.

### Current (soft and hard) dependencies used in the project
Here is a list of all the current dependencies used in KoTH:  
\- Essentials  
\- Factions  
\- FactionsUUID  
\- Featherboard  
\- Feudal Kingdoms  
\- GangsPlus  
\- GLib (As a dependency of Kingdoms)  
\- Kingdoms  
\- LegacyFactions  
\- Lombok (Required)  
\- MassiveCore (As a dependency of Factions)  
\- McMMO  
\- PlaceholderAPI (Through Maven)  
\- PvPManager  
\- Spigot 1.12.2 (Through maven)  
\- VanishNoPacket  
\- Worldedit (Through maven)  
  
_Small tip, whenever I re-import all jars, lots of times I have trouble with the CappingFactionsUUID. If this happens, remove the Factions dependency, wait for CappingFactionsUUID to stop showing errors, and then re-add it. Most of the time this fixes it._  
   
  
### Current state of the code
Don't be scared of the amount of code! After the refactorization of the beginning of 2017, the code has been a lot more cleaned up. If you want to change something with the code and you have something specific in mind, big chance it has been sorted for you and you can easily find it. Change something to the Gamemodes? It's in the gamemodes package. Commands? Commands package. Etc etc.  
  
The code might be badly commented, but whenever I write new code now I try to comment it so it is easily readable for everyone else.  
  
At this point this plugin is still pretty much a one-person project. Which means only 1 person is looking at the code. This means that there are only 1 kind of eyes looking at a certain problem, which can lead in maybe not-so efficient code. If you think something can be improved, don't hesitate to help me ofcourse! ^^<br />  

### Contributing
If you want to contribute something to the project start with forking the project. 
  
Add all the dependencies you can get your hands on and possibly remove the code for the dependencies you don't have.  
For example, if you want to remove VanishNoPackets as dependency, it is as simple as removing VanishHook.java from the hooks package, and removing ``registerHook(new VanishHook(this));`` from the onEnable in HookManager.  
  
Make the additions/changes you need and make a commit. Make sure you only commit the code with the needed additions. Make sure you don't commit the code that you removed for removing a dependency.  
  
And finally, make a pull-request preferably to the **Development** branch. Master is only pushed to for releases.  
<br /><br />
## For developers
If you want to add support to KoTH in some way with a custom plugin, you can use the API to do so. Here are some examples on how you would use the API.

### Using the Plugin API
```java
Plugin plugin = getServer().getPluginManager().getPlugin("KoTH");
if (plugin == null) {
  getLogger().log(Level.SEVERE, "KoTH plugin not found!");
  return;
}

KothPlugin kothPlugin = (KothPlugin)plugin;

// Then you can access the complete plugin from here, most of the getters should be self-explanatory, 
// like getScheduleHandler, getCommandHandler, getKothHandler.
// All running KoTH's are handled by the KothHandler.
KothHandler kothHandler = kothPlugin.getKothHandler();
RunningKoth rKoth = kothHandler.getRunningKoth();

if(rKoth != null){
    getLogger().log(Level.INFO, "Current KoTH that is running: " + rKoth.getKoth().getName());
} else {
    getLogger().log(Level.INFO, "There is currently no KoTH running.");
}
```

### Using the custom events
Just like normal events in Spigot, you can use the @EventHandler to listen to custom events

```java
import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothInitializeEvent;

public class EventListener implements Listener {
    
    private Plugin plugin;
    
    public EventListener(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onKothInitialize(KothInitializeEvent event){
         plugin.getLogger().log(Level.INFO, event.getRunningKoth().getKoth().getName() + " just started!");
    }
    
    @EventHandler
    public void onKothEnd(KothEndEvent event){
        plugin.getLogger().log(Level.INFO, event.getRunningKoth().getKoth().getName() + " just ended!");
    }
}
```

### Creating custom gamemodes and capturetypes
If you want to create a custom gamemode or capturetype, I recommend copying `KothClassic` for gamemodes and `CappingFactionNormal` for capturetypes as those are best commented.  
Just make sure you register them once you made it!  
  
Registering a custom gamemode:
```java
// Grabbed using the method above
KothPlugin kothPlugin = (KothPlugin)plugin;

kothPlugin.getGamemodeRegistry().register('yourcustomgamemodenamehere', YourGamemodeClass.class);
```
  
Registering a custom capturetype:
```java
// Grabbed using the method above
KothPlugin kothPlugin = (KothPlugin)plugin;

kothPlugin.getCaptureTypeRegistry().registerCaptureType("yourcustomcapturetypenamehere", YourCaptureTypeClass.class, true);
```
Make sure that if you're making a captureType that can exist of multiple players that you extend the CappingGroup class.  
Previously it was nessesary to register the Group class as well, this is no longer needed as the registerCaptureType function checks if the class you're registering is an instance of the CappingGroup.  
Also making it prefered or not is done with the registerCaptureType. If you set the last parameter to true it will automatically set that class as preferred.  
<br /><br />
## Copyright
This project is released under the LGPL license.
