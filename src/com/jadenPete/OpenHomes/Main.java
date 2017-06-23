package com.jadenPete.OpenHomes;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;

public class Main extends JavaPlugin {
	FileConfiguration config = getConfig();
	
	// Fired when plugin is first enabled
	@Override
	public void onEnable() {
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
	}
	
	// Fired when plugin is disabled
	@Override
	public void onDisable() {
	
	}
	
	// Sends a message to command executor
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player player = getServer().getPlayer(sender.getName());
			String playerUUID = "players." + player.getUniqueId().toString();
			String playerHomelist = playerUUID + ".homelist";
			String currentHome = null;
			
			if(args.length == 1){
				currentHome = playerUUID + ".homes." + args[0];
			}
			
			switch(cmd.getName().toLowerCase()){
				case "home":{
					if(args.length == 0){						
						if(!config.contains(playerHomelist) ||
							config.getStringList(playerHomelist).size() == 0){
							sender.sendMessage(config.getString("messages.no-homes"));
						} else {
							sender.sendMessage(config.getString("messages.list-homes").replaceAll("%h", 
											   String.join(" ", config.getStringList(playerHomelist))));
						}
					} else if(args.length == 1){						
						if(config.contains(currentHome)){
							World homeWorld = getServer().getWorld(config.getString(currentHome + ".world"));
							Location homeLocation = new Location(homeWorld, config.getDouble(currentHome + ".x"),
																			config.getDouble(currentHome + ".y"),
																			config.getDouble(currentHome + ".z"));
							
							homeLocation.setYaw((float) config.getDouble(currentHome + ".yaw"));
							homeLocation.setPitch((float) config.getDouble(currentHome + ".pitch"));
							
							((Player) sender).getPlayer().teleport(homeLocation);
						} else {
							sender.sendMessage(config.getString("messages.invalid-home").replaceAll("%h", args[0]));
						}
					} else {
						return false;
					}
					
					break;
				}
				
				case "sethome":{
					if(args.length == 1){
						if(StringUtils.isAlphanumeric(args[0])){
							List<String> currentHomelist = config.getStringList(playerHomelist);
							
							if(config.contains(playerHomelist)){							
								if(!currentHomelist.contains(args[0])){
									currentHomelist.add(args[0]);
									
									java.util.Collections.sort(currentHomelist);
								}
							} else {
								currentHomelist.add(args[0]);
							}
							
							config.set(playerHomelist, currentHomelist);

							config.set(currentHome + ".world", player.getWorld().getName());
							config.set(currentHome + ".x", player.getLocation().getX());
							config.set(currentHome + ".y", player.getLocation().getY());
							config.set(currentHome + ".z", player.getLocation().getZ());
							config.set(currentHome + ".yaw", player.getEyeLocation().getYaw());
							config.set(currentHome + ".pitch", player.getEyeLocation().getPitch());
							
							sender.sendMessage(config.getString("messages.set-home").replaceAll("%h", args[0]));
						} else {
							sender.sendMessage(config.getString("messages.invalid-name"));
						}
					} else {
						return false;
					}
					
					break;
				}
				
				case "delhome":{
					if(args.length == 1){
						if(config.contains(currentHome)){
							List<String> currentHomelist = config.getStringList(playerHomelist);
							int currentHomeLocation = -1;
							
							for(String homeName : currentHomelist){
								currentHomeLocation++;
								
								if(homeName == args[0]){
									break;
								}
							}
							
							currentHomelist.remove(currentHomeLocation);
							
							config.set(playerHomelist, currentHomelist);
							config.set(currentHome, null);
							
							sender.sendMessage(config.getString("messages.del-home").replaceAll("%h", args[0]));
						} else {
							sender.sendMessage(config.getString("messages.invalid-home").replaceAll("%h", args[0]));
						}
					} else {
						return false;
					}
					
					break;
				}
			}
		} else {
			sender.sendMessage(config.getString("messages.non-player"));
		}
		
		this.saveConfig();
			
		return true;
	}
}
