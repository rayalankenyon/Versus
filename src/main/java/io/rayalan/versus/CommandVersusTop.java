package io.rayalan.versus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class CommandVersusTop implements CommandExecutor {

	private ArrayList<PlayerData> leaderboard_cache;
	private LocalDateTime current_time;
	private LocalDateTime previous_time;
	
	private int leaderboard_size;
	private int leaderboard_cache_update_frequency;
	
	public CommandVersusTop(Versus plugin) {
		plugin.getCommand("versustop").setExecutor(this);
		
		leaderboard_cache = new ArrayList<PlayerData>();
		leaderboard_cache.ensureCapacity(Bukkit.getOfflinePlayers().length);
		
		Database db = new Database();
		leaderboard_cache = db.getPlayerData();
		previous_time = LocalDateTime.now();
		
		leaderboard_size = plugin.getConfig().getInt("leaderboard.size", 10);
		leaderboard_cache_update_frequency = plugin.getConfig().getInt("leaderboard.cache_update_frequency", 60);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		current_time = LocalDateTime.now();
		
		if(ChronoUnit.MINUTES.between(previous_time, current_time) > leaderboard_cache_update_frequency) {
			Database db = new Database();
			leaderboard_cache = db.getPlayerData();
		}
		previous_time = current_time;

		// find the longest player name before we print messages
		int max_name_length = 0;
		for(int i = 0; i < leaderboard_cache.size(); i++) {
			PlayerData pd = leaderboard_cache.get(i);
			
			String player_name = Bukkit.getOfflinePlayer(pd.getUserId()).getName();
			if(max_name_length < player_name.length()) {
				max_name_length = player_name.length();
			}
		}
		
		sender.sendMessage(ChatColor.DARK_PURPLE + "Versus Leaderboard");
		for(int i = 0; i < leaderboard_cache.size(); i++) {
			
			if(i >= leaderboard_size) { return true; }
			PlayerData pd = leaderboard_cache.get(i);
			String player_name = Bukkit.getOfflinePlayer(pd.getUserId()).getName();
			
			// todo(rayalan): change rating color based on tiers?
			sender.sendMessage(Integer.toString(i + 1) + ". " + ChatColor.GRAY + player_name + " - " + ChatColor.DARK_PURPLE + pd.getRating() + "\n");
		}
		

		return true;
	}
}
