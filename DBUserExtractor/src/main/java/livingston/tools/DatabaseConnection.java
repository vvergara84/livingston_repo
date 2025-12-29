/**
 * 
 */
package livingston.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import livingston.object.TsUser;

/**
 * @author Victor Vergara
 *
 */
public class DatabaseConnection {
	private static Logger logger = Logger.getLogger(DatabaseConnection.class);

	public static Properties oProperties = new Properties();
	
	public static String sPropertiesFileName = "config/main.properties";
	
	public static Tools oTools = new Tools();
	
	static Connection oConn = null;
	
	public static void connect(String sEnv, String sApp) {
		try {
			logger.info("Connecting to the Database");
			logger.info("Environment: " + sEnv);
			logger.info("Application: " + sApp);
			
			oProperties = oTools.loadProperties();
			
			String sDriver = ((oProperties.getProperty("db.class")) == null) ? "oracle.jdbc.driver.OracleDriver" : (oProperties.getProperty("db.class"));
			String sHost = ((oProperties.getProperty("db." + sApp + "." + sEnv + ".host")) == null) ? "10.153.248.155" : (oProperties.getProperty("db." + sApp + "." + sEnv + ".host"));
			String sPort = ((oProperties.getProperty("db." + sApp + "." + sEnv + ".port")) == null) ? "1521" : (oProperties.getProperty("db." + sApp + "." + sEnv + ".port"));
			String sSid = ((oProperties.getProperty("db." + sApp + "." + sEnv + ".sid")) == null) ? "ERATSP01" : (oProperties.getProperty("db." + sApp + "." + sEnv + ".sid"));
			String sUser = ((oProperties.getProperty("db." + sApp + "." + sEnv + ".user")) == null) ? "emsmgr" : (oProperties.getProperty("db." + sApp + "." + sEnv + ".user"));
			String sPass = ((oProperties.getProperty("db." + sApp + "." + sEnv + ".pass")) == null) ? "c9srt4991" : (oProperties.getProperty("db." + sApp + "." + sEnv + ".pass"));
						
			Class.forName(sDriver);  

			String sConnString = "jdbc:oracle:thin:@" + sHost + ":" + sPort + ":" + sSid;
			
			logger.info("Connection String: " + sConnString);
			logger.info("User: " + sUser);
			logger.info("Password: " + sPass);
			
			oConn = DriverManager.getConnection(sConnString, sUser, sPass);  
		}
		catch (SQLException sqle) {
			logger.warn("Exception: " + sqle.getMessage());
			
			sqle.printStackTrace();
		}
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
	}
		
	public static List<TsUser> executeSelectQuery(String sQuery) {
		logger.info("Executing Query: " + sQuery);
		
		Statement oStatement = null;
		
		ResultSet oResultSet = null;
		
		List<TsUser> lTsUser = new ArrayList<TsUser>();
		
		try {
			oStatement = oConn.createStatement();
			
			oResultSet = oStatement.executeQuery(sQuery);
						
			while (oResultSet.next()) {
				TsUser oTsUser = new TsUser();
				oTsUser.setsUserId(oResultSet.getString("USER_ID"));
				oTsUser.setsPassword(oResultSet.getString("PASSWORD"));
				
				logger.debug("Record obtained: " + oTsUser.toString());
				
				lTsUser.add(oTsUser);
			}
		}
		catch (SQLException sqle) {
			logger.warn("Exception: " + sqle.getMessage());
			
			sqle.printStackTrace();
		}
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
		finally {
		    if (oResultSet != null) {
		        try {
		        	oResultSet.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (oStatement != null) {
		        try {
		        	oStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (oConn != null) {
		        try {
		        	oConn.close();
		        } 
		        catch (SQLException e) { /* Ignored */}
		    }
		}
		return lTsUser; 
	}
}
