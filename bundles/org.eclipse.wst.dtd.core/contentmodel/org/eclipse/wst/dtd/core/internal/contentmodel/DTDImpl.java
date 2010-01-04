/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.contentmodel;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.ibm.icu.util.StringTokenizer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicType;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDType;
import org.eclipse.wst.dtd.core.internal.emf.impl.DTDBasicTypeImpl;
import org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDMetrics;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDUtil;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocumentation;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMEntityDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.annotation.AnnotationMap;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMDataTypeImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMEntityDeclarationImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMNamedNodeMapImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMNodeListImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDescriptionBuilder;


public class DTDImpl {
	static {
		// Call init on the DTD package to avoid strange initialization bugs
		//
		DTDPackageImpl.init();
	}

	protected static DTDAdapterFactoryImpl dtdAdapterFactoryImpl = new DTDAdapterFactoryImpl();
	protected static CMDataTypeInfoTable cmDataTypeInfoTable = new CMDataTypeInfoTable();

	public static CMDocument buildCMDocument(String uri) {
		DTDFile dtdFile = buildDTDModel(uri);
		CMDocument cmDocument = (CMDocument) getAdapter(dtdFile);
		
		return cmDocument;
	}

	public static DTDFile buildDTDModel(String uri) {
		DTDUtil dtdUtil = new DTDUtil();
		dtdUtil.setexpandEntityReferences(true);
		dtdUtil.parse(new ResourceSetImpl(), uri);
		return dtdUtil.getDTDFile();
	}

	public static CMDocument buildCMDocument(DTDFile dtdFile) {
		return (CMDocument) getAdapter(dtdFile);
	}

	public static CMNode getAdapter(Notifier o) {
		return (CMNode) dtdAdapterFactoryImpl.adapt(o);
	}

	public static int getMinOccurHelper(DTDRepeatableContent content) {
		int occurence = content.getOccurrence().getValue();
		boolean isOptional = (occurence == DTDOccurrenceType.OPTIONAL || occurence == DTDOccurrenceType.ZERO_OR_MORE);
		return isOptional ? 0 : 1;
	}

	public static int getMaxOccurHelper(DTDRepeatableContent content) {
		int occurence = content.getOccurrence().getValue();
		boolean isMulti = (occurence == DTDOccurrenceType.ONE_OR_MORE || occurence == DTDOccurrenceType.ZERO_OR_MORE);
		return isMulti ? -1 : 1;
	}

	public static class DTDAdapterFactoryImpl extends AdapterFactoryImpl {
		public Adapter createAdapter(Notifier target) {
			Adapter result = null;
			if (target != null) {
				if (target instanceof DTDAttribute) {
					result = new DTDAttributeAdapter((DTDAttribute) target);
				}
				else if (target instanceof DTDElement) {
					result = new DTDElementAdapter((DTDElement) target);
				}
				else if (target instanceof DTDElementReferenceContent) {
					result = new DTDElementReferenceContentAdapter((DTDElementReferenceContent) target);
				}
				else if (target instanceof DTDFile) {
					result = new DTDFileAdapter((DTDFile) target);
				}
				else if (target instanceof DTDGroupContent) {
					result = new DTDGroupContentAdapter((DTDGroupContent) target);
				}
				else if (target instanceof DTDEntity) {
					result = new DTDEntityAdapter((DTDEntity) target);
				}
			}
			return result;
		}

		public synchronized Adapter adapt(Notifier target) {
			return adapt(target, this);
		}
	}

	/**
	 * DTDBaseAdapter
	 */
	public static abstract class DTDBaseAdapter extends CMNodeImpl {
		public String getNodeName() {
			return ""; //$NON-NLS-1$
		}

		public boolean isAdapterForType(Object type) {
			return type == dtdAdapterFactoryImpl;
		}

