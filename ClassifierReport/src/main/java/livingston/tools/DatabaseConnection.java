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

import livingston.object.LucentRecord;

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
	
	public static void executeQuery(String sQuery) {
		logger.info("Executing Query: " + sQuery);
		
		Statement oStatement = null;
		
		ResultSet oResultSet = null;
		
		try {
			oStatement = oConn.createStatement();
			
			oResultSet = oStatement.executeQuery(sQuery);
			
			logger.info("Result: " + oResultSet);
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
	}
	
	public static List<LucentRecord> executeSelectQuery(String sQuery) {
		logger.info("Executing Query: " + sQuery);
		
		Statement oStatement = null;
		
		ResultSet oResultSet = null;
		
		List<LucentRecord> lLucentRecords = new ArrayList<LucentRecord>();
		
		try {
			oStatement = oConn.createStatement();
			
			oResultSet = oStatement.executeQuery(sQuery);
						
			while (oResultSet.next()) {
				LucentRecord oLucentRecord = new LucentRecord();
				oLucentRecord.setsProdId(oResultSet.getString("PROD_ID"));
				oLucentRecord.setsClassValue(oResultSet.getString("CLASS_VALUE_1"));
				oLucentRecord.setsClassifierDate(oResultSet.getString("CLASSIFIED_DATE"));
				oLucentRecord.setsClassifiedBy(oResultSet.getString("CLASSIFIED_BY"));
				oLucentRecord.setsLastModifiedDate(oResultSet.getString("LAST_MODIFIED_DATE"));
				oLucentRecord.setsLastModifiedBy(oResultSet.getString("LAST_MODIFIED_BY"));
				oLucentRecord.setsProdStatus(oResultSet.getString("PROD_STATUS"));
				oLucentRecord.setsDescExpand(oResultSet.getString("DESC_EXPAND"));
				oLucentRecord.setsDescCtry(oResultSet.getString("DESC_CTRY"));

				logger.debug("Record obtained: " + oLucentRecord.toString());
				
				lLucentRecords.add(oLucentRecord);
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
		return lLucentRecords; 
	}
}
