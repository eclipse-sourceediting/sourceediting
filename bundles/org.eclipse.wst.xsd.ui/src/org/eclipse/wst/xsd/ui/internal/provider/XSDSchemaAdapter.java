/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;


public class XSDSchemaAdapter extends XSDAbstractAdapter {
	protected XSDPackage xsdPackage;

	/**
	 * @param adapterFactory
	 */
	public XSDSchemaAdapter(AdapterFactory adapterFactory) {
		super(adapterFactory);
		xsdPackage = XSDPackage.eINSTANCE;
	}

	public Image getImage(Object element) {
		return XSDEditorPlugin.getXSDImage("icons/XSDFile.gif");
	}

	public String getText(Object element) {
		XSDSchema xsdSchema = (XSDSchema) element;
		String result = xsdSchema.getSchemaLocation();
		if (result == null) {
			return "";
		}
		else {
			return URI.createURI(result).lastSegment();
		}
	}

	List children;
	private CategoryAdapter fDirectivesCategory;
	private CategoryAdapter fElementsCategory;
	private CategoryAdapter fAttributesCategory;
	private CategoryAdapter fAttributeGroupsCategory;
	private CategoryAdapter fTypesCategory;
	private CategoryAdapter fGroupsCategory;
	private CategoryAdapter fNotationsCategory;

	/**
	 * Create all the category adapters
	 * 
	 * @param xsdSchema
	 */
	private void createCategoryAdapters(XSDSchema xsdSchema) {
		List directivesList = getDirectives(xsdSchema);
		List elementsList = getGlobalElements(xsdSchema);
		List attributeGroupList = getAttributeGroupList(xsdSchema);
		List attributesList = getAttributeList(xsdSchema);
		List groups = getGroups(xsdSchema);
		List notations = getNotations(xsdSchema);
		List types = getComplexTypes(xsdSchema);
		types.addAll(getSimpleTypes(xsdSchema));

		fDirectivesCategory = new CategoryAdapter( // XSDEditPlugin.getString("_UI_Elements_label"),
					XSDEditorPlugin.getXSDString("_UI_GRAPH_DIRECTIVES"), XSDEditorPlugin.getPlugin().getIconImage("obj16/directivesheader"), directivesList, xsdSchema, CategoryAdapter.DIRECTIVES);
		fElementsCategory = new CategoryAdapter( // XSDEditPlugin.getString("_UI_Elements_label"),
					XSDEditorPlugin.getXSDString("_UI_GRAPH_ELEMENTS"), XSDEditorPlugin.getPlugin().getIconImage("obj16/elementsheader"), elementsList, xsdSchema, CategoryAdapter.ELEMENTS);
		fAttributesCategory = new CategoryAdapter( // XSDEditPlugin.getString("_UI_Attributes_label"),
					XSDEditorPlugin.getXSDString("_UI_GRAPH_ATTRIBUTES"), XSDEditorPlugin.getPlugin().getIconImage("obj16/attributesheader"), attributesList, xsdSchema, CategoryAdapter.ATTRIBUTES);
		fAttributeGroupsCategory = new CategoryAdapter(// XSDEditPlugin.getString("_UI_AttributeGroups_label"),
					XSDEditorPlugin.getXSDString("_UI_GRAPH_ATTRIBUTE_GROUPS"), XSDEditorPlugin.getPlugin().getIconImage("obj16/attributegroupsheader"), attributeGroupList, xsdSchema, CategoryAdapter.ATTRIBUTE_GROUPS);
		fTypesCategory = new CategoryAdapter( // XSDEditPlugin.getString("_UI_Types_label"),
					XSDEditorPlugin.getXSDString("_UI_GRAPH_TYPES"), XSDEditorPlugin.getPlugin().getIconImage("obj16/typesheader"), types, xsdSchema, CategoryAdapter.TYPES);
		fGroupsCategory = new CategoryAdapter( // XSDEditPlugin.getString("_UI_ModelGroups_label"),
					XSDEditorPlugin.getXSDString("_UI_GRAPH_GROUPS"), XSDEditorPlugin.getPlugin().getIconImage("obj16/groupsheader"), groups, xsdSchema, CategoryAdapter.GROUPS);
		fNotationsCategory = new CategoryAdapter( // XSDEditPlugin.getString("_UI_Notations_label"),
					XSDEditorPlugin.getXSDString("_UI_GRAPH_NOTATIONS"), XSDEditorPlugin.getPlugin().getIconImage("obj16/notationsheader"), notations, xsdSchema, CategoryAdapter.NOTATIONS);
	}

	public void setTarget(Notifier newTarget) {
		super.setTarget(newTarget);

		XSDSchema xsdSchema = ((XSDSchema) newTarget);
		createCategoryAdapters(xsdSchema);
	}