		public Object getProperty(String propertyName) {
			Object result = null;
			if (propertyName.equals("CMDocument")) { //$NON-NLS-1$
				result = getCMDocument();
			}
			else if (propertyName.equals(PROPERTY_DOCUMENTATION)) {
				result = getDocumentation();
			}
			else if (propertyName.equals(PROPERTY_DEFINITION_INFO)) {
				result = getDefinitionInfo();
			}
			else if (propertyName.equals(PROPERTY_DEFINITION)) {
				result = getDefinition();
			}
			else if (propertyName.equals(PROPERTY_MOF_NOTIFIER)) {
				result = getKey();
			}
			else if (propertyName.equals("spec")) { //$NON-NLS-1$
				result = getSpec();
			}
			else {
				result = super.getProperty(propertyName);
				if (result == null) {
					CMDocument cmDocument = getCMDocument();
					if (cmDocument instanceof DTDFileAdapter) {
						AnnotationMap map = ((DTDFileAdapter) cmDocument).annotationMap;
						if (map != null) {
							String spec = getSpec();
							if (spec != null) {
								result = map.getProperty(getSpec(), propertyName);
							}
						}
						if (result == null) {
							Map globalPropertyMap = ((DTDFileAdapter) cmDocument).globalPropertyMap;
							result = globalPropertyMap.get(propertyName);
						}
					}
				}
			}
			return result;
		}

		public CMDocument getCMDocument() {
			return null;
		}

		protected CMNodeList getDocumentation() {
			return new CMNodeListImpl();
		}

		public String getDefinitionInfo() {
			return null;
		}

		public CMNode getDefinition() {
			return null;
		}

		public String getSpec() {
			return getNodeName();
		}
	}

	/**
	 * DTDAttributeAdapter
	 */
	public static class DTDAttributeAdapter extends DTDBaseAdapter implements CMAttributeDeclaration {
		protected DTDAttribute attribute;
		protected CMDataType dataType;

		public DTDAttributeAdapter(DTDAttribute attribute) {
			this.attribute = attribute;
			dataType = new DataTypeImpl();
		}

		public Object getKey() {
			return attribute;
		}

		public String getSpec() {
			return attribute.getDTDElement().getName() + "/@" + attribute.getName(); //$NON-NLS-1$
		}

		public int getNodeType() {
			return ATTRIBUTE_DECLARATION;
		}

		public String getNodeName() {
			return attribute.getName();
		}

		public String getAttrName() {
			return attribute.getName();
		}

		public CMDataType getAttrType() {
			return dataType;
		}

		public String getDefaultValue() {
			return attribute.getDefaultValueString();
		}

		public Enumeration getEnumAttr() {
			return Collections.enumeration(attribute.getEnumeratedValues());
		}

		public int getUsage() {
			int usage = OPTIONAL;
			switch (attribute.getDefaultKind().getValue()) {
				case DTDDefaultKind.REQUIRED : {
					usage = REQUIRED;
					break;
				}
				case DTDDefaultKind.FIXED : {
					usage = FIXED;
					break;
				}
			}
			return usage;
		}

		public CMDocument getCMDocument() {
			DTDFile dtdFile = attribute.getDTDElement().getDTDFile();
			return dtdFile != null ? (CMDocument) getAdapter(dtdFile) : null;
		}

		public class DataTypeImpl implements CMDataType {
			public int getNodeType() {
				return CMNode.DATA_TYPE;
			}

			public String getNodeName() {
				return getDataTypeName();
			}

			public boolean supports(String propertyName) {
				return false;
			}

			public Object getProperty(String propertyName) {
				Object result = null;
				if (propertyName.equals("isValidEmptyValue")) { //$NON-NLS-1$
					String dataTypeName = getDataTypeName();
					result = (dataTypeName == null || dataTypeName.equals(CMDataType.CDATA)) ? "true" : "false"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				return result;
			}

			public String getDataTypeName() {
				return cmDataTypeInfoTable.getDataTypeName(attribute.getDTDType());
			}

			public String generateInstanceValue() {
				return cmDataTypeInfoTable.getInstanceValue(attribute.getDTDType());
			}

			public int getImpliedValueKind() {
				int result = IMPLIED_VALUE_NONE;
				int defaultValueKind = attribute.getDefaultKind().getValue();
				if (defaultValueKind == DTDDefaultKind.NOFIXED) {
					result = IMPLIED_VALUE_DEFAULT;
				}
				else if (defaultValueKind == DTDDefaultKind.FIXED) {
					result = IMPLIED_VALUE_FIXED;
				}
				return result;
			}

			public String getImpliedValue() {
				return attribute.getDefaultValueString();
			}

			public String[] getEnumeratedValues() {
				Object[] objectList = attribute.getEnumeratedValues().toArray();
				String[] result = new String[objectList.length];
				for (int i = 0; i < objectList.length; i++) {
					result[i] = objectList[i].toString();
				}
				return result;
			}
		}
	}


	/**
	 * DTDElementBaseAdapter
	 */
	public static abstract class DTDElementBaseAdapter extends DTDBaseAdapter implements CMElementDeclaration {
		protected CMDataType dataType;

