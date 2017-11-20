/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation 
 *     Trung de Irene <trungha@ca.ibm.com>
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.util.XSDConstants;


/**
 * This class provides the content for SelectBuiltInTypesForFilterDialog
 * readability Warning: Some simple tricks to tweak efficiency are used
 */
public class BuiltInTypesTreeViewerProvider {
	
	ILabelProvider labelProvider;
	
	ITreeContentProvider contentProvider;
	
//	private static final String CONST_PARENT = "parent";
	
	/**
	 * Currently there are 3 subgroups: Numbers, Data and Time, Other
	 * Folks can choose to expand to more subgroups
	 */
	private static int BUILT_IN_TYPES_SUB_GROUP = 3;
	
    static String[] numberTypes = 
    	{ "base64Binary", "byte", "decimal", "double", "float", "hexBinary",
    	  "int", "integer", "long", "negativeInteger", "nonNegativeInteger",
    	  "nonPositiveInteger", "positiveInteger", "short", "unsignedByte",
    	  "unsignedInt", "unsignedLong", "unsignedShort"};
    
    static String[] dateAndTimeTypes =
    	{ "date", "dateTime", "duration", "gDay",
    	  "gMonth", "gMonthDay", "gYear", "gYearMonth", "time"};
	
	
    public static List getAllBuiltInTypes() {
        List items = new ArrayList();
        //for (int i = 0; i < XSDDOMHelper.dataType.length; i++) {
        //  items.add(XSDDOMHelper.dataType[i][0]);
        //}
        Iterator it = items.iterator();
        
        List mainContainer = new ArrayList(BUILT_IN_TYPES_SUB_GROUP);
        ComponentSpecification header = new ComponentSpecification("", "Root", null);
        mainContainer.add(header);
        
        List numbersGroup = new ArrayList();
        header = new ComponentSpecification("", "Numbers", null);
        numbersGroup.add(header);
        mainContainer.add(numbersGroup);
        
        List dateAndTimeGroup = new ArrayList();
        header = new ComponentSpecification("", "Date and Time", null);
        dateAndTimeGroup.add(header);
        mainContainer.add(dateAndTimeGroup);
        
        List otherGroup = new ArrayList();
        header = new ComponentSpecification("", "Other", null);
        otherGroup.add(header);
        mainContainer.add(otherGroup);

        while (it.hasNext()) {
        	Object item = it.next();
            String name = item.toString();

            ComponentSpecification builtInTypeItem = new ComponentSpecification(name, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, null);
          
            // if this built-In Type is in Number group 
            if ( partOf(name, numberTypes) ){
            	// Set parent
            	//builtInTypeItem.addAttributeInfo(CONST_PARENT, numbersGroup);
            	
            	numbersGroup.add(builtInTypeItem);
            }
            // if this built-In Type is in Date-and-Time group 
            else if ( partOf(name, dateAndTimeTypes)){
            	//builtInTypeItem.addAttributeInfo(CONST_PARENT, dateAndTimeGroup);
            	dateAndTimeGroup.add(builtInTypeItem);
            }
            // otherwise, put in Other group
            else {
            	//builtInTypeItem.addAttributeInfo(CONST_PARENT, otherGroup);
            	otherGroup.add(builtInTypeItem);
            }
        }

        return mainContainer;
    }
    
    public ILabelProvider getLabelProvider(){
		if (labelProvider != null)
			return labelProvider;
		
		labelProvider = new BuiltInTypeLabelProvider();
		return labelProvider;
	}
	
	public ITreeContentProvider getContentProvider() {
		if (contentProvider != null)
			return contentProvider;
		
		contentProvider = new BuiltInTypesTreeContentProvider();
		return contentProvider;
	}
	
	/**
	 * Determines whether an equivalent of 'item' appears in 'array'
	 * @param item
	 * @param array
	 * @return
	 */
	private static boolean partOf(String item, String[] array){
	    for(int i = 0; i < array.length; i++ ){
	    	if ( item.equals(array[i]) ){
	    		return true;
	    	}            		
	    }
	    return false;
	}
	
	class BuiltInTypeLabelProvider implements ILabelProvider{
		public Image getImage(Object element) {			
			if ( getText(element).equals("Numbers") )
				return XSDEditorPlugin.getXSDImage("icons/XSDNumberTypes.gif");
			if ( getText(element).equals("Date and Time") )
				return XSDEditorPlugin.getXSDImage("icons/XSDDateAndTimeTypes.gif");
			if ( getText(element).equals("Other") )
				return XSDEditorPlugin.getXSDImage("icons/browsebutton.gif");
			if ( element instanceof ComponentSpecification ){
				return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
			}
			return null;
		}

		public String getText(Object element) {
			ComponentSpecification spec = null;
			
			/* if not non-leaf node, the first element has the name for 
			 * the whole list */
			if (element instanceof List){
				spec = (ComponentSpecification) ((List) element).get(0);
			}
			else if (element instanceof ComponentSpecification ){
				spec = (ComponentSpecification) element;
			}
			return spec.getName();
		}

		public void addListener(ILabelProviderListener listener) {
			
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			
		}  
		
	}


	class BuiltInTypesTreeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof List) {
				List parentContent = (List) parentElement;
				
				/** Ignore the first element (which contains the name of this list
				 * ie. 'Numbers', 'Date and time', 'Other') */
				return parentContent.subList(1, parentContent.size()).toArray();
			}
			return new Object[0];
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public Object getParent(Object element) {
		    return null;
		}

		public boolean hasChildren(Object element) {
			if (getChildren(element).length > 1) {
				return true;
			}
			return false;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}
	}
}
