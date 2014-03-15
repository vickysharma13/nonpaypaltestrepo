package com.paypal.sea.s2dbservices;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;
import org.junit.Test;

import com.paypal.sea.s2dbservices.oracledbaccess.DbConnection;
import com.paypal.sea.s2dbservices.oracledbaccess.OracleConnection2;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DbConnection.class })
public class DbConnectionTest {

	String testID = "1000";
	String testPod = "AAA";
	String testStage = "stage2p1234";
	String testDbServer = "lvsvmdb01";
	String testCloneVersion = "99RQALVS11G";
	String testSnapshotVersion = "99RQALVS11G06";
	String testCloneCycle = "RQA";
	String testCloneOption = "BASIC,PAY11G,PAYPILOT11G";
	String testDate = "01-jan-13";
	String testStatus = "SUCCESS";
	String testDbStatus = "DBDOWN";
	String testFiler = "lvsvna01";
	String testHal = "HAL_LVS1";
	String testScript = "Run_this_script";
	String command;
	String testDb = "QADBA111";
	String testMetaDb = "META";

	ReadProperty mockRd = mock(ReadProperty.class);
	Runtime mockRunTime = mock(Runtime.class);
	Process mockProcess = mock(Process.class);
	BufferedReader mockBrOutput = mock(BufferedReader.class);
	BufferedReader mockBrErr = mock(BufferedReader.class);
	InputStream mockIsOutput = mock(InputStream.class);
	InputStream mockIsErr = mock(InputStream.class);
	InputStreamReader mockIsOutputReader = mock(InputStreamReader.class);
	InputStreamReader mockIsErrReader = mock(InputStreamReader.class);
	DbConnection obj = new DbConnection(mockRd);

	StreamGobbler mockSGI = mock(StreamGobbler.class);
	StreamGobbler mockSGE = mock(StreamGobbler.class);

