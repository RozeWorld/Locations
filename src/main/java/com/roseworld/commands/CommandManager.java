package com.roseworld.commands;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

public class CommandManager {
    @SuppressWarnings("UnstableApiUsage")
    public CommandManager(Plugin plugin){
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            new LocationsCommand().register(commands);
            new xyzCommand().register(commands);
        });
    }

}
