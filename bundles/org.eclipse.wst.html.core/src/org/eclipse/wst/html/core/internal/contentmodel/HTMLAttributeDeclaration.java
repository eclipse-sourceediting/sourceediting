/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;



/**
 * This interface is intended to be a public interface which has
 * interfaces defined in both of {@link <code>CMAttributeDeclaration</code>}
 * and {@link <code>HTMLCMNode</code>}.<br>
 */
public interface HTMLAttributeDeclaration extends CMAttributeDeclaration {
	String IS_HTML = "isHTML"; //$NON-NLS-1$
}
