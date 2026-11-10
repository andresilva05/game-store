package edu.ifsp.games.gamestore.dao;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
				.getDeclaredConstructor().newInstance();		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static HikariDataSource ds = null;
	
	
	public static Connection getConnection() throws SQLException {
		
		// Lazy Initialization
		if (ds == null) {
			ds = new HikariDataSource();
			ds.setJdbcUrl("jdbc:mysql://localhost:3306/loja");
			ds.setUsername("root");
			ds.setPassword("root");
		}
		
		return ds.getConnection();
	}
}
