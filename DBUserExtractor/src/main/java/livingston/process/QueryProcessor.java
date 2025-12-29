/**
 * 
 */
package livingston.process;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.text.SimpleDateFormat;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Date;

import org.apache.log4j.Logger;

import livingston.object.TsUser;
import livingston.tools.DatabaseConnection;
import livingston.tools.Tools;

/**
 * @author Victor Vergara
 *
 */
public class QueryProcessor {
	private static Logger logger = Logger.getLogger(QueryProcessor.class);
	
	public static DatabaseConnection oDatabaseConnection = new DatabaseConnection();
	
	public static Properties oProperties = new Properties();
	
	public static Tools oTools = new Tools();
	
	Scanner oScanner = null;
	
	/**
	 * 
	 * @param oFile
	 */
	public void getUsers() {
		try {
			logger.info("**********   S T A R T   G E T   U S E R   P R O C E S S   **********");
			
			String sPattern = "yyyyMMdd";
			
			String sDate = new SimpleDateFormat(sPattern).format(new Date());
			
			List<TsUser> lResult = null;
			
			// Writing Column Names
			writeFile(sDate, "ENV,APP,USERID,PASSWORD");
						
			oProperties = oTools.loadProperties();
						
			String sEnvironments = ((oProperties.getProperty("app.env")) == null) ? "" : (oProperties.getProperty("app.env"));
			String sQuery = ((oProperties.getProperty("query.select")) == null) ? "" : (oProperties.getProperty("query.select"));
			String sUserIds = ((oProperties.getProperty("query.userId")) == null) ? "" : (oProperties.getProperty("query.userId"));
			
			sQuery = sQuery.replace("%USER_IDS%", sUserIds);
			
			logger.info("Environments to Process: " + sEnvironments);
			logger.info("Query to Execute: " + sQuery);
			
			String[] aEnvironments = sEnvironments.split(",");

			logger.info("Getting Applications per Enviroments");
			
			for (int i = 0; i < aEnvironments.length; i++) {
				String sEnv = aEnvironments[i];
				
				logger.info("Processing Environment: " + sEnv);
				
				String sApps = ((oProperties.getProperty("app." + sEnv)) == null) ? "" : (oProperties.getProperty("app." + sEnv));
				
				logger.info("Applications to Process: " + sApps);
				
				String[] aApps = sApps.split(",");
				
				logger.info("Getting Information");
				
				for (int j = 0; j < aApps.length; j++) {
					String sApp = aApps[j];
					
					logger.info("Processing Application: " + sApp);
					
					logger.info("**********   S T A R T   D B   S E A R C H   **********");
					
					// Doing Select to the TS User Table
					lResult = executeSelectQuery(sEnv, sApp, sQuery);
					
					logger.info("**********   P R E P A R I N G   I N F O R M A T I O N   **********");			
					logger.info("Total Records: " + lResult.size());
					
					for (int k = 0; k < lResult.size(); k++) {
						writeFile(sDate, sEnv + "," + sApp + "," + lResult.get(k).toExcel());
					}
				}
			}
			logger.info("Finish Extract Process");
		} 
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
	}	

	public List<TsUser> executeSelectQuery(String sEnv, String sApp, String sQuery) {
		logger.info("Executing Select Query");
		logger.debug("Query: " + sQuery);
		
		DatabaseConnection.connect(sEnv, sApp);
		List<TsUser> lTsUser = DatabaseConnection.executeSelectQuery(sQuery);
		
		logger.info("Query Result Size: " + lTsUser.size());
		logger.debug("Database Lucent Record List: " + lTsUser.toString());
		
		return lTsUser;
	}
		
	public void writeFile(String sDate, String sLine) {
		logger.info("Writing Line New File");
		
		String sOutputPath = ((oProperties.getProperty("path.output")) == null) ? "C:/Users/vvergara/Documents/Tools/Java_Scripts/DBUserExtractor/Output" : (oProperties.getProperty("path.output"));
		
		try {	
			Path oFilePath = Paths.get(sOutputPath + "/Ts_User_List_" + sDate + ".csv");
			
			logger.info("Writing in File: " + oFilePath.toString());
			
			Files.write(oFilePath, sLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			Files.write(oFilePath, ("\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
		catch (IOException ioe) {
			logger.warn("Exception: " + ioe.getMessage());
			
			ioe.printStackTrace();
			
			System.exit(0);
		}
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
			
			System.exit(0);
		}
	}
}
