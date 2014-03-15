package com.paypal.sea.s2dbservices;

import java.util.HashMap;

public final class StringConstants {

	public enum ERROR_CODES {
		NO_ERROR, STAGE_NOT_FOUND, INTERNAL_ERROR, ALREADY_UP, STAGE_REPEAT, DB_NOT_CLONED, VERSION_NOT_EXISTS, INPUT_ERROR, CLONING_ERROR, CAPACITY_FULL, ALREADY_IN_QUEUE, ALREADY_DOWN, REMOVAL_ALREADY_INPROGRESS, PEGA_DB_REFRESH, SOURCE_DB_NOT_FOUND, TARGET_DB_NOT_FOUND, PEGA_MASTER_DOWN, WRONG_NO_OF_ARGUMENTS, METRICS_NOT_AVAILABLE, POINT_DB_ALREADY, INVALID_REQUEST_ID, INCORRECT_SERVER, LONG_STAGE_NAME, CATLOGDB_INACCESSIBLE, NON_CLONABLE_STAGE, CHILD_DB_STARTUP_FAIL, CHILD_DB_SHUTDOWN_FAIL, PUPPET_SCRIPT_ERROR, SCRIPT_NAME_INCORRECT, LISTENER_FLAG_OFF
	};

	public static HashMap<Integer, String> ERROR_CODES_STRINGS = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(ERROR_CODES.NO_ERROR.ordinal(), "No error");
			put(ERROR_CODES.STAGE_NOT_FOUND.ordinal(), "Stage not found");
			put(ERROR_CODES.INTERNAL_ERROR.ordinal(),
					"Operation unsuccessful due to internal error. Please contact the DBA.");
			put(ERROR_CODES.ALREADY_UP.ordinal(),
					"All the databases connected to this stage are already up");
			put(ERROR_CODES.STAGE_REPEAT.ordinal(), "Stage already present");
			put(ERROR_CODES.DB_NOT_CLONED.ordinal(), "DB not cloned");
			put(ERROR_CODES.VERSION_NOT_EXISTS.ordinal(),
					"Version does not exist");
			put(ERROR_CODES.INPUT_ERROR.ordinal(), "Incorrect input");
			put(ERROR_CODES.CLONING_ERROR.ordinal(),
					"Cannot delete the stage. Cloning in progress");
			put(ERROR_CODES.CAPACITY_FULL.ordinal(),
					"Capacity full. Cant allocate more space");
			put(ERROR_CODES.ALREADY_IN_QUEUE.ordinal(),
					"Already in queue for cloning");
			put(ERROR_CODES.ALREADY_DOWN.ordinal(),
					"All the databases connected to this stage are already down");
			put(ERROR_CODES.REMOVAL_ALREADY_INPROGRESS.ordinal(),
					"Removal already in progress");
			put(ERROR_CODES.PEGA_DB_REFRESH.ordinal(),
					"Don't Try to Refresh PEGA Master DB. PG11MSTR and PDVSMSTR can only be the source DB");
			put(ERROR_CODES.SOURCE_DB_NOT_FOUND.ordinal(),
					"Source DB not found in the list. Please contact the DBA");
			put(ERROR_CODES.TARGET_DB_NOT_FOUND.ordinal(),
					"Target DB not found in the list. Please contact the DBA");
			put(ERROR_CODES.PEGA_MASTER_DOWN.ordinal(),
					"PEGA MASTER down for another clone");
			put(ERROR_CODES.WRONG_NO_OF_ARGUMENTS.ordinal(),
					"Wrong number of arguments");
			put(ERROR_CODES.METRICS_NOT_AVAILABLE.ordinal(),
					"No information available for selected dates");
			put(ERROR_CODES.POINT_DB_ALREADY.ordinal(),
					"Stage is already pointing to the requested DB");
			put(ERROR_CODES.INVALID_REQUEST_ID.ordinal(), "Invalid Request ID");
			put(ERROR_CODES.INCORRECT_SERVER.ordinal(), "Invalid server name");
			put(ERROR_CODES.LONG_STAGE_NAME.ordinal(), "Incorrect input. Stage name too long");
			put(ERROR_CODES.CATLOGDB_INACCESSIBLE.ordinal(), "CATLOGDB inaccessible. Please try again");
			put(ERROR_CODES.NON_CLONABLE_STAGE.ordinal(), "You are requesting a clone of a non-clonable DB, hence it is rejected. Please contact Frontline for further assistance");

			// Errors which are to be displayed directly to the user must be
			// written above this comment.

			// The errors below this comment are only used inside the code and
			// not displayed directly to the user.

			put(ERROR_CODES.CHILD_DB_STARTUP_FAIL.ordinal(),
					"Restore successful. DB startup failed for the given stage after restore. Please contact the DBA");
			put(ERROR_CODES.CHILD_DB_SHUTDOWN_FAIL.ordinal(),
					"Share DB request successful. DB shutdown failed for the child stage after pointing. Please contact the DBA");
			put(ERROR_CODES.PUPPET_SCRIPT_ERROR.ordinal(),
					"Tns generation unsuccessful. Please contact the DBA.");
			put(ERROR_CODES.SCRIPT_NAME_INCORRECT.ordinal(),
					"Script name incorrect in properties file. Check $JETTY_HOME/resources/s2dbservices.properties for details");
			put(ERROR_CODES.LISTENER_FLAG_OFF.ordinal(),
					"Listener flag is set to zero in properties file. Check $JETTY_HOME/resources/s2dbservices.properties for details");
		}
	};

	public enum DBSTATUS {
		DBUP, DBNOTCLONED, DBDOWN, DBERROR, NA
	};

	public static HashMap<Integer, String> DBSTATUSSTRING = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(DBSTATUS.DBUP.ordinal(), "DBUP");
			put(DBSTATUS.DBNOTCLONED.ordinal(), "DBNOTCLONED");
			put(DBSTATUS.DBDOWN.ordinal(), "DBDOWN");
			put(DBSTATUS.DBERROR.ordinal(), "DBERROR");
			put(DBSTATUS.NA.ordinal(), "");
		}
	};

}