	public Object[] getChildren(Object parentElement) {
		XSDSchema xsdSchema = ((XSDSchema) parentElement);

		children = new ArrayList();

		// just set categoryadapters' children if category adapters are
		// already created
		if (fDirectivesCategory != null) {
			List directivesList = getDirectives(xsdSchema);
			List elementsList = getGlobalElements(xsdSchema);
			List attributeGroupList = getAttributeGroupList(xsdSchema);
			List attributesList = getAttributeList(xsdSchema);
			List groups = getGroups(xsdSchema);
			List notations = getNotations(xsdSchema);
			List types = getComplexTypes(xsdSchema);
			types.addAll(getSimpleTypes(xsdSchema));

			fDirectivesCategory.setChildren(directivesList);
			fElementsCategory.setChildren(elementsList);
			fAttributesCategory.setChildren(attributesList);
			fAttributeGroupsCategory.setChildren(attributeGroupList);
			fTypesCategory.setChildren(types);
			fGroupsCategory.setChildren(groups);
			fNotationsCategory.setChildren(notations);

			// children.add
			// (new CategoryAdapter
			// ( //XSDEditPlugin.getString("_UI_IdentityConstraints_label"),
			// "Identity Constraints",
			// XSDEditorPlugin.getPlugin().getIconImage("full/obj16/XSDIdentityConstraintDefinitionKey"),
			// xsdSchema.getIdentityConstraintDefinitions(), xsdSchema,
			// CategoryAdapter.IDENTITY_CONSTRAINTS));
			// children.add
			// (new CategoryAdapter
			// ( // XSDEditPlugin.getString("_UI_Annotations_label"),
			// "Annotations",
			// XSDEditorPlugin.getPlugin().getIconImage("obj16/annotationsheader"),
			// xsdSchema.getAnnotations(), xsdSchema,
			// CategoryAdapter.ANNOTATIONS));
		}
		else {
			createCategoryAdapters(xsdSchema);
		}

		children.add(fDirectivesCategory);
		children.add(fElementsCategory);
		children.add(fAttributesCategory);
		children.add(fAttributeGroupsCategory);
		children.add(fTypesCategory);
		children.add(fGroupsCategory);
		children.add(fNotationsCategory);

		return children.toArray();
	}

	public boolean hasChildren(Object object) {
		return true;
	}

	public Object getParent(Object object) {
		return null;
	}

	public void notifyChanged(final Notification msg) {
		class CategoryNotification extends NotificationImpl {
			protected Object category;

			public CategoryNotification(Object category) {
				super(msg.getEventType(), msg.getOldValue(), msg.getNewValue(), msg.getPosition());
				this.category = category;
			}

			public Object getNotifier() {
				return category;
			}

			public Object getFeature() {
				return msg.getFeature();
			}
		}

		if (children == null) {
			getChildren(target);
		}

		if (msg.getFeature() == xsdPackage.getXSDSchema_Contents()) {
			CategoryAdapter adapter = (CategoryAdapter) children.get(0);
			XSDSchema xsdSchema = adapter.getXSDSchema();
			adapter.setChildren(getDirectives(xsdSchema));
			this.fireNotifyChanged(new CategoryNotification(adapter));
			return;
		}
		else if (msg.getFeature() == xsdPackage.getXSDSchema_ElementDeclarations()) {
			CategoryAdapter adapter = (CategoryAdapter) children.get(1);
			XSDSchema xsdSchema = adapter.getXSDSchema();
			adapter.setChildren(getGlobalElements(xsdSchema));
			this.fireNotifyChanged(new CategoryNotification(adapter));
			return;
		}
		else if (msg.getFeature() == xsdPackage.getXSDSchema_AttributeDeclarations()) {
			CategoryAdapter adapter = (CategoryAdapter) children.get(2);
			XSDSchema xsdSchema = adapter.getXSDSchema();
			adapter.setChildren(getAttributeList(xsdSchema));
			this.fireNotifyChanged(new CategoryNotification(adapter));
			return;
		}
		else if (msg.getFeature() == xsdPackage.getXSDSchema_AttributeGroupDefinitions()) {
			CategoryAdapter adapter = (CategoryAdapter) children.get(3);
			XSDSchema xsdSchema = adapter.getXSDSchema();
			adapter.setChildren(getAttributeGroupList(xsdSchema));
			this.fireNotifyChanged(new CategoryNotification(adapter));
			return;
		}
		else if (msg.getFeature() == xsdPackage.getXSDSchema_TypeDefinitions()) {
			CategoryAdapter adapter = (CategoryAdapter) children.get(4);
			XSDSchema xsdSchema = adapter.getXSDSchema();
			List types = getComplexTypes(xsdSchema);
			types.addAll(getSimpleTypes(xsdSchema));

			adapter.setChildren(types);
			this.fireNotifyChanged(new CategoryNotification(adapter));
			return;
		}
		else if (msg.getFeature() == xsdPackage.getXSDSchema_ModelGroupDefinitions()) {
			CategoryAdapter adapter = (CategoryAdapter) children.get(5);
			XSDSchema xsdSchema = adapter.getXSDSchema();
			adapter.setChildren(getGroups(xsdSchema));
			this.fireNotifyChanged(new CategoryNotification(adapter));
			return;
		}
		else if (msg.getFeature() == xsdPackage.getXSDSchema_NotationDeclarations()) {
			CategoryAdapter adapter = (CategoryAdapter) children.get(6);
			XSDSchema xsdSchema = adapter.getXSDSchema();
			adapter.setChildren(getNotations(xsdSchema));
			this.fireNotifyChanged(new CategoryNotification(adapter));
			return;
		}
		// else if (msg.getFeature() ==
		// xsdPackage.getXSDSchema_IdentityConstraintDefinitions())
		// {
		// this.fireNotifyChanged(new CategoryNotification(children.get(7)));
		// return;
		// }
		else if (msg.getFeature() == xsdPackage.getXSDSchema_Annotations()) {
			// this.fireNotifyChanged(new
			// CategoryNotification(children.get(7)));
			return;
		}
		else if (msg.getFeature() == xsdPackage.getXSDSchema_SchemaLocation()) {
			this.fireNotifyChanged(msg);
			return;
		}

		super.notifyChanged(msg);
	}