	@Test
	public void testStartupSuccessful() {

		mockStatic(Runtime.class);
		command = "ssh " + testDbServer + " " + testScript + " " + testDb;

		try {
			when(mockRd.getValue("StartDB_in_target_server")).thenReturn(
					testScript);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenReturn(mockBrOutput);
			whenNew(BufferedReader.class).withArguments(mockIsErrReader)
					.thenReturn(mockBrErr);
			when(mockBrOutput.readLine()).thenReturn("Database opened")
					.thenReturn(null);
			when(mockBrErr.readLine()).thenReturn("Error stream output")
					.thenReturn(null);
			when(mockProcess.exitValue()).thenReturn(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.startup(testDb, testDbServer));

	}

	@Test
	public void testStartupException() {

		mockStatic(Runtime.class);
		command = "ssh " + testDbServer + " " + testScript + " " + testDb;

		try {
			when(mockRd.getValue("StartDB_in_target_server")).thenReturn(
					testScript);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenThrow(new Exception());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.startup(testDb, testDbServer));

	}

	@Test
	public void testRemoveStageSuccessful() {

		mockStatic(Runtime.class);
		command = "ssh " + testDbServer + " " + testScript + " " + testStage
				+ " " + testMetaDb;

		try {
			when(mockRd.getValue("Remove_Stage_Script")).thenReturn(testScript);
			when(mockRd.getValue("Metadata_Db_sid")).thenReturn(testMetaDb);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenReturn(mockBrOutput);
			whenNew(BufferedReader.class).withArguments(mockIsErrReader)
					.thenReturn(mockBrErr);
			when(mockBrOutput.readLine()).thenReturn("Commit complete")
					.thenReturn(null);
			when(mockBrErr.readLine()).thenReturn("Permission denied")
					.thenReturn(null);
			when(mockProcess.exitValue()).thenReturn(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.removeStage(testStage, testDbServer));
	}

	@Test
	public void testRemoveStageException() {

		mockStatic(Runtime.class);
		command = "ssh " + testDbServer + " " + testScript + " " + testStage
				+ " " + testMetaDb;

		try {
			when(mockRd.getValue("Remove_Stage_Script")).thenReturn(testScript);
			when(mockRd.getValue("Metadata_Db_sid")).thenReturn(testMetaDb);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenThrow(new Exception());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.removeStage(testStage, testDbServer));
	}

	@Test
	public void testCreateDbInstanceSuccessful() {

		mockStatic(Runtime.class);
		String command = "ssh " + testDbServer + " " + testScript + " "
				+ testPod + " " + testStage + " " + testFiler + " "
				+ testCloneVersion + " " + testCloneCycle + " "
				+ testCloneOption + " " + testMetaDb;

		try {
			when(mockRd.getValue("ORAinstall_script")).thenReturn(testScript);
			when(mockRd.getValue("Metadata_Db_sid")).thenReturn(testMetaDb);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenReturn(mockBrOutput);
			whenNew(BufferedReader.class).withArguments(mockIsErrReader)
					.thenReturn(mockBrErr);
			when(mockBrOutput.readLine()).thenReturn(
					"The Scripts are completed").thenReturn(null);
			when(mockBrErr.readLine()).thenReturn("Error stream output")
					.thenReturn(null);
			when(mockProcess.exitValue()).thenReturn(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		assertEquals(0,
				obj.createDbInstance(testPod, testStage, testFiler,
						testCloneVersion, testCloneCycle, testCloneOption,
						testDbServer));
	}

	@Test
	public void testCreateDbInstanceException() {

		mockStatic(Runtime.class);
		String command = "ssh " + testDbServer + " " + testScript + " "
				+ testPod + " " + testStage + " " + testFiler + " "
				+ testCloneVersion + " " + testCloneCycle + " "
				+ testCloneOption + " " + testMetaDb;

		try {
			when(mockRd.getValue("ORAinstall_script")).thenReturn(testScript);
			when(mockRd.getValue("Metadata_Db_sid")).thenReturn(testMetaDb);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenThrow(new Exception());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0,
				obj.createDbInstance(testPod, testStage, testFiler,
						testCloneVersion, testCloneCycle, testCloneOption,
						testDbServer));
	}

	@Test
	public void testShutdownSuccessful() {

		mockStatic(Runtime.class);
		command = "ssh " + testDbServer + " " + testScript + " " + testDb;

		try {
			when(mockRd.getValue("ShutDB_in_target_server")).thenReturn(
					testScript);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenReturn(mockBrOutput);
			whenNew(BufferedReader.class).withArguments(mockIsErrReader)
					.thenReturn(mockBrErr);
			when(mockBrOutput.readLine()).thenReturn(
					"ORACLE instance shut down").thenReturn(null);
			when(mockBrErr.readLine()).thenReturn("Error stream output")
					.thenReturn(null);
			when(mockProcess.exitValue()).thenReturn(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.shutdown(testDb, testDbServer));
	}

	@Test
	public void testShutdownException() {

		mockStatic(Runtime.class);
		command = "ssh " + testDbServer + " " + testScript + " " + testDb;

		try {
			when(mockRd.getValue("ShutDB_in_target_server")).thenReturn(
					testScript);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenThrow(new Exception());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.shutdown(testDb, testDbServer));
	}

	@Test
	public void testRunPegaCloneSuccessful() {

		String source = "SRC";
		String target = "TARGET";
		String version = "11G";

		mockStatic(Runtime.class);
		String command = "ssh " + testDbServer + " " + testScript + " "
				+ source + " " + target;

		try {
			when(mockRd.getValue("PEGAclone_script_" + version)).thenReturn(
					testScript);
			when(mockRd.getValue("Server_PEGAclone_" + version)).thenReturn(
					testDbServer);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenReturn(mockBrOutput);
			whenNew(BufferedReader.class).withArguments(mockIsErrReader)
					.thenReturn(mockBrErr);
			when(mockBrOutput.readLine()).thenReturn("Standard Output")
					.thenReturn(null);
			when(mockBrErr.readLine()).thenReturn("Error stream output")
					.thenReturn(null);
			when(mockProcess.exitValue()).thenReturn(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.runPegaClone(source, target, version));
	}

	@Test
	public void testRunPegaCloneException() {

		String source = "SRC";
		String target = "TARGET";
		String version = "11G";

		mockStatic(Runtime.class);
		String command = "ssh " + testDbServer + " " + testScript + " "
				+ source + " " + target;

		try {
			when(mockRd.getValue("PEGAclone_script_" + version)).thenReturn(
					testScript);
			when(mockRd.getValue("Server_PEGAclone_" + version)).thenReturn(
					testDbServer);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);
			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenThrow(new Exception());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.runPegaClone(source, target, version));
	}

	@Test
	public void testRunTNSGeneratorSuccessful() {

		mockStatic(Runtime.class);
		String command = "ssh " + testStage + " " + testScript;
		try {
			when(mockRd.getValue("tnsGenerator_script")).thenReturn(testScript);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);

			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenReturn(mockBrOutput);
			whenNew(BufferedReader.class).withArguments(mockIsErrReader)
					.thenReturn(mockBrErr);
			when(mockBrOutput.readLine()).thenReturn("Standard Output")
					.thenReturn(null);
			when(mockBrErr.readLine()).thenReturn("Error stream output")
					.thenReturn(null);
			when(mockProcess.exitValue()).thenReturn(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.runTNSGenerator(testStage));
	}

	@Test
	public void testReloadListenerSuccessful() {

		mockStatic(Runtime.class);
		String command = "ssh " + testDbServer + " " + testScript;
		try {
			when(mockRd.getValue("Reload_Listener_Flag")).thenReturn("1");
			when(mockRd.getValue("listener_reload_script")).thenReturn(
					testScript);
			when(Runtime.getRuntime()).thenReturn(mockRunTime);
			when(mockRunTime.exec(command)).thenReturn(mockProcess);

			when(mockProcess.getInputStream()).thenReturn(mockIsOutput);
			when(mockProcess.getErrorStream()).thenReturn(mockIsErr);
			whenNew(InputStreamReader.class).withArguments(mockIsOutput)
					.thenReturn(mockIsOutputReader);
			whenNew(InputStreamReader.class).withArguments(mockIsErr)
					.thenReturn(mockIsErrReader);
			whenNew(BufferedReader.class).withArguments(mockIsOutputReader)
					.thenReturn(mockBrOutput);
			whenNew(BufferedReader.class).withArguments(mockIsErrReader)
					.thenReturn(mockBrErr);
			when(mockBrOutput.readLine()).thenReturn("Standard Output")
					.thenReturn(null);
			when(mockBrErr.readLine()).thenReturn("Error stream output")
					.thenReturn(null);
			when(mockProcess.exitValue()).thenReturn(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(0, obj.reloadListener(testDbServer));
	}

	@Test
	public void testReloadListenerFlagOff() {
		try {
			when(mockRd.getValue("Reload_Listener_Flag")).thenReturn("0");
			when(mockRd.getValue("listener_reload_script")).thenReturn(
					testScript);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(StringConstants.ERROR_CODES.LISTENER_FLAG_OFF.ordinal(),
				obj.reloadListener(testDbServer));
	}

	@Test
	public void testReloadListenerScriptIncorrect() {
		try {
			when(mockRd.getValue("Reload_Listener_Flag")).thenReturn("1");
			when(mockRd.getValue("listener_reload_script")).thenReturn(null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(
				StringConstants.ERROR_CODES.SCRIPT_NAME_INCORRECT.ordinal(),
				obj.reloadListener(testDbServer));
	}

}
