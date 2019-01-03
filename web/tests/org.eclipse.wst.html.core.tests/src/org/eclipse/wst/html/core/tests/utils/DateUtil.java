/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.utils;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author davidw
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DateUtil {

	/**
	 * Constructor for AllTests.
	 * @param name
	 */
	private static String now = null;


	public static String now() {
		// just calculate once, so whole run as same timestamp
		if (DateUtil.now == null) {
			DateFormat format = new SimpleDateFormat("yyyy'-'MM'-'dd'-'kk'-'mm'-'ss");
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			DateUtil.now = format.format(today);
		}
		return DateUtil.now;
	}

}
