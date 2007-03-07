/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.jsdt.ui.JavaElementContentProvider;
import org.eclipse.wst.jsdt.ui.JavaElementLabelProvider;
import org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIPlugin;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.eclipse.wst.html.ui.views.contentoutline.HTMLContentOutlineConfiguration;

/**
 * Configuration for outline view page which shows JSP content.
 * 
 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration
 * @since 1.0
 */
public class JSPContentOutlineConfiguration extends
		HTMLContentOutlineConfiguration {

	// private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	// TODO: Automate the loading of a real configuration based on the model
	// type at
	// creation time; clear on unConfigure so that a new embedded
	// configuration can
	// be used
	// private StructuredContentOutlineConfiguration fEmbeddedConfiguration =
	// null;

	
	
//	private StandardJavaElementContentProvider fJavaElementProvider;
//	private JavaElementLabelProvider fJavaElementLabelProvider;
//	
//	private StandardJavaElementContentProvider getElementProvider(){
//		if(fJavaElementProvider==null){
//			fJavaElementProvider = new StandardJavaElementContentProvider();
//		}
//		return fJavaElementProvider;
//	}
	
//	private StandardJavaElementContentProvider getLabelElementProvider(){
//		if(fJavaElementLabelProvider==null){
//			fJavaElementLabelProvider = new JavaElementLabelProvider();
//			
//		}
//		return fJavaElementProvider;
//	}
	
	protected XMLNodeActionManager createNodeActionManager(TreeViewer treeViewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method createNodeActionManager" );
		return super.createNodeActionManager(treeViewer);
	}

	@Override
	protected IContributionItem[] createMenuContributions(TreeViewer viewer) {
		// TODO Auto-generated method stub
		
		System.out.println("Umiplement method createMenuContributions" );
		return super.createMenuContributions(viewer);
	}

	@Override
	protected void enableShowAttributes(boolean showAttributes,
			TreeViewer treeViewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method enableShowAttributes" );
		super.enableShowAttributes(showAttributes, treeViewer);
	}

	@Override
	public IContentProvider getContentProvider(TreeViewer viewer) {
		// TODO Auto-generated method stub
		
		//System.out.println("Umiplement method getContentProvider" );
		return super.getContentProvider(viewer);
        
		//return new JSDTElementContentProvider(super.getContentProvider(viewer));
        
	}

	@Override
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		// TODO Auto-generated method stub
//		System.out.println("Umiplement method getLabelProvider" );
        //return new JFaceNodeAdapterForJSDT();
		//return super.getLabelProvider(viewer);
		return new JSDTLabelElementProvider(super.getLabelProvider(viewer));
	}

	@Override
	public IMenuListener getMenuListener(TreeViewer viewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getMenuListener" );
		return super.getMenuListener(viewer);
	}

	@Override
	public ISelection getSelection(TreeViewer viewer, ISelection selection) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getSelection" );
		return super.getSelection(viewer, selection);
	}

	@Override
	public TransferDragSourceListener[] getTransferDragSourceListeners(
			TreeViewer treeViewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getTransferDragSourceListeners" );
		return super.getTransferDragSourceListeners(treeViewer);
	}

	@Override
	public TransferDropTargetListener[] getTransferDropTargetListeners(
			TreeViewer treeViewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getTransferDropTargetListeners" );
		return super.getTransferDropTargetListeners(treeViewer);
	}

	@Override
	public void unconfigure(TreeViewer viewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method unconfigure" );
		super.unconfigure(viewer);
	}

	@Override
	protected IContributionItem[] createToolbarContributions(TreeViewer viewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method createToolbarContributions" );
		return super.createToolbarContributions(viewer);
	}

	@Override
	public KeyListener[] getKeyListeners(TreeViewer viewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getKeyListeners" );
		return super.getKeyListeners(viewer);
	}

	@Override
	public boolean isLinkedWithEditor(TreeViewer treeViewer) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method isLinkedWithEditor" );
		return super.isLinkedWithEditor(treeViewer);
	}

	/**
	 * Create new instance of JSPContentOutlineConfiguration
	 */
	public JSPContentOutlineConfiguration() {
		// Must have empty constructor to createExecutableExtension
		super();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}
}
