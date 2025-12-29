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

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.apache.log4j.Logger;

import livingston.object.LucentRecord;
import livingston.tools.DatabaseConnection;
import livingston.tools.Tools;

import javax.activation.*;

/**
 * @author Victor Vergara
 *
 */
public class ReportProcessor {
	private static Logger logger = Logger.getLogger(ReportProcessor.class);
	
	public static DatabaseConnection oDatabaseConnection = new DatabaseConnection();
	
	public static Properties oProperties = new Properties();
	
	public static Tools oTools = new Tools();
	
	Scanner oScanner = null;
	
	/**
	 * 
	 * @param oFile
	 */
	public void reportProcess() {
		try {
			logger.info("**********   S T A R T   R E P O R T   P R O C E S S   **********");
			
			String sPattern = "yyyyMMdd";
			
			String sDate =new SimpleDateFormat(sPattern).format(new Date());
			
			oProperties = oTools.loadProperties();
						
			String sQueryDrop = ((oProperties.getProperty("query.drop")) == null) ? "" : (oProperties.getProperty("query.drop"));
			String sQueryCreate = ((oProperties.getProperty("query.create")) == null) ? "" : (oProperties.getProperty("query.create"));
			String sQueryAlter = ((oProperties.getProperty("query.alter")) == null) ? "" : (oProperties.getProperty("query.alter"));
			String sQueryUpdate = ((oProperties.getProperty("query.update")) == null) ? "" : (oProperties.getProperty("query.update"));
			String sQuerySelect = ((oProperties.getProperty("query.select")) == null) ? "" : (oProperties.getProperty("query.select"));
			
			logger.info("**********   S T A R T   D B   O P E R A T I O N S   **********");
			
			logger.info("Dropping Temporary Table");
			
			// Dropping temporary table to generate a new one
			executeQuery(sQueryDrop);
			
			logger.info("Creating Temporary Table");
			
			// Creating temporary table to save the report information
			executeQuery(sQueryCreate);
			
			logger.info("Altering Temporary Table");
			
			// Altering temporary table
			executeQuery(sQueryAlter);
			
			logger.info("Updating Temporary Table");
			
			// Updating temporary table
			executeQuery(sQueryUpdate);
			
			logger.info("Generating Report");
			
			// Extracting Report Information
			List<LucentRecord> lLucentRecords = executeSelectQuery(sQuerySelect);
			
			logger.info("**********   P R E P A R I N G   I N F O R M A T I O N   **********");			
			logger.info("Total Records: " + lLucentRecords.size());
			
			// Writing Column Names
			writeFile(sDate, "PROD_ID,CLASS_VALUE_1,CLASSIFIED_DATE,CLASSIFIED_BY,LAST_MODIFIED_DATE,LAST_MODIFIED_BY,PROD_STATUS,DESC_EXPAND,DESC_CTRY,PRODUCT_NOTE");
			
			for (int i = 0; i < lLucentRecords.size(); i++) {
				writeFile(sDate, lLucentRecords.get(i).toExcel());
			}
			
			logger.info("**********   S E N D I N G   E M A I L   **********");			
			sendEmail(sDate);
			
			logger.info("Finish Report Process");
		} 
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
	}	

	public void executeQuery(String sQuery) {
		logger.info("Executing Query");
		logger.debug("Query: " + sQuery);
				
		DatabaseConnection.connect();
		DatabaseConnection.executeQuery(sQuery);
	}
	
	public List<LucentRecord> executeSelectQuery(String sQuery) {
		logger.info("Executing Select Query");
		logger.debug("Query: " + sQuery);
		
		DatabaseConnection.connect();
		List<LucentRecord> lLucentRecords = DatabaseConnection.executeSelectQuery(sQuery);
		
		logger.info("Query Result Size: " + lLucentRecords.size());
		logger.debug("Database Lucent Record List: " + lLucentRecords.toString());
		
		return lLucentRecords;
	}
	
