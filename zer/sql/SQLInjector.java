package zer.sql;



import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.NoSuchMethodException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;



public class SQLInjector extends SQLConfig
{
	public static void wakeup() throws SQLException
	{
		/*
		 * Wake up the connection if it is closed.
		 */
		statement.execute("SELECT 1");
	}
	
	public static void inject(SQLAction action)
	{
		try
		{
			wakeup();
			statement.execute(action.query());
		}
		catch (SQLException e) { e.printStackTrace(); }
	}

	public static <TModel extends SQLModel> ArrayList<TModel> inject(Class<TModel> modelClazz, SQLAction action)
	{
		try
		{
			wakeup();

			ArrayList<TModel> resultArray = new ArrayList<TModel>();

			ResultSet set = statement.executeQuery(action.query());
			while (set.next())
			{
				TModel model = modelClazz.getDeclaredConstructor().newInstance();

				Field[] fields = model.getClass().getFields();
				for (Field field : fields)
					field.set(model, set.getObject(field.getName()));

				resultArray.add(model);
			}

			return resultArray;
		}
		catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) { e.printStackTrace(); }
		return null;
	}
}
