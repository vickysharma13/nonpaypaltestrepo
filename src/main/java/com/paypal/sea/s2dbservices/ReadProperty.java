package com.paypal.sea.s2dbservices;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

public class ReadProperty {

	Properties pro = new Properties();
	Properties externalProp = new Properties();

	public ReadProperty() {
		try {

			ClassLoader cl = this.getClass().getClassLoader();
			InputStream inputStream = cl
					.getResourceAsStream("Credentials.properties");
			InputStream inputStreamExternal = cl
					.getResourceAsStream("s2dbservices.properties");
			pro.load(inputStream);
			if (inputStreamExternal != null) {
				externalProp.load(inputStreamExternal);
			}
			inputStream.close();
			if (inputStreamExternal != null) {
				inputStreamExternal.close();
			}
		} catch (IOException e) {
			MyLogger.getInstance().log(Level.SEVERE,
					"EXCEPTION IN READ PROPERTY");
		}
	}

	public String getValue(String key) {
		String value = null;
		value = externalProp.getProperty(key);
		if (value == null) {
			value = pro.getProperty(key);
		}
		return value;
	}
}