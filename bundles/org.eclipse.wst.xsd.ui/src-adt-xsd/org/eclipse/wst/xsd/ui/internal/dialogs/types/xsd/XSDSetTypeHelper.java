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
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.actions.CreateElementAction;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.XSDExternalFileCleanup;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XSDSetTypeHelper {
    private XSDSchema xsdSchema;
    private IFile currentIFile;
    
    public XSDSetTypeHelper(IFile iFile, XSDSchema schema) {
        currentIFile = iFile;
        xsdSchema = schema;
    }

    public void setType(Element element, String property, XMLComponentSpecification spec) {
        addImportIfNecessary(element, spec);        
        String typeObject = getPrefixedTypeName(spec);
        
        // Get the previous type --> previousStringType
        String previousStringType = "";
        Attr attr = element.getAttributeNode(property);
        if (attr != null) {
            attr.getValue();
        }

        if (!XSDDOMHelper.inputEquals(element, XSDConstants.UNION_ELEMENT_TAG, false))
        {/*
            if (spec != null && spec.getTagPath().equals("**anonymous**"))
            {
              if (spec.getTagPath().equals("ANONYMOUS_SIMPLE_TYPE"))
              {
                if (!previousStringType.equals("**anonymous**"))
                {
                  updateElementToAnonymous(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
                }
              }
              else
              {
                if (!previousStringType.equals("**anonymous**"))
                {
                  updateElementToAnonymous(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
                }
              }
              // element.removeAttribute(XSDConstants.TYPE_ATTRIBUTE);
              element.removeAttribute(property);
            }
            else*/
            {
              XSDDOMHelper.updateElementToNotAnonymous(element);
              //element.setAttribute(XSDConstants.TYPE_ATTRIBUTE, typeObject.toString());
              element.setAttribute(property, typeObject.toString());
            }
        }
    }
    
    public void addImportIfNecessary(Element element, XMLComponentSpecification spec) {
        
        // Get the new type --> typeObject
        if (spec != null) {
            QualifiedName metaName = spec.getMetaName();
           
            if (metaName != null) {
                // Do an actual import if needed
                XSDParser parser = new XSDParser();
                parser.parse(spec.getFileLocation());
                XSDSchema schema = parser.getSchema();
                String tns = schema.getTargetNamespace();
                
                boolean exists = false;
                // Check if the type is defined in the 'current' file itself.
                String currentFile = getNormalizedLocation(xsdSchema.getSchemaLocation());
                IPath currentFilePath = new Path(currentFile);
                if (currentFilePath.equals(new Path(spec.getFileLocation()))) {
                    exists = true;
                }

                if (!exists) {
                    if (tns.equals(xsdSchema.getTargetNamespace())) {
                        // Check if the schema is in a redefine/include
                        List existingList = getXSDIncludes();
                        existingList.addAll(getXSDRedefines());
                        Iterator it = existingList.iterator();
                        while (it.hasNext()) {
                            XSDSchemaDirective existingSchema = (XSDSchemaDirective) it.next();
                            String normalizedFile = getNormalizedLocation(existingSchema.getResolvedSchema().getSchemaLocation());
                            String normalizedSpec = spec.getFileLocation();
                            
                            if (normalizedFile.equals(normalizedSpec)) {
                                // Found and existing one
                                exists = true;
                            }
                        }                        
                    }
                    else {
                        // Check if the schema is in a import
                        List existingList = getXSDImports();
                        Iterator it = existingList.iterator();
                        while (it.hasNext()) {
                            XSDSchemaDirective existingSchema = (XSDSchemaDirective) it.next();
                            String normalizedFile = getNormalizedLocation(existingSchema.getResolvedSchema().getSchemaLocation());
                            String normalizedSpec = spec.getFileLocation();

                            if (normalizedFile.equals(normalizedSpec)) {
                                // Found and existing one
                                exists = true;
                            }
                        }                        
                    }
                }
                
                if (!exists) {
                    doImport(spec.getFileLocation(), schema);
                }
            }
        }
    }
    
    /*
     * Return the prefixed type name for the type described by the given
     * XMLComponentSpecification object.
     * If the type described is a Built-in type, do not add the prefix
     */
    public String getPrefixedTypeName(XMLComponentSpecification spec) {
        String typeObject = (String) spec.getAttributeInfo("name");
        
        TypesHelper typesHelper = new TypesHelper(xsdSchema); // ???? Is this correct?
        List prefixedNames = typesHelper.getPrefixedNames(spec.getTargetNamespace(), typeObject);
        if (prefixedNames.size() > 0) {
            // Grab the first prefixed name
            typeObject = (String) prefixedNames.get(0);
        }

        return typeObject;
    }
    
    private void updateElementToAnonymous(Element element, String xsdType) {
        String prefix = element.getPrefix();
        prefix = (prefix == null) ? "" : (prefix + ":");
        XSDDOMHelper.updateElementToNotAnonymous(element);
        Element childNode = null;
        if (xsdType.equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG)) {
            childNode = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
        }
        else if (xsdType.equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG)) {
            childNode = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.SIMPLETYPE_ELEMENT_TAG);
        }
        
        if (childNode != null) {
            XSDDOMHelper helper = new XSDDOMHelper();
            Node annotationNode = helper.getChildNode(element, XSDConstants.ANNOTATION_ELEMENT_TAG);
            if (annotationNode == null) {
                Node firstChild = element.getFirstChild();
                element.insertBefore(childNode, firstChild);
            } else {
                Node nextSibling = annotationNode.getNextSibling();
                element.insertBefore(childNode, nextSibling);
            }
            XSDDOMHelper.formatChild(childNode);
        }
    }

    // TODO: We shouldn't need to pass in IPath externalSchemaPath.
    private void doImport(String externalSchemaPath, XSDSchema externalSchema) {
        // Determine schemaLocation
        String locationAttribute = URIHelper.getRelativeURI(externalSchemaPath, currentIFile.getLocation().toOSString());
        
        boolean isInclude = false;
        if (externalSchema.getTargetNamespace().equals(xsdSchema.getTargetNamespace())) {
            isInclude = true;
        }
        
        if (externalSchema != null) { // In case we have problems loading the file.... we should display an error message.
                Element newElement;
                if (isInclude) {                
                    List attributes = new ArrayList();
                    attributes.add(new DOMAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, locationAttribute));
                    newElement = createElement(XSDConstants.INCLUDE_ELEMENT_TAG, attributes);
                }
                else if (!isInclude) {
                    List attributes = new ArrayList();
                    attributes.add(new DOMAttribute(XSDConstants.NAMESPACE_ATTRIBUTE, externalSchema.getTargetNamespace()));
                    attributes.add(new DOMAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, locationAttribute));
                    newElement = createElement(XSDConstants.IMPORT_ELEMENT_TAG, attributes);
                    handleImportNS(newElement, externalSchema);
                }
        }
    }

    private void handleImportNS(Element importElement, XSDSchema externalSchema) {
        String namespace = externalSchema.getTargetNamespace();
        if (namespace == null) namespace = "";

        XSDImport xsdImport = (XSDImport) xsdSchema.getCorrespondingComponent(importElement);
        xsdImport.setResolvedSchema(externalSchema);
        
        java.util.Map map = xsdSchema.getQNamePrefixToNamespaceMap();
        
//        System.out.println("changed Import Map is " + map.values());
//        System.out.println("changed import Map keys are " + map.keySet());

        // Referential integrity on old import
        // How can we be sure that if the newlocation is the same as the oldlocation
        // the file hasn't changed
                
        XSDSchema referencedSchema = xsdImport.getResolvedSchema();
        if (referencedSchema != null)
        {
          XSDExternalFileCleanup cleanHelper = new XSDExternalFileCleanup(referencedSchema);
          cleanHelper.visitSchema(xsdSchema);
        }
         
        Element schemaElement = xsdSchema.getElement();

        // update the xmlns in the schema element first, and then update the import element next
        // so that the last change will be in the import element.  This keeps the selection
        // on the import element
        TypesHelper helper = new TypesHelper(externalSchema);
        String prefix = helper.getPrefix(namespace, false);
        
        if (map.containsKey(prefix))
        {
          prefix = null;
        }

        if (prefix == null || (prefix !=null && prefix.length() == 0))
        {
          StringBuffer newPrefix = new StringBuffer("pref");  //$NON-NLS-1$
          int prefixExtension = 1;
          while (map.containsKey(newPrefix.toString()) && prefixExtension < 100)
          {
            newPrefix = new StringBuffer("pref" + String.valueOf(prefixExtension));
            prefixExtension++;
          }
          prefix = newPrefix.toString();
        }

        if (namespace.length() > 0)
        {
          // if ns already in map, use its corresponding prefix
          if (map.containsValue(namespace))
          {
            TypesHelper typesHelper = new TypesHelper(xsdSchema);
            prefix = typesHelper.getPrefix(namespace, false);
          }
          else // otherwise add to the map
          {
            schemaElement.setAttribute("xmlns:"+prefix, namespace);
          }
        }


//        System.out.println("changed Import Map is " + map.values());
//        System.out.println("changed import Map keys are " + map.keySet());
    }
    
    private Element createElement(String elementTag, List attributes) {
        Node relativeNode = XSDDOMHelper.getNextElementNode(xsdSchema.getElement().getFirstChild());
        
        CreateElementAction action = new CreateElementAction("");
        action.setElementTag(elementTag);
        action.setAttributes(attributes);
        action.setParentNode(xsdSchema.getElement());
        action.setRelativeNode(relativeNode);
        action.setXSDSchema(xsdSchema);
        return action.createAndAddNewChildElement();
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
    
    private List getXSDImports() {
        List imports = new ArrayList();
        
        Iterator contents = xsdSchema.getContents().iterator();
        while (contents.hasNext()) {
            XSDSchemaContent content = (XSDSchemaContent) contents.next();
            if (content instanceof XSDImport) {
                imports.add(content);             
            }
        }
        
        return imports;
    }
    
    private List getXSDIncludes() {
        List includes = new ArrayList();
        
        Iterator contents = xsdSchema.getContents().iterator();
        while (contents.hasNext()) {
            XSDSchemaContent content = (XSDSchemaContent) contents.next();
            if (content instanceof XSDInclude) {
                includes.add(content);            
            }
        }
        
        return includes;
    }

    private List getXSDRedefines() {
        List includes = new ArrayList();
        
        Iterator contents = xsdSchema.getContents().iterator();
        while (contents.hasNext()) {
            XSDSchemaContent content = (XSDSchemaContent) contents.next();
            if (content instanceof XSDRedefine) {
                includes.add(content);            
            }
        }
        
        return includes;
    }

}