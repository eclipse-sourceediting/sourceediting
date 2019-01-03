/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint;

/**
 * @author pavery
 */
public interface IBreakpointConstants {
	String ATTR_HIDDEN = "hidden"; //$NON-NLS-1$
	/**
	 * Setters of this attribute should use '/'for segment separators when
	 * representing paths.
	 */
	String RESOURCE_PATH = "org.eclipse.wst.sse.ui.extensions.breakpoint.path"; //$NON-NLS-1$
}
