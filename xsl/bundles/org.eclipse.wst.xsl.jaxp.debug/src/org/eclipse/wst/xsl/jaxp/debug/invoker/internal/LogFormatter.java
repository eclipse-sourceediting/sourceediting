/********************************************************************************
 * Copyright (c) 2022 IBM
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Stewart Francis - initial API and implementation
 ********************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.invoker.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        ZonedDateTime zdt = ZonedDateTime.ofInstant(
        		Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault());
        java.util.Formatter formatter = new java.util.Formatter(builder);
        try {
            formatter.format(
                "%1$tT,%1$tL %2$-7s [%3$s] ", //$NON-NLS-1$
                zdt,
                record.getLevel().getLocalizedName(),
                record.getThreadID());
		
            //Only show the last segment of the logger name
            String loggerName = record.getLoggerName();
            int lastDot = loggerName.lastIndexOf('.');
            //If there are no dots append the whole thing
            if (lastDot < 0) {
                builder.append(loggerName);
            } else if (lastDot == loggerName.length() - 1) {
            	//If last char is a dot, check for a previous dot
            	int secondLastDot = loggerName.lastIndexOf('.', lastDot - 1);
            	if (secondLastDot < 0) {
            		//Append from the previous dot if any
            		builder.append(loggerName, secondLastDot + 1, loggerName.length());
            	} else {
            		//Else append the whole thing
            		builder.append(loggerName);
            	}
            } else {
            	//Append the last segment
                builder.append(loggerName, lastDot + 1, loggerName.length());
            }

			builder.append(" - "); //$NON-NLS-1$
			builder.append(record.getMessage());

            //Append any exception information
            String throwable = ""; //$NON-NLS-1$
	        if (record.getThrown() != null) {
	            StringWriter sw = new StringWriter();
	            PrintWriter pw = new PrintWriter(sw);
	            pw.println();
	            record.getThrown().printStackTrace(pw);
	            pw.close();
	            throwable = sw.toString();
	        }
	        
	        formatter.format("%s%n", throwable); //$NON-NLS-1$
            return builder.toString();
        } finally {
            formatter.close();
        }
    } 
}
