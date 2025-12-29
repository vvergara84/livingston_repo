/**
 * 
 */
package livingston.tools;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author Victor Vergara
 *
 */
public class Tools {
	private static Logger logger = Logger.getLogger(Tools.class);
	
	public static String sPropertiesFileName = "/main.properties";
	
	public Properties loadProperties() {
		logger.info("Loading Properties: " + sPropertiesFileName);
		
		Properties oProperties = new Properties();
		
		// Loading Application Properties
		try {
			InputStream oInput = getClass().getResourceAsStream(sPropertiesFileName);
					
			if(oInput != null) {
				logger.info("Properties File Found");
						
				oProperties.load(oInput);
			}
        } 
		catch (IOException ioe) {
			logger.warn("Exception: " + ioe.getMessage());
        }
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
		}
		return oProperties;
	}
}
