package com.roseworld.events;

import com.rosekingdom.rosekingdom.Core.Database.Main_Statements.UserStatement;
import com.roseworld.statements.Statement;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class onDeath implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();
        int id = UserStatement.getId(player.getUniqueId());
        Location loc = player.getLocation();
        Statement.insertLocation(id, "Death№"+player.getStatistic(Statistic.DEATHS), loc, false);
    }
}