	protected List getDirectives(XSDSchema schema) {
		List list = new ArrayList();
		for (Iterator i = schema.getContents().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof XSDSchemaDirective) {
				list.add(o);
			}
		}
		return list;
	}

	protected List getAttributeGroupList(XSDSchema xsdSchema) {
		List attributeGroupList = new ArrayList();
		for (Iterator i = xsdSchema.getAttributeGroupDefinitions().iterator(); i.hasNext();) {
			XSDAttributeGroupDefinition attrGroup = (XSDAttributeGroupDefinition) i.next();
			if (attrGroup.getRootContainer() == xsdSchema) {
				attributeGroupList.add(attrGroup);
			}
		}
		return attributeGroupList;
	}

	protected List getAttributeList(XSDSchema xsdSchema) {
		List attributesList = new ArrayList();
		for (Iterator iter = xsdSchema.getAttributeDeclarations().iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o instanceof XSDAttributeDeclaration) {
				XSDAttributeDeclaration attr = (XSDAttributeDeclaration) o;
				if (attr != null) {
					if (attr.getTargetNamespace() != null) {
						if (!(attr.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema-instance"))) {
							if (attr.getRootContainer() == xsdSchema) {
								attributesList.add(attr);
							}
						}
					}
					else {
						if (attr.getRootContainer() == xsdSchema) {
							attributesList.add(attr);
						}
					}
				}
			}
		}
		return attributesList;
	}

	protected List getGlobalElements(XSDSchema schema) {
		List elements = schema.getElementDeclarations();
		List list = new ArrayList();
		for (Iterator i = elements.iterator(); i.hasNext();) {
			XSDElementDeclaration elem = (XSDElementDeclaration) i.next();
			if (elem.getRootContainer() == schema) {
				list.add(elem);
			}
		}
		return list;
	}

	protected List getComplexTypes(XSDSchema schema) {
		List allTypes = schema.getTypeDefinitions();
		List list = new ArrayList();
		for (Iterator i = allTypes.iterator(); i.hasNext();) {
			XSDTypeDefinition td = (XSDTypeDefinition) i.next();
			if (td instanceof XSDComplexTypeDefinition) {
				XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition) td;
				if (ct.getRootContainer() == schema) {
					list.add(ct);
				}
			}
		}
		return list;
	}

	protected List getSimpleTypes(XSDSchema schema) {
		List allTypes = schema.getTypeDefinitions();
		List list = new ArrayList();
		for (Iterator i = allTypes.iterator(); i.hasNext();) {
			XSDTypeDefinition td = (XSDTypeDefinition) i.next();
			if (td instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition) td;
				if (st.getRootContainer() == schema) {
					list.add(st);
				}
			}
		}
		return list;
	}

	protected List getGroups(XSDSchema schema) {
		List groups = schema.getModelGroupDefinitions();
		List list = new ArrayList();
		for (Iterator i = groups.iterator(); i.hasNext();) {
			XSDModelGroupDefinition group = (XSDModelGroupDefinition) i.next();
			if (group.getRootContainer() == schema) {
				list.add(group);
			}
		}
		return list;
	}

	protected List getNotations(XSDSchema schema) {
		List notations = schema.getNotationDeclarations();
		List list = new ArrayList();
		for (Iterator i = notations.iterator(); i.hasNext();) {
			XSDNotationDeclaration notation = (XSDNotationDeclaration) i.next();
			if (notation.getRootContainer() == schema) {
				list.add(notation);
			}
		}
		return list;
	}

}