		protected abstract DTDElement getDTDElement();

		protected CMDocumentation documentation = null;

		public int getNodeType() {
			return ELEMENT_DECLARATION;
		}

		public String getNodeName() {
			return getDTDElement().getName();
		}

		public CMNamedNodeMap getAttributes() {
			CMNamedNodeMapImpl result = new CMNamedNodeMapImpl();
			List attributeList = getDTDElement().getDTDAttribute();
			for (Iterator i = attributeList.iterator(); i.hasNext();) {
				DTDAttribute attribute = (DTDAttribute) i.next();
				result.getHashtable().put(attribute.getName(), getAdapter(attribute));
			}
			return result;
		}

		public CMContent getContent() {
			return (CMContent) getAdapter(getDTDElement().getContent());
		}

		public int getContentType() {
			// todo
			int result = ELEMENT;
			DTDElementContent dtdElementContent = getDTDElement().getContent();
			if (dtdElementContent instanceof DTDPCDataContent) {
				result = PCDATA;
			}
			else if (dtdElementContent instanceof DTDGroupContent) {
				DTDGroupContent groupContent = (DTDGroupContent) dtdElementContent;
				int groupKind = groupContent.getGroupKind().getValue();
				if (groupKind == DTDGroupKind.CHOICE) {
					List list = groupContent.getContent();
					if (list.size() > 0 && list.get(0) instanceof DTDPCDataContent) {
						result = MIXED;
					}
				}
			}
			else if (dtdElementContent instanceof DTDAnyContent) {
				result = ANY;
			}
			else if (dtdElementContent instanceof DTDEmptyContent) {
				result = EMPTY;
			}

			return result;
		}

		public String getElementName() {
			return getDTDElement().getName();
		}

		public CMDataType getDataType() {
			int contentType = getContentType();
			boolean hasDataType = contentType == PCDATA || contentType == MIXED;
			return hasDataType ? dataType : null;
		}

		public CMNamedNodeMap getLocalElements() {
			return CMNamedNodeMapImpl.EMPTY_NAMED_NODE_MAP;
		}

		public CMDocument getCMDocument() {
			DTDFile dtdFile = getDTDElement().getDTDFile();
			return dtdFile != null ? (CMDocument) getAdapter(dtdFile) : null;
		}

		protected CMNodeList getDocumentation() {
			CMNodeListImpl nodeList = new CMNodeListImpl();
			if (documentation == null) {
				String comment = getDTDElement().getComment();
				if (comment != null) {
					String value = ""; //$NON-NLS-1$
					StringTokenizer st = new StringTokenizer(comment, "\n"); //$NON-NLS-1$
					while (st.hasMoreTokens()) {
						value += st.nextToken().trim() + "\n"; //$NON-NLS-1$
					}
					documentation = new CMDocumentationImpl(value);
				}
			}
			if (documentation != null) {
				nodeList.getList().add(documentation);
			}
			return nodeList;
		}
	}


	public static class CMDocumentationImpl implements CMDocumentation {
		protected String value;

		public CMDocumentationImpl(String value) {
			this.value = value;
		}

		public String getNodeName() {
			return ""; //$NON-NLS-1$
		}

		public int getNodeType() {
			return DOCUMENTATION;
		}

		public boolean supports(String propertyName) {
			return false;
		}

		public Object getProperty(String propertyName) {
			return null;
		}

		public String getValue() {
			return value;
		}

		public String getLanguage() {
			return null;
		}

		public String getSource() {
			return null;
		}
	}


	/**
	 * DTDElementAdapter
	 */
	public static class DTDElementAdapter extends DTDElementBaseAdapter {
		protected DTDElement element;

		public DTDElementAdapter(DTDElement element) {
			this.element = element;
			dataType = new CMDataTypeImpl("#PCDATA", getDTDElement().getName()); //$NON-NLS-1$
		}

		public Object getKey() {
			return element;
		}

		protected DTDElement getDTDElement() {
			return element;
		}

		public int getMinOccur() {
			return 1;
		}

		public int getMaxOccur() {
			return 1;
		}

		public String getDefinitionInfo() {
			return "global"; //$NON-NLS-1$
		}

		public CMNode getDefinition() {
			return this;
		}
	}


	/**
	 * DTDElementReferenceContentAdapter
	 */
	public static class DTDElementReferenceContentAdapter extends DTDElementBaseAdapter {
		protected DTDElementReferenceContent content;

