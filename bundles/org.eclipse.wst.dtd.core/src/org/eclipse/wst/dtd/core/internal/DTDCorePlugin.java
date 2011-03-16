/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal;

import org.eclipse.core.runtime.Plugin;

public class DTDCorePlugin extends Plugin {
	private static DTDCorePlugin instance;

	public synchronized static DTDCorePlugin getInstance() {
		return instance;
	}

	public static DTDCorePlugin getPlugin() {
		return instance;
	}

	public DTDCorePlugin() {
		super();
		instance = this;
	}

}
