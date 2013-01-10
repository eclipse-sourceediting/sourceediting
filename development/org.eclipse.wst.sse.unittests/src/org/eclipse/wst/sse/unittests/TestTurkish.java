/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.sse.unittests;

import java.util.Locale;

/**
 * TestTurkish
 * See http://www.i18nguy.com/unicode/turkish-i18n.html
 */

public class TestTurkish {
	public static void main(String[] args) {
		String turkishLowerCase = "\u0131" + "\u0069";
		String turkishUpperCase = "\u0130" + "\u0049";
		Locale[] locales = Locale.getAvailableLocales();
		for (int i = 0; i < locales.length; i++) {
			// System.out.println(locales[i]);
		}
		Locale turkishLocale = new Locale("TR");
		String testUppercase = turkishLowerCase.toUpperCase(turkishLocale);
		System.out.println(turkishUpperCase.equals(testUppercase));
	}
}
