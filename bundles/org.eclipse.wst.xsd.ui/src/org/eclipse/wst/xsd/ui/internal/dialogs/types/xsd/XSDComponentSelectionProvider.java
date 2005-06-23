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
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentList;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentFinder;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.util.XSDConstants;

/*
 *
 */
public class XSDComponentSelectionProvider extends XMLComponentSelectionProvider {
    private XSDComponentFinder xsdComponentFinder;
    private XSDComponentSelectionDialog dialog;
    private XSDSchema schema;
    private XSDComponentLabelProvider labelProvider;
    
    private boolean showComplexTypes = true;
    
    /*
     * Takes in the IFile we are currently editing.
     */
    public XSDComponentSelectionProvider(IFile file, XSDSchema schema) {
        xsdComponentFinder = new XSDComponentFinder();
        xsdComponentFinder.setFile(file);
        this.schema = schema;
        labelProvider = new XSDComponentLabelProvider();
    }
    
    public void setDialog(XSDComponentSelectionDialog dialog) {
        this.dialog = dialog;
    }
    
    public void showComplexTypes(boolean show) {
        showComplexTypes = show;
    }
    
    public String getType(Object element) {
        return null;
    }
        
    /*
     * The return value is a List of XMLComponentTreeObjects.
     * 
     */
	public void getComponents(IComponentList list, boolean quick) {
	    if (quick) {
            // Populate IComponentList list with components most easily accessible (fastest)
            // Grab Built-In types
            Iterator builtInIt = getBuiltInTypes().iterator();
            while (builtInIt.hasNext()) {
                XMLComponentSpecification tagItem = (XMLComponentSpecification) builtInIt.next();
                addDataItemToTreeNode(list, tagItem);
            }
            
            // Create current Schema's complex and simple types
            createComplexTypes(list);
            createSimpleTypes(list);
        }
        else {
            getComponents(list);
        }
	}
    
