package com.roseworld;

import com.roseworld.commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Locations extends JavaPlugin {
    @Override
    public void onEnable() {
        new CommandManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
