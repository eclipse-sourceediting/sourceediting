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

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentList;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;

/*
 *
 */
public class XSDComponentSelectionProvider extends LabelProvider implements IComponentSelectionProvider {
    private XSDComponentFinder xsdComponentFinder;
    private XSDComponentSelectionDialog dialog;
    private XSDSchema schema;
    
    private boolean showComplexTypes = true;
    
    /*
     * Takes in the IFile we are currently editing.
     */
    public XSDComponentSelectionProvider(IFile file, XSDSchema schema) {
        xsdComponentFinder = new XSDComponentFinder();
        xsdComponentFinder.setFile(file);
        this.schema = schema;
    }
    
    public void setDialog(XSDComponentSelectionDialog dialog) {
        this.dialog = dialog;
    }
    
    public void showComplexTypes(boolean show) {
        showComplexTypes = show;
    }
        
    /*
     * The return value is a List of XMLComponentTreeObjects.
     * 
     */
	public void getComponents(IComponentList list) {

	}
    
    /*
     * TODO: Need to revisit how we build up our treeObject list.... how we use
     * the filterText.  And it's somewhat messy, clean this up.
     */
    public List getComponents() {
        List extensions = new ArrayList();
        extensions.add("xsd");
        
        String scope = "";
        if (dialog != null) {
            scope = dialog.getSearchScope();
        }
        
        List comps = new ArrayList();
        if (scope.equals(XSDComponentSelectionDialog.enclosingProjectString)) {
            comps = xsdComponentFinder.getWorkbenchResourceComponents(XSDComponentFinder.ENCLOSING_PROJECT_SCOPE);
        }
        else if (scope.equals(XSDComponentSelectionDialog.entireWorkspaceString)) {
            comps = xsdComponentFinder.getWorkbenchResourceComponents(XSDComponentFinder.ENTIRE_WORKSPACE_SCOPE);            
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
        List treeObjects = new ArrayList();
        if (showComplexTypes) {
            while (complexIt.hasNext()) {
                XMLComponentSpecification item = (XMLComponentSpecification) complexIt.next();
                addDataItemToTreeNode(treeObjects, item);
            }
        }
        
        while (simpleIt.hasNext()) {
            XMLComponentSpecification item = (XMLComponentSpecification) simpleIt.next();
            addDataItemToTreeNode(treeObjects, item);
        }
        
        // Grab Built-In types
        Iterator builtInIt = getBuiltInTypes().iterator();
        while (builtInIt.hasNext()) {
            XMLComponentSpecification tagItem = (XMLComponentSpecification) builtInIt.next();
            addDataItemToTreeNode(treeObjects, tagItem);
        }
        
        // Create current Schema's complex and simple types
        createComplexTypes(treeObjects);
        createSimpleTypes(treeObjects);
        
        // Create from imports, includes, and redefines
        createFromImport(treeObjects);
        createFromInclude(treeObjects);
        createFromRedefine(treeObjects);
        
       return treeObjects;
    }


    protected void addDataItemToTreeNode(List comps, XMLComponentSpecification dataItem) {
        boolean foundMatch = false;
        Iterator it = comps.iterator();
        XMLComponentTreeObject containingTreeObject = null;

        while (it.hasNext()) {
            XMLComponentTreeObject treeObject = (XMLComponentTreeObject) it.next();
            if (treeObject.getName().equals(dataItem.getAttributeInfo("name"))) {
                // If the existing data item and the new data item have the same names
                if (treeObject.getXMLComponentSpecification().size() > 0) {
                    String existingPath = ((XMLComponentSpecification) treeObject.getXMLComponentSpecification().get(0)).getTagPath();
                    if (existingPath.equals(dataItem.getTagPath())) {
                        // If they are the same 'type' of items (according to the path value)
                        containingTreeObject = treeObject;
                        foundMatch = true;
                        break;
                    }
                }
            }
        }
        
        if (!foundMatch) {
            containingTreeObject = new XMLComponentTreeObject(dataItem);
            comps.add(containingTreeObject);
        }
        else {
            containingTreeObject.addXMLComponentSpecification(dataItem);
        }
    }
    
////////////////////////////////////////////////////////////////////////////////    
    private List getBuiltInTypes() {
        TypesHelper helper = new TypesHelper(schema);
        Iterator it = helper.getBuiltInTypeNamesList().iterator();
        
        List builtInComponentSpecs = new ArrayList();
        while (it.hasNext()) {
            Object item = it.next();
            String itemString = item.toString();
            
            XMLComponentSpecification builtInTypeItem = new XMLComponentSpecification("BUILT_IN_SIMPLE_TYPE");
            builtInTypeItem.addAttributeInfo("name", itemString);
            builtInTypeItem.setTargetNamespace(schema.getTargetNamespace());
            String normalizedFile = getNormalizedLocation(schema.getSchemaLocation());
            builtInTypeItem.setFileLocation(normalizedFile);
            
            builtInComponentSpecs.add(builtInTypeItem);
        }
        
        return builtInComponentSpecs;
    }

    private void createComplexTypes(List treeObjectList) {
        TypesHelper typesHelper = new TypesHelper(schema);
        List complexTypes = typesHelper.getUserComplexTypes();
        createComplexSimpleTreeObject(treeObjectList, complexTypes, true);
    }
    
    private void createSimpleTypes(List treeObjectList) {
        TypesHelper typesHelper = new TypesHelper(schema);
        List complexTypes = typesHelper.getUserSimpleTypes();
        createComplexSimpleTreeObject(treeObjectList, complexTypes, true);
    }

    
    private void createFromImport(List treeObjectList) {
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
    
        private void createFromInclude(List treeObjectList) {
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
    
    private void createFromRedefine(List treeObjectList) {
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

    private void createComplexSimpleTreeObject(List treeObjectList, List complexTypes, boolean sameNS) {
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
    
    private String getNormalizedLocation(String location) {
        try {
            URL url = new URL(location);
            URL resolvedURL = Platform.resolve(url);
            location = resolvedURL.getPath();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
    
    
////////////////////////////////////////////////////////////////////////////////
    
	public ILabelProvider getLabelProvider() {
		// TODO Auto-generated method stub
		return this;
	}

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
    
    public String getText(Object element) {
        XMLComponentTreeObject specification = (XMLComponentTreeObject) element;
        return specification.getName();
    }
    
	public List getQualifier(Object component) {
        List qualifiers = new ArrayList();
        if (component != null) {
    		XMLComponentTreeObject specification = (XMLComponentTreeObject) component;
    		Iterator it = specification.getXMLComponentSpecification().iterator();
            while (it.hasNext()) {
                XMLComponentSpecification spec = (XMLComponentSpecification) it.next();
                IPath path = new Path(spec.getFileLocation());
                qualifiers.add(spec.getTargetNamespace() + " - " + path.lastSegment());
            }
        }
        
        return qualifiers;
	}
    
	public String getType(Object component) {
        XMLComponentTreeObject specification = (XMLComponentTreeObject) component;		
//		return specification.kind;
        return "";
	}
    
    private String processSeparators(String location) {
        String processedString = location;
        
        
        
        return processedString;
    }
    
    /*
     * Object used to hold components with the same name but different qualifiers.
     * This object will contain a list of XMLComponentSpecifications (with the same
     * names but different qualifiers).
     */
    public class XMLComponentTreeObject {
        private String name;
        private List xmlComponentSpecifications;
        
        public XMLComponentTreeObject(XMLComponentSpecification spec) {
            xmlComponentSpecifications = new ArrayList();
            xmlComponentSpecifications.add(spec);
            name = (String) spec.getAttributeInfo("name");
        }
        
        public String getName() {
            return name;
        }
        
        public void addXMLComponentSpecification(XMLComponentSpecification spec) {
            xmlComponentSpecifications.add(spec);
        }
        
        public List getXMLComponentSpecification() {
            return xmlComponentSpecifications;
        }
    }
}
