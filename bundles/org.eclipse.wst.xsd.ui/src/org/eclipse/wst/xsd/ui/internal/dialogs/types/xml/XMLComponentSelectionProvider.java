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

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentList;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentSelectionProvider;

public abstract class XMLComponentSelectionProvider implements IComponentSelectionProvider {
    public List getQualifiers(Object component) {
        List qualifiers = new ArrayList();
        if (component != null) {
            XMLComponentTreeObject specification = (XMLComponentTreeObject) component;
            Iterator it = specification.getXMLComponentSpecification().iterator();
            while (it.hasNext()) {
                XMLComponentSpecification spec = (XMLComponentSpecification) it.next();
                qualifiers.add(createQualifierText(spec));
            }
        }
        
        return qualifiers;
    }
    
    protected String createQualifierText(XMLComponentSpecification spec) {
        IPath path = new Path(spec.getFileLocation());
        return spec.getTargetNamespace() + " - " + path.lastSegment();        
    }
    
    protected void addDataItemToTreeNode(IComponentList comps, XMLComponentSpecification dataItem) {
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
            comps.addComponent(containingTreeObject);
        }
        else {
            // Only add to the tree object if the qualifier text differs than existing
            // qualifier information contained in the tree object.
            Iterator existingQualifiers = getQualifiers(containingTreeObject).iterator();
            boolean alreadyExists = false;
            while (existingQualifiers.hasNext()) {
                String existingText = (String) existingQualifiers.next();
                String newItemText = createQualifierText(dataItem);
                if (existingText.equals(newItemText)) {
                    alreadyExists = true;
                    break;
                }
            }
            
            if (!alreadyExists) {
                containingTreeObject.addXMLComponentSpecification(dataItem);
            }
        }
    }
    
    protected String getNormalizedLocation(String location) {
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

    /*
     * Used to provide labels to the ComponentSeletionDialog
     */
    public class XMLComponentSelectionLabelProvider extends LabelProvider {
        public String getText(Object element) {
            XMLComponentTreeObject specification = (XMLComponentTreeObject) element;
            return specification.getName();
        }        
    }
}