		public DTDElementReferenceContentAdapter(DTDElementReferenceContent content) {
			this.content = content;
			dataType = new CMDataTypeImpl("#PCDATA", getDTDElement().getName()); //$NON-NLS-1$
		}

		public Object getKey() {
			return content;
		}

		protected DTDElement getDTDElement() {
			return content.getReferencedElement();
		}

		public int getMinOccur() {
			return getMinOccurHelper(content);
		}

		public int getMaxOccur() {
			return getMaxOccurHelper(content);
		}


		public CMNode getDefinition() {
			return getAdapter(getDTDElement());
		}
	}


	/**
	 * DTDEntityAdapter
	 */
	public static class DTDEntityAdapter extends DTDBaseAdapter implements CMEntityDeclaration {
		protected DTDEntity dtdEntity;

		public DTDEntityAdapter(DTDEntity dtdEntity) {
			this.dtdEntity = dtdEntity;
		}

		public int getNodeType() {
			return ENTITY_DECLARATION;
		}

		public Object getKey() {
			return dtdEntity;
		}

		public String getName() {
			return dtdEntity.getName();
		}

		public String getValue() {
			String value = ""; //$NON-NLS-1$
			if (dtdEntity.getContent() instanceof DTDInternalEntity) {
				DTDInternalEntity content = (DTDInternalEntity) dtdEntity.getContent();
				value = content.getValue();
			}
			return value;
		}
	}


	/**
	 * DTDGroupContentAdapter
	 */
	public static class DTDGroupContentAdapter extends DTDBaseAdapter implements CMGroup {
		protected DTDGroupContent content;

		public DTDGroupContentAdapter(DTDGroupContent content) {
			this.content = content;
		}

		public Object getKey() {
			return content;
		}

		public int getNodeType() {
			return GROUP;
		}

		public String getNodeName() {
			CMDescriptionBuilder descriptionBuilder = new CMDescriptionBuilder();
			return descriptionBuilder.buildDescription(this);
		}

		public CMNodeList getChildNodes() {
			List list = content.getContent();
			CMNodeListImpl result = new CMNodeListImpl();
			for (Iterator i = list.iterator(); i.hasNext();) {
				CMNode node = getAdapter((Notifier) i.next());
				if (node != null) {
					result.getList().add(node);
				}
			}
			return result;
		}


		public int getMaxOccur() {
			return getMaxOccurHelper(content);
		}

		public int getMinOccur() {
			return getMinOccurHelper(content);
		}

		public int getOperator() {
			// todo... handle ALONE case by checkig if child count == 1
			int groupKind = content.getGroupKind().getValue();
			return (groupKind == DTDGroupKind.CHOICE) ? CHOICE : SEQUENCE;
		}
	}

	/**
	 * DTDFileAdapter
	 */
	public static class DTDFileAdapter extends DTDBaseAdapter implements CMDocument {
		protected final String DEFAULT_ROOT_NAME = "http://org.eclipse.wst/cm/properties/defaultRootName"; //$NON-NLS-1$

		protected DTDFile dtdFile;
		protected CMNamedNodeMapImpl namedNodeMap;
		protected CMNamedNodeMapImpl entityNodeMap;

		protected AnnotationMap annotationMap = new AnnotationMap();
		protected Map globalPropertyMap = new HashMap();

		public DTDFileAdapter(DTDFile dtdFile) {
			this.dtdFile = dtdFile;
		}

		public Object getKey() {
			return dtdFile;
		}

		public AnnotationMap getAnnotationMap() {
			return annotationMap;
		}

		public CMNamedNodeMap getElements() {
			if (namedNodeMap == null) {
				namedNodeMap = new CMNamedNodeMapImpl();
				for (Iterator iterator = dtdFile.listDTDElement().iterator(); iterator.hasNext();) {
					DTDElement dtdElement = (DTDElement) iterator.next();
					namedNodeMap.getHashtable().put(dtdElement.getName(), getAdapter(dtdElement));
				}
			}
			return namedNodeMap;
		}

