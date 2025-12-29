/**
 * 
 */
package livingston.process;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import livingston.tools.DatabaseConnection;
import livingston.tools.Tools;

/**
 * @author Victor Vergara
 *
 */
public class Processor {
	private static Logger logger = Logger.getLogger(Processor.class);
	
	public static DatabaseConnection oDatabaseConnection = new DatabaseConnection();
	
	public static Properties oProperties = new Properties();
	
	public static Tools oTools = new Tools();
	
	Scanner oScanner = null;
	
	/**
	 * 
	 */
	public void stopApp() {
		logger.info("**********   S T O P P I N G   T R A D E S P H E R E   A P P S   **********");
		
		Session session = null;
	    ChannelExec channel = null;
	    
		try {
			oProperties = oTools.loadProperties();
			
			String sConfig = ((oProperties.getProperty("json.setup")) == null) ? "" : (oProperties.getProperty("json.setup"));
			String sUser = ((oProperties.getProperty("json.setup")) == null) ? "vvergara" : (oProperties.getProperty("json.setup"));
			String sPass = ((oProperties.getProperty("json.setup")) == null) ? "Monday50" : (oProperties.getProperty("json.setup"));
			
			logger.info("Configuration : " + sConfig);
			logger.info("SSH User : " + sUser);
			logger.info("SSH Pass : " + sPass);
			
			JSONArray oConfig = new JSONArray(sConfig);

			logger.info("*** Stopping Application ***");
			
	    	for (int j = 0; j < oConfig.length(); j++) {
	    		JSONObject oApp = oConfig.getJSONObject(j);
	    		
	    		String sAppName = "" + oApp.get("app_name");
	    		String sServerName = "" + oApp.get("server_name");
	    		String sIp = "" + oApp.get("ip");
	    		String sPort = "" + oApp.get("port");
	    		String sAppUser = "" + oApp.get("app_user");
	    		String sAppHome = "" + oApp.get("app_home");
	    		String sTomcatHome = "" + oApp.get("tomcat_home");
	    		String sStartCmd = "" + oApp.get("start_cmd");
	    		String sStopCmd = "" + oApp.get("stop_cmd");
	    		
	    		logger.info("App Name: " + sAppName);
	    		logger.info("Server Name: " + sServerName);
	    		logger.info("Ip: " + sIp);
	    		logger.info("Port: " + sPort);
	    		logger.info("App Home: " + sAppHome);
	    		logger.info("Tomcat Home: " + sTomcatHome);
	    		logger.info("Start Command: " + sStartCmd);
	    		logger.info("Stop Command: " + sStopCmd);
	    		
	    		// Starting Connection
	    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    		JSch jsch = new JSch();
	    		
	    	    session = jsch.getSession(sUser, sIp, Integer.parseInt(sPort));
	            session.setPassword(sPass);
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.connect();
	            	          
	            channel = (ChannelExec) session.openChannel("shell");
	            channel.setOutputStream(outputStream);
	            PrintStream stream = new PrintStream(channel.getOutputStream());
	            channel.connect();

	            // Swithching to User
	            String sCommand = "sudo su - " + sAppUser;
	            
	            stream.println(sCommand);
	            stream.flush();
	            String sResponse = waitForPrompt(outputStream, "$");
	            
	            logger.info("Command: " + sCommand);
	            logger.info("Response: " + sResponse);

	            // Moving to Application Path
	            sCommand = "cd " + sAppHome;
	            
	            stream.println(sCommand);
	            stream.flush();
	            sResponse = waitForPrompt(outputStream, "$");

	            logger.info("Command: " + sCommand);
	            logger.info("Response: " + sResponse);
	            
	            // Stopping Application
	            sCommand = sStopCmd;
	            
	            stream.println(sCommand);
	            stream.flush();
	            sResponse = waitForPrompt(outputStream, "$");

	            logger.info("Command: " + sCommand);
	            logger.info("Response: " + sResponse);
	            
	            // Stopping Application (Twice to Make Sure)
	            sCommand = sStopCmd;
	            
	            stream.println(sCommand);
	            stream.flush();
	            sResponse = waitForPrompt(outputStream, "$");

	            logger.info("Just to Make Sure");
	            logger.info("Command: " + sCommand);
	            logger.info("Response: " + sResponse);

	            // Moving to Tomcat Home
	            sCommand = "cd " + sTomcatHome;
	            
	            stream.println(sCommand);
	            stream.flush();
	            sResponse = waitForPrompt(outputStream, "$");

	            logger.info("Command: " + sCommand);
	            logger.info("Response: " + sResponse);
	            
	            // Stopping Tomcat
	            sCommand = "./shudown.sh";
	            
	            stream.println(sCommand);
	            stream.flush();
	            sResponse = waitForPrompt(outputStream, "$");

	            logger.info("Command: " + sCommand);
	            logger.info("Response: " + sResponse);
	            
	            // Stopping Tomcat (Twice to Make Sure)
	            sCommand = "./shudown.sh";
	            
	            stream.println(sCommand);
	            stream.flush();
	            sResponse = waitForPrompt(outputStream, "$");

	            logger.info("Just to Make Sure");
	            logger.info("Command: " + sCommand);
	            logger.info("Response: " + sResponse);
	            
	            logger.info("*** Finished Stopping Application ***");
	    	}
    	}
    	catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		} 
    	finally {
    		if (session != null) {
    			session.disconnect();
	        }
	        if (channel != null) {
	            channel.disconnect();
	        }
    	}
	}
	
	/**
	 * 
	 */
	public void startApp() {
		logger.info("**********   S T O P P I N G   T R A D E S P H E R E   A P P S   **********");
	}
	
	/**
	 * 
	 */
	public void startTomcat() {
		logger.info("**********   S T O P P I N G   T R A D E S P H E R E   A P P S   **********");
	}
	
	/**
	 * 
	 */
	public void getStatus() {
		logger.info("**********   S T O P P I N G   T R A D E S P H E R E   A P P S   **********");
	}
}
