/*******************************************************************************
 * Copyright (c) 2007, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Gary Karasiuk (IBM Corporation) - initial implementation
 *     Jesper Steen M�ller - adapted for XSL
 *******************************************************************************/

package org.eclipse.wst.xsl.launching.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Some miscellaneous helper methods. 
 * @author karasiuk
 *
 */
public class Misc {
	
	private static DateFormat _df = new SimpleDateFormat("HH:mm:ss.SSSS"); //$NON-NLS-1$
	
	public static void close(InputStream in){
		if (in == null)return;
		try {
			in.close();
		}
		catch (IOException e){
			// eat it
		}
	}

	public static void close(OutputStream out) {
		if (out == null)return;
		try {
			out.close();
		}
		catch (IOException e){
			// eat it
		}		
	}
	
	/**
	 * Write a line to the console for debugging.
	 * @param line
	 */
	public static void log(String line){
		System.err.println(timestampIt(line));  
	}
	
	public static String timestampIt(String line){
		Date date = new Date();
		long thread = Thread.currentThread().getId();
		return _df.format(date) + " " + thread + " " + line;//$NON-NLS-1$//$NON-NLS-2$
	}
	
	public static void log(StringBuffer b){
		log(b.toString());
		b.setLength(0);
	}
	
	public static void niy(String msg){
		if (msg == null)msg = "Sorry, this function is not implemented yet"; //$NON-NLS-1$
		throw new RuntimeException(msg);
	}
	
}
