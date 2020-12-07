package io.rayalan.versus;

import java.util.UUID;

public class PlayerData {

	private static int default_rating;
	
	private UUID user_id;
	private int rating, streak, wins, losses;

	
	public PlayerData(Versus plugin) {
		default_rating = plugin.getConfig().getInt("database.default_rating", 1200);
	}
	
	
	// default values
	public PlayerData() {
		this.user_id = null;
		this.rating = default_rating;
		this.streak = 0;
		this.wins = 0;
		this.losses = 0;
	}
	
	// todo(rayalan): this is wrong based on config setting
	public PlayerData(UUID user_id) {
		this.user_id = user_id;
		this.rating = default_rating;
		this.streak = 0;
		this.wins = 0;
		this.losses = 0;		
	}
	
	public PlayerData(UUID user_id, int rating, int streak, int wins, int losses) {
		this.user_id = user_id;
		this.rating = rating;
		this.streak = streak;
		this.wins = wins;
		this.losses = losses;
	}
	
	public UUID getUserId() {
		return this.user_id;
	}
	
	public int getRating() {
		return this.rating;
	}
	
	public int getStreak() {
		return this.streak;
	}
	
	public int getWins() {
		return this.wins;
	}
	
	public int getLosses() {
		return this.losses;
	}
	
	public void setId(UUID user_id) {
		this.user_id = user_id;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public void setStreak(int streak) {
		this.streak = streak;
	}
	
	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public void setLosses(int losses) {
		this.losses = losses;
	}
}
