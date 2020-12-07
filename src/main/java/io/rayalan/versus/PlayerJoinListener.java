package io.rayalan.versus;

import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinListener implements Listener {

	public PlayerJoinListener(Versus plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		String join_message;
		String player_name = e.getPlayer().getPlayerListName();
		UUID player_id = e.getPlayer().getUniqueId();
		Database db = new Database();
		
		if(!db.playerExists(player_id)) {
			db.createPlayerData(player_id);
			join_message = "Welcome " + player_name + " (:" ;
		} else {
			join_message = player_name + "(" + db.getRating(player_id) + ") joined";
		}
		
		System.out.println("[versus] plugin tried to send join message: " + join_message);
		e.setJoinMessage(join_message);
	}
}
