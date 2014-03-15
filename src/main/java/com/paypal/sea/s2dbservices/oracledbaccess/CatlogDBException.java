package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.paypal.sea.s2dbservices.StringConstants;

public class CatlogDBException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	public static String newline = System.getProperty("line.separator");
	
	public CatlogDBException() {
		super(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("<status>"
								+ newline
								+ "<message>"
								+ StringConstants.ERROR_CODES_STRINGS
										.get(StringConstants.ERROR_CODES.CATLOGDB_INACCESSIBLE
												.ordinal())
								+ "</message>"
								+ newline
								+ "<code>"
								+ StringConstants.ERROR_CODES.CATLOGDB_INACCESSIBLE
										.ordinal() + "</code>" + newline
								+ "</status>").type("application/xml").build());
	}
}
