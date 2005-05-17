/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.common.command;

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class EMFCommonMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.common.command.EMFCommonResources"; //$NON-NLS-1$

	private EMFCommonMessages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, EMFCommonMessages.class);
	}
	public static String _UI_UnexecutableCommand_label;
	public static String _UI_UnexecutableCommand_description;
	public static String _UI_IdentityCommand_label;
	public static String _UI_IdentityCommand_description;
	public static String _UI_IgnoreException_exception;
	public static String _UI_CompoundCommand_label;
	public static String _UI_CompoundCommand_description;
	public static String _UI_CommandWrapper_label;
	public static String _UI_CommandWrapper_description;
	public static String _EXC_Method_not_implemented;
	public static String _UI_AbstractCommand_label;
	public static String _UI_AbstractCommand_description;
}
