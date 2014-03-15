package com.paypal.sea.s2dbservices;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.paypal.sea.s2dbservices.oracledbaccess.*;

@Path("/stage")
public class Stage2Resource {

	private CatalogDataAccess mODA;
	private DbConnection db;
	private static Lock lock = new ReentrantLock();
	private long beginTime;
	private long elapsedTime;
	private String apiCall = "";

	public static String newline = System.getProperty("line.separator");

	public Stage2Resource() {
		mODA = new CatalogDataAccess();
		db = new DbConnection();
	}

	public Stage2Resource(CatalogDataAccess oda, DbConnection dbConn,
			ReentrantLock rLock) {
		mODA = oda;
		db = dbConn;
		lock = rLock;
	}

	@Path("/start")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("application/xml")
	public Status startDb(StartDbInput request) {

		beginTime = System.currentTimeMillis();
		apiCall = "start";
		Status response = new Status();
		String name = request.getStageName().toLowerCase();

		StageDetails sd = mODA.findDBInfo(name);

		if (sd.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
			giveException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
					beginTime, apiCall, name);
		}
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
			giveException(sd.getExceptionCode(), sd.getExceptionMessage(),
					beginTime, apiCall, name);
		}

		StringConstants.DBSTATUS dbBasicStatus = sd.getDBStatus();
		if (dbBasicStatus == StringConstants.DBSTATUS.DBNOTCLONED) {
			giveException(StringConstants.ERROR_CODES.DB_NOT_CLONED, beginTime,
					apiCall, name);
		}

		StringConstants.ERROR_CODES code = executeStartup(sd);

		if (code == StringConstants.ERROR_CODES.ALREADY_UP) {
			giveException(StringConstants.ERROR_CODES.ALREADY_UP, beginTime,
					apiCall, name);
		} else if (code == StringConstants.ERROR_CODES.NO_ERROR) {
			MyLogger.getInstance().log(Level.INFO, "Startup successful");
			response.setMessage(name + " DB startup successful");
			elapsedTime = System.currentTimeMillis() - beginTime;
			mODA.saveMetrics(apiCall, elapsedTime,
					StringConstants.ERROR_CODES.NO_ERROR.ordinal(), name);
		}
		return response;
	}

	private StringConstants.ERROR_CODES executeStartup(StageDetails sd) {
		String podId = sd.getPodName();
		String dbServer = sd.getDBServerName();
		String dbName = "QADBA" + podId;
		String dbNamePay = "QADBB" + podId;
		String dbNamePilot = "QADBC" + podId;
		String failStartUp = " ";
		int success = 0;

		Vector<String> hs = new Vector<String>();
		Vector<String> fail = new Vector<String>();

		if (sd.getDBStatus() == StringConstants.DBSTATUS.DBDOWN) {
			hs.add(dbName);
		}
		if (sd.getDbStatusPay() == StringConstants.DBSTATUS.DBDOWN) {
			hs.add(dbNamePay);
		}
		if (sd.getDbStatusPayPilot() == StringConstants.DBSTATUS.DBDOWN) {
			hs.add(dbNamePilot);
		}

		if (hs.size() == 0) {
			return StringConstants.ERROR_CODES.ALREADY_UP;
		}

		for (String dbname : hs) {
			int retCode = db.startup(dbname, dbServer);
			success |= retCode;
			if (retCode != 0) {
				fail.add(dbname);
			}
		}
		if (success == 0) {
			return StringConstants.ERROR_CODES.NO_ERROR;
		}
		for (String dbname : fail) {
			failStartUp += "DB " + dbname + " ";
		}
		failStartUp += "failed to start. Contact the DBA.";
		giveException(failStartUp, beginTime, apiCall, sd.getStageName());
		return StringConstants.ERROR_CODES.NO_ERROR;
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("application/xml")
	public Status createDb(Input request)

	{
		apiCall = "create";
		beginTime = System.currentTimeMillis();
		String error = mODA.validateInput(request);
		String name = request.getStageName().toLowerCase();
		if (!error.isEmpty()) {
			MyLogger.getInstance().log(Level.INFO, error);
			elapsedTime = System.currentTimeMillis() - beginTime;
			mODA.saveMetrics(apiCall, elapsedTime,
					StringConstants.ERROR_CODES.INPUT_ERROR.ordinal(), name);
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("<status>" + newline + "<message>" + error
							+ "</message>" + newline + "<code>"
							+ StringConstants.ERROR_CODES.INPUT_ERROR.ordinal()
							+ "</code>" + newline + "</status>")
					.type("application/xml").build());
		}

		String cloneVer = request.getCloneVersion();
		String cloneCycle = request.getCloneCycle();
		String cloneOption = request.getCloneOption();
		Status res = new Status();
		String filer = "";
		String pod = "";
		String dbServer = "";

		StageDetails sd = mODA.findDBInfo(name);
		if (sd.getErrorCode() != StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
			giveException(StringConstants.ERROR_CODES.STAGE_REPEAT, beginTime,
					apiCall, name);
		}

		lock.lock();
		try {
			pod = mODA.checkUnusedPod();

			if (pod == null) {
				String max = mODA.getMaxPod();
				pod = mODA.findUniqueName(max);
				MyLogger.getInstance().log(Level.INFO, "new_pod : " + pod);
			}

			dbServer = mODA.getDbServer();
			MyLogger.getInstance().log(Level.INFO, "db_server : " + dbServer);
			if (dbServer == null) {
				giveException(StringConstants.ERROR_CODES.CAPACITY_FULL,
						beginTime, apiCall, name);
			}

			int halVersion = 1;
			String webserver = name.substring(5);

			int podId = Integer.parseInt(mODA.getPID());
			MyLogger.getInstance().log(Level.INFO, "PID : " + podId);

			halVersion = ((Integer.parseInt(mODA.getHal())) % 5) + 1;

			String halProcess = "HAL_LVS" + Integer.toString(halVersion);
			MyLogger.getInstance().log(Level.INFO,
					"hal_process : " + halProcess);

			filer = mODA.getFiler(dbServer);
			if (filer == null) {
				MyLogger.getInstance().log(Level.SEVERE, "filer not found");
				giveException(StringConstants.ERROR_CODES.INTERNAL_ERROR,
						beginTime, apiCall, name);
			}
			MyLogger.getInstance().log(Level.INFO, "filer : " + filer);
			String masterVer = mODA.getMasterVersion(cloneVer);
			if (masterVer == null) {
				MyLogger.getInstance().log(Level.SEVERE,
						"Master version not found");
				giveException(StringConstants.ERROR_CODES.INTERNAL_ERROR,
						beginTime, apiCall, name);
			}

			if (mODA.insertMetadata(podId, webserver, name, dbServer, pod,
					filer, halProcess, cloneVer, cloneOption, cloneCycle,
					masterVer) == 0) {
				MyLogger.getInstance().log(Level.SEVERE,
						"Metadata insertion failed");
				giveException(StringConstants.ERROR_CODES.INTERNAL_ERROR,
						beginTime, apiCall, name);
			}
		} finally {
			lock.unlock();
		}
		if (db.createDbInstance(pod, name, filer, cloneVer, cloneCycle,
				cloneOption, dbServer) == StringConstants.ERROR_CODES.NO_ERROR
				.ordinal()) {
			res.setMessage("The database instance has been created. Cloning in progress.");
			elapsedTime = System.currentTimeMillis() - beginTime;
			mODA.saveMetrics(apiCall, elapsedTime,
					StringConstants.ERROR_CODES.NO_ERROR.ordinal(), name);
		} else {
			mODA.removeMetadata(name, pod);
			giveException(StringConstants.ERROR_CODES.INTERNAL_ERROR,
					beginTime, apiCall, name);
		}
		return res;
	}

	@GET
	@Path("{name}")
	@Produces("application/xml")
	public StageDetails getStageDetails(@PathParam("name") String name) {
		String stageName = name.toLowerCase();
		apiCall = "details";
		beginTime = System.currentTimeMillis();
		MyLogger.getInstance().log(Level.INFO,
				"getStageDBStatus() : " + stageName);

		// Return when input length is > max length allowed for stage name
		if (stageName.length() > 20) {
			giveException(StringConstants.ERROR_CODES.LONG_STAGE_NAME,
					beginTime, apiCall, stageName.substring(0, 20));
		}
		StageDetails sd = mODA.findDBInfo(stageName);
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
			giveException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND, beginTime, apiCall, stageName);
		}
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
			giveException(sd.getExceptionCode(), sd.getExceptionMessage(),
					beginTime, apiCall, stageName);
		}
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics("details", elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), stageName);
		return sd;
	}
	
	
	

	@GET
	@Path("/dbversions")
	@Produces("application/xml")
	public Vector<SnapshotDetail> getCurrentDBVersions() {
		apiCall = "dbversions";
		beginTime = System.currentTimeMillis();
		Vector<SnapshotDetail> retResult = new Vector<SnapshotDetail>();
		retResult = mODA.getSnapshotVersions();
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), null);
		return retResult;
	}
	
	//Changed by snsaha below here
	
		@GET
		@Path("/hierarchy/{stage}")
		@Produces(MediaType.APPLICATION_JSON)
		public Hierarchy getStageHierarchy(
			 @PathParam("stage") String name) {
			String stageName = name.toLowerCase();
			apiCall = "hierarchy";
			beginTime = System.currentTimeMillis();
			StageDetails sd = mODA.findDBInfo(stageName);
			if (sd.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
				giveJSONException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
						beginTime, apiCall, stageName);
			}
			if (sd.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
				giveException(sd.getExceptionCode(), sd.getExceptionMessage(),
						beginTime, apiCall, name);
			}
			
			Hierarchy result = mODA.getHierarchy(stageName);
			elapsedTime = System.currentTimeMillis() - beginTime;
			mODA.saveMetrics(apiCall, elapsedTime,
					StringConstants.ERROR_CODES.NO_ERROR.ordinal(), stageName);
			return result;
		}
		
		
		//Changed by snsaha above here

	
	
	@GET
	@Path("/clonehistory/{stage}")
	@Produces(MediaType.APPLICATION_JSON)
	public Vector<CloneHistory> getDBCloneHistory(
			@PathParam("stage") String name) {
		String stageName = name.toLowerCase();
		apiCall = "clonehistory";
		beginTime = System.currentTimeMillis();
		StageDetails sd = mODA.findDBInfo(stageName);
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
			giveJSONException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
					beginTime, apiCall, stageName);
		}
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
			giveException(sd.getExceptionCode(), sd.getExceptionMessage(),
					beginTime, apiCall, stageName);
		}
		Vector<CloneHistory> result = new Vector<CloneHistory>();
		result = mODA.getCloneHistory(stageName);
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), stageName);
		return result;
	}

	@GET
	@Path("/stagenames/{dbserver}")
	@Produces(MediaType.APPLICATION_JSON)
	public Vector<ServerDetails> getStageNames(
			@PathParam("dbserver") String dbServer) {
		apiCall = "stagenames";
		beginTime = System.currentTimeMillis();
		Vector<ServerDetails> result = new Vector<ServerDetails>();
		result = mODA.getStages(dbServer);
		if (result == null) {
			giveJSONException(StringConstants.ERROR_CODES.INCORRECT_SERVER,
					beginTime, apiCall, dbServer);
		}
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), dbServer);
		return result;
	}

	@GET
	@Path("/clonestatus/{requestID}")
	@Produces(MediaType.APPLICATION_JSON)
	public CloneDetails getDBCloneStatus(
			@PathParam("requestID") String requestID) {
		apiCall = "clonestatus";
		beginTime = System.currentTimeMillis();
		CloneDetails result = mODA.getCloneDetailsByID(requestID);
		if (result == null) {
			giveJSONException(StringConstants.ERROR_CODES.INVALID_REQUEST_ID,
					beginTime, apiCall, requestID);
		}
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), requestID);
		return result;
	}

	@GET
	@Path("/metrics")
	@Produces(MediaType.APPLICATION_JSON)
	public List<MetricsSet> getMetrics(@QueryParam("start") String startDate,
			@QueryParam("end") String endDate) {

		apiCall = "metrics";
		beginTime = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy",
				Locale.ENGLISH);

		if (startDate.equals("today")) {
			Date date = new Date();
			startDate = dateFormat.format(date).toString();
		}
		if (endDate.equals("today")) {
			Date date = new Date();
			endDate = dateFormat.format(date).toString();
		}

		Date end = null;
		try {
			@SuppressWarnings("unused")
			Date start = dateFormat.parse(startDate);
			end = dateFormat.parse(endDate);
		} catch (ParseException e) {
			giveJSONException(StringConstants.ERROR_CODES.INPUT_ERROR,
					beginTime, apiCall, null);
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(end);
		cal.add(Calendar.DATE, 1);
		endDate = dateFormat.format(cal.getTime()).toString();

		List<MetricsSet> retResult = mODA.getMetrics(startDate, endDate);
		if (retResult == null) {
			giveJSONException(
					StringConstants.ERROR_CODES.METRICS_NOT_AVAILABLE,
					beginTime, apiCall, null);
		}
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), null);
		return retResult;
	}

	@POST
	@Path("/clone")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("application/xml")
	public CloneResponse cloneRequest(Input request) {
		apiCall = "clone";
		beginTime = System.currentTimeMillis();
		String error = mODA.validateInput(request);
		String stageName = request.getStageName().toLowerCase();
		if (!error.isEmpty()) {
			MyLogger.getInstance().log(Level.INFO, error);
			elapsedTime = System.currentTimeMillis() - beginTime;
			mODA.saveMetrics(apiCall, elapsedTime,
					StringConstants.ERROR_CODES.INPUT_ERROR.ordinal(),
					stageName);
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("<status>" + newline + "<message>" + error
							+ "</message>" + newline + "<code>"
							+ StringConstants.ERROR_CODES.INPUT_ERROR.ordinal()
							+ "</code>" + newline + "</status>")
					.type("application/xml").build());
		}

		String cloneVer = request.getCloneVersion();
		String cloneCycle = request.getCloneCycle();
		String cloneOption = request.getCloneOption();
		CloneResponse response = new CloneResponse();

		MyLogger.getInstance()
				.log(Level.INFO,
						"cloneRequest() : " + stageName + " version : "
								+ cloneVer + " cycle : " + cloneCycle
								+ " option : " + cloneOption);

		StageDetails sd = mODA.findDBInfo(stageName);
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
			giveException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
					beginTime, apiCall, stageName);
		}
		if (!sd.getIsCloneable()) {
			giveException(StringConstants.ERROR_CODES.NON_CLONABLE_STAGE,
					beginTime, apiCall, stageName);
		}
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
			giveException(sd.getExceptionCode(), sd.getExceptionMessage(),
					beginTime, apiCall, stageName);
		}
		String requestID = mODA.getRequestID();
		response.setRequestID(requestID);
		int ret = mODA.createCloneRequest(stageName, cloneVer, cloneCycle,
				cloneOption, requestID);
		MyLogger.getInstance().log(Level.INFO, "cloneRequest() : ret" + ret);
		if (ret == StringConstants.ERROR_CODES.NO_ERROR.ordinal()) {
			response.setMessage("Cloning in progress");
		}
		if (ret == StringConstants.ERROR_CODES.ALREADY_IN_QUEUE.ordinal()) {
			giveException(StringConstants.ERROR_CODES.ALREADY_IN_QUEUE,
					beginTime, apiCall, stageName);
		}
		if (ret == StringConstants.ERROR_CODES.VERSION_NOT_EXISTS.ordinal()) {
			giveException(StringConstants.ERROR_CODES.VERSION_NOT_EXISTS,
					beginTime, apiCall, stageName);
		}
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), stageName);
		return response;
	}

	@DELETE
	@Path("{id}")
	@Produces("application/xml")
	public Status remove(@PathParam("id") String name) throws IOException,
			InterruptedException {

		String stageName = name.toLowerCase();
		beginTime = System.currentTimeMillis();
		Status res = new Status();

		StageDetails sd = mODA.findDBInfo(stageName);
		apiCall = "remove";

		if (sd.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
			giveException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
					beginTime, apiCall, stageName);
		}
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
			giveException(sd.getExceptionCode(), sd.getExceptionMessage(),
					beginTime, apiCall, stageName);
		}
		if (sd.getCloneStatus().equals("QUEUED")
				|| sd.getCloneStatus().equals("INPROGRESS")) {
			giveException(StringConstants.ERROR_CODES.CLONING_ERROR, beginTime,
					apiCall, stageName);
		}
		MyLogger.getInstance().log(
				Level.INFO,
				"The server corresponding to " + stageName + " is : "
						+ sd.getDBServerName());
		String pod = sd.getPodName();
		if (mODA.checkRemovalInProgress(pod) == 1) {
			giveException(
					StringConstants.ERROR_CODES.REMOVAL_ALREADY_INPROGRESS,
					beginTime, apiCall, stageName);
		}

		mODA.savePodForReuse(pod);
		if (db.removeStage(stageName, sd.getDBServerName()) == StringConstants.ERROR_CODES.NO_ERROR
				.ordinal()) {
			res.setMessage(stageName + " has been successfully removed");
			MyLogger.getInstance().log(Level.INFO,
					stageName + " has been successfully removed");
		} else {
			mODA.removeFromReuse(pod);
			giveException(StringConstants.ERROR_CODES.INTERNAL_ERROR,
					beginTime, apiCall, stageName);
		}
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), stageName);
		return res;
	}

	@GET
	@Path("/isAlive")
	@Produces("application/xml")
	public Status testAvailability() {
		apiCall = "isAlive";
		beginTime = System.currentTimeMillis();
		Status res = new Status();
		res.setMessage("1");
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), null);
		return res;
	}

	@Path("/shutdown")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("application/xml")
	public Status shutdownDb(ShutDbInput request) {

		beginTime = System.currentTimeMillis();
		apiCall = "shutdown";
		Status response = new Status();

		if (request.getStageName() == null) {
			giveException(StringConstants.ERROR_CODES.INPUT_ERROR, beginTime,
					apiCall, null);
		}
		String name = request.getStageName().toLowerCase();
		StageDetails sd = mODA.findDBInfo(name);

		if (sd.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
			giveException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
					beginTime, apiCall, name);

		}
		if (sd.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
			giveException(sd.getExceptionCode(), sd.getExceptionMessage(),
					beginTime, apiCall, name);
		}
		if (sd.getDBStatus() == StringConstants.DBSTATUS.DBNOTCLONED) {
			giveException(StringConstants.ERROR_CODES.DB_NOT_CLONED, beginTime,
					apiCall, name);
		}

		StringConstants.ERROR_CODES code = executeShutdown(sd);

		if (code == StringConstants.ERROR_CODES.ALREADY_DOWN) {
			giveException(StringConstants.ERROR_CODES.ALREADY_DOWN, beginTime,
					apiCall, name);
		} else if (code == StringConstants.ERROR_CODES.NO_ERROR) {
			MyLogger.getInstance().log(Level.INFO, "Shutdown successful");
			response.setMessage(name + " DB shutdown successful");
			elapsedTime = System.currentTimeMillis() - beginTime;
			mODA.saveMetrics(apiCall, elapsedTime,
					StringConstants.ERROR_CODES.NO_ERROR.ordinal(), name);
		}
		return response;
	}

	private StringConstants.ERROR_CODES executeShutdown(StageDetails sd) {
		String podId = sd.getPodName();
		String dbServer = sd.getDBServerName();
		String dbName = "QADBA" + podId;
		String dbNamePay = "QADBB" + podId;
		String dbNamePilot = "QADBC" + podId;
		String failShutdown = " ";
		int success = 0;

		Vector<String> hs = new Vector<String>();
		Vector<String> fail = new Vector<String>();

		if (sd.getDBStatus() == StringConstants.DBSTATUS.DBUP) {
			hs.add(dbName);
		}
		if (sd.getDbStatusPay() == StringConstants.DBSTATUS.DBUP) {
			hs.add(dbNamePay);
		}
		if (sd.getDbStatusPayPilot() == StringConstants.DBSTATUS.DBUP) {
			hs.add(dbNamePilot);
		}

		if (hs.size() == 0) {
			return StringConstants.ERROR_CODES.ALREADY_DOWN;
		}
		for (String dbname : hs) {
			int retCode = db.shutdown(dbname, dbServer);
			success |= retCode;
			if (retCode != 0) {
				fail.add(dbname);
			}
		}
		if (success == 0) {
			return StringConstants.ERROR_CODES.NO_ERROR;
		}
		for (String dbname : fail) {
			failShutdown += "DB " + dbname + " ";
		}
		failShutdown += "failed to start. Contact the DBA.";
		giveException(failShutdown, beginTime, apiCall, sd.getStageName());
		return StringConstants.ERROR_CODES.NO_ERROR;
	}

	@POST
	@Path("/pegaclone")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("application/xml")
	public Status pegaClone(PegaCloneInput request)

	{
		apiCall = "pegaclone";
		beginTime = System.currentTimeMillis();
		Status res = new Status();
		String source = request.getSource();
		String target = request.getTarget();
		String version = request.getVersion();
		if (!(version.equals("10G") || version.equals("11G"))) {
			giveException(StringConstants.ERROR_CODES.INPUT_ERROR, beginTime,
					apiCall, null);
		}

		int exitCode = db.runPegaClone(source, target, version);
		if (exitCode == StringConstants.ERROR_CODES.NO_ERROR.ordinal()) {
			res.setMessage("PEGA clone completed from " + source + " to "
					+ target);
		} else if (exitCode >= StringConstants.ERROR_CODES.PEGA_DB_REFRESH
				.ordinal()
				&& exitCode <= StringConstants.ERROR_CODES.WRONG_NO_OF_ARGUMENTS
						.ordinal()) {
			giveException(StringConstants.ERROR_CODES.values()[exitCode],
					beginTime, apiCall, null);
		} else {
			giveException(StringConstants.ERROR_CODES.INTERNAL_ERROR,
					beginTime, apiCall, null);
		}
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), null);
		return res;
	}

	@POST
	@Path("/sharedb")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("application/xml")
	public Status pointto(ShareDBInput request) {
		beginTime = System.currentTimeMillis();
		Status response = new Status();
		String fromStageName = request.getFromStageName().toLowerCase();
		String toStageName = request.getToStageName().toLowerCase();
		apiCall = "sharedb";

		// get the input from the user and validate

		if (fromStageName.length() == 0) {
			giveException(StringConstants.ERROR_CODES.INPUT_ERROR, beginTime,
					apiCall, toStageName);
		}

		StageDetails fs = mODA.findDBInfo(fromStageName);
		// validation done on the from-stage
		if (fs.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
			giveException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
					beginTime, apiCall, fromStageName);
		}
		if (fs.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
			giveException(fs.getExceptionCode(), fs.getExceptionMessage(),
					beginTime, apiCall, fromStageName);
		}

		// validation done on to-stage
		StageDetails ts = null;
		if (toStageName.length() > 0) {
			ts = mODA.findDBInfo(toStageName);
			if (ts.getErrorCode() == StringConstants.ERROR_CODES.STAGE_NOT_FOUND) {
				giveException(StringConstants.ERROR_CODES.STAGE_NOT_FOUND,
						beginTime, apiCall, toStageName);
			}
			if (ts.getErrorCode() == StringConstants.ERROR_CODES.INTERNAL_ERROR) {
				giveException(ts.getExceptionCode(), ts.getExceptionMessage(),
						beginTime, apiCall, toStageName);
			}

		}

		// get the pod_name and dbserver_name
		String fromstagePod = fs.getPodName();
		String fromServer = fs.getDBServerName();
		StringConstants.ERROR_CODES code = StringConstants.ERROR_CODES.NO_ERROR;

		if (toStageName.length() == 0
				|| fromStageName.equalsIgnoreCase(toStageName)) {
			// to update the pods with the old data and revert the stage back to
			// the original db
			StringConstants.ERROR_CODES retCode = mODA.pointsame(fromStageName);
			if (retCode != StringConstants.ERROR_CODES.NO_ERROR) {
				giveException(retCode, beginTime, apiCall, null);
			}

			fs = mODA.findDBInfo(fromStageName);
			// STARTUP child DB
			try {
				executeStartup(fs);
			} catch (WebApplicationException e) {
				code = StringConstants.ERROR_CODES.CHILD_DB_STARTUP_FAIL;
			}
		} else {
			String toPod = ts.getPodName();
			String toServer = ts.getDBServerName();

			// to update the pods table from "from-stage" to "to-stage"
			StringConstants.ERROR_CODES retCode = mODA.pointother(
					fromStageName, fromstagePod, fromServer, toStageName,
					toPod, toServer);
			if (retCode != StringConstants.ERROR_CODES.NO_ERROR) {
				giveException(retCode, beginTime, apiCall, null);
			}
			// SHUTDOWN child DB
			try {
				executeShutdown(fs);
			} catch (WebApplicationException e) {
				String errorMsg = StringConstants.ERROR_CODES_STRINGS
						.get(StringConstants.ERROR_CODES.CHILD_DB_SHUTDOWN_FAIL
								.ordinal());
				giveException(errorMsg, beginTime, apiCall, fs.getStageName());
			}
		}

		if (db.runTNSGenerator(fromStageName) != 0) {
			code = StringConstants.ERROR_CODES.PUPPET_SCRIPT_ERROR;
		}

		if (code != StringConstants.ERROR_CODES.NO_ERROR) {
			giveException(
					StringConstants.ERROR_CODES_STRINGS.get(code.ordinal()),
					beginTime, apiCall, fs.getStageName());
		}
		response.setMessage("The request completed successfully");
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), fromStageName);
		return response;
	}

	@GET
	@Path("/capacity")
	@Produces(MediaType.APPLICATION_JSON)
	public CapacityCheck getCapacityDetails() {
		apiCall = "capacity";
		beginTime = System.currentTimeMillis();
		CapacityCheck capacity = new CapacityCheck();
		capacity = mODA.checkAvailableCapacity();
		elapsedTime = System.currentTimeMillis() - beginTime;
		mODA.saveMetrics(apiCall, elapsedTime,
				StringConstants.ERROR_CODES.NO_ERROR.ordinal(), null);
		return capacity;
	}

	private WebApplicationException giveException(String exceptionMsg,
			long startTime, String apiType, String stageName) {
		elapsedTime = System.currentTimeMillis() - startTime;
		mODA.saveMetrics(apiType, elapsedTime,
				StringConstants.ERROR_CODES.INTERNAL_ERROR.ordinal(), stageName);
		MyLogger.getInstance().log(Level.INFO, exceptionMsg);
		throw new WebApplicationException(Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity("<status>" + newline + "<message>" + exceptionMsg
						+ "</message>" + newline + "<code>"
						+ StringConstants.ERROR_CODES.INTERNAL_ERROR.ordinal()
						+ "</code>" + newline + "</status>")
				.type("application/xml").build());
	}

	private WebApplicationException giveException(
			StringConstants.ERROR_CODES errorCode, long startTime,
			String apiType, String stageName) {
		elapsedTime = System.currentTimeMillis() - startTime;
		mODA.saveMetrics(apiType, elapsedTime, errorCode.ordinal(), stageName);
		String errorMsg = StringConstants.ERROR_CODES_STRINGS.get(errorCode
				.ordinal());
		MyLogger.getInstance().log(Level.SEVERE, errorMsg);
		throw new WebApplicationException(Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity("<status>" + newline + "<message>" + errorMsg
						+ "</message>" + newline + "<code>"
						+ errorCode.ordinal() + "</code>" + newline
						+ "</status>").type("application/xml").build());
	}

	private WebApplicationException giveJSONException(
			StringConstants.ERROR_CODES errorCode, long startTime,
			String apiType, String stageName) {
		elapsedTime = System.currentTimeMillis() - startTime;
		mODA.saveMetrics(apiType, elapsedTime, errorCode.ordinal(), stageName);
		String errorMsg = StringConstants.ERROR_CODES_STRINGS.get(errorCode
				.ordinal());
		MyLogger.getInstance().log(Level.SEVERE, errorMsg);
		throw new WebApplicationException(Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(errorMsg, errorCode.ordinal()))
				.build());
	}

	private WebApplicationException giveException(int exceptionCode,
			String exceptionMessage, long startTime, String apiType,
			String stageName) {

		StringConstants.ERROR_CODES errc = StringConstants.ERROR_CODES.INTERNAL_ERROR;
		elapsedTime = System.currentTimeMillis() - startTime;
		mODA.saveMetrics(apiType, elapsedTime, errc.ordinal(), stageName,
				exceptionCode, exceptionMessage);
		String eMsg = StringConstants.ERROR_CODES_STRINGS.get(errc.ordinal());
		MyLogger.getInstance().log(Level.SEVERE, exceptionMessage);
		throw new WebApplicationException(Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity("<status>" + newline + "<message>" + eMsg + "##\n"
						+ exceptionMessage + "</message>" + newline + "<code>"
						+ exceptionCode + "</code>" + newline + "</status>")
				.type("application/xml").build());
	}
	
	
	  //Added by Chandra Gaurav
		@GET
		@Path("/dbservers")
		@Produces(MediaType.APPLICATION_JSON)
		public Vector<DbServer> getListOfdbServers() {
			apiCall = "dbservers";
			beginTime = System.currentTimeMillis();
			Vector<DbServer> retResult = new Vector<DbServer>();
			retResult = mODA.getDbServersList();
			if (retResult == null) {
				giveJSONException(StringConstants.ERROR_CODES.INCORRECT_SERVER,
						beginTime, apiCall, null);
			}
			elapsedTime = System.currentTimeMillis() - beginTime;
			mODA.saveMetrics(apiCall, elapsedTime,
					StringConstants.ERROR_CODES.NO_ERROR.ordinal(), null);
			return retResult;
		}
		//Method ended here

}