	public void writeFile(String sDate, String sLine) {
		logger.info("Writing Line New File");
		
		String sOutputPath = ((oProperties.getProperty("path.output")) == null) ? "/home/TRADESPHERE/vvergara/Scripts/Reports" : (oProperties.getProperty("path.output"));
		
		try {	
			Path oFilePath = Paths.get(sOutputPath + "/Classifier_Report_" + sDate + ".csv");
			
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
	
	public void sendEmail(String sDate) {
		logger.info("Sending report through email");
		
		String sOutputPath = ((oProperties.getProperty("path.output")) == null) ? "/home/TRADESPHERE/vvergara/Scripts/Reports" : (oProperties.getProperty("path.output"));
		Path oFilePath = Paths.get(sOutputPath + "/Classifier_Report_" + sDate + ".csv");
		
		logger.info("File to Send: " + oFilePath);
		
		String sEmailHost = ((oProperties.getProperty("email.host")) == null) ? "email-smtp.us-east-1.amazonaws.com" : (oProperties.getProperty("email.host"));
		String sEmailPort = ((oProperties.getProperty("email.port")) == null) ? "587" : (oProperties.getProperty("email.port"));
		String sEmailSender = ((oProperties.getProperty("email.sender")) == null) ? "noreply+TSC@tradesphere.net" : (oProperties.getProperty("email.sender"));
		String sEmailReceiver = ((oProperties.getProperty("email.receiver")) == null) ? "ABucio@livingston.com" : (oProperties.getProperty("email.receiver"));
		
		final String sEmailUser = ((oProperties.getProperty("email.user")) == null) ? "AKIA43G6FKJAYLD6QWPW" : (oProperties.getProperty("email.user"));
		final String sEmailPassword = ((oProperties.getProperty("email.pass")) == null) ? "" : (oProperties.getProperty("email.pass"));
		
		Properties oProps = System.getProperties();
		oProps.put("mail.transport.protocol", "smtp");  
		oProps.put("mail.smtp.port", Integer.parseInt(sEmailPort));  
		oProps.put("mail.smtp.starttls.enable", "true");  
		oProps.put("mail.smtp.auth", "true");
        

		// Get the default Session object.
		Session session = Session.getDefaultInstance(oProps);  

		Transport oTransport = null;
		try {
			MimeMessage oMessage = new MimeMessage(session);
			oMessage.setFrom(new InternetAddress(sEmailSender, "Classifier"));
			oMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(sEmailReceiver));
			oMessage.setSubject("Lucent Classifier Report: " + sDate);
			
			BodyPart oMessageBodyPart = new MimeBodyPart();  
			oMessageBodyPart.setText("Hola Arlena: \n\n" + 
					"Te anexo el reporte solicitado \n" +
					"\n\n" + 
					"Saludos");  
			 
			String sFileName = "Classifier_Report_" + sDate + ".csv";
			DataSource oDataSource = new FileDataSource("" + oFilePath);  
			
			MimeBodyPart oMessageBodyPartFile = new MimeBodyPart();  
			oMessageBodyPartFile.setDataHandler(new DataHandler(oDataSource));  
			oMessageBodyPartFile.setFileName(sFileName);  

		    Multipart oMultipart = new MimeMultipart();  
		    oMultipart.addBodyPart(oMessageBodyPart);  
		    oMultipart.addBodyPart(oMessageBodyPartFile);  
		    
		    oMessage.setContent(oMultipart);
		    
			// Send message
			oTransport = session.getTransport(); 
			oTransport.connect(sEmailHost, sEmailUser, sEmailPassword);
			oTransport.sendMessage(oMessage, oMessage.getAllRecipients());
			
			System.out.println("Sent message successfully....");
		} 
		catch (MessagingException me) {
			logger.warn("Exception: " + me.getMessage());
			
			me.printStackTrace();
		}
		catch (Exception e) {
			logger.warn("Exception: " + e.getMessage());
			
			e.printStackTrace();
		}
		finally {
			try {
				oTransport.close();
			} 
			catch (MessagingException me) {
				logger.warn("Exception: " + me.getMessage());
				
				me.printStackTrace();
			}
		}
	}
}
