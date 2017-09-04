package testTrans;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



public class ConnectionUtils {
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	static {
		try {
			Properties ps = new Properties();// getClassLoader是方法 后面必须加括弧！！
			InputStream is = ConnectionUtils.class.getClassLoader()
					.getResourceAsStream("testTrans/cf.properties");
			ps.load(is);
			driver = ps.getProperty("driver");
			url = ps.getProperty("url");
			username = ps.getProperty("username");
			password = ps.getProperty("password");
			Class.forName(driver);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

}
