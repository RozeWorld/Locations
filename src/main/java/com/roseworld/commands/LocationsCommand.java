package com.roseworld.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.roseworld.statements.Statement;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.List;

public class LocationsCommand {
    Player player;
    int id;

    @SuppressWarnings("UnstableApiUsage")
    public void register(Commands cm) {
        Message message = MessageComponentSerializer.message().serialize(Component.text("Invalid Location!"));
        cm.register(Commands.literal("locations")
                .requires(sourceStack -> {
                    if(sourceStack.getSender() instanceof Player player){
                        this.player = player;
                        this.id = Statement.getId(player.getUniqueId());
                        return true;
                    }
                    return false;
                })
                .executes(context -> {
                    player.sendMessage(Component.text("Locations commands:", TextColor.fromHexString("#ffb114")));
                    player.sendMessage(Component.text("/locations create [<name>|<coordinates> <name>|<coordinates> <world> <name>]", TextColor.fromHexString("#FFF522")));
                    player.sendMessage(Component.text("/locations delete <name>", TextColor.fromHexString("#FFF522")));
                    player.sendMessage(Component.text("/locations get <location>", TextColor.fromHexString("#FFF522")));
                    player.sendMessage(Component.text("/locations edit <location> [name|coordinates|world] <value>", TextColor.fromHexString("#FFF522")));
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("create")
                        .then(Commands.argument("Location name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "Location name");
                                    Statement.insertLocation(id, name, player.getLocation(),false);
                                    player.sendMessage(Component.text("Created " + name + " location!", TextColor.fromHexString("#6be649")));
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.argument("Coordinates", ArgumentTypes.blockPosition())
                                .then(Commands.argument("Location name", StringArgumentType.string())
                                        .executes(context -> {
                                            Location loc = context.getLastChild()
                                                    .getArgument("Coordinates", BlockPositionResolver.class)
                                                    .resolve(context.getSource())
                                                    .toLocation(player.getWorld());
                                            String name = StringArgumentType.getString(context, "Location name");
                                            Statement.insertLocation(id, name, loc,false);
                                            player.sendMessage(Component.text("Created " + name + " location!", TextColor.fromHexString("#6be649")));
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(Commands.argument("Dimension", ArgumentTypes.world())
                                        .then(Commands.argument("Location name", StringArgumentType.string())
                                                .executes(context -> {
                                                    Location loc = context.getLastChild()
                                                            .getArgument("Coordinates", BlockPositionResolver.class)
                                                            .resolve(context.getSource())
                                                            .toLocation(context.getArgument("Dimension", World.class));
                                                    String name = StringArgumentType.getString(context, "Location name");
                                                    Statement.insertLocation(id, name, loc,false);
                                                    player.sendMessage(Component.text("Created " + name + " location!", TextColor.fromHexString("#6be649")));
                                                    return Command.SINGLE_SUCCESS;
                                                })))
                        .then(Commands.argument("Location name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "Location name");
                                    Statement.insertLocation(id, name, player.getLocation(),false);
                                    player.sendMessage(Component.text("Created " + name + " location!", TextColor.fromHexString("#6be649")));
                                    return Command.SINGLE_SUCCESS;
                                }))))
                .then(Commands.literal("get")
                        .then(Commands.argument("Location", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for(String suggestion : Statement.getLocations(id)){
                                        builder.suggest(suggestion);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "Location");
                                    if(Statement.getLocations(id).contains(name)) {
                                        Location loc = Statement.getLocation(id, name);
                                        String world = null;
                                        switch (loc.getWorld().getEnvironment()) {
                                            case NORMAL -> world = "Overworld";
                                            case NETHER -> world = "Nether";
                                            case THE_END -> world = "The End";
                                        }
                                        String text = String.format("%s's coordinates: %d %d %d in %s", name, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), world);
                                        player.sendMessage(Component.text(text, TextColor.fromHexString("#6be649")));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
                                })))
                .then(Commands.literal("delete")
                        .then(Commands.argument("Location", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for(String suggestion : Statement.getLocations(id)){
                                        builder.suggest(suggestion);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "Location");
                                    if(Statement.getLocations(id).contains(name)) {
                                        Statement.deleteLocation(id, name);
                                        player.sendMessage(Component.text("Location " + name + " was deleted!", TextColor.fromHexString("#e6131d")));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
                                })))
                .then(Commands.literal("edit")
                        .then(Commands.argument("Location", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for(String suggestion : Statement.getLocations(id)){
                                        builder.suggest(suggestion);
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.literal("name")
                                        .then(Commands.argument("Location name", StringArgumentType.string())
                                                .executes(context -> {
                                                    String oldName = StringArgumentType.getString(context, "Location");
                                                    if(Statement.getLocations(id).contains(oldName)) {
                                                        String newName = StringArgumentType.getString(context, "Location name");
                                                        Statement.updateLocationName(id, oldName, newName);
                                                        player.sendMessage(Component.text("Updated location name from " + oldName + " to " + newName + "!", TextColor.fromHexString("#e6c00f")));
                                                        return Command.SINGLE_SUCCESS;
                                                    }
                                                    throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
                                                })))
                                .then(Commands.literal("coordinates")
                                        .then(Commands.argument("Coordinates", ArgumentTypes.blockPosition())
                                                .executes(context -> {
                                                    String name = StringArgumentType.getString(context, "Location");
                                                    if(Statement.getLocations(id).contains(name)) {
                                                        Location loc = context.getLastChild()
                                                                .getArgument("Coordinates", BlockPositionResolver.class)
                                                                .resolve(context.getSource())
                                                                .toLocation(player.getWorld());
                                                        Statement.updateLocationCoordinates(id, name, loc);
                                                        String text = String.format("Updated %s coordinates to %d %d %d!", name, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                                                        player.sendMessage(Component.text(text, TextColor.fromHexString("#E6C00F")));
                                                        return Command.SINGLE_SUCCESS;
                                                    }
                                                    throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
                                                })))
                                .then(Commands.literal("world")
                                        .then(Commands.argument("Dimension", ArgumentTypes.world())
                                                .executes(context -> {
                                                    String name = StringArgumentType.getString(context, "Location");
                                                    if(Statement.getLocations(id).contains(name)) {
                                                        World world = context.getArgument("Dimension", World.class);
                                                        Statement.updateLocationWorld(id, name, world);
                                                        String text = String.format("Updated %s dimension to %s!", name, world);
                                                        player.sendMessage(Component.text(text, TextColor.fromHexString("#E6C00F")));
                                                        return Command.SINGLE_SUCCESS;
                                                    }
                                                    throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
                                                })))))
                .build(),"Saves location coordinates", List.of("loc", "l"));
    }
}
