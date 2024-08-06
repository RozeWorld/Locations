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
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;


public class xyzCommand {

    Player player;
    int id;

    @SuppressWarnings("UnstableApiUsage")
    public void register(Commands cm){
        Message message = MessageComponentSerializer.message().serialize(Component.text("Invalid Location!"));
        cm.register(Commands.literal("xyz")
                        .requires(sourceStack -> {
                            if(sourceStack.getSender() instanceof Player player){
                                this.player = player;
                                this.id = Statement.getId(player.getUniqueId());
                                return true;
                            }
                            return false;
                        })
                .then(Commands.argument("Player", ArgumentTypes.players())
                        .then(Commands.literal("location")
                                .then(Commands.argument("Location", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            for(String location : Statement.getLocations(id)){
                                                builder.suggest(location);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            var name = StringArgumentType.getString(context, "Location");
                                            List<Player> selector = context.getArgument("Player", PlayerSelectorArgumentResolver.class)
                                                    .resolve(context.getSource());
                                            List<String> locations = Statement.getLocations(id);
                                            if(locations.contains(name)){
                                                Location loc = Statement.getLocation(id, name);
                                                String xyz = "x: " + loc.getBlockX() + " y: " + loc.getBlockY() + " z: " + loc.getBlockZ();
                                                for(Player target : selector){
                                                target.sendMessage(Component.text(player.getName())
                                                        .append(Component.text(" shared his coordinates for " + name + ":\n"))
                                                        .append(Component.text(xyz))
                                                        .color(TextColor.fromHexString("#6be649")));
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
                                        })))
                        .executes(context -> {
                            Location loc = player.getLocation();
                            List<Player> selector = context.getArgument("Player", PlayerSelectorArgumentResolver.class)
                                    .resolve(context.getSource());
                            String xyz = "x: " + loc.getBlockX() + " y: " + loc.getBlockY() + " z: " + loc.getBlockZ();
                            for(Player target : selector){
                                target.sendMessage(Component.text(player.getName())
                                        .append(Component.text("'s current coordinates: "))
                                        .append(Component.text(xyz))
                                        .color(TextColor.fromHexString("#6be649")));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .build(),
                "Allows you to share your current and saved locations");
    }
}
