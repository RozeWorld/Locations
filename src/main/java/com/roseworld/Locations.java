package com.roseworld;

import com.google.common.collect.Range;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.roseworld.database.Database;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.range.IntegerRangeProvider;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Locations extends JavaPlugin {


    @Override
    public void onEnable() {
        PluginManager manager = getServer().getPluginManager();
        FileConfiguration config = manager.getPlugin("RoseKingdom").getConfig();
        new Database(config);
        new LocationsCommand(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
