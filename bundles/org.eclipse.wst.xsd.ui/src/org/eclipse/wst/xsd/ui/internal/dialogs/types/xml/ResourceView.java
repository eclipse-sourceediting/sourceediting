/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xml;

public class ResourceView {
/*
	Label scopeComboLabel = new Label(topComposite, SWT.NONE);
	scopeComboLabel.setText(searchScopeComboLabel);
	scopeCombo = new Combo(topComposite, SWT.NONE);
	initializeSearchScopeCombo();
	
	sashForm = new SashForm(topComposite,  SWT.VERTICAL);
	GridData sashGD = new GridData(GridData.FILL_BOTH);
	sashGD.grabExcessHorizontalSpace = true;
	sashGD.grabExcessVerticalSpace = true;
	sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	sashForm.setLayout(new GridLayout());
	
				int[] sashFormWeights = new int[2];
			sashFormWeights[0] = 0;
			sashFormWeights[1] = 7;
			sashForm.setWeights(sashFormWeights);
	
	// Create PageBook for High Level TreeViewer
			highLevelPageBook = new PageBook(sashForm, SWT.NONE);
			GridData fileSystemPBData = new GridData();
			fileSystemPBData.horizontalAlignment = SWT.FILL;
			highLevelPageBook.setLayoutData(fileSystemPBData);
			
			blankHighLevelComposite = new Composite(highLevelPageBook, SWT.NONE);
			highLevelComposite = new Composite(highLevelPageBook, SWT.NONE);
			GridLayout fileSystemLayout = new GridLayout();
			fileSystemLayout.marginWidth = 0;
			highLevelComposite.setLayout(fileSystemLayout);
			highLevelComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			createHighLevelTreeViewer(highLevelComposite);
			populateHighLevelTreeViewer();
			
			highLevelPageBook.showPage(blankHighLevelComposite);
//////////////////////////////////////////////////////////////////////////////	//
			
		/*
		 * Creates the High Level TreeViewer (Top TreeViewer).
		 *
		private void createHighLevelTreeViewer(Composite base) {
			highLevelTreeViewer = createTreeViewer(highLevelComposite, "Resources");
			initializeHighLevelTreeViewer();
			String ext[] = new String[1];
			ext[0] = "xsd";
			addFilterExtensions(highLevelTreeViewer, ext, new IFile[0]); 
			
			initializeHighLevelTreeViewer();
			populateHighLevelTreeViewer();
		}
        
        
          protected void showHighLevelView(boolean show) {
        if (show) {
            int[] sashFormWeights = new int[2];
            sashFormWeights[0] = 4;
            sashFormWeights[1] = 5;
            sashForm.setWeights(sashFormWeights);

            highLevelPageBook.showPage(highLevelComposite);
            topComposite.layout(true, true);

            showHighLevelView();
        } else {
            int[] sashFormWeights = new int[2];
            sashFormWeights[0] = 0;
            sashFormWeights[1] = 7;
            sashForm.setWeights(sashFormWeights);

            highLevelPageBook.showPage(blankHighLevelComposite);
            topComposite.layout(true, true);

            hideHighLevelView();
        }
    }
    
    protected void setFilter(TreeViewer treeViewer, ViewerFilter filter) {
        treeViewer.getTree().setRedraw(false);
        treeViewer.resetFilters();
        treeViewer.addFilter(filter);
        treeViewer.getTree().setRedraw(true);
        treeViewer.getTree().redraw();
    }
    
  // ///////////////////////////////////////////////////
    // This is a convenience method that allows filtering of the given file
    // exensions. It internally creates a ResourceFilter so that users of this
    // class don't have to construct one.
    // If the extensions provided don't have '.', one will be added.
    protected void addFilterExtensions(TreeViewer treeViewer, String[] filterExtensions, IFile[] excludedFiles) {
        // First add the '.' to the filterExtensions if they don't already have
        // one
        String[] correctedFilterExtensions = new String[filterExtensions.length];
        for (int i = 0; i < filterExtensions.length; i++) {
            // If the extension doesn't start with a '.', then add one.
            if (filterExtensions[i].startsWith(".")) {
                correctedFilterExtensions[i] = filterExtensions[i];
            } else {
                correctedFilterExtensions[i] = "." + filterExtensions[i];
            }
        }
        ViewerFilter filter;
        if (excludedFiles != null) {
            filter = new SetTypeResourceFilter(correctedFilterExtensions, excludedFiles, null);
        } else {
            filter = new SetTypeResourceFilter(correctedFilterExtensions, null);
        }
        setFilter(treeViewer, filter);
    }    
    
    
  
     * Creates a Generic TreeViewer object with the specified label and sets
     * it's GridData value.
  
    protected TreeViewer createTreeViewer(Composite comp, String title) {
        Label label = new Label(comp, SWT.NONE);
        label.setText(title);

        TreeViewer treeViewer = new TreeViewer(new Tree(comp, SWT.SINGLE | SWT.BORDER));
        Control treeWidget = treeViewer.getTree();
        GridData gd = new GridData(GridData.FILL_BOTH);
        treeWidget.setLayoutData(gd);

        return treeViewer;
    }
*/		
}
