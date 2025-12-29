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
	
	public static void connect() {
		try {
			logger.info("Connecting to the Database");
			
			oProperties = oTools.loadProperties();
			
			String sDriver = ((oProperties.getProperty("db.class")) == null) ? "oracle.jdbc.driver.OracleDriver" : (oProperties.getProperty("db.class"));
			String sHost = ((oProperties.getProperty("db.host")) == null) ? "10.153.248.155" : (oProperties.getProperty("db.host"));
			String sPort = ((oProperties.getProperty("db.port")) == null) ? "1521" : (oProperties.getProperty("db.port"));
			String sSid = ((oProperties.getProperty("db.sid")) == null) ? "ERATSP01" : (oProperties.getProperty("db.sid"));
			String sUser = ((oProperties.getProperty("db.user")) == null) ? "emsmgr" : (oProperties.getProperty("db.user"));
			String sPass = ((oProperties.getProperty("db.pass")) == null) ? "c9srt4991" : (oProperties.getProperty("db.pass"));
						
			Class.forName(sDriver);  

			String sConnString = "jdbc:oracle:thin:@" + sHost + ":" + sPort + ":" + sSid;
			
			logger.info("Connection String: " + sConnString);
			
			oConn = DriverManager.getConnection(sConnString, sUser, sPass);  
		}
		catch (SQLException sqle) {
			logger.warn("Exception: " + sqle.getMessage());
			
			sqle.printStackTrace();
			
			System.exit(0);
		}
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
			
			System.exit(0);
		}
	}
	
	public static List<String> executeQuery(String sQuery) {
		logger.info("Executing Query: " + sQuery);
		
		Statement oStatement = null;
		
		ResultSet oResultSet = null;
		
		List<String> lDBEntryNumbers = new ArrayList<String>();
		
		try {
			oStatement = oConn.createStatement();
			
			oResultSet = oStatement.executeQuery(sQuery);
			
			while (oResultSet.next()) {
				String sEntryNumber = oResultSet.getString("IMTX_NUM");
				
				logger.debug("Result: " + sEntryNumber);
				
				lDBEntryNumbers.add(sEntryNumber);
			}
		}
		catch (SQLException sqle) {
			logger.warn("Exception: " + sqle.getMessage());
			
			sqle.printStackTrace();
			
			System.exit(0);
		}
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
			
			System.exit(0);
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
		        } catch (SQLException e) { /* Ignored */}
		    }
		}
		return lDBEntryNumbers; 
	}
}