    /*
     * TODO: Need to revisit how we build up our treeObject list.... 
     * And it's somewhat messy, clean this up.
     */
    private void getComponents(IComponentList list) {
        List extensions = new ArrayList();
        extensions.add("xsd");
        
        String scope = "";
        if (dialog != null) {
            scope = dialog.getSearchScope();
        }
        
        List comps = new ArrayList();
        if (scope.equals(XSDComponentSelectionDialog.SCOPE_ENCLOSING_PROJECT)) {
            comps = xsdComponentFinder.getWorkbenchResourceComponents(XMLComponentFinder.ENCLOSING_PROJECT_SCOPE);
        }
        else if (scope.equals(XSDComponentSelectionDialog.SCOPE_WORKSPACE)) {
            comps = xsdComponentFinder.getWorkbenchResourceComponents(XMLComponentFinder.ENTIRE_WORKSPACE_SCOPE);            
        }
        
//      Group same item types together (simple/complex)
        List complex = new ArrayList();
        List simple = new ArrayList();
        Iterator itemsIterator = comps.iterator();
        while (itemsIterator.hasNext()) {
            XMLComponentSpecification tagItem = (XMLComponentSpecification) itemsIterator.next();
            if (tagItem.getTagPath().equals("/schema/complexType")) {
                complex.add(tagItem);
            }               
            else if (tagItem.getTagPath().equals("/schema/simpleType")) {
                simple.add(tagItem);
            }
        }
        
        Iterator complexIt = complex.iterator();
        Iterator simpleIt = simple.iterator();
        if (showComplexTypes) {
            while (complexIt.hasNext()) {
                XMLComponentSpecification item = (XMLComponentSpecification) complexIt.next();
                addDataItemToTreeNode(list, item);
            }
        }
        
        while (simpleIt.hasNext()) {
            XMLComponentSpecification item = (XMLComponentSpecification) simpleIt.next();
            addDataItemToTreeNode(list, item);
        }
        
        // Create from imports, includes, and redefines
        createFromImport(list);
        createFromInclude(list);
        createFromRedefine(list);
    }
    
////////////////////////////////////////////////////////////////////////////////    
    private List getBuiltInTypes() {        
        List items = new ArrayList();
        for (int i = 0; i < XSDDOMHelper.dataType.length; i++) {
          items.add(XSDDOMHelper.dataType[i][0]);
        }
        Iterator it = items.iterator();
        
        List builtInComponentSpecs = new ArrayList();
        while (it.hasNext()) {
            Object item = it.next();
            String itemString = item.toString();
            
            XMLComponentSpecification builtInTypeItem = new XMLComponentSpecification("BUILT_IN_SIMPLE_TYPE");
            builtInTypeItem.addAttributeInfo("name", itemString);
            builtInTypeItem.setTargetNamespace(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
//            String normalizedFile = getNormalizedLocation(schema.getSchemaLocation());
//            builtInTypeItem.setFileLocation(normalizedFile);
            builtInTypeItem.setFileLocation("Built-In");
            
            builtInComponentSpecs.add(builtInTypeItem);
        }
        
        return builtInComponentSpecs;
    }

    private void createComplexTypes(IComponentList treeObjectList) {
        TypesHelper typesHelper = new TypesHelper(schema);
        List complexTypes = typesHelper.getUserComplexTypes();
        createComplexSimpleTreeObject(treeObjectList, complexTypes, true);
    }
    
    private void createSimpleTypes(IComponentList treeObjectList) {
        TypesHelper typesHelper = new TypesHelper(schema);
        List complexTypes = typesHelper.getUserSimpleTypes();
        createComplexSimpleTreeObject(treeObjectList, complexTypes, true);
    }

    
    private void createFromImport(IComponentList treeObjectList) {
        Iterator imports = getXSDImports().iterator();
        while (imports.hasNext()) {
            XSDImport importItem = (XSDImport) imports.next();
            if (importItem.getSchemaLocation() != null) {
                ((XSDImportImpl) importItem).importSchema();
                TypesHelper helper = new TypesHelper(importItem.getResolvedSchema());
                
                List types = helper.getUserComplexTypes();
                types.addAll(helper.getUserSimpleTypes());
                createComplexSimpleTreeObject(treeObjectList, types, false);
            }
        }
    }
    
        private void createFromInclude(IComponentList treeObjectList) {
        Iterator imports = getXSDIncludes().iterator();
        while (imports.hasNext()) {
            XSDInclude includeItem = (XSDInclude) imports.next();
            if (includeItem.getSchemaLocation() != null) {
                TypesHelper helper = new TypesHelper(includeItem.getResolvedSchema());  
                
                List types = helper.getUserComplexTypes();
                types.addAll(helper.getUserSimpleTypes());
                createComplexSimpleTreeObject(treeObjectList, types, false);
            }
        }
    }
    
    private void createFromRedefine(IComponentList treeObjectList) {
        Iterator redefines = getXSDRedefines().iterator();
        while (redefines.hasNext()) {
            XSDRedefine redefineItem = (XSDRedefine) redefines.next();
            if (redefineItem.getSchemaLocation() != null) {
                TypesHelper helper = new TypesHelper(redefineItem.getResolvedSchema());
                
                List types = helper.getUserComplexTypes();
                types.addAll(helper.getUserSimpleTypes());
                createComplexSimpleTreeObject(treeObjectList, types, false);
            }
        }
    }
    
        protected List getXSDImports() {
        List imports = new ArrayList();
        
        Iterator contents = schema.getContents().iterator();
        while (contents.hasNext()) {
            XSDSchemaContent content = (XSDSchemaContent) contents.next();
            if (content instanceof XSDImport) {
                imports.add(content);             
            }
        }
        
        return imports;
    }
    
    protected List getXSDIncludes() {
        List includes = new ArrayList();
        
        Iterator contents = schema.getContents().iterator();
        while (contents.hasNext()) {
            XSDSchemaContent content = (XSDSchemaContent) contents.next();
            if (content instanceof XSDInclude) {
                includes.add(content);            
            }
        }
        
        return includes;
    }

    protected List getXSDRedefines() {
        List includes = new ArrayList();
        
        Iterator contents = schema.getContents().iterator();
        while (contents.hasNext()) {
            XSDSchemaContent content = (XSDSchemaContent) contents.next();
            if (content instanceof XSDRedefine) {
                includes.add(content);            
            }
        }
        
        return includes;
    }

    private void createComplexSimpleTreeObject(IComponentList treeObjectList, List complexTypes, boolean sameNS) {
        boolean proceed = true;
        
        for (int i = 0; i < complexTypes.size(); i++) {
            XSDNamedComponent item = (XSDNamedComponent) complexTypes.get(i); 
        
            if (sameNS) {
                // We do this check because Types from Includes might show up.  However, we don't want to show them
                String itemLocation = item.getSchema().getSchemaLocation();
                String currentSchemaLocation = schema.getSchemaLocation();
                if (itemLocation != null) {
                    proceed = itemLocation.equals(currentSchemaLocation);
                }
                else {
                    proceed = false;
                }
            }
            
            if (proceed) {
                XMLComponentSpecification typeItem =  null;
                if (showComplexTypes && item instanceof XSDComplexTypeDefinition) {
                    typeItem = new XMLComponentSpecification("/schema/complexType");
                    typeItem.addAttributeInfo("name", ((XSDComplexTypeDefinition) item).getName());
                }
                else if (item instanceof XSDSimpleTypeDefinition) {
                    typeItem = new XMLComponentSpecification("/schema/simpleType");
                    typeItem.addAttributeInfo("name", ((XSDSimpleTypeDefinition) item).getName());
                }
                
                if (typeItem != null) {
                    typeItem.setTargetNamespace(item.getTargetNamespace());
                    String normalizedFile = getNormalizedLocation(schema.getSchemaLocation());
                    typeItem.setFileLocation(normalizedFile);
    
                    addDataItemToTreeNode(treeObjectList, typeItem);
                }
            }
        }
    }

    
	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}

    
    public class XSDComponentLabelProvider extends XMLComponentSelectionLabelProvider {
        public Image getImage(Object element) {
            XMLComponentTreeObject specification = (XMLComponentTreeObject) element;
            XMLComponentSpecification spec = (XMLComponentSpecification) specification.getXMLComponentSpecification().get(0);
            if (spec.getTagPath().equals("/schema/complexType")) {
                return XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif");
            }
            else if (spec.getTagPath().equals("/schema/simpleType")) {
                return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
            }
            else if (spec.getTagPath().equals("BUILT_IN_SIMPLE_TYPE")) {
                return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
            }
    
            return null;
        }
    }
    
    

	public String getListTitle() {
		return XSDEditorPlugin.getXSDString("_UI_LABEL_MATCHING_TYPES");
	}

	public String getNameFieldTitle() {
		return XSDEditorPlugin.getXSDString("_UI_LABEL_TYPE_NAME");
	}
}
