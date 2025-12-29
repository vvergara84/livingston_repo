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
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
			logger.info("**********   R E A D I N G   N E W   F I L E   **********");
			
			oProperties = oTools.loadProperties();
			
			String sInputPath = ((oProperties.getProperty("path.input")) == null) ? "/dsprojects/Projects/ER_ATSI/WORKDIR/INBOUND/ATSI_ES_IN/Input" : (oProperties.getProperty("path.input"));
			String sOutputPath = ((oProperties.getProperty("path.output")) == null) ? "/dsprojects/Projects/ER_ATSI/WORKDIR/INBOUND/ATSI_ES_IN/" : (oProperties.getProperty("path.output"));
			String sBackupPath = ((oProperties.getProperty("path.backup")) == null) ? "/dsprojects/Projects/ER_ATSI/WORKDIR/INBOUND/ATSI_ES_IN/Backup" : (oProperties.getProperty("path.backup"));
			
			logger.info("Processing File: " + oFile.getName());
			
			oScanner = new Scanner(oFile);
			
			String sFileLine;
			
			List<String> lFileHeaderEntryNumbers = new ArrayList<String>();
//			List<String> lFileBiEntryNumbers = new ArrayList<String>();
			
			// For each of the Lines in the file, extract the Entry Type to obtain the list of Entry Numbers 
			while (oScanner.hasNextLine()) {
				sFileLine = oScanner.nextLine();
				
				logger.info("Reading Line : " + sFileLine);
				
				String[] sLineSplit = sFileLine.split(",");
				
				logger.debug("Line Element Count: " + sLineSplit.length);
				
				String sEntryType = sLineSplit[0].replace("\"", "");
				
				// If Entry Type is Header then obtain the Entry Number
				if (sEntryType.equals("EH") || sEntryType.equals("RH")) {
					String sEntryNumber = sLineSplit[1].replace("\"", "");
					
					logger.debug("Header Found, getting Entry Number: " + sEntryNumber);
					
					lFileHeaderEntryNumbers.add(sEntryNumber);
				}	
//				else if (sEntryType.equals("BI")) {
//					String sEntryNumber = sLineSplit[1].replace("\"", "");
//					
//					logger.debug("BI Found, getting Entry Number: " + sEntryNumber);
//					
//					lFileBiEntryNumbers.add(sEntryNumber);
//				}
			}
			logger.info("Header Entries Found: " + lFileHeaderEntryNumbers.size());
//			logger.info("Bi Entries Found: " + lFileBiEntryNumbers.size());
			
			logger.info("**********   S T A R T   D B   S E A R C H   **********");
			
			// Obtaining the Entry Numbers Version from the DB
			List<String> lDBEntryNumbers = databaseSearch(lFileHeaderEntryNumbers);
			
			// Obtaining the Entry Numbers Version from the DB
