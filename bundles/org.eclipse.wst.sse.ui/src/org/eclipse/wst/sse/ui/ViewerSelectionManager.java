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
package org.eclipse.wst.sse.ui;



import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.view.events.ICaretListener;
import org.eclipse.wst.sse.ui.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.view.events.ITextSelectionListener;


public interface ViewerSelectionManager extends ICaretListener, IDoubleClickListener, ISelectionChangedListener, SelectionListener {

	void addNodeDoubleClickListener(IDoubleClickListener listener);

	void addNodeSelectionListener(INodeSelectionListener listener);

	void addTextSelectionListener(ITextSelectionListener listener);

	int getCaretPosition();

	List getSelectedNodes();

	void release();

	void removeNodeDoubleClickListener(IDoubleClickListener listener);

	void removeNodeSelectionListener(INodeSelectionListener listener);

	void removeTextSelectionListener(ITextSelectionListener listener);

	void setModel(IStructuredModel newModel);

	void setTextViewer(ITextViewer newTextViewer);
}
