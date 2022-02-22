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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class LoggingConfig {
    public LoggingConfig() throws SecurityException, IOException {
        final LogManager logManager = LogManager.getLogManager();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Properties props = new Properties();
        String consoleHandler = SystemOutConsoleHandler.class.getName();

        props.put("handlers", consoleHandler); //$NON-NLS-1$
        props.put(".level", Level.FINE.getName()); //$NON-NLS-1$
        props.put(consoleHandler + ".level", Level.FINE.getName()); //$NON-NLS-1$
        props.put(consoleHandler + ".formatter", LogFormatter.class.getName()); //$NON-NLS-1$

        props.store(os, null);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        logManager.readConfiguration(is);
    }
}
