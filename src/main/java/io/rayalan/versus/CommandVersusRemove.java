package io.rayalan.versus;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class CommandVersusRemove implements CommandExecutor {

	public CommandVersusRemove(Versus plugin) {
		plugin.getCommand("versusremove").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(args.length == 1) {
			OfflinePlayer p = Bukkit.getPlayerExact(args[0]);
			
			if(p != null && p.hasPlayedBefore()) {
				Database db = new Database();
				db.removePlayerData(p.getUniqueId());
				
				sender.sendMessage(ChatColor.DARK_PURPLE + "Verus database information for player " + ChatColor.GRAY + p.getName() + ChatColor.DARK_PURPLE + " has been removed.");
				return true;
			} else {
				// if the plugin.yml is parsed properly error message should? be sent to the player
				return false;			
			}
		} else {
			return false;
		}
	}
}
