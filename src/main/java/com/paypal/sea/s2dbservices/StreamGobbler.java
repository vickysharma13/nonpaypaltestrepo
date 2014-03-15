package com.paypal.sea.s2dbservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
	private InputStream is;
	private StringBuffer msb;

	public StreamGobbler(InputStream is, String type) {
		this.is = is;
		msb = new StringBuffer();
	}

	public StreamGobbler(InputStream is) {
		this.is = is;
		msb = new StringBuffer();
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				msb.append(line);
				msb.append("\n");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public StringBuffer getMessages() {
		return msb;
	}
}