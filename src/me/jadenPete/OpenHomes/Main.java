package me.jadenPete.OpenHomes;

import java.util.regex.Pattern;
import java.sql.DriverManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class Main extends JavaPlugin {
	private FileConfiguration config = getConfig();
	
	// Fired when plugin is first enabled.
	@Override
	public void onEnable(){
		// If the configuration file doesn't exist, copy the default one.
		saveDefaultConfig();
		
		// Run the Commands class constructor, which uses an
		// instance of the main class to access non-static methods.
		new Commands(this);
		
		getConnection();
	}
	
	// Fired when plugin is disabled.
	@Override
	public void onDisable(){
		closeConnection();
	}
	
	// Connect to the MySQL Database.
	public void getConnection(){
		String username = "jadenpete";
		String password = "password";
		
		String url = "jdbc:mysql://localhost/openhomes";
		
		try {
			Commands.connection = DriverManager.getConnection(url, username, password);
		} catch(Exception e){
			System.out.println("Error connecting to the OpenHomes database.");
		}
	}
	
	// Disconnect from the MySQL Database.
	public void closeConnection(){
		try {
			Commands.connection.close();
		} catch(Exception e){
			System.out.println("Error disconnecting from the OpenHomes database.");
		}
	}
	
	// Parses the various plugin commands.
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		// Check that the command is being executed by a player,
		// and not the console, another plugin, or a command block.
		if(sender instanceof Player){
			// Variable for the player who executed the command,
			Player player = getServer().getPlayer(sender.getName());
			
			// Parse the command on a case-insensitive level.
			switch(cmd.getName().toLowerCase()){
				case "home":{
					// If no home is specified, list the player's homes.
					// Otherwise teleport the player to the specified home.
					if(args.length == 1){	
						Commands.home(player, args);
					} else if(args.length == 0){
						Commands.list_homes(player, args);
					} else {
						return false;
					}
					
					break;
				}
				
				case "sethome":{
					// If a home is specified, carry on with the command.
					if(args.length == 1){
						// Make sure that the home only contains letters, numbers, or underscores.						
						if(Pattern.compile("^[a-zA-Z0-9_]*$").matcher(args[0]).matches()){
							Commands.set_home(player, args);
						} else {
							sender.sendMessage(config.getString("messages.invalid-name"));
						}
					} else {
						return false;
					}
					
					break;
				}
				
				case "delhome":{
					// If a home is specified, carry on with the command.
					if(args.length == 1){
						// Make sure that the home only contains letters, numbers, or underscores.						
						if(Pattern.compile("^[a-zA-Z0-9_]*$").matcher(args[0]).matches()){
							Commands.del_home(player, args);
						} else {
							sender.sendMessage(config.getString("messages.invalid-name"));
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
			
		return true;
	}
}
