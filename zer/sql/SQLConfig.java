package zer.sql;



import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;



public class SQLConfig
{
	private static String user;
	private static String password;

	public static void auth(String u, String p)
	{
		user = u;
		password = p;
	}

	protected static Statement statement;

	public static void connect(String jdbc_driver, String connectionString)
	{
		try
		{
			Class.forName(jdbc_driver);
			Connection connection = DriverManager.getConnection(connectionString, user, password);
			statement = connection.createStatement();
		}
		catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
	}
}
