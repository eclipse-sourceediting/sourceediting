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
package org.eclipse.wst.xsd.ui.internal.dialogs.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class TypesDialogTreeObject {
	private Object dataObject;
	private TypesDialogTreeObject parent;
	private ArrayList children;
	private String label;
	private String appendLabel = "";
	private int type;
	
	// Used to determine the type of object we're dealing with.
	// If dataObject is String, we need more info.... so we look at the type (below).
	public final static int UNKNOWN_TYPE = 0;
	public final static int BUILT_IN_TYPE = 1;
	public final static int INLINE_SCHEMA = 2;
	public final static int ANONYMOUS_COMPLEX_TYPE = 3;
	public final static int ANONYMOUS_SIMPLE_TYPE = 4;
	
	public TypesDialogTreeObject(Object dataObject) {
		this.dataObject = dataObject;
		children = new ArrayList();
		type = TypesDialogTreeObject.UNKNOWN_TYPE;
	}

	public TypesDialogTreeObject(Object dataObject, int type) {
		this(dataObject);
		this.type = type;
	}
	
	public void addChild(TypesDialogTreeObject child) {
		children.add(child);
		child.setParent(this);
	}
	
	public void addAll(List kids) {
		Iterator it = kids.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			if (item instanceof TypesDialogTreeObject) {
				addChild((TypesDialogTreeObject) item);
			}
		}
	}

	public List getChildren() {
		return children;
	}
	
	public TypesDialogTreeObject getParent() {
		return parent;
	}
	
	public void setParent(TypesDialogTreeObject newParent) {
		parent = newParent;
	}
	
	/*
	 * We should move getImage() and computeDefaultLabel() code out of TypesDialogTreeObject to make
	 * it more generic...... but since this class was created specifically for the TypesDialog we'll
	 * leave it here.
	 */
	private void computeDefaultLabel() {
		if (dataObject instanceof XSDSchema) {
			XSDSchema schema = (XSDSchema) dataObject;
			URI schemaURI = URI.createURI(schema.getSchemaLocation());
			label = schemaURI.lastSegment();
		}
		else if (dataObject instanceof XSDComplexTypeDefinition) {
			label = ((XSDComplexTypeDefinition) dataObject).getName();
		}
		else if (dataObject instanceof XSDSimpleTypeDefinition) {
			label = ((XSDSimpleTypeDefinition) dataObject).getName();
		}
		else if (dataObject instanceof String) {
			label = (String) dataObject;
		}
		else {
			label = "";
		}
	}
	
	public void setLabel(String newLabel) {
		label = newLabel;
	}
	
	public String getLabel() {
		if (label == null) {
			computeDefaultLabel();
		}
		return label;
	}
	
	public void setAppendLabel(String newLabel) {
		appendLabel = newLabel;
	}
	
	public String getAppendLabel() {
		return appendLabel;
	}
	
	public String getEntireLabel() {
		return getLabel() + getAppendLabel();
	}
	
	/*
	 * We should move getImage() and computeDefaultLabel() code out of TypesDialogTreeObject to make
	 * it more generic...... but since this class was created specifically for the TypesDialog we'll
	 * leave it here.
	 */
	public Image getImage() {
		if (dataObject instanceof XSDSchema) {
			return XSDEditorPlugin.getXSDImage("icons/XSDFile.gif");
		}
		else if (dataObject instanceof XSDComplexTypeDefinition) {
			return XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif");
		}
		else if (dataObject instanceof XSDSimpleTypeDefinition) {
			return XSDEditorPlugin.getPlugin().getIconImage("obj16/smpl_restrict_obj");
		}
		else if (dataObject instanceof XSDElementDeclaration) {
			return XSDEditorPlugin.getXSDImage("icons/XSDElement.gif");
		}
		else if (dataObject instanceof String && getType() == TypesDialogTreeObject.BUILT_IN_TYPE) {
			return XSDEditorPlugin.getPlugin().getIconImage("obj16/smpl_restrict_obj");
		}
		else if (dataObject instanceof String && getType() == TypesDialogTreeObject.ANONYMOUS_COMPLEX_TYPE) {
			return XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif");
		}
		else if (dataObject instanceof String && getType() == TypesDialogTreeObject.ANONYMOUS_SIMPLE_TYPE) {
			return XSDEditorPlugin.getPlugin().getIconImage("obj16/smpl_restrict_obj");
		}

		else {
			return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
		}
	}
	
	public Object getDataObject() {
		return dataObject;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	/*
	 * Search for the first match of the given fileName with the return value of
	 * TypesDialogTreeObject.getLabel().  This method will search the children of
	 * the given TypesDialogTreeObject.  It will not search recursively.
	 */
	public static TypesDialogTreeObject getTreeObject(TypesDialogTreeObject rootObject, String fileName) {
		Iterator rootChildrenIt = rootObject.getChildren().iterator();
		TypesDialogTreeObject rootChild = null;
		boolean found = false;
		
		while (rootChildrenIt.hasNext()) {
			rootChild = (TypesDialogTreeObject) rootChildrenIt.next();
			if (rootChild.getLabel().equals(fileName)) {
				found = true;
				break;
			}
		}

		if (found) {
			return rootChild;
		}
		else {
			return null;
		}
	}		
}
