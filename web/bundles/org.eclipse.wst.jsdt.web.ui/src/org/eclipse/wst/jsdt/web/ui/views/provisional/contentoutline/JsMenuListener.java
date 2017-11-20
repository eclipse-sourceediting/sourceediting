/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.html.ui.internal.contentoutline.HTMLNodeActionManager;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.CompilationUnitEditorActionContributor;
import org.eclipse.wst.jsdt.ui.IContextMenuConstants;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.IJavaWebNode;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JsMenuListener extends HTMLNodeActionManager implements IMenuListener, IReleasable {
	public static final String EDIT_GROUP_ID = "group.edit"; //$NON-NLS-1$
	CompilationUnitEditorActionContributor contrib;
	private XMLNodeActionManager fActionManager;
	private TreeViewer fTreeViewer;
	ISelectionProvider selectionProvider;
	
	public JsMenuListener(TreeViewer viewer) {
		super((IStructuredModel) viewer.getInput(), viewer);
		contrib = new CompilationUnitEditorActionContributor();
		fTreeViewer = viewer;
//			
// fActionGroups= new CompositeActionGroup(new ActionGroup[] {
// new OpenViewActionGroup(getWorkbenchSite(), getSelectionProvider()),
// new CCPActionGroup(getWorkbenchSite()),
// new GenerateActionGroup(getWorkbenchSite()),
// new RefactorActionGroup(getWorkbenchSite()),
// new JavaSearchActionGroup(getWorkbenchSite())});
	}
	
	public IAction[] getAllJsActions() {
		return null;
	}
	
//	private IWorkbenchSite getWorkbenchSite() {
//		return InternalHandlerUtil.getActiveSite(fTreeViewer);
//	}
	
	public void menuAboutToShow(IMenuManager manager) {
		ISelection selection = fTreeViewer.getSelection();
		if (selection instanceof TreeSelection) {
			TreeSelection tselect = (TreeSelection) selection;
			Object[] elements = tselect.toArray();
			int javaCount = 0;
			for (int i = 0; i < elements.length; i++) {
				if (elements[i] instanceof IJavaWebNode) {
					javaCount++;
				}
			}
			//IContributionItem[] items = manager.getItems();
// manager.add(new Separator(IContextMenuConstants.GROUP_NEW));
// menu.add(new GroupMarker(IContextMenuConstants.GROUP_GOTO));
// menu.add(new Separator(IContextMenuConstants.GROUP_OPEN));
// manager.add(new GroupMarker(IContextMenuConstants.GROUP_SHOW));
// menu.add(new Separator(ICommonMenuConstants.GROUP_EDIT));
// menu.add(new Separator(IContextMenuConstants.GROUP_REORGANIZE));
// menu.add(new Separator(IContextMenuConstants.GROUP_GENERATE));
// menu.add(new Separator(IContextMenuConstants.GROUP_SEARCH));
// menu.add(new Separator(IContextMenuConstants.GROUP_BUILD));
// menu.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
// menu.add(new Separator(IContextMenuConstants.GROUP_VIEWER_SETUP));
// menu.add(new Separator(IContextMenuConstants.GROUP_PROPERTIES));
			if (javaCount == elements.length && javaCount != 0) {
				// see plugin.xml for object contributions that populate these
				// menus
				/*
				 * Menu for:
				 * 
				 * Open Type Hierarchy Open Call Hierarchy Show In--> Script
				 * Explorer Navigator
				 */
				manager.add(new Separator(IContextMenuConstants.GROUP_SHOW));
				manager.add(new GroupMarker(IContextMenuConstants.GROUP_SHOW));
				/*
				 * Menu for: Cut Copy Paste Delete
				 */
				manager.add(new Separator(JsMenuListener.EDIT_GROUP_ID));
				manager.add(new GroupMarker(JsMenuListener.EDIT_GROUP_ID));
				/*
				 * Menu for:
				 * 
				 * Source--> Generate Element Comment
				 * 
				 * 
				 * Refactor--> Rename Move Change Function Signature Inline
				 * Introduce Indirection Infer Generic Type Arguments
				 */
				manager.add(new Separator(IContextMenuConstants.GROUP_SOURCE));
				manager.add(new GroupMarker(IContextMenuConstants.GROUP_SOURCE));
				/*
				 * Menu for:
				 * 
				 * Refrences--> Workspace Project Hierarchy Working Set
				 * 
				 * Declerations--> Workspace Project Hierarchy Working Set
				 * 
				 */
				manager.add(new Separator(IContextMenuConstants.GROUP_SEARCH));
				manager.add(new GroupMarker(IContextMenuConstants.GROUP_SEARCH));
				/* all Java Elements */
//					
//					
//					
// JavaScriptPlugin.createStandardGroups(manager);
// String[] actionSets = JSDTActionSetUtil.getAllActionSets();
//
// IAction[] actions = JSDTActionSetUtil.getActionsFromSet(actionSets);
// for(int i = 0;i<actions.length;i++) {
// manager.add(actions[i]);
// }
// fActionGroups.setContext(new ActionContext(selection));
// fActionGroups.fillContextMenu(manager);
//					
			} else if (javaCount == 0) {
				fillContextMenu(manager, selection);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.IReleasable#release()
	 */
	public void release() {
		fTreeViewer = null;
		if (fActionManager != null) {
			fActionManager.setModel(null);
		}
	}
}
