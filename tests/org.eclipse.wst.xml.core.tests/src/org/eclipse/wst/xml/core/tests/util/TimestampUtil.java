/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.util;

import java.util.Calendar;
import java.util.Date;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * @author davidw
 *
 * This class provides one timestamp per VM run.
 * (Technically, per class loading.)
 */
public class TimestampUtil {

	private static Date timestamp = null;
	private static DateFormat shortFormat = new SimpleDateFormat("yyyy'-'MM'-'dd");
	//	private static DateFormat longFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'-'kk'-'mm'-'ss");

	private static String nowShort = null;

	public static String timestamp() {

		if (TimestampUtil.nowShort == null) {
			TimestampUtil.nowShort = shortFormat.format(ensureTimestamp());
		}
		return TimestampUtil.nowShort;
	}

	protected static Date ensureTimestamp() {
		// just calculate 'timestamp' once per class loading, 
		// so this 'timestamp' remains the same during 
		// entire run. 
		if (TimestampUtil.timestamp == null) {
			Calendar calendar = Calendar.getInstance();
			TimestampUtil.timestamp = calendar.getTime();
		}
		return TimestampUtil.timestamp;
	}

}
