package com.jadenPete.OpenHomes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;

public class Commands {
	// This variable as well as the class's constructor allows
	// us to access non-static methods from the Main class.
	public static Main plugin;
	
	private static FileConfiguration config;
	public static Connection connection;
	
	public Commands(Main instance){
		plugin = instance;
		config = plugin.getConfig();
	}
	
	public static void home(Player player, String[] args){
		try {
			// If the home exists, carry on with the command.
			String query = "select home from homes where uuid='" + player.getUniqueId() + "' and home='" + args[0] + "'";
			
			if(connection.prepareStatement(query).executeQuery().next()){
				// Load the home's world and dimension, coordinates, and eye position.
				query = "select * from homes where uuid='" + player.getUniqueId() + "' and home='" + args[0] + "'";
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
				
				rs.first();
				
				World homeWorld = plugin.getServer().getWorld(rs.getString("world"));
				Location homeLocation = new Location(homeWorld, rs.getDouble("x"),
																rs.getDouble("y"),
																rs.getDouble("z"));
				
				homeLocation.setYaw((float) rs.getDouble("yaw"));
				homeLocation.setPitch((float) rs.getDouble("pitch"));
				
				// Then combine it into one location variable and teleport the player to it.
				((Player) player).getPlayer().teleport(homeLocation);
				
				// Send the player a message telling them that the command completed successfully.
				player.sendMessage(config.getString("messages.set-home").replaceAll("%h", args[0]));
			} else {
				player.sendMessage(config.getString("messages.invalid-home").replaceAll("%h", args[0]));
			}
		} catch(Exception e){
			// If it doesn't work, send the player an error message.
			player.sendMessage(config.getString("messages.home-error"));
		}
	}
	
	public static void list_homes(Player player, String[] args){	
		try {
			// If the player has no homes, tell them.
			String query = "select home from homes where uuid='" + player.getUniqueId() + "'";
			
			if(connection.prepareStatement(query).executeQuery().next()){
				// List the player's homes
				query = "select home from homes where uuid='" + player.getUniqueId() + "'";
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
				
				String playerHomes = "";
				
				// Loop over every home and combine it into one list.
				for(int home = 0; rs.next(); home++){
					if(home != 0){
						playerHomes += " ";
					}
					
					playerHomes += rs.getString("home");
				}
				
				// Send the player a message telling them that the command completed successfully.
				player.sendMessage(config.getString("messages.list-homes").replaceAll("%h", playerHomes));
			} else {
				player.sendMessage(config.getString("messages.no-homes"));
			}
		} catch(Exception e){
			player.sendMessage(config.getString("messages.list-homes-error"));
		}
	}
	
	public static void set_home(Player player, String[] args){
		try {
			// If the home exists, update it. Otherwise, create it.
			String query = "select home from homes where uuid='" + player.getUniqueId() + "' and home='" + args[0] + "'";
			
			if(connection.prepareStatement(query).executeQuery().next()){
				// Update the existing record.
				query = "update homes set world=?, x=?, y=?, z=?, yaw=?, pitch=? where uuid=? and home=?";
				PreparedStatement ps = connection.prepareStatement(query);
				
				// Save the player's world and dimension, coordinates, and eye position.
				ps.setString(1, player.getLocation().getWorld().getName());
				ps.setDouble(2, player.getLocation().getX());
				ps.setDouble(3, player.getLocation().getY());
				ps.setDouble(4, player.getLocation().getZ());
				ps.setDouble(5, player.getEyeLocation().getYaw());
				ps.setDouble(6, player.getEyeLocation().getPitch());
				
				// Save the player's uuid and home name.
				ps.setString(7, player.getUniqueId().toString());
				ps.setString(8, args[0]);
				
				// Update the database after modifying it.
				ps.executeUpdate();
			} else {
				// Register a new record.
				query = "insert into homes values (?,?,?,?,?,?,?,?)";
				PreparedStatement ps = connection.prepareStatement(query);
				
				// Save the player's uuid and home name.
				ps.setString(1, player.getUniqueId().toString());
				ps.setString(2, args[0]);
				
				// Save the player's world and dimension, coordinates, and eye position.
				ps.setString(3, player.getLocation().getWorld().getName());
				ps.setDouble(4, player.getLocation().getX());
				ps.setDouble(5, player.getLocation().getY());
				ps.setDouble(6, player.getLocation().getZ());
				ps.setDouble(7, player.getEyeLocation().getYaw());
				ps.setDouble(8, player.getEyeLocation().getPitch());
				
				// Update the database after modifying it.
				ps.executeUpdate();
				
				// Send the player a message telling them that the command completed successfully.
				player.sendMessage(config.getString("messages.set-home").replaceAll("%h", args[0]));
			}
		} catch(Exception e){
			// If it doesn't work, send the player an error message.
			player.sendMessage(config.getString("messages.sethome-error"));
		}
	}
	
	public static void del_home(Player player, String[] args){
		try {
			// If the home exists, carry on with the command.
			String query = "select home from homes where uuid='" + player.getUniqueId() + "' and home='" + args[0] + "'";

			if(connection.prepareStatement(query).executeQuery().next()){
				// Delete the home record.
				query = "delete from homes where uuid='" + player.getUniqueId() + "' and home='" + args[0] + "'";
				PreparedStatement ps = connection.prepareStatement(query);
				
				// Update the database after deleting it.
				ps.executeUpdate();
				
				// Send the player a message telling them that the command completed successfully.
				player.sendMessage(config.getString("messages.del-home").replaceAll("%h", args[0]));
			} else {
				player.sendMessage(config.getString("messages.invalid-home").replaceAll("%h", args[0]));
			}
		} catch(Exception e){
			// If it doesn't work, send the player an error message.
			player.sendMessage(config.getString("messages.delhome-error"));
		}
	}
}
