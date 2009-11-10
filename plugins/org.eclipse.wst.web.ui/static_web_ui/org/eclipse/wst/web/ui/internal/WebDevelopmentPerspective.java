/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.ui.internal;

import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.wst.project.facet.IProductConstants;
import org.eclipse.wst.project.facet.ProductManager;

public class WebDevelopmentPerspective implements IPerspectiveFactory {

	public static final String ID = "org.eclipse.wst.web.ui.webDevPerspective"; //$NON-NLS-1$

	protected static final String TOP_LEFT_LOCATION = "topLeft"; //$NON-NLS-1$
	protected static final String BOTTOM_LEFT_LOCATION = "bottomLeft"; //$NON-NLS-1$ 
	protected static final String BOTTOM_RIGHT_LOCATION = "bottomRight"; //$NON-NLS-1$
	protected static final String TOP_RIGHT_LOCATION = "topRight"; //$NON-NLS-1$
	protected static final String BOTTOM_LOCATION = "bottom"; //$NON-NLS-1$

	// view id's
	protected static String ID_PROJECT_EXPLORER = "org.eclipse.ui.navigator.ProjectExplorer"; //$NON-NLS-1$
	protected static final String ID_SERVER = "org.eclipse.wst.server.ui.ServersView"; //$NON-NLS-1$
	protected static String ID_WST_SNIPPETS_VIEW = "org.eclipse.wst.common.snippets.internal.ui.SnippetsView"; //$NON-NLS-1$

	public WebDevelopmentPerspective() {
		super();
		//If preference exists for alternate view, replace.
		String viewerID = ProductManager.getProperty(IProductConstants.ID_PERSPECTIVE_HIERARCHY_VIEW);
		if (viewerID != null) {
			// verify that the view actually exists
			if (PlatformUI.getWorkbench().getViewRegistry().find(viewerID) != null){
				ID_PROJECT_EXPLORER = viewerID;
			}
		}
	}

	/*
	 *____ ________________ _____
	 *|    |                |     |
	 *| P  |                |  O  |
	 *| R  |     ED         |  u  |
	 *| o  |                |  t  |
	 *| j  |                |  l  |
	 *|    |________________|  i  |
	 *| E  |                |  n  |
	 *| X  | PROB/SERV/PROP |  e  |
	 *|_P__|________________|_____|
	 *
	 */
	public void createInitialLayout(IPageLayout layout) {
		// everything is based off the editor area
		String editorArea = layout.getEditorArea();

		// LEFT Area (Project Explorer)
		IFolderLayout topLeft = layout.createFolder(TOP_LEFT_LOCATION,
				IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(ID_PROJECT_EXPLORER);
		topLeft.addPlaceholder(IPageLayout.ID_RES_NAV);

		// TOP RIGHT Area (Outline)
		IFolderLayout topRight = layout.createFolder(TOP_RIGHT_LOCATION,
				IPageLayout.RIGHT, 0.80f, editorArea);
		topRight.addView(IPageLayout.ID_OUTLINE);
		topRight.addView(ID_WST_SNIPPETS_VIEW);

		// BOTTOM Area (Problems, Server, Properties)
		IFolderLayout bottom = layout.createFolder(BOTTOM_LOCATION,
				IPageLayout.BOTTOM, 0.70f, editorArea);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(ID_SERVER);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		bottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		bottom.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
		bottom.addPlaceholder(IPageLayout.ID_TASK_LIST);
	}

}
