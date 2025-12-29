/**
 * 
 */
package livingston;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import livingston.tools.Tools;

import livingston.process.ReportProcessor;

/**
 * Class that will generate a Classifier Report
 * 
 * @author Victor Vergara
 *
 */
public class ClassifierReportMain {
	private static Logger logger = Logger.getLogger(ClassifierReportMain.class);
	
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
					
			ReportProcessor oReportProcessor = new ReportProcessor();
			
			oReportProcessor.reportProcess();
		} 
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
		long lEndTime = System.currentTimeMillis(); 
		
		logger.info("Process Time: " + TimeUnit.MILLISECONDS.toSeconds((lEndTime - lStartTime)) + " seconds");
	}
}
