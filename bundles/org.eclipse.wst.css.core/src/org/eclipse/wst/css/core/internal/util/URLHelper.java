/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.util;



import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.wst.sse.core.internal.util.PathHelper;

/**
 * Hyperlink manager. Proved useful services like link conversion An utility
 * class to help other parsre modules to convert links
 */
public class URLHelper {

	private static final String FILE_PROTOCOL_SEARCH_SIG = "file:/";//$NON-NLS-1$
	private static final String FILE_PROTOCOL_SIG = "file:///";//$NON-NLS-1$
	private static String RELATIVE_PATH_SIG = "..";//$NON-NLS-1$	
	private static final String FORWARD_SLASH = "/";//$NON-NLS-1$	
	private URL fBaseUrl = null; // base url. assumed to a absulute url
	private String fBaseUrlString = null;
	private URL fDocRoot = null;
	private String fDocRootString = null;

	public URLHelper(String baseUrl) {
		initialize(baseUrl, null);
	}

	public URLHelper(String baseUrl, String docRoot) {
		initialize(baseUrl, docRoot);
	}

	/**
	 * Special adujust for file url
	 */
	public String adjustFileProtocolUrl(String url) {
		if (url.startsWith(FILE_PROTOCOL_SEARCH_SIG)) {
			url = FILE_PROTOCOL_SIG + url.substring(FILE_PROTOCOL_SEARCH_SIG.length());
		}
		return url;
	}

	private String convert(String url, URL baseUrl, String urlString) {
		String absUrl = url;
		if (baseUrl != null) {
			try {
				// do special thing file protocol
				if (baseUrl.getProtocol().equalsIgnoreCase("file")) {//$NON-NLS-1$
					// remove the fist
					if (url.startsWith("/"))//$NON-NLS-1$
						url = url.substring(1);
					// empty string causes malformed exception
					String tempUrl = url;
					if ("".equals(url))//$NON-NLS-1$
						tempUrl = " ";//$NON-NLS-1$
					URL newUrl = new URL(baseUrl, tempUrl);
					// do extra math for file protocol to support ie based
					absUrl = adjustFileProtocolUrl(newUrl.toString());
				}
				else {
					URL newUrl = new URL(fBaseUrl, url);
					absUrl = newUrl.toString();
				}
			}
			catch (MalformedURLException me) {
			}
			// convert everything to forward slash
			absUrl = PathHelper.switchToForwardSlashes(absUrl);
		}
		else {
			// the given may be based on file
			if (urlString != null) {
				// swich the url to proper direction
				url = PathHelper.removeLeadingSeparator(url);
				File fle = new File(urlString, url);
				absUrl = fle.getPath();

			}
			// convert everything to forward slash
			absUrl = PathHelper.switchToForwardSlashes(absUrl);

			// if the path ends with ".." we need to add a terminating slash
			// or
			// else the link will not be resolved correctly. (it will look
			// like
			// /url/path/to/somewhere/.. instead of /url/path/to/
			if (absUrl.endsWith(FORWARD_SLASH + RELATIVE_PATH_SIG)) {
				absUrl += FORWARD_SLASH;
			}
		}

		// resolve relative path to absolute
		absUrl = PathHelper.adjustPath(absUrl);
		return absUrl;
	}

	private void initialize(String baseUrl, String docRoot) {
		//
		// Find out whether baes url is a url or file name
		//
		try {
			String temp = PathHelper.appendTrailingURLSlash(baseUrl);
			fBaseUrl = new URL(temp);
		}
		catch (MalformedURLException mue) {
		}
		if (fBaseUrl == null) {
			// it is a file based url
			fBaseUrlString = baseUrl;
		}

		// do the same for doc root
		if (docRoot != null) {
			try {
				String temp = PathHelper.appendTrailingURLSlash(docRoot);
				fDocRoot = new URL(temp);

			}
			catch (MalformedURLException mue) {
			}
			if (fDocRoot == null) {
				// it is a file based url
				fDocRootString = docRoot;
			}
		}
	}

	/**
	 * Convert the given url to a abolute url using the base url Input url can
	 * be of any othe following format Absolute urls -------------- .
	 * http://www.foo.com/ . http://www.foo.com . http://www.foo.com/folder .
	 * http://www.foo.com/folder/ . http://www.foo.com/index.html .
	 * http://www.foo.com/folder/index.html .
	 * http://www.foo.com/folder/../index.html Url relative on document root
	 * ------------------------- . / . /folder . /index.html .
	 * /folder/index.html . /folder/../index.html
	 * 
	 * Self relative ------------------------- . index.html . ./index.html .
	 * ../index.html . folder/index.html . folder/../index.html
	 * 
	 * file based url adds another dimension since it doesn't have a document
	 * root So uses fDocRoot if provided
	 */
	public String toAbsolute(String url) {
		String absUrl = url;

		URL newUrl = null;
		// try to see it is a absolute url
		try {
			newUrl = new URL(url);
		}
		catch (MalformedURLException me) {
		}
		// if document root is provided
		// do special case
		if (newUrl == null) {
			if (fDocRoot == null && fDocRootString == null) {
				// try to check relative url
				absUrl = convert(url, fBaseUrl, fBaseUrlString);
			}
			else {
				// doc root is provided
				// if the url is a doc root based use doc root

				if (url.startsWith("/"))//$NON-NLS-1$
					absUrl = convert(url, fDocRoot, fDocRootString);
				else
					absUrl = convert(url, fBaseUrl, fBaseUrlString);

			}
		}
		return absUrl;
	}

	/**
	 * Convert from an absolute url to relative url based on the given url
	 * Both are assumed to be ablsute url
	 */
	public String toRelative(String url, String documentUrl) {
		String output = url;
		// assuming both urls are absolute
		try {
			URL inputUrl = new URL(url);
			URL docUrl = new URL(documentUrl);
			// filter out if the urls are not fro the same domain
			if (!inputUrl.getProtocol().equals(docUrl.getProtocol()) || !inputUrl.getHost().equals(docUrl.getHost()) || inputUrl.getPort() != docUrl.getPort())
				return output;
			// both url are coming form the same place
			// strip off the domain parts
			String inputName = inputUrl.getFile();
			String docName = docUrl.getFile();
			output = PathHelper.convertToRelative(inputName, docName);
		}
		catch (MalformedURLException me) {
			output = null;
		}
		return output;
	}
}
