/*******************************************************************************
 * Copyright (c) 2005-2007 Orangevolt (www.orangevolt.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Lars Gersmann (www.orangevolt.com) 
 *     
 *******************************************************************************/

package org.eclipse.wst.xsl.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;

public class XSLPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		IFolderLayout folder= layout.createFolder("left", IPageLayout.LEFT, (float)0.20, editorArea); //$NON-NLS-1$
		folder.addView( IPageLayout.ID_RES_NAV);
	
		IFolderLayout left_bottom = layout.createFolder( "left_bottom", IPageLayout.BOTTOM, (float)0.40, "left");
		left_bottom.addView( "org.eclipse.wst.xml.xpath.ui.views.XPathNavigator");
		
			// Add "show views".
		layout.addShowViewShortcut( "org.eclipse.wst.xml.xpath.ui.views.XPathNavigator");
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addActionSet( IPageLayout.ID_NAVIGATE_ACTION_SET);
		layout.addActionSet( IDebugUIConstants.LAUNCH_ACTION_SET);

		IFolderLayout bottom= layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.75, editorArea); //$NON-NLS-1$
		bottom.addView( IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView( IPageLayout.ID_PROBLEM_VIEW);
		bottom.addPlaceholder( NewSearchUI.SEARCH_VIEW_ID);		
		bottom.addView( IPageLayout.ID_BOOKMARKS);
		bottom.addPlaceholder( IProgressConstants.PROGRESS_VIEW_ID);

		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float)0.75, editorArea); //$NON-NLS-1$
		right.addView(IPageLayout.ID_OUTLINE);	
		
		
		IFolderLayout right_bottom = layout.createFolder("right_bottom", IPageLayout.BOTTOM, (float)0.50, "right"); //$NON-NLS-1$
		right_bottom.addView( IPageLayout.ID_PROP_SHEET);
		
		layout.addNewWizardShortcut("org.eclipse.wst.xml.ui.internal.wizards.NewXMLWizard");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder"); 
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file"); 		
	}
}
