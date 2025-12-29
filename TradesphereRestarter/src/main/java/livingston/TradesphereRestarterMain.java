/**
 * 
 */
package livingston;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import livingston.process.Processor;

/**
 * Class that Stops Tradesphere Applications and Restarts them
 * 
 * @author Victor Vergara
 *
 */
public class TradesphereRestarterMain {
	private static Logger logger = Logger.getLogger(TradesphereRestarterMain.class);
	
	/**
	 * Main Method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {	
		logger.info("------------------------   E N T R Y   S U M M A R Y   V E R S I O N E R   M A I N   ------------------------");
		
		long lStartTime = System.currentTimeMillis(); 
							
		logger.info("Args input: " + args.toString());
		
		Processor oProcessor = new Processor();
		
		if (args.length > 0) {
			if (args[0] == "STOP") {
				// Calling Stop Process
				oProcessor.stopApp();
			} 
			else if (args[0] == "START") {
				if (args[1] == "APP") {
					// Calling Start App Process
					oProcessor.startApp();
				}	
				else if (args[1] == "TOMCAT") {
					// Calling Start Tomcat Process
					oProcessor.startTomcat();
				}
				else {
					logger.warn("Wrong Argument Inserted");
					logger.warn("Valid Arguments: \n"
							+ "	- STOP\n"
							+ "	- START APP\n"
							+ "	- START TOMCAT\n"
							+ "	- STATUS\n");
				}
			}	
			else if (args[0] == "STATUS") {
				// Calling Get Status Process
				oProcessor.getStatus();
			}
			else {
				logger.warn("Wrong Argument Inserted");
				logger.warn("Valid Arguments: \n"
						+ "	- STOP\n"
						+ "	- START APP\n"
						+ "	- START TOMCAT\n"
						+ "	- STATUS\n");
			}
		}
		else {
			logger.warn("Missing Argument Inserted");
			logger.warn("Valid Arguments: \n"
					+ "	- STOP\n"
					+ "	- START APP\n"
					+ "	- START TOMCAT\n"
					+ "	- STATUS\n");
		}
		long lEndTime = System.currentTimeMillis(); 
		
		logger.info("Process Time: " + TimeUnit.MILLISECONDS.toSeconds((lEndTime - lStartTime)) + " seconds");
	}
}
