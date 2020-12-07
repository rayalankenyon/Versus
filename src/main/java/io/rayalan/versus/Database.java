package io.rayalan.versus;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

/*
 * player information to store in the database
 * 
 * UUID
 * rating
 * streak
 * win
 * loss
 * 
 */

/*
 * config settings
 * 
 * database.url
 * database.user
 * database.password
 * database.default_rating
 */

public final class Database {

	private static Connection db_connection;
	private static int default_rating;
	
	private String url;
	private String user;
	private String password;
	
	public Database() {
		if(db_connection == null) {		
			try {
				Class.forName("org.postgresql.Driver");
			} catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			try {
				db_connection = DriverManager.getConnection(url, user, password);
	
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Database(Versus plugin) {
		if(db_connection == null) {
			//String url = "jdbc:postgresql://localhost:5432/Versus";
			url = "jdbc:postgresql://" + plugin.getConfig().getString("database.url", "localhost:5432/Versus");
			user = plugin.getConfig().getString("database.user", "postgres");
			password = plugin.getConfig().getString("database.password", "postgres");
			default_rating = plugin.getConfig().getInt("database.default_rating", 1200);
			
			try {
				Class.forName("org.postgresql.Driver");
			} catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			try {
				db_connection = DriverManager.getConnection(url, user, password);
				createTable(default_rating, user, db_connection);
				Statement st = db_connection.createStatement();
				String sql = "ALTER TABLE public.player_data\r\n"
						   + "OWNER to " + user + ";";		
				st.execute(sql);
				st.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void createTable(int rating, String owner, Connection db) {
		try {
			Statement statement = db.createStatement();

			String sql = "CREATE TABLE IF NOT EXISTS public.player_data\r\n"
					+ "(\r\n"
					+ "    user_id uuid NOT NULL,\r\n"
					+ "    rating integer NOT NULL DEFAULT " + Integer.toString(rating) + ",\r\n"
					+ "    streak integer NOT NULL DEFAULT 0,\r\n"
					+ "    wins integer NOT NULL DEFAULT 0,\r\n"
					+ "    losses integer NOT NULL DEFAULT 0,\r\n"
					+ "    CONSTRAINT player_data_pkey PRIMARY KEY (user_id)\r\n"
					+ ")\r\n"
				 	+ "\r\n"
					+ "TABLESPACE pg_default;\r\n";		
			statement.execute(sql);
			statement.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean playerExists(UUID id) {
		boolean ret = false;
		Statement statement = null;
		try {
			statement = db_connection.createStatement();
			ResultSet results = statement.executeQuery(
					"SELECT * FROM player_data\n" + 
					"WHERE user_id = \'" + id.toString() + "\';"
			);
			
			while(results.next()) {
				ret = true;
			}
			
			// clean up
			results.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public void createPlayerData(UUID id) {
        Statement statement = null;
        try {
	        statement = db_connection.createStatement();
	        String sql = "INSERT INTO player_data (user_id,rating,streak,wins,losses) "
	        			+ "VALUES ('"+ id.toString() + "', " + default_rating + ", 0, 0, 0);";
	        statement.executeUpdate(sql);
	        
	        // clean up
	        statement.close();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
	}
	
	public void removePlayerData(UUID id) {
        Statement statement = null;
        try {
	        statement = db_connection.createStatement();
	        String sql = "DELETE FROM player_data WHERE user_id = '" + id.toString() + "';";
	        statement.executeUpdate(sql);
	        
	        // clean up
	        statement.close();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
	}
	
	public int getRating(UUID id) {
		int rating = 0;
		Statement statement = null;
		try {
			statement = db_connection.createStatement();
			ResultSet results = statement.executeQuery(
					"SELECT rating FROM player_data\n" + 
					"WHERE user_id = \'" + id.toString() + "\';"
			);
			
			if(results.next()) {
				rating = results.getInt("rating");
			}
			
			results.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rating;
	}
	
	// todo(rayalan): add in calls to return / update multiple users information at one time
	public PlayerData getPlayerData(UUID id) {
		PlayerData data = null; 
		
		Statement statement = null;
		try {
			statement = db_connection.createStatement();
			ResultSet results = statement.executeQuery(
					"SELECT * FROM player_data\n" + 
					"WHERE user_id = \'" + id.toString() + "\';"
			);
			
			if(results.next()) {
				data = new PlayerData (
						id, 
						results.getInt("rating"),
						results.getInt("streak"),
						results.getInt("wins"),
						results.getInt("losses")
				);
			}
			
			results.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	
	public ArrayList<PlayerData> getPlayerData() {
		ArrayList<PlayerData> data= new ArrayList<PlayerData>();
		
		Statement statement = null;
		try {
			statement = db_connection.createStatement();
			ResultSet results = statement.executeQuery(
					"SELECT * FROM player_data\n" +
					"ORDER BY rating DESC;"
			);

			while(results.next()) {
				PlayerData pd = new PlayerData (
						(UUID) results.getObject("user_id"),
						results.getInt("rating"),
						results.getInt("streak"),
						results.getInt("wins"),
						results.getInt("losses")
				);
				data.add(pd);
			}
			results.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	public void updatePlayerData(PlayerData update) {
        Statement statement = null;
        try {
	        statement = db_connection.createStatement();
	        String sql = "UPDATE player_data\n" +
	        			 "SET rating = " + Integer.toString(update.getRating()) + ",\n" +
	        			 "	  streak = " + Integer.toString(update.getStreak()) + ",\n" +
	        			 "    wins = " + Integer.toString(update.getWins()) + ",\n" +
	        			 "    losses = "  + Integer.toString(update.getLosses()) + "\n" + 
	        			 "WHERE user_id = '" + update.getUserId() + "'";
	        statement.executeUpdate(sql);
	        
	        // clean up
	        statement.close();
        } catch (SQLException e) {
        	e.printStackTrace();
        }		
	}
	
	public void close() {
		if(db_connection != null) {
			try {
				db_connection.commit();
				db_connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