		public CMNamedNodeMap getEntities() {
			if (entityNodeMap == null) {
				entityNodeMap = new CMNamedNodeMapImpl();

				// add the built in entity declarations
				//
				entityNodeMap.getHashtable().put("amp", new CMEntityDeclarationImpl("amp", "&")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				entityNodeMap.getHashtable().put("lt", new CMEntityDeclarationImpl("lt", "<")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				entityNodeMap.getHashtable().put("gt", new CMEntityDeclarationImpl("gt", ">")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				entityNodeMap.getHashtable().put("quot", new CMEntityDeclarationImpl("quot", "\"")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				entityNodeMap.getHashtable().put("apos", new CMEntityDeclarationImpl("apos", "'")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				for (Iterator i = dtdFile.listDTDEntity().iterator(); i.hasNext();) {
					DTDEntity dtdEntity = (DTDEntity) i.next();
					if (!dtdEntity.isParameterEntity() && dtdEntity.getName() != null) {
						// for now... just handle DTDInternalEntity
						//
						if (dtdEntity.getContent() instanceof DTDInternalEntity) {
							entityNodeMap.getHashtable().put(dtdEntity.getName(), getAdapter(dtdEntity));
						}
					}
				}
			}
			return entityNodeMap;
		}

		public CMNamespace getNamespace() {
			return null;
		}

		public int getNodeType() {
			return DOCUMENT;
		}

		public String getNodeName() {
			return dtdFile.getName() + ".dtd"; //$NON-NLS-1$
		}

		public Object getProperty(String propertyName) {
			Object result = null;
			if (propertyName.equals(DEFAULT_ROOT_NAME)) {
				DTDMetrics metrics = new DTDMetrics(dtdFile);
				DTDElement dtdElement = metrics.getLeastReferencedElement();
				if (dtdElement != null) {
					result = dtdElement.getName();
				}
			}
			else if (propertyName.equals("annotationMap")) { //$NON-NLS-1$
				result = annotationMap;
			}
			else if (propertyName.equals("globalPropertyMap")) { //$NON-NLS-1$
				result = globalPropertyMap;
			}
			else {
				result = super.getProperty(propertyName);
			}
			return result;
		}
	}


	public static class CMDataTypeInfoTable {
		protected String[] instanceValueTable;
		protected String[] dataTypeNameTable;

		public CMDataTypeInfoTable() {
			// hashtable.put(DTDType.CDATA, null));
			instanceValueTable = new String[DTDBasicTypeImpl.basicTypeKinds.length];
			instanceValueTable[DTDBasicTypeKind.ENTITY] = "entity"; //$NON-NLS-1$
			instanceValueTable[DTDBasicTypeKind.ENTITIES] = "entities"; //$NON-NLS-1$
			instanceValueTable[DTDBasicTypeKind.NMTOKEN] = "nmtoken"; //$NON-NLS-1$
			instanceValueTable[DTDBasicTypeKind.NMTOKENS] = "nmtokens"; //$NON-NLS-1$

			dataTypeNameTable = new String[DTDBasicTypeImpl.basicTypeKinds.length];
			dataTypeNameTable[DTDBasicTypeKind.CDATA] = CMDataType.CDATA;
			dataTypeNameTable[DTDBasicTypeKind.ID] = CMDataType.ID;
			dataTypeNameTable[DTDBasicTypeKind.IDREF] = CMDataType.IDREF;
			dataTypeNameTable[DTDBasicTypeKind.IDREFS] = CMDataType.IDREFS;
			dataTypeNameTable[DTDBasicTypeKind.ENTITY] = CMDataType.ENTITY;
			dataTypeNameTable[DTDBasicTypeKind.ENTITIES] = CMDataType.ENTITIES;
			dataTypeNameTable[DTDBasicTypeKind.NMTOKEN] = CMDataType.NMTOKEN;
			dataTypeNameTable[DTDBasicTypeKind.NMTOKENS] = CMDataType.NMTOKENS;
		}

		public String getInstanceValue(DTDType dtdType) {
			String result = null;
			if (dtdType instanceof DTDBasicType) {
				int kind = ((DTDBasicType) dtdType).getKind().getValue();
				if (kind >= 0 && kind < DTDBasicTypeImpl.basicTypeDescriptions.length) {
					result = instanceValueTable[kind];
				}
			}
			return result;
		}

		public String getDataTypeName(DTDType dtdType) {
			String result = null;
			if (dtdType instanceof DTDBasicType) {
				int kind = ((DTDBasicType) dtdType).getKind().getValue();
				if (kind >= 0 && kind < DTDBasicTypeImpl.basicTypeDescriptions.length) {
					result = dataTypeNameTable[kind];
				}
			}
			else if (dtdType instanceof DTDEnumerationType) {
				result = CMDataType.ENUM;
			}
			return result;
		}
	}
}
