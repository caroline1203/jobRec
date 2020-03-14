package database;

public class MySQLDBUtil {
	private static final String INSTANCE = "jobrecommendation-instance.cad62iievcr5.us-east-2.rds.amazonaws.com";
	private static final String PORT_NUM = "3306";
	public static final String DB_NAME = "jobDB";
	private static final String USERNAME = "caro";
	private static final String PASSWORD = "Mysql1203!";
	public static final String URL = "jdbc:mysql://"
			+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";
}
