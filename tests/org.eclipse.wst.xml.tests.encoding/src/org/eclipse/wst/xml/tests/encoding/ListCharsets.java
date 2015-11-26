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
package org.eclipse.wst.xml.tests.encoding;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Small class to list charset detected for a particular VM. Simple run as Java
 * Applications to get output to standard out.
 */
public class ListCharsets {
	private final String tab = "\t";

	public static void main(String[] args) {

		ListCharsets thisApp = new ListCharsets();

		System.out.println();

		System.out.println("Current Locale: " + Locale.getDefault());

		System.out.println();

		String name = System.getProperty("java.fullversion");
		if (name == null) {
			name = System.getProperty("java.version") + " (" + System.getProperty("java.runtime.version") + ")";
		}
		System.out.println("JRE version: " + name);

		System.getProperties().list(System.out);

		thisApp.listOfLocales();

		System.out.println("file.encoding.pkg: " + System.getProperty("file.encoding.pkg"));
		System.out.println("file.encoding: " + System.getProperty("file.encoding"));
		System.out.println();

		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		System.out.println();
		thisApp.listOfCharsets();
	}

	private void listOfLocales() {
		System.out.println("Available Locales");
		Locale[] locales = Locale.getAvailableLocales();
		for (int i = 0; i < locales.length; i++) {
			System.out.println(locales[i]);

		}

	}

	private void listOfCharsets() {
		System.out.println("Available Charsets");
		int count = 0;
		Map map = Charset.availableCharsets();
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			count++;
			// Get charset name
			String charsetName = (String) it.next();
			System.out.println(count + ". " + " Charsetname: " + charsetName);
			// Get charset
			Charset charset = Charset.forName(charsetName);
			System.out.println(tab + "displayName: " + charset.displayName(Locale.getDefault()));
			Set set = charset.aliases();
			System.out.println(tab + "aliases: " + set);
		}
	}
}