//			List<String> lDBBiEntryNumbers = databaseSearchBi(lFileBiEntryNumbers);
			
			logger.info("**********   R E P L A C I N G   I N F O R M A T I O N   **********");
			
			oScanner = new Scanner(oFile);
			
			// For each of the Line, compare the Entry Number Version
			while (oScanner.hasNextLine()) {
				sFileLine = oScanner.nextLine();
				
				logger.info("Reading Line: " + sFileLine);
				
				String[] aLineSplit = sFileLine.split(",");
				
				logger.debug("Line Element Count: " + aLineSplit.length);
				
				String sEntryType = aLineSplit[0].replace("\"", "");
				
				// In the case of the Header Line, the version is in the 3rd Position
				if (sEntryType.equals("EH") || sEntryType.equals("RH")) {
					String sEntryNumber = aLineSplit[1].replace("\"", "");
					String sEntryVersion = aLineSplit[2].replace("\"", "");
					String sEntryTypeNumber = aLineSplit[3].replace("\"", "");
					String sEntryDateCopy = aLineSplit[6].replace("\"", "");
					
					logger.debug("Line Entry Number: " + sEntryNumber);
					logger.debug("Line Header Version: " + sEntryVersion);
					logger.debug("Line Entry Type Number: " + sEntryTypeNumber);
					logger.debug("Line Entry Date: " + sEntryDateCopy);
					
					Boolean bMatched = false;
					
					// If Line Entry Number is in the DB Entry Number List
					for (int i = 0; i < lDBEntryNumbers.size(); i++) {
						String[] sDBEntryArray = lDBEntryNumbers.get(i).split("_");
						
						String sDBEntryNumber = sDBEntryArray[0];
						int iDBEntryVersion = Integer.parseInt(sDBEntryArray[1]);
						
						logger.debug("Database Entry Number: " + sDBEntryNumber);
						logger.debug("Database Entry Version: " + iDBEntryVersion);						
						logger.debug("Comparing Line Entry Number vs Database Entry Number: " + sEntryNumber + " vs " + sDBEntryNumber);
						
						
						// If the Line Entry Number found its Match
						if (sEntryNumber.equals(sDBEntryNumber)) {
							logger.info("Entry Number Matched");
							logger.debug("Comparing Line Entry Version vs Database Entry Version: " + sEntryVersion + " vs " + iDBEntryVersion);
							
							int iNewDBEntryVersion = iDBEntryVersion + 1;
							
							// Since we found a Match we enable the Flag
							bMatched = true;
							
							// Replace the Version in the Line Array
							aLineSplit[2] = "\"" + iNewDBEntryVersion + "\"";										
							
							// We exit the For, since we found the record.
							break;
						}						
					}
					
					// If we never found a Match for the Entry
					if (!bMatched) {
						// Replace the Version in the Line Array with First Version
						aLineSplit[2] = "\"1\"";
					}
					
					// Changing Export Date with the Entry Date Copy
					if (sEntryTypeNumber.equals("06")) {
						// If it has the correct number of elements, just replace
						if (aLineSplit.length == 33) {
							aLineSplit[7] = "" + sEntryDateCopy;
						}
						else if (aLineSplit.length == 32) {
							logger.debug("Header Line has less elements (32)");
							
							List<String> lLineSplit = new ArrayList<String>(Arrays.asList(aLineSplit));
							
							logger.debug("Line converted to List: " + Arrays.toString(lLineSplit.toArray())); 
							
							lLineSplit.add(7, sEntryDateCopy);
							
							logger.debug("Line converted to List: " + Arrays.toString(lLineSplit.toArray())); 
							
							aLineSplit = lLineSplit.toArray(aLineSplit);
						}
						
					}					
					logger.info("New Header Line to Save: " + String.join(",", aLineSplit));

					// Write the Entry Line in the New File
					writeEntryLine(sOutputPath, oFile.getName(), String.join(",", aLineSplit));					
				}	// In the case of the Entry Line, the version is in the 3rd Position
				else if (sEntryType.equals("EL") || sEntryType.equals("RL")) {
					String sEntryNumber = aLineSplit[1].replace("\"", "");
					String sEntryVersion = aLineSplit[3].replace("\"", "");
					
					logger.debug("Line Entry Number: " + sEntryNumber);
					logger.debug("Line Header Version: " + sEntryVersion);
					
					Boolean bMatched = false;
					
					// If Line Entry Number is in the DB Entry Number List
					for (int i = 0; i < lDBEntryNumbers.size(); i++) {
						String[] sDBEntryArray = lDBEntryNumbers.get(i).split("_");
						
						String sDBEntryNumber = sDBEntryArray[0];
						int iDBEntryVersion = Integer.parseInt(sDBEntryArray[1]);
						
						logger.debug("Database Entry Number: " + sDBEntryNumber);
						logger.debug("Database Entry Version: " + iDBEntryVersion);						
						logger.debug("Comparing Line Entry Number vs Database Entry Number: " + sEntryNumber + " vs " + sDBEntryNumber);
						
						
						// If the Line Entry Number found its Match
						if (sEntryNumber.equals(sDBEntryNumber)) {
							logger.info("Entry Number Matched");
							logger.debug("Comparing Line Entry Version vs Database Entry Version: " + sEntryVersion + " vs " + iDBEntryVersion);
							
							int iNewDBEntryVersion = iDBEntryVersion + 1;
							
							// Since we found a Match we enable the Flag
							bMatched = true;
							
							// Replace the Version in the Line Array
							aLineSplit[3] = "\"" + iNewDBEntryVersion + "\"";										
							
							// We exit the For, since we found the record.
							break;
						}						
					}
					
					// If we never found a Match for the Entry
					if (!bMatched) {
						// Replace the Version in the Line Array with First Version
						aLineSplit[3] = "\"1\"";
					}									
					logger.info("New Entry Line to Save: " + String.join(",", aLineSplit));
					
					// Write the Entry Line in the New File
					writeEntryLine(sOutputPath, oFile.getName(), String.join(",", aLineSplit));
				}	// In the case of BI records the version is in the 4th Position
				else if (sEntryType.equals("BI")) {
/*					String sEntryNumber = aLineSplit[1].replace("\"", "");
					String sEntryVersion = aLineSplit[4].replace("\"", "");
					
					logger.debug("Line Entry Number: " + sEntryNumber);
					logger.debug("Line Header Version: " + sEntryVersion);
					
					Boolean bMatched = false;
					
					// If Line Entry Number is in the DB Entry Number List
					for (int i = 0; i < lDBBiEntryNumbers.size(); i++) {
						String[] sDBEntryArray = lDBBiEntryNumbers.get(i).split("_");
						
						String sDBEntryNumber = sDBEntryArray[0];
						int iDBEntryVersion = Integer.parseInt(sDBEntryArray[1]);
						
						logger.debug("Database Entry Number: " + sDBEntryNumber);
						logger.debug("Database Entry Version: " + iDBEntryVersion);						
						logger.debug("Comparing Line Entry Number vs Database Entry Number: " + sEntryNumber + " vs " + sDBEntryNumber);
						
						
						// If the Line Entry Number found its Match
						if (sEntryNumber.equals(sDBEntryNumber)) {
							logger.info("Entry Number Matched");
							logger.debug("Comparing Line Entry Version vs Database Entry Version: " + sEntryVersion + " vs " + iDBEntryVersion);
							
							int iNewDBEntryVersion = iDBEntryVersion + 1;
							
							// Since we found a Match we enable the Flag
							bMatched = true;
							
							// Replace the Version in the Line Array
							aLineSplit[4] = "\"" + iNewDBEntryVersion + "\"";										
							
							// We exit the For, since we found the record.
							break;
						}						
					}
					
					// If we never found a Match for the Entry
					if (!bMatched) {
						// Replace the Version in the Line Array with First Version
						aLineSplit[2] = "\"1\"";
					}									
					logger.info("New Entry Line to Save: " + String.join(",", aLineSplit));
*/					
					// Write the Entry Line in the New File
					writeEntryLine(sOutputPath, oFile.getName(), String.join(",", aLineSplit));
				}
			}
			logger.info("**********   B A C K U P   F I L E S   **********");
			
			// After Processing the File, Backup the Original and Modified Files
			backupFiles(sInputPath, sOutputPath, sBackupPath, oFile.getName());
			
			logger.info("Finish Processing File");
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
/*	
	public List<String> databaseSearch(List<String> lFileEntryNumbers) {
		logger.info("Looking for Records in Database");
		
		String sQuery = "";
		String sQueryFilter = "";
		
		for (int i = 0; i < lFileEntryNumbers.size(); i++) {
			// Every 100 Lines, add the UNION
			if (i > 0 && i % 100 == 0) {
				sQueryFilter += "imtx_num like '" + lFileEntryNumbers.get(i) + "%') UNION SELECT imtx_num FROM ems_imtx WHERE user_varchar_30 = 'Y' and (";
			} // If its the last entry, then don't add the OR
			else if (i == (lFileEntryNumbers.size() - 1)) {
				sQueryFilter += "imtx_num like '" + lFileEntryNumbers.get(i) + "%'";
			}
			else {
				sQueryFilter += "imtx_num like '" + lFileEntryNumbers.get(i) + "%' or ";
			}
		}
		logger.info("Query Filter: " + sQueryFilter);
		
		sQuery = "SELECT imtx_num FROM ems_imtx WHERE user_varchar_30 = 'Y' and (" + sQueryFilter + ")";
		
		DatabaseConnection.connect();
		List<String> lDBEntryNumbers = DatabaseConnection.executeQuery(sQuery);
		
		logger.info("Query Result Size: " + lDBEntryNumbers.size());
		logger.info("Database Entry Number List: " + lDBEntryNumbers.toString());
		
		return lDBEntryNumbers;
	}
*/	
	public List<String> databaseSearch(List<String> lFileEntryNumbers) {
		logger.info("Looking for Records in Database");
		
		String sQuery = "";
		String sTempQuery = "";
		String sQueryFilter = "";
				
		for (int i = 0; i < lFileEntryNumbers.size(); i++) {
			logger.debug("Contador: " + i);
			// If its the last entry, then don't add the OR
			if (i == (lFileEntryNumbers.size() - 1)) {
				sQueryFilter += "imtx_num like '" + lFileEntryNumbers.get(i) + "%'";
			}
			// Every 100 Lines, add the UNION
			else if (i > 0 && i % 100 == 0) {
				sQueryFilter += "imtx_num like '" + lFileEntryNumbers.get(i) + "%') group by SUBSTR(imtx_num, 0, 13) UNION SELECT SUBSTR(imtx_num, 0, 13) as \"NUMBER\", MAX(SUBSTR(imtx_num, INSTR(imtx_num, '_', -1) + 1)) as \"VERSION\" FROM ems_imtx WHERE user_varchar_30 = 'Y' and (";
			} 
			else {
				sQueryFilter += "imtx_num like '" + lFileEntryNumbers.get(i) + "%' or ";
			}
		}
		logger.debug("Query Filter: " + sQueryFilter);
		
		sTempQuery = "SELECT SUBSTR(imtx_num, 0, 13) as \"NUMBER\", MAX(SUBSTR(imtx_num, INSTR(imtx_num, '_', -1) + 1)) as \"VERSION\" FROM ems_imtx WHERE user_varchar_30 = 'Y' and (" + sQueryFilter + ") group by SUBSTR(imtx_num, 0, 13)";
		
		sQuery = "SELECT \"NUMBER\"||'_'||\"VERSION\" as \"IMTX_NUM\" from (" + sTempQuery + ")";
		
		DatabaseConnection.connect();
		List<String> lDBEntryNumbers = DatabaseConnection.executeQuery(sQuery);
		
		logger.info("Query Result Size: " + lDBEntryNumbers.size());
		logger.debug("Database Entry Number List: " + lDBEntryNumbers.toString());
		
		return lDBEntryNumbers;
	}
	
	public List<String> databaseSearchBi(List<String> lFileBiEntryNumbers) {
		logger.info("Looking for Records in Database");
		
		String sQuery = "";
		String sQueryFilter = "";
		
		for (int i = 0; i < lFileBiEntryNumbers.size(); i++) {
			// If its the last entry, then don't add the OR
			if (i == (lFileBiEntryNumbers.size() - 1)) {
				sQueryFilter += "imtx_num like '" + lFileBiEntryNumbers.get(i) + "%'";
			} // Every 100 Lines, add the UNION
			else if (i > 0 && i % 100 == 0) {
				sQueryFilter += "imtx_num like '" + lFileBiEntryNumbers.get(i) + "%') UNION SELECT imtx_num FROM ems_imtx_ci WHERE (";
			} 
			else {
				sQueryFilter += "imtx_num like '" + lFileBiEntryNumbers.get(i) + "%' or ";
			}
		}
		logger.debug("Query Filter: " + sQueryFilter);
		
		sQuery = "SELECT imtx_num FROM ems_imtx_ci WHERE (" + sQueryFilter + ")";
		
		DatabaseConnection.connect();
		List<String> lDBEntryNumbers = DatabaseConnection.executeQuery(sQuery);
		
		logger.info("Query Result Size: " + lDBEntryNumbers.size());
		logger.debug("Database Entry Number List: " + lDBEntryNumbers.toString());
		
		return lDBEntryNumbers;
	}
	
	public void writeEntryLine(String sOutputPath, String sFileName, String sNewEntryLine) {
		logger.info("Writing Entry Line in New File");
		
		try {	
			Path oFilePath = Paths.get(sOutputPath + "/" + sFileName);
			
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
	
	public void backupFiles(String sInputPath, String sOutputPath, String sBackupPath, String sFileName) {
		logger.info("Backup the Files");
		
		try {
			logger.debug("File Name: " + sFileName);
			
			String[] sFileNameSplit = sFileName.split("[.]", 0);
			
			logger.debug("File Name Split: " + sFileNameSplit.length);
			
			Path oInputFilePath = Paths.get(sInputPath + "/" + sFileName);
			Path oBackupOrigFilePath = Paths.get(sBackupPath + "/" + sFileNameSplit[0] + "_Original." + sFileNameSplit[1]);
			
			Path oOutputFilePath = Paths.get(sOutputPath + "/" + sFileName);
			Path oBackupFilePath = Paths.get(sBackupPath + "/" + sFileName);
			
			logger.info("Copying Original File: " + oInputFilePath + " to " + oBackupOrigFilePath);
			
			Files.copy(oInputFilePath, oBackupOrigFilePath, StandardCopyOption.REPLACE_EXISTING);
			
			logger.info("Copying New File: " + oOutputFilePath + " to " + oBackupFilePath);
			
			Files.copy(oOutputFilePath, oBackupFilePath, StandardCopyOption.REPLACE_EXISTING);
			
			// Deleting processed file
			logger.info("Deleting Processed File: " + oInputFilePath);
			
			Files.delete(oInputFilePath);
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
