package com.paypal.sea.s2dbservices.oracledbaccess;

import java.util.logging.Level;

import com.paypal.sea.s2dbservices.MyLogger;
import com.paypal.sea.s2dbservices.ReadProperty;
import com.paypal.sea.s2dbservices.StackTraceUtil;
import com.paypal.sea.s2dbservices.StreamGobbler;
import com.paypal.sea.s2dbservices.StringConstants;

public class DbConnection {

	ReadProperty rd;

	public DbConnection() {
		rd = new ReadProperty();
	}

	public DbConnection(ReadProperty read) {
		rd = read;
	}

	public int startup(String dbName, String dbServer) {
		String runScript = rd.getValue("StartDB_in_target_server");
		String command = "ssh " + dbServer + " " + runScript + " " + dbName;
		return executeScript(command);
	}

	public int removeStage(String stageName, String dbServer) {
		String runScript = rd.getValue("Remove_Stage_Script");
		String targetMetadataDb = rd.getValue("Metadata_Db_sid");
		String command = "ssh " + dbServer + " " + runScript + " " + stageName
				+ " " + targetMetadataDb;
		return executeScript(command);
	}

	public int createDbInstance(String pod, String stage, String filer,
			String cloneVer, String cloneCycle, String cloneOption,
			String dbServer) {
		String runScript = rd.getValue("ORAinstall_script");
		String targetMetadataDb = rd.getValue("Metadata_Db_sid");
		String command = "ssh " + dbServer + " " + runScript + " " + pod + " "
				+ stage + " " + filer + " " + cloneVer + " " + cloneCycle + " "
				+ cloneOption + " " + targetMetadataDb;
		return executeScript(command);
	}

	public int shutdown(String dbName, String dbServer) {
		String runScript = rd.getValue("ShutDB_in_target_server");
		String command = "ssh " + dbServer + " " + runScript + " " + dbName;
		return executeScript(command);
	}

	public int runPegaClone(String source, String target, String version) {
		String runScript = rd.getValue("PEGAclone_script_" + version);
		String targetServer = rd.getValue("Server_PEGAclone_" + version);
		String command = "ssh " + targetServer + " " + runScript + " " + source
				+ " " + target;
		return executeScript(command);
	}

	public int runTNSGenerator(String stageName) {
		String runScript = rd.getValue("tnsGenerator_script");
		String command = "ssh " + stageName + " " + runScript;
		return executeScript(command);
	}

	public int reloadListener(String dbServerName) {
		String listenerFlag = rd.getValue("Reload_Listener_Flag");
		String runScript = rd.getValue("listener_reload_script");
		if (listenerFlag == null || listenerFlag.equals("0")) {
			MyLogger.getInstance().log(
					Level.INFO,
					StringConstants.ERROR_CODES_STRINGS
							.get(StringConstants.ERROR_CODES.LISTENER_FLAG_OFF
									.ordinal()));
			return StringConstants.ERROR_CODES.LISTENER_FLAG_OFF.ordinal();
		}
		if (runScript == null) {
			MyLogger.getInstance()
					.log(Level.INFO,
							StringConstants.ERROR_CODES_STRINGS
									.get(StringConstants.ERROR_CODES.SCRIPT_NAME_INCORRECT
											.ordinal()));
			return StringConstants.ERROR_CODES.SCRIPT_NAME_INCORRECT.ordinal();
		}
		String command = "ssh " + dbServerName + " " + runScript;
		return executeScript(command);
	}

	private int executeScript(String command) {
		MyLogger.getInstance().log(Level.INFO, command);
		int exitVal = 1;
		try {
			Process process = Runtime.getRuntime().exec(command);
			StreamGobbler sgi = new StreamGobbler(process.getInputStream());
			StreamGobbler sge = new StreamGobbler(process.getErrorStream());
			sge.start();
			sgi.start();
			exitVal = process.waitFor();

			StringBuffer sbi = sgi.getMessages();
			MyLogger.getInstance()
					.log(Level.INFO, "Output:>>" + sbi.toString());
			StringBuffer sbe = sge.getMessages();
			MyLogger.getInstance().log(Level.INFO,
					"Std error:>>" + sbe.toString());
			MyLogger.getInstance().log(Level.INFO, "Exit Value: " + exitVal);
			return exitVal;
		} catch (Exception e) {
			MyLogger.getInstance().log(
					Level.SEVERE,
					"DB CONNECTION EXCEPTION" + e.getMessage() + "\n"
							+ StackTraceUtil.getStackTrace(e));
		}
		return exitVal;
	}
}
