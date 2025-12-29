/**
 * 
 */
package livingston.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

import livingston.tools.DatabaseConnection;
import livingston.tools.Tools;

/**
 * @author Victor Vergara
 *
 */
public class FileProcessor {
	private static Logger logger = Logger.getLogger(FileProcessor.class);
	
	public static DatabaseConnection oDatabaseConnection = new DatabaseConnection();
	
	public static Properties oProperties = new Properties();
	
	public static Tools oTools = new Tools();
	
	Scanner oScanner = null;
	
	/**
	 * 
	 * @param oFile
	 */
	public void processFile(File oFile) {
		try {
			logger.info("**********   R E A D I N G   F I L E   **********");
			
			oProperties = oTools.loadProperties();
			
			String sOutputPath = ((oProperties.getProperty("path.output")) == null) ? "./extra/newInfo.in" : (oProperties.getProperty("path.output"));
						
			oScanner = new Scanner(oFile);
			
			String sFileLine;
			
			while (oScanner.hasNextLine()) {
				sFileLine = oScanner.nextLine();
				
				logger.info("Reading Line : " + sFileLine);
				
				String[] aLineSplit = sFileLine.split(",");
				
				logger.debug("Line Element Count: " + aLineSplit.length);
				
				String sEntryType = aLineSplit[0].replace("\"", "");
				
				// If Entry Type is Header then obtain the Entry Number
				if (sEntryType.equals("EL") || sEntryType.equals("RL")) {
					String sLine = aLineSplit[1].replace("\"", "") + "," + aLineSplit[3].replace("\"", "") + "," + aLineSplit[5].replace("\"", "") + "," + aLineSplit[aLineSplit.length-14].replace("\"", "") + "," + aLineSplit[13].replace("\"", "") + "," + aLineSplit[14].replace("\"", "") + "," + aLineSplit[2].replace("\"", "");
					
					writeEntryLine(sOutputPath, sLine);	
				}
				
			}
		}
		catch (FileNotFoundException fnfe) {
			logger.warn("Exception: " + fnfe.getMessage());
			
			fnfe.printStackTrace();
		}
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
	}	
	
	public void writeEntryLine(String sOutputPath, String sNewEntryLine) {
		logger.info("Writing Entry Line in New File");
		
		try {	
			Path oFilePath = Paths.get(sOutputPath);
			
			logger.info("Writing in File: " + oFilePath.toString());
			
			Files.write(oFilePath, sNewEntryLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
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
