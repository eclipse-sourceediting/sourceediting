/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.json.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.provisonnal.com.eclipsesource.json.ParseException;

/**
 * Nodejs Tern helper.
 * 
 */
public class HttpHelper {

	// properties for remote access
	public static final boolean DEFAULT_REMOTE_ACCESS = false;
	public static final int DEFAULT_REMOTE_PORT = 1234;

	// properties for direct access
	public static final long DEFAULT_TIMEOUT = 200L; // 200ms
	public static final int DEFAULT_TEST_NUMBER = 50; // try to retrieve the
														// node.js port 50
														// each time on timeout
														// (max=50*200ms=10000ms).

	public static final boolean DEFAULT_PERSISTENT = false;

	public static JsonValue makeRequest(String url) throws IOException {
		long startTime = 0;
		HttpClient httpClient = new DefaultHttpClient();
		try {
			// Post JSON Tern doc
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			InputStream in = entity.getContent();
			// Check the status
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				// node.js server throws error
				/*
				 * String message = IOUtils.toString(in); if
				 * (StringUtils.isEmpty(message)) { throw new
				 * TernException(statusLine.toString()); } throw new
				 * TernException(message);
				 */
			}

			try {
				JsonValue response = JsonValue.readFrom(new InputStreamReader(
						in));
				return response;
			} catch (ParseException e) {
				throw new IOException(e);
			}
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			throw new IOException(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public static long getElapsedTimeInMs(long startTime) {
		return ((System.nanoTime() - startTime) / 1000000L);
	}

}
