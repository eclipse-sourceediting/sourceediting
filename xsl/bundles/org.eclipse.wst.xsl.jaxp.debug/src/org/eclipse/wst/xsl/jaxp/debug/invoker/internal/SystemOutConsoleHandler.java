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

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;

public class SystemOutConsoleHandler extends ConsoleHandler {
    protected void setOutputStream(OutputStream out) throws SecurityException {
        super.setOutputStream(System.out);
    }
}