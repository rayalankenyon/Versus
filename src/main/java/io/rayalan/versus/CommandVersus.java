package io.rayalan.versus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CommandVersus implements CommandExecutor {

	CommandVersus(Versus plugin) {
		plugin.getCommand("versus").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
        	Database db = new Database();
            Player player = (Player)sender;
            PlayerData pd = db.getPlayerData(player.getUniqueId());
            
            player.sendMessage(ChatColor.DARK_PURPLE + "Versus Player Statistics"
            				 + ChatColor.DARK_PURPLE + "\nRating: " + ChatColor.GRAY + pd.getRating()
            				 + ChatColor.DARK_PURPLE + "\nStreak: " +  ChatColor.GRAY + pd.getStreak()
            				 + ChatColor.DARK_PURPLE + "\nWins: " +  ChatColor.GRAY + pd.getWins()
            				 + ChatColor.DARK_PURPLE + "\nLosses: " + ChatColor.GRAY + pd.getLosses());
            return true;
        } else {
        	System.out.println("You must be a player to execute this command.");
        }
		return false;
	}
}
