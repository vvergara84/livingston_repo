/**
 * 
 */
package livingston;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import livingston.tools.Tools;

import livingston.process.QueryProcessor;

/**
 * Class that will generate a Classifier Report
 * 
 * @author Victor Vergara
 *
 */
public class DBUserExtractorMain {
	private static Logger logger = Logger.getLogger(DBUserExtractorMain.class);
	
	public static Properties oProperties = new Properties();
	
	public static Tools oTools = new Tools();
			
	/**
	 * Main Method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {	
		logger.info("------------------------   E N T R Y   S U M M A R Y   V E R S I O N E R   M A I N   ------------------------");
		
		long lStartTime = System.currentTimeMillis(); 
		
		try {
			oProperties = oTools.loadProperties();
					
			QueryProcessor oQueryProcessor = new QueryProcessor();
			oQueryProcessor.getUsers();
		} 
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
		long lEndTime = System.currentTimeMillis(); 
		
		logger.info("Process Time: " + TimeUnit.MILLISECONDS.toSeconds((lEndTime - lStartTime)) + " seconds");
	}
}
