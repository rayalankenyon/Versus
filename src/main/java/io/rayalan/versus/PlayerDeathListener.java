package io.rayalan.versus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.md_5.bungee.api.ChatColor;

/*
 * config settings
 * 
 * rating.k_factor
 * rating.streak_max
 */
public final class PlayerDeathListener implements Listener {

	private double k_factor;
	private double streak_max;
	
	public PlayerDeathListener(Versus plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.k_factor = plugin.getConfig().getDouble("rating.k_factor", 17.0d);
		this.streak_max = plugin.getConfig().getDouble("rating.streak_max", 10.0d);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Database db = new Database();
		String death_message = e.getDeathMessage();
		
		if(e.getEntity().getKiller() != null) {
			PlayerData w = db.getPlayerData(e.getEntity().getKiller().getUniqueId());
			PlayerData l = db.getPlayerData(e.getEntity().getUniqueId());
			
			double ev_winner = 1.0f / (1.0f + Math.pow(10, (l.getRating() - w.getRating()) / 400.0f));
			double ev_loser =  1.0f / (1.0f + Math.pow(10, (w.getRating() - l.getRating()) / 400.0f));
			
			int streak_winner = Math.max(w.getStreak() + 1, 1);
			int streak_loser = Math.min(l.getStreak() - 1, -1);
				
			double k_winner = k_factor * Math.min(streak_winner, streak_max);
			double k_loser = k_factor * Math.min(Math.abs(streak_loser), streak_max);
			
			int winner_rating = w.getRating() + (int)(k_winner * (1.0f - ev_winner));
			int loser_rating = l.getRating() + (int)(k_loser * (0.0f - ev_loser));
			int winner_delta = winner_rating - w.getRating();
			int loser_delta = loser_rating - l.getRating();
			
			db.updatePlayerData(new PlayerData(w.getUserId(), winner_rating, streak_winner, w.getWins()+1, w.getLosses()));
			db.updatePlayerData(new PlayerData(l.getUserId(), loser_rating, streak_loser, l.getWins(), l.getLosses()+1));
			
			death_message = death_message.replaceAll(e.getEntity().getKiller().getName(), e.getEntity().getKiller().getName() + "(" + winner_rating + ")" + ChatColor.GREEN + "[+" + Integer.toString(winner_delta) + ChatColor.WHITE + "]");
			death_message = death_message.replaceAll(e.getEntity().getName(), e.getEntity().getName() + "(" + loser_rating + ")[" + ChatColor.RED + Integer.toString(loser_delta) + ChatColor.WHITE + "]");
		}
		
		e.setDeathMessage(death_message);
	}
}
