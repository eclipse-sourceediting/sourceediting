/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
/*
 * Created on Mar 17, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.sse.ui.style;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.wst.sse.core.text.IStructuredDocument;



/**
 * @author davidw
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public interface IHighlighter extends LineStyleListener {

	public void addProvider(String partitionType, LineStyleProvider provider);

	void install(ITextViewer viewer);

	public void removeProvider(String partitionType);

	public void setDocument(IStructuredDocument structuredDocument);

	void uninstall();



}
