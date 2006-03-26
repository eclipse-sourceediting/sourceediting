/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.editor.internal.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSearchListDialog;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.editor.internal.dialogs.BuiltInTypesTreeViewerProvider;
import org.eclipse.wst.xsd.editor.internal.dialogs.SelectBuiltInTypesForFilteringDialog;
import org.eclipse.xsd.XSDTypeDefinition;

public class FilterMenuContributor {
	private static final String CONST_PREFERED_BUILT_IN_TYPES = XSDEditorPlugin.CONST_PREFERED_BUILT_IN_TYPES;
	
	private XSDTypesSearchListProvider searchListProvider;
	private ComponentSearchListDialog parentDialog;

	private MenuManager fMenuManager;
	private ToolItem toolItem;
	private ToolBar filterToolBar;
	private IXSDTypesFilter filter;

	private String toolItemIconFile = "icons/filter.gif"; 
	private String on_off_filter_actionText = "Uncommon built-in types";
	private String configureFilterDialogText = "Filter...";
	private String filterIconFile = "filter.gif";
	
	/**
	 * I need to know which dialog i will contribute to and which provider that
	 * dialog use,provide it for me here
	 * @param searchListProvider
	 * @param parentDialog
	 */
	public FilterMenuContributor(XSDTypesSearchListProvider searchListProvider,
			ComponentSearchListDialog parentDialog) {
		this.searchListProvider = searchListProvider;		
		this.parentDialog = parentDialog;
	}



