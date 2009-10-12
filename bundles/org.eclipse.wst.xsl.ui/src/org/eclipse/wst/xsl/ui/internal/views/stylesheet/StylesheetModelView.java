/*******************************************************************************
 * Copyright (c) 2008, 2009 Chase Technology Ltd and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 244674 - Enhanced and cleaned up view 
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.views.stylesheet;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.core.model.XSLNode;

/**
 * 
 *
 */
public class StylesheetModelView extends ViewPart
{
	private IEditorPart activeEditor;
	//private boolean isFiringSelection;
	private TreeViewer tv;
	private IPartListener partListener = new IPartListener(){
		
		public void partActivated(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				IEditorPart editor = (IEditorPart)part;
				IEditorInput edInput = editor.getEditorInput();
				if (edInput instanceof IFileEditorInput)
				{
					IFileEditorInput edFileInput = (IFileEditorInput)edInput;
					if (XSLCore.isXSLFile(edFileInput.getFile()))
					{
						activeEditor = editor;
						StylesheetModel model = XSLCore.getInstance().getStylesheet(edFileInput.getFile());
						tv.setInput(model.getStylesheet());
					}
				}
			}
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
		}

		public void partClosed(IWorkbenchPart part)
		{
			if (part == activeEditor)
			{
				tv.setInput(null);
				activeEditor = null;
			}
		}

		public void partDeactivated(IWorkbenchPart part)
		{
		}

		public void partOpened(IWorkbenchPart part)
		{
		}
		
	}; 

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);
		site.getPage().addPartListener(partListener);
	}
	
	@Override
	public void dispose()
	{
		getSite().getPage().removePartListener(partListener);
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		Tree tree = new Tree(parent,SWT.NONE);
		this.tv = new TreeViewer(tree);
		tv.setContentProvider(new BaseWorkbenchContentProvider());
		tv.setLabelProvider(new WorkbenchLabelProvider());
		tv.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event)
			{
				if (getSite().getPage().getActivePart() == StylesheetModelView.this)
					handleTreeSelection((IStructuredSelection)event.getSelection(),false);
			}
		});
		tv.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event)
			{
				handleTreeSelection((IStructuredSelection)event.getSelection(),true);
			}
			
		});
	}
	
	private void handleTreeSelection(IStructuredSelection selection, boolean reveal)
	{
		if (activeEditor != null)
		{
//			isFiringSelection = true;
			if (selection.getFirstElement() instanceof XSLNode)
			{
				XSLNode node = (XSLNode)selection.getFirstElement();
				
				ITextEditor textEditor = (ITextEditor)activeEditor.getAdapter(ITextEditor.class);
				if (textEditor != null)
				{
					//if (reveal)
						textEditor.selectAndReveal(node.getOffset(), 0);
					//else
						// textEditor.setHighlightRange(node.getOffset(), 0, true);
				}
			}
//			isFiringSelection = false;
		}
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

}
