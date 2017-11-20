/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class TestAtSign {

	public TestAtSign() {
		super();
	}

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties(); //System.getProperties();
		properties.put("at sign", "this is an \u0040 sign");
		properties.list(System.out);
		FileOutputStream outputStream = new FileOutputStream("testAt.txt");
		properties.store(outputStream, "tests");
		outputStream.close();
		InputStream inStream = new FileInputStream("testAt.txt");
		Properties readProperties = new Properties();
		readProperties.load(inStream);
		String atSign = (String) readProperties.get("at sign");
		System.out.println("at sign from read: " + atSign);



	}
}