	/**
	 * Give me the toolbar where I will contribute the items to
	 * @param toolBar
	 * @return
	 */
	public ToolItem createToolItem(ToolBar toolBar) {
		this.filterToolBar = toolBar;
		toolItem = new ToolItem(filterToolBar, SWT.PUSH, 0);
		
		GridData data= new GridData();
		data.horizontalAlignment= GridData.END;
		filterToolBar.setLayoutData(data);
		
		fMenuManager = new MenuManager();
		fMenuManager.add(new FilterUncommonBuiltInTypesAction());
		fMenuManager.add(new LaunchConfigFilterDialogAction());
		
		toolItem.setImage(XSDEditorPlugin.getXSDImage(toolItemIconFile));
		toolItem.setToolTipText("Filter uncommon built-in types");
		toolItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Menu menu = fMenuManager.createContextMenu(parentDialog.getShell());
				Rectangle bounds = toolItem.getBounds();
				Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
				topLeft = filterToolBar.toDisplay(topLeft);
				menu.setLocation(topLeft.x, topLeft.y);
				menu.setVisible(true);
			}
		}); ///
		
		// register the filter with the Provider object
		filter = new BuiltInTypesFilter();
		filter.turnOn();
		searchListProvider.setBuiltInFilter(filter);
		
		return toolItem;
	}


	/**
	 * Action for the button to turn on and off Built-In types filter
	 */
	private class FilterUncommonBuiltInTypesAction extends Action {
		public FilterUncommonBuiltInTypesAction() {
			super(on_off_filter_actionText, Action.AS_CHECK_BOX);
		}

		public void run() {
			// flip states of the filter
			if(	filter.isOn() )
				filter.turnOff();
			else
				filter.turnOn();
			
			parentDialog.updateForFilterChange();
		}
	}

	private class LaunchConfigFilterDialogAction extends Action {
		public LaunchConfigFilterDialogAction(){
			super(configureFilterDialogText, 
					XSDEditorPlugin.getImageDescriptor(filterIconFile, true)	);
		}
		public void run(){
			Shell shell = Display.getCurrent().getActiveShell();

			BuiltInTypesTreeViewerProvider provider = 
				new BuiltInTypesTreeViewerProvider();

			SelectBuiltInTypesForFilteringDialog typeFilterDialog = 
				new SelectBuiltInTypesForFilteringDialog(shell, 
						provider.getLabelProvider(), provider.getContentProvider());

			List allBuiltInTypes = BuiltInTypesTreeViewerProvider.getAllBuiltInTypes();
			typeFilterDialog.setInput(allBuiltInTypes);
			
			IPreferenceStore store = XSDEditorPlugin.getPlugin().getPreferenceStore();			
			String listString = store.getString(CONST_PREFERED_BUILT_IN_TYPES);

			Object[] selectedBuiltInTypes = 
				SelectBuiltInTypesForFilteringDialog.getSelectedBuiltInTypesFromString(listString,
						allBuiltInTypes).toArray();

			typeFilterDialog.setInitialSelections(selectedBuiltInTypes);

			typeFilterDialog.create();
			//typeFilter.getTreeViewer().setSorter(new ViewerSorter());

			if (typeFilterDialog.open() == Window.OK){
				/* we don't use getResult() because it also returns grayed
				 * elements */
				selectedBuiltInTypes = typeFilterDialog.getResult();

				// removed grayed checked elements (parent node)
				List nonGrayItems = new ArrayList();
				for(int i = 0; i < selectedBuiltInTypes.length; i++ ){
					if ( selectedBuiltInTypes[i] instanceof ComponentSpecification){
						nonGrayItems.add(selectedBuiltInTypes[i]);
					}
				}
				selectedBuiltInTypes = nonGrayItems.toArray();

				String newlySelectionToBeStored = 
					SelectBuiltInTypesForFilteringDialog.getTypesListInString(selectedBuiltInTypes);
				store.setValue(CONST_PREFERED_BUILT_IN_TYPES, newlySelectionToBeStored);

				parentDialog.updateForFilterChange();				
			}
		}
	}
	
	/**
	 * This is a concrete filter, used to filter out built-in types
	 * Register it with a provider to use it. 
	 */
	private class BuiltInTypesFilter implements IXSDTypesFilter{
		
		private static final String BUILT_IN_TYPES_TARGET_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
		private static final String LIST_SEP = SelectBuiltInTypesForFilteringDialog.CUSTOM_LIST_SEPARATOR;
		private IPreferenceStore store = XSDEditorPlugin.getPlugin().getPreferenceStore();
		
		/**
		 * True is the Filter is currently turned on, false otherwise
		 */
		public boolean currentStatus = true;
		
		private String selectedBuiltInTypes;
		
		/**
		 * Give me the name of the built-in type, i will say whether or not
		 * to filter it out.
		 * Note: I will say no/false whenever I am not sure..
		 */
		public boolean shouldFilterOut(Object o) {
			// if the I am turned on
			if ( currentStatus ){				
				
				// selectedBuiltInTypes must always contain a trail LIST_SEP
				// at the end for the filter to work properly
				selectedBuiltInTypes =
					store.getString(CONST_PREFERED_BUILT_IN_TYPES);
				
				if ( o instanceof XSDTypeDefinition){
					XSDTypeDefinition td = (XSDTypeDefinition) o;
					/*  if the targetname space indicates this is of built-in types AND
					 *  the name is not in the allowed list (not-filter list) then
					 *  it should be filtered
					 */
					if  ( td.getTargetNamespace().equals(BUILT_IN_TYPES_TARGET_NAMESPACE) &&
							selectedBuiltInTypes.indexOf(td.getName() + LIST_SEP) == -1 ){
						return true;
					}
				}
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.xsd.adt.search.IXSDTypesFilter#turnOn()
		 */
		public void turnOn() {
			currentStatus = true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.xsd.adt.search.IXSDTypesFilter#turnOff()
		 */
		public void turnOff() {
			currentStatus = false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.xsd.adt.search.IXSDTypesFilter#isOn()
		 */
		public boolean isOn() {
			return currentStatus;
		}
		
	}

	public void setConfigureFilterDialogText(String configureFilterDialogText) {
		this.configureFilterDialogText = configureFilterDialogText;
	}

	public void setFilterIconFile(String filterIconFile) {
		this.filterIconFile = filterIconFile;
	}

	public void setOn_off_filter_actionText(String on_off_action) {
		this.on_off_filter_actionText = on_off_action;
	}

	public void setToolItemIconFile(String toolItemIconFile) {
		this.toolItemIconFile = toolItemIconFile;
	}
}
