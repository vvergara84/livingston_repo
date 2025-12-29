/**
 * 
 */
package livingston;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import livingston.tools.Tools;

import livingston.process.FileProcessor;

/**
 * Class that Process the Entry Summary files and search for the latest version in the DB
 * It will verify the version and overwrite if the version is less than the DB one.
 * 
 * @author Victor Vergara
 *
 */
public class EntrySummaryFixerMain {
	private static Logger logger = Logger.getLogger(EntrySummaryFixerMain.class);
	
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
			
			String sInputFile = ((oProperties.getProperty("path.input")) == null) ? "./extra/info.in" : (oProperties.getProperty("path.input"));
		
			File oFile = new File(sInputFile);
			
			FileProcessor oFileProcessor = new FileProcessor();
			oFileProcessor.processFile(oFile);
		} 
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
		long lEndTime = System.currentTimeMillis(); 
		
		logger.info("Process Time: " + TimeUnit.MILLISECONDS.toSeconds((lEndTime - lStartTime)) + " seconds");
	}
}
