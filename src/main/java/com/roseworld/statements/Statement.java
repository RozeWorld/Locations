package com.roseworld.statements;

import com.rosekingdom.rosekingdom.Core.Database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Statement extends Database{

    public static int getId(UUID uuid){
        int id = 0;
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM rk_user WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            try(ResultSet result = ps.executeQuery()) {
                result.next();
                id = result.getInt("rowid");
            }
        }catch (SQLException e){
            Bukkit.getLogger().info("Non-existing or broken connection");
        }
        return id;
    }

    public static void insertLocation(int id, String name, Location location, boolean publicity){
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO rk_location(id,name,x,y,z,dim,public) VALUES(?,?,?,?,?,?,?)")){
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, location.getBlockX());
            ps.setInt(4, location.getBlockY());
            ps.setInt(5, location.getBlockZ());
            ps.setString(6, location.getWorld().getName());
            ps.setBoolean(7, publicity);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info("Trying to save data to the DB failed!");
        }
    }

    public static void updateLocationName(int id, String oldName, String newName){
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE rk_location SET name=? WHERE id=? AND name=?")) {
            ps.setString(1, newName);
            ps.setInt(2, id);
            ps.setString(3, oldName);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info("Failed to update location!");
        }
    }

    public static void updateLocationCoordinates(int id, String name, Location location){
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE rk_location SET x=? AND y=? AND z=? WHERE id=? AND name=?")) {
            ps.setInt(1, location.getBlockX());
            ps.setInt(2, location.getBlockY());
            ps.setInt(3, location.getBlockZ());
            ps.setInt(4, id);
            ps.setString(5, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info("Failed to update location!");
        }
    }

    public static void updateLocationWorld(int id, String name, World world){
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE rk_location SET world=? WHERE id=? AND name=?")) {
            ps.setString(1, world.getName());
            ps.setInt(2, id);
            ps.setString(3, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info("Failed to update location!");
        }
    }

    public static Location getLocation(int id, String name){
        int x, y, z;
        World world;
        Location location = null;
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM rk_location WHERE id=? AND name=?")) {
            ps.setInt(1, id);
            ps.setString(2, name);
            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                world = Bukkit.getWorld(rs.getString("dim"));
                x = rs.getInt("x");
                y = rs.getInt("y");
                z = rs.getInt("z");
                location = new Location(world, x, y, z);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().info("Failed fetching the location!");
        }
        return location;
    }

    public static List<String> getLocations(int id){
        List<String> locations = new ArrayList<>();
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT name FROM rk_location WHERE id=?")) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    locations.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().info("Unable to get the locations");
        }
        return locations;
    }

    public static void deleteLocation(int id,String name){
        try(Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM rk_location WHERE id=? AND name=?")){
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info("Unsuccessful deletion!");
        }
    }
}
