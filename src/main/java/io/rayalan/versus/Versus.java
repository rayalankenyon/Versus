package io.rayalan.versus;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public final class Versus extends JavaPlugin {

	private static Database player_info;
	private boolean first_start;
	
	@Override
	public void onEnable() {
		File cfg = new File(this.getDataFolder(), "config.yml");
		first_start = !cfg.exists();
		this.saveDefaultConfig();
		if(first_start) { System.out.println("Versus launched for first time! Update settings and reload to enable plugin."); return; }
		
		// note(rayalan): database must be initialized like this before other plugins call new Database()
		player_info = new Database(this);
		new PlayerData(this);
		
		new PlayerDeathListener(this);
		new PlayerJoinListener(this);
		
		new CommandVersus(this);
		new CommandVersusTop(this);
		new CommandVersusRemove(this);
	}
	
	@Override
	public void onDisable() {
		if(player_info != null) {
			player_info.close();
		}
	}

}
