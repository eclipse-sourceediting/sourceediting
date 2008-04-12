/*******************************************************************************
 * Copyright (c) 2005-2007 Standards for Technology in Automotive Retail
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - bug 213883 - initial api 
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.internal.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.navigator.resources.ProjectExplorer;


/**
 * @author dcarver
 *
 */
public class XMLPerspectiveFactory implements IPerspectiveFactory {

    /**
     * Creates the initial layout.  This is what the layout is reset to
     * when the Reset Perspective is selected.   It takes as input a
     * IPageLayout object.
     *
     * @param layout 
     */
	public void createInitialLayout(IPageLayout layout) {
		// Get the Editor Area
		String editorArea = layout.getEditorArea();

		this.addShortCutViews(layout);
		this.addShortCutPerspective(layout);

		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet("org.eclipse.wst.xsl.ui.xslActionSet"); //$NON-NLS-1$

		// Turn on the Editor Area
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
		
		// Add the various views
		IFolderLayout left =
        layout.createFolder("left", IPageLayout.LEFT, (float) 0.26, editorArea); //$NON-NLS-1$
		left.addView(ProjectExplorer.VIEW_ID);
		
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.75, editorArea); //$NON-NLS-1$
		right.addView(IPageLayout.ID_OUTLINE);
		
		// Add Outline to the bottom-left
		IFolderLayout bottomLeft = 
			layout.createFolder("bottom-left", IPageLayout.BOTTOM, (float) 0.50, "left"); //$NON-NLS-1$ //$NON-NLS-2$
		bottomLeft.addView("org.eclipse.wst.xml.views.XPathView"); //$NON-NLS-1$
		bottomLeft.addView("org.eclipse.wst.xml.xpath.ui.views.XPathNavigator"); //$NON-NLS-1$
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.75, editorArea); //$NON-NLS-1$
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView("org.eclipse.wst.common.snippets.internal.ui.SnippetsView"); //$NON-NLS-1$
		
		//layout.addNewWizardShortcut("org.eclipse.wst.xml.ui.internal.wizards.NewXMLWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.wst.xsl.ui.internal.wizards.NewStylesheetWizard"); //$NON-NLS-1$
		//layout.addNewWizardShortcut("org.eclipse.wst.dtd.ui.internal.wizard.NewDTDWizard"); //$NON-NLS-1$
		//layout.addNewWizardShortcut("org.eclipse.wst.xsd.ui.internal.wizards.NewXSDWizard"); //$NON-NLS-1$
		//layout.addNewWizardShortcut("org.eclipse.wst.wsdl.ui"); //$NON-NLS-1$
		//layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder"); //$NON-NLS-1$
		//layout.addNewWizardShortcut("additions");
		
		layout.addPerspectiveShortcut("org.eclipse.debug.ui.DebugPerspective"); //$NON-NLS-1$
		
	}
	
	/**
	 * Adds View short cuts to the XML Perspective.  The short cuts added are:
	 * XPath Navigator
	 * ProjectExplorer
	 * Navigator
	 * Problems View
	 * Console View 
	 * @param layout
	 */
	private void addShortCutViews(IPageLayout layout) {
		layout.addShowViewShortcut("org.eclipse.wst.xml.views.XPathView"); //$NON-NLS-1$
        layout.addShowViewShortcut("org.eclipse.wst.xml.xpath.ui.views.XPathNavigator"); //$NON-NLS-1$
		layout.addShowViewShortcut(ProjectExplorer.VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
        layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
        layout.addShowViewShortcut("org.eclipse.wst.common.snippets.internal.ui.SnippetsView"); //$NON-NLS-1$
	}
	
	private void addShortCutPerspective(IPageLayout layout) {
		
	}
}
