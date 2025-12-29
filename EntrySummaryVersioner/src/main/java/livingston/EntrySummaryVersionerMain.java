/**
 * 
 */
package livingston;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
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
public class EntrySummaryVersionerMain {
	private static Logger logger = Logger.getLogger(EntrySummaryVersionerMain.class);
	
	public static Properties oProperties = new Properties();
	
	public static Tools oTools = new Tools();
	
	/**
	 * Method that will read the Directory and will process each file 
	 * 
	 * @return File[] - The array of files to process
	 */
	public static void readDirectory(String sInputPath) throws IOException {
		logger.info("Reading Directory: " + sInputPath);
		
		File oFilePath = new File(sInputPath);
		
		File[] aFilesList = oFilePath.listFiles();
		
		// Order by Name
		Arrays.sort(aFilesList);
		
		FileProcessor oFileProcessor = new FileProcessor();
		
		// Process each of the files
		for(File oFile : aFilesList) {
			logger.info("File: " + oFile);
			
			oFileProcessor.processFile(oFile);
		}
	}
		
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
			
			String sInputPath = ((oProperties.getProperty("path.input")) == null) ? "/dsprojects/Projects/ER_ATSI/WORKDIR/INBOUND/ATSI_ES_IN/Input" : (oProperties.getProperty("path.input"));
		
			readDirectory(sInputPath);
		} 
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
		long lEndTime = System.currentTimeMillis(); 
		
		logger.info("Process Time: " + TimeUnit.MILLISECONDS.toSeconds((lEndTime - lStartTime)) + " seconds");
	}
}
