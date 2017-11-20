/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf.util;

import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.dtd.core.internal.DTDCoreMessages;
import org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicType;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType;
import org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDFactory;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.DTDType;
import org.eclipse.wst.dtd.core.internal.emf.impl.DTDFactoryImpl;
import org.eclipse.wst.dtd.core.internal.saxparser.AttNode;
import org.eclipse.wst.dtd.core.internal.saxparser.Attlist;
import org.eclipse.wst.dtd.core.internal.saxparser.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.saxparser.CMGroupNode;
import org.eclipse.wst.dtd.core.internal.saxparser.CMNode;
import org.eclipse.wst.dtd.core.internal.saxparser.CMNodeType;
import org.eclipse.wst.dtd.core.internal.saxparser.CMReferenceNode;
import org.eclipse.wst.dtd.core.internal.saxparser.CMRepeatableNode;
import org.eclipse.wst.dtd.core.internal.saxparser.DTD;
import org.eclipse.wst.dtd.core.internal.saxparser.DTDSaxArtifactVisitor;
import org.eclipse.wst.dtd.core.internal.saxparser.ElementDecl;
import org.eclipse.wst.dtd.core.internal.saxparser.EntityDecl;
import org.eclipse.wst.dtd.core.internal.saxparser.ErrorMessage;
import org.eclipse.wst.dtd.core.internal.saxparser.NotationDecl;

public class DTDModelBuilder extends DTDSaxArtifactVisitor {
	DTD dtd;
	DTDUtil dtdUtil;
	DTDFile dtdFile;
	ResourceSet resources;

	public DTDModelBuilder(ResourceSet resources, DTDUtil dtdUtil, DTD dtd, DTDFile dtdFile) {
		this.resources = resources;
		this.dtdUtil = dtdUtil;
		this.dtd = dtd;
		this.dtdFile = dtdFile;
	}

	public DTDFactoryImpl getFactory() {
		return (DTDFactoryImpl) dtdUtil.getFactory();
	}

	public DTDFile getDTDFile() {
		return dtdFile;
	}

	public void visitDTD(DTD dtd) {
		super.visitDTD(dtd);

		// - 2nd pass -
		// do final processing for Attlists and adding contentModel
		addAttlistAndContentModel(dtd, dtdFile);
	}

	public void visitElementDecl(ElementDecl element) {
		// Element might have been added earlier because it is referenced
		String declName = element.getNodeName();
		// System.out.println("process ElementDecl:" + declName );
		DTDElement dtdelement = (DTDElement) dtdUtil.getElementPool().get(declName);
		if (dtdelement == null) {
			// System.out.println("process ElementDecl - not found - create"
			// );
			dtdelement = getFactory().createDTDElement();
			dtdelement.setName(declName);
			dtdFile.getDTDObject().add(dtdelement);
			dtdUtil.getElementPool().put(declName, dtdelement);
		}

		super.visitElementDecl(element);
	}

	public void visitNotationDecl(NotationDecl notation) {
		// Notation might have been added earlier because it is referenced
		// by an entity
		DTDNotation dtdnot = dtdFile.findNotation(notation.getNodeName());
		if (dtdnot == null) {
			dtdnot = getFactory().createDTDNotation();
			dtdFile.getDTDObject().add(dtdnot);
		}
		dtdnot.setName(notation.getNodeName());

		if (notation.getSystemId() != null)
			dtdnot.setSystemID(notation.getSystemId());
		if (notation.getPublicId() != null)
			dtdnot.setPublicID(notation.getPublicId());

		if (notation.getComment() != null)
			dtdnot.setComment(notation.getComment());
		if (notation.getErrorMessage() != null) {
			addErrorMessage(notation.getErrorMessage(), dtdnot);
		}

		super.visitNotationDecl(notation);
	}

	public void visitParameterEntityReferenceDecl(EntityDecl entity) {
		// This is a parameter entity reference.
		// Add the link to the real entity that it references
		DTDParameterEntityReference parmEntRef = getFactory().createDTDParameterEntityReference();
		dtdFile.getDTDObject().add(parmEntRef);

		String entityName = entity.getNodeName();

		// Add the reference to the DTDEntity
		DTDEntity dtdentity = getDTDFile().findEntity(entityName);
		if (dtdentity != null) {
			parmEntRef.setEntity(dtdentity);
		}
		if (entity.getErrorMessage() != null) {
			addErrorMessage(entity.getErrorMessage(), dtdentity);
		}
		// System.out.println("adding PE reference: " + declName);

		super.visitParameterEntityReferenceDecl(entity);
	}

	public void visitExternalEntityDecl(EntityDecl entity) {
		DTDEntity dtdEntity = createDTDEntity(entity);
		// System.out.println("adding entity: " + declName);
		DTDExternalEntity extEntity = getFactory().createDTDExternalEntity();
		dtdEntity.setContent(extEntity);

		finishExternalEntity(extEntity, entity);
		// System.out.println(" ext entity toMof: " );
		if (dtdEntity.isParameterEntity()) {

			dtdUtil.getPEPool().put("%" + entity.getNodeName() + ";", dtdEntity); //$NON-NLS-1$ //$NON-NLS-2$
		}

		super.visitExternalEntityDecl(entity);
	}

	public void visitInternalEntityDecl(EntityDecl entity) {
		DTDEntity dtdEntity = createDTDEntity(entity);

		DTDInternalEntity intEntity = getFactory().createDTDInternalEntity();
		dtdEntity.setContent(intEntity);
		intEntity.setValue(entity.getValue());
		// System.out.println(" int entity toMof: " );
		if (dtdEntity.isParameterEntity()) {
			dtdUtil.getPEPool().put("%" + entity.getNodeName() + ";", dtdEntity); //$NON-NLS-1$ //$NON-NLS-2$
		}

		super.visitInternalEntityDecl(entity);
	}

	private void addAttlistAndContentModel(DTD dtd, DTDFile dFile) {
		// System.out.println("addAttListAndCotentModel - dtd:" +
		// dtd.getName());
		Enumeration en = dtd.externalElements();
		while (en.hasMoreElements()) {
			Object e = en.nextElement();
			if (e instanceof ElementDecl) {
				DTDElement dtdelement = (DTDElement) dtdUtil.getElementPool().get(((ElementDecl) e).getNodeName());
				if (dtdelement == null) {
					dtdelement = getFactory().createDTDElement();
					dtdFile.getDTDObject().add(dtdelement);
				}
				finishElementDecl(dtdelement, (ElementDecl) e);
			}
			else if (e instanceof Attlist) {
				processAttList((Attlist) e);
			}
		}
	}


	private void processAttList(Attlist attList) {
		DTDElement e = (DTDElement) dtdUtil.getElementPool().get(attList.getNodeName());
		if (e != null) {
			// Element has been added. Add the attributes
			// System.out.println(" processAttlist - adding Attlist:" +
			// attList.getNodeName());
			createAttributes(e, attList);
		}
		else {
			// System.out.println("!!!!!!!!! element is null ");
		}
	}

	// /////////////////////////////////////////////////////////////
	//
	// Methods for finishing the import of a DTDExternalEntity
	//
	// /////////////////////////////////////////////////////////////
	void finishExternalEntity(DTDExternalEntity extEntity, EntityDecl entityDecl) {
		updateSystemID(extEntity, entityDecl);
		extEntity.setPublicID(entityDecl.getPublicId());

		String notationName = entityDecl.getNotationName();
		if (notationName != null) {
			DTDNotation dtdNotation = createOrFindNotation(extEntity, notationName, true);
			extEntity.setNotation(dtdNotation);
		}
	}

	public DTDNotation createOrFindNotation(DTDExternalEntity extEntity, String name, boolean create) {
		DTDNotation aNotation = extEntity.getDTDEntity().getDTDFile().findNotation(name);
		if (aNotation != null) {
			return aNotation;
		}

		//
		// Create a notation for the reference
		//
		if (create) {
			aNotation = getFactory().createDTDNotation();
			dtdFile.getDTDObject().add(aNotation);
			aNotation.setName(name);
		}
		return aNotation;
	}

	/**
	 * The SystemID attribute is set to whatever the user enters e.g.
	 * com/ibm/b2b/xmimodels/xxx.dtd.xmi
	 * 
	 * In the unparse() method, parse out the DTD file name from the classpath
	 * name. e.g. returns xxx.dtd
	 */
	private void updateSystemID(DTDExternalEntity extEntity, EntityDecl entityDecl) {
		String systemId = entityDecl.getSystemId();
		String publicId = entityDecl.getPublicId();
		if (systemId != null) {
			URIResolver idResolver = URIResolverPlugin.createResolver();
			String uri = idResolver.resolve(dtd.getName(), publicId, systemId);
			ExternalDTDModel ed = dtdUtil.getExternalDTDModel(resources, uri);
			if (ed != null) {
				DTDFile referenceDtdFile = ed.getExternalDTDFile();
				extEntity.setEntityReferencedFromAnotherFile(referenceDtdFile);
				extEntity.setSystemID(systemId);
			}
			else {
				if (entityDecl.getErrorMessage() == null) {
					ErrorMessage dtdError = new ErrorMessage();
					dtdError.setErrorMessage(DTDCoreMessages._ERROR_INCL_FILE_LOAD_FAILURE + " '" + systemId + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					addErrorMessage(dtdError, extEntity.getDTDEntity());
				}

				if (systemId != null) {
					extEntity.setSystemID(systemId);
				}
				else {
					extEntity.setSystemID(""); //$NON-NLS-1$
				}
			}
		} // end of if ()
		else {
			// set the system id to be ""
			extEntity.setSystemID(""); //$NON-NLS-1$
		}
	}

	public DTDEntity createDTDEntity(EntityDecl entity) {
		// create and do what we can to fill in some basic things
		DTDEntity dtdEntity = getFactory().createDTDEntity();
		dtdFile.getDTDObject().add(dtdEntity);

		dtdEntity.setName(entity.getNodeName());
		dtdEntity.setParameterEntity(entity.isParameter());
		if (entity.getComment() != null) {
			dtdEntity.setComment(entity.getComment());
		}
		if (entity.getErrorMessage() != null) {
			addErrorMessage(entity.getErrorMessage(), dtdEntity);
		}

		return dtdEntity;
	}


	// /////////////////////////////////////////////////////////////
	//
	// Methods for finishing the import of a DTDElement
	//
	// /////////////////////////////////////////////////////////////
	public void finishElementDecl(DTDElement dtdElement, ElementDecl ed) {
		dtdElement.setName(ed.getNodeName());

		CMNode cmNode = ed.getContentModelNode();

		if (ed.getComment() != null) {
			dtdElement.setComment(ed.getComment());
		}
		if (ed.getErrorMessage() != null) {
			addErrorMessage(ed.getErrorMessage(), dtdElement);
		}

		if (cmNode instanceof CMBasicNode) {
			CMBasicNode bn = (CMBasicNode) cmNode;
			switch (bn.getType()) {
				case CMNodeType.EMPTY :
					DTDEmptyContent emptyContent = getFactory().createDTDEmptyContent();
					dtdElement.setContent(emptyContent);
					break;

				case CMNodeType.ANY :
					DTDAnyContent anyContent = getFactory().createDTDAnyContent();
					dtdElement.setContent(anyContent);
					break;

				case CMNodeType.PCDATA :
					DTDPCDataContent pcData = getFactory().createDTDPCDataContent();
					dtdElement.setContent(pcData);
			}

		}
		else if (cmNode instanceof CMReferenceNode) {
			CMReferenceNode rn = (CMReferenceNode) cmNode;
			if (rn.getType() == CMNodeType.ENTITY_REFERENCE) {
				String entityName = rn.getName().trim();

				DTDEntity anEntity = (DTDEntity) dtdUtil.getPEPool().get(entityName);
				if (anEntity != null) {
					//
					// Create an DTDEntityReference and set its referenced
					// element
					//
					DTDEntityReferenceContent enRef = getFactory().createDTDEntityReferenceContent();
					enRef.setElementReferencedEntity(anEntity);

					DTDOccurrenceType occurrenceType = DTDOccurrenceType.get(computeMofOccurrence(rn));
					enRef.setOccurrence(occurrenceType);

					dtdElement.setContent(enRef);
				}
				else { // create default content
					DTDEmptyContent emptyContent = getFactory().createDTDEmptyContent();
					dtdElement.setContent(emptyContent);
				}
			}
			else {
				//
				// Find the real element for this element references
				// If the real element does not yet exist, create it
				//
				DTDElement anElement = createOrFindElement(rn.getName(), dtdElement);

				//
				// Create an DTDElementReference and set its referenced
				// element
				//
				DTDElementReferenceContent elemRef = getFactory().createDTDElementReferenceContent();
				elemRef.setReferencedElement(anElement);

				DTDOccurrenceType occurrenceType = DTDOccurrenceType.get(computeMofOccurrence(rn));
				elemRef.setOccurrence(occurrenceType);

				// setContent to DTDElementReference
				dtdElement.setContent(elemRef);

			}
		}
		else if (cmNode instanceof CMGroupNode) {
			CMGroupNode grpNode = (CMGroupNode) cmNode;
			DTDGroupContent groupContent = getFactory().createDTDGroupContent();
			DTDGroupKind groupKind = DTDGroupKind.get(computeMofGroupKind(grpNode.getGroupKind()));
			groupContent.setGroupKind(groupKind);
			DTDOccurrenceType occurrenceType = DTDOccurrenceType.get(computeMofOccurrence(grpNode));
			groupContent.setOccurrence(occurrenceType);

			// just use the locator for the element as the closest guess
			processGroupContent(groupContent, grpNode);
			dtdElement.setContent(groupContent);
		}
		else if (cmNode == null) {
			// bad thing happened here, just create a pcdata
			DTDEmptyContent emptyContent = getFactory().createDTDEmptyContent();
			dtdElement.setContent(emptyContent);
		}


	}


	/**
	 * Add a new group to the current group
	 * 
	 * @param parent -
	 *            the parent node for this group element
	 * @param op1Node -
	 *            the group e.g. (a,b)
	 * @param op2Node -
	 *            set only if called by processCM1op e.g. (a,b)+
	 */
	void processGroupContent(DTDGroupContent parent, CMGroupNode grpNode) {
		Enumeration children = grpNode.getChildren().elements();
		DTDFactory factory = getFactory();
		while (children.hasMoreElements()) {
			CMNode cnode = (CMNode) children.nextElement();

			if (cnode instanceof CMGroupNode) {
				CMGroupNode gNode = (CMGroupNode) cnode;
				DTDGroupContent groupContent = factory.createDTDGroupContent();
				DTDGroupKind groupKind = DTDGroupKind.get(computeMofGroupKind(gNode.getGroupKind()));
				groupContent.setGroupKind(groupKind);
				DTDOccurrenceType occurrenceType = DTDOccurrenceType.get(computeMofOccurrence(gNode));
				groupContent.setOccurrence(occurrenceType);

				parent.getContent().add(groupContent);
				processGroupContent(groupContent, gNode);
			}
			else if (cnode instanceof CMBasicNode) {
				CMBasicNode n = (CMBasicNode) cnode;
				if (n.getType() == CMNodeType.PCDATA) {
					// Create a DTDPCDataContent for a leaf PCData node
					//
					DTDPCDataContent pcData = factory.createDTDPCDataContent();
					// Add #PCDATA to the Group, i.e Mixed content model
					parent.getContent().add(pcData);
				}
			}
			else if (cnode instanceof CMReferenceNode) {
				CMReferenceNode rn = (CMReferenceNode) cnode;
				if (rn.getType() == CMNodeType.ELEMENT_REFERENCE) {
					// System.out.println("CM Element Ref name: " +
					// rn.getName());
					//
					// Create an DTDElementReference and set its referenced
					// element
					//
					DTDElementReferenceContent elemRef = factory.createDTDElementReferenceContent();

					//
					// Find the real element for this element references
					// If the real element does not yet exist, create it
					//
					DTDElement anElement = createOrFindElement(rn.getName(), elemRef);

					elemRef.setReferencedElement(anElement);

					DTDOccurrenceType occurrenceType = DTDOccurrenceType.get(computeMofOccurrence(rn));
					elemRef.setOccurrence(occurrenceType);

					// Add DTDElementReference to the Group
					parent.getContent().add(elemRef);
				}
				else // PE Reference
				{
					String entityName = rn.getName().trim();

					// System.out.println("CM PE Ref name: " + entityName);
					DTDEntity anEntity = (DTDEntity) dtdUtil.getPEPool().get(entityName);
					if (anEntity != null) {
						//
						// Create an DTDEntityReference and set its referenced
						// element
						//
						DTDEntityReferenceContent enRef = factory.createDTDEntityReferenceContent();
						enRef.setElementReferencedEntity(anEntity);

						DTDOccurrenceType occurrenceType = DTDOccurrenceType.get(computeMofOccurrence(rn));
						enRef.setOccurrence(occurrenceType);

						// Add DTDEntityReference to the Group
						parent.getContent().add(enRef);

					}
				}
			}
		}
	}

	/**
	 * Find the real element that is referenced by the current element
	 */
	private DTDElement createOrFindElement(String name, Object obj) {
		// DTDElement aElement = getDTDFile().findElement(name);

		DTDElement aElement = (DTDElement) dtdUtil.getElementPool().get(name);

		if (aElement != null) {
			return aElement;
		}

		String errorMsg = DTDCoreMessages._ERROR_UNDECLARED_ELEMENT_1; //$NON-NLS-1$
		errorMsg += "\"" + name + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		errorMsg += DTDCoreMessages._UI_ERRORPART_UNDECLARED_ELEMENT_2; //$NON-NLS-1$

		ErrorMessage dtdError = new ErrorMessage();

		dtdError.setErrorMessage(errorMsg);
		addErrorMessage(dtdError, obj);
		// System.out.println(errorMsg);
		// setDTDErrorMessage(errorMsg);
		getDTDFile().setParseError(true);

		//
		// Create an empty element for the reference to make it valid
		//

		DTDFactory factory = getFactory();
		DTDElement dtdelement = factory.createDTDElement();
		dtdelement.setName(name);

		DTDEmptyContent emptyContent = factory.createDTDEmptyContent();
		dtdelement.setContent(emptyContent);

		getDTDFile().getDTDObject().add(dtdelement);
		dtdUtil.getElementPool().put(name, dtdelement);
		return dtdelement;
	}


	/**
	 * Compute the MOF occurrence from the xml4j occurrence
	 */
	private int computeMofOccurrence(CMRepeatableNode rnode) {
		int occurrence = rnode.getOccurrence();
		int mofoccur = DTDOccurrenceType.ONE;

		if (occurrence == CMNodeType.ZERO_OR_MORE) {
			mofoccur = DTDOccurrenceType.ZERO_OR_MORE;
		}
		else if (occurrence == CMNodeType.ONE_OR_MORE) {
			mofoccur = DTDOccurrenceType.ONE_OR_MORE;
		}
		else if (occurrence == CMNodeType.OPTIONAL) {
			mofoccur = DTDOccurrenceType.OPTIONAL;
		}
		return mofoccur;
	}

	/**
	 * Compute the MOF model group from the xml4j model group
	 */
	private int computeMofGroupKind(int type) {
		if (type == CMNodeType.GROUP_CHOICE) {
			return DTDGroupKind.CHOICE;
		}
		else {
			return DTDGroupKind.SEQUENCE;
		}
	}


	// /////////////////////////////////////////////////////////////
	//
	// Methods for creating the attributes of a DTDElement
	//
	// /////////////////////////////////////////////////////////////
	public void createAttributes(DTDElement element, Attlist attList) {
		if (attList.getErrorMessage() != null) {
			addErrorMessage(attList.getErrorMessage(), element);
		}

		for (int i = 0; i < attList.size(); i++) {
			AttNode ad = attList.elementAt(i);
			// DTDAttributeElement dtdAtt=
			// getDTDAttributeElement(a.getName());

			// only add the AttDef if it is not added yet
			// ignore the dup AttDef as documented in the XML 1.0 specs
			// if( dtdAtt==null)
			addAttribute(element, ad);
		}

	}

	/**
	 * Create a DTDAttribute from the xml4j attribute
	 */
	public void addAttribute(DTDElement dtdelement, AttNode ad) {
		DTDAttribute dtdattr = getFactory().createDTDAttribute();
		dtdelement.getDTDAttribute().add(dtdattr);
		finishAttribute(dtdattr, ad);
	}

	// Stuff for populating attribute
	public void finishAttribute(DTDAttribute dtdattr, AttNode attdef) {
		boolean parseError = false;

		if (attdef.name.startsWith("%")) { //$NON-NLS-1$
			String peName = attdef.name.trim();
			DTDEntity en = (DTDEntity) dtdUtil.getPEPool().get(peName);
			if (en != null) {
				dtdattr.setAttributeNameReferencedEntity(en);
			}
		}

		dtdattr.setName(attdef.name);

		int attrType = attdef.getDeclaredType();

		if (attrType == AttNode.PEREFERENCE && attdef.type != null) {
			String peName = attdef.type.trim();
			DTDEntity en = (DTDEntity) dtdUtil.getPEPool().get(peName);
			if (en != null) {
				dtdattr.setAttributeTypeReferencedEntity(en);
				setAttrDTDType(dtdattr, getFactory().getDTDBasicType_CDATA()); // hack,
																				// so
																				// we
																				// can
																				// get
																				// back
																				// the
																				// default
																				// value
			}
			else
				// set default type
				setAttrDTDType(dtdattr, getFactory().getDTDBasicType_CDATA());

		}
		else {
			switch (attrType) {
				case AttNode.CDATA :
					setAttrDTDType(dtdattr, getFactory().getDTDBasicType_CDATA());
					break;

				case AttNode.ENTITIES :
					setAttrDTDType(dtdattr, getFactory().getDTDBasicType_ENTITIES());
					break;

				case AttNode.ENTITY :
					setAttrDTDType(dtdattr, getFactory().getDTDBasicType_ENTITY());
					break;

				case AttNode.ID :
					// check for duplicate ID attribute
					if (hasIDAttribute(dtdattr)) {
						/*String errMsg = DTDCoreMessages._ERROR_DUP_ID_ATTRIBUTE_1; //$NON-NLS-1$
						errMsg += attdef.name + DTDCoreMessages._UI_ERRORPART_DUP_ID_ATTRIBUTE_2; //$NON-NLS-1$*/
						// dtdattr.getDTDElement().getIElement().setDTDErrorMessage(errMsg);
						// dtdattr.getDTDElement().getDTDFile().setParseError(true);
						parseError = true;
					}
					setAttrDTDType(dtdattr, getFactory().getDTDBasicType_ID());
					break;

				case AttNode.IDREF :
					setAttrDTDType(dtdattr, getFactory().getDTDBasicType_IDREF());
					break;

				case AttNode.IDREFS :
					setAttrDTDType(dtdattr, getFactory().getDTDBasicType_IDREFS());
					break;

				case AttNode.ENUMERATION :
					setAttrDTDType(dtdattr, createDTDEnumeration(dtdattr, attdef, DTDEnumGroupKind.NAME_TOKEN_GROUP));
					break;

				case AttNode.NOTATION :
					setAttrDTDType(dtdattr, createDTDEnumeration(dtdattr, attdef, DTDEnumGroupKind.NOTATION_GROUP));
					break;

				case AttNode.NMTOKEN :
					setAttrDTDType(dtdattr, getFactory().getDTDBasicType_NMTOKEN());
					break;

				case AttNode.NMTOKENS :
					setAttrDTDType(dtdattr, getFactory().getDTDBasicType_NMTOKENS());
					break;

				default :
			// System.out.println("DTDATTR '" +attdef.name + "'Unknown
			// type..." + attrType);
			}
		}

		int attrDefault = attdef.getDefaultType();
		int defaultKind = DTDDefaultKind.IMPLIED;
		switch (attrDefault) {
			case AttNode.FIXED :
				defaultKind = DTDDefaultKind.FIXED;
				break;

			case AttNode.IMPLIED :
				defaultKind = DTDDefaultKind.IMPLIED;
				break;

			case AttNode.REQUIRED :
				defaultKind = DTDDefaultKind.REQUIRED;
				break;

			case AttNode.NOFIXED :
				defaultKind = DTDDefaultKind.NOFIXED;
				break;

			default :
		// System.out.println("DTDATTR '" +attdef.name + "' Unknown default
		// type... " + attrDefault);
		}

		DTDDefaultKind defaultKindObj = DTDDefaultKind.get(defaultKind);
		dtdattr.setDefaultKind(defaultKindObj);

		if (parseError) {
			return;
		}

		String defaultValue = attdef.defaultValue;
		if (defaultValue != null) {
			if (attrType == AttNode.ENUMERATION || attrType == AttNode.NOTATION) {
				if (!isDefaultEnumValueValid(attdef, defaultValue)) {
					/*String typeString = (attrType == AttNode.ENUMERATION ? "enumeration" : "notation"); //$NON-NLS-1$ //$NON-NLS-2$
					String errMsg = DTDCoreMessages._ERROR_INVALID_DEFAULT_ATTR_VALUE_1; //$NON-NLS-1$
					errMsg += typeString + DTDCoreMessages._UI_ERRORPART_INVALID_DEFAULT_ATTR_VALUE_2; //$NON-NLS-1$
					errMsg += attdef.name + "'"; //$NON-NLS-1$*/

					// dtdattr.getDTDElement().getIElement().setDTDErrorMessage(errMsg);
					// dtdattr.getDTDElement().getDTDFile().setParseError(true);
					return;
				}
			}
			dtdattr.setDefaultValueString(defaultValue);
		}
		// System.out.println("DTDAttr - toMof getDefaultValueString " +
		// getDefaultValueString());
		// System.out.println("DTDAttr - toMof getDefaultValue: " +
		// getDefaultValue());
	}

	public boolean hasIDAttribute(DTDAttribute dtdattr) {
		boolean hasID = false;

		DTDElement element = dtdattr.getDTDElement();
		EList attrs = element.getDTDAttribute();

		Iterator i = attrs.iterator();
		while (i.hasNext()) {
			DTDAttribute attr = (DTDAttribute) i.next();
			DTDType dType = attr.getDTDType();
			if (dType instanceof DTDBasicType) {
				if (((DTDBasicType) dType).getKind().getValue() == DTDBasicTypeKind.ID) {
					hasID = true;
					break;
				}
			}
		}
		return hasID;
	}

	public DTDEnumerationType createDTDEnumeration(DTDAttribute dtdattr, String[] enumValues, int enumKind) {
		DTDEnumerationType enumeration = getFactory().createDTDEnumerationType();
		DTDEnumGroupKind groupKind = DTDEnumGroupKind.get(enumKind);
		enumeration.setKind(groupKind);
		// Enumeration values
		if (enumValues != null) {
			for (int i = 0; i < enumValues.length; i++) {
				EEnumLiteral enumLiteral = createEEnumLiteral();
				// enumVal.setXMIDocument(dtdattr.getXMIDocument());
				// MOF2EMF Port
				// enumLiteral.refSetLiteral(enumValues[i]);
				enumLiteral.setName(enumValues[i]);

				// enumLiteral.setNumber(i);
				enumeration.getEnumLiterals().add(enumLiteral);
			}
		}
		dtdattr.getDTDElement().getDTDFile().getDTDEnumerationType().add(enumeration);
		return enumeration;
	}

	private EEnumLiteral createEEnumLiteral() {
		EcorePackage ePackage = (EcorePackage) EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
		// MOF2EMF Port
		// return ((EcoreFactory)ePackage.getFactory()).createEEnumLiteral();
		return ePackage.getEcoreFactory().createEEnumLiteral();
	}

	private DTDEnumerationType createDTDEnumeration(DTDAttribute dtdattr, AttNode attdef, int enumKind) {
		DTDEnumerationType enumeration = getFactory().createDTDEnumerationType();
		DTDEnumGroupKind groupKind = DTDEnumGroupKind.get(enumKind);
		enumeration.setKind(groupKind);
		dtdattr.getDTDElement().getDTDFile().getDTDEnumerationType().add(enumeration);

		// Enumeration values
		Enumeration tokenIter = attdef.elements();
		if (tokenIter != null) {
			while (tokenIter.hasMoreElements()) {
				String val = (String) tokenIter.nextElement();
				EEnumLiteral enumLiteral = createEEnumLiteral();
				// enumLiteral.setXMIDocument(dtdattr.getXMIDocument());
				// MOF2EMF Port
				// enumLiteral.refSetLiteral(val);
				enumLiteral.setName(val);


				// enumLiteral.setNumber(i++);
				enumeration.getEnumLiterals().add(enumLiteral);
			}
		}

		return enumeration;
	}

	private boolean isDefaultEnumValueValid(AttNode attdef, String defaultValue) {
		boolean valid = false;
		boolean containsPercent = false;

		// Enumeration values
		Enumeration enumValues = attdef.elements();
		while (enumValues.hasMoreElements()) {
			String val = (String) enumValues.nextElement();
			if (val.equals(defaultValue)) {
				valid = true;
				break;
			}
			if (val.indexOf('%') >= 0) {
				containsPercent = true;
			}
		}
		return valid || containsPercent;
	}

	public void setAttrDTDType(DTDAttribute dtdattr, DTDType type) {
		dtdattr.setDTDType(type);
	}

	protected void addErrorMessage(ErrorMessage errMsg, Object obj) {
		errMsg.setObject(obj);
		dtdUtil.addErrorMessage(errMsg);
	}

	// /**
	// * @generated
	// */
	// protected DTDFactoryImpl getFactoryGen() {
	//
	// return (DTDFactoryImpl)dtdUtil.getFactory();
	// }
	// /**
	// * @generated
	// */
	// protected DTDFile getDTDFileGen() {
	//
	// return dtdFile;
	// }
	// /**
	// * @generated
	// */
	// protected void visitDTDGen(DTD dtd) {
	//
	// super.visitDTD(dtd);
	//
	//
	// // - 2nd pass -
	// // do final processing for Attlists and adding contentModel
	// addAttlistAndContentModel(dtd,dtdFile);
	// }
	// /**
	// * @generated
	// */
	// protected void visitElementDeclGen(ElementDecl element) {
	//
	// // Element might have been added earlier because it is referenced
	// String declName = element.getNodeName() ;
	// // System.out.println("process ElementDecl:" + declName );
	// DTDElement dtdelement = (DTDElement)
	// dtdUtil.getElementPool().get(declName);
	// if (dtdelement == null)
	// {
	// // System.out.println("process ElementDecl - not found - create" );
	// dtdelement = getFactory().createDTDElement();
	// dtdelement.setName(declName);
	// dtdFile.getDTDObject().add(dtdelement);
	// dtdUtil.getElementPool().put(declName,dtdelement);
	// }
	//
	//
	// super.visitElementDecl(element);
	// }
	// /**
	// * @generated
	// */
	// protected void visitNotationDeclGen(NotationDecl notation) {
	//
	// // Notation might have been added earlier because it is referenced
	// // by an entity
	// DTDNotation dtdnot = dtdFile.findNotation( notation.getNodeName() );
	// if (dtdnot == null)
	// {
	// dtdnot = getFactory().createDTDNotation();
	// dtdFile.getDTDObject().add(dtdnot);
	// }
	// dtdnot.setName(notation.getNodeName());
	//
	//
	// if (notation.getSystemId()!= null )
	// dtdnot.setSystemID(notation.getSystemId());
	// if (notation.getPublicId()!= null )
	// dtdnot.setPublicID(notation.getPublicId());
	//
	//
	// if (notation.getComment()!=null)
	// dtdnot.setComment(notation.getComment());
	// if (notation.getErrorMessage()!=null)
	// {
	// addErrorMessage(notation.getErrorMessage(), dtdnot);
	// }
	//
	//
	// super.visitNotationDecl(notation);
	// }
	// /**
	// * @generated
	// */
	// protected void visitParameterEntityReferenceDeclGen(EntityDecl entity)
	// {
	//
	// // This is a parameter entity reference.
	// // Add the link to the real entity that it references
	// DTDParameterEntityReference parmEntRef =
	// getFactory().createDTDParameterEntityReference();
	// dtdFile.getDTDObject().add(parmEntRef);
	//
	//
	// String entityName = entity.getNodeName();
	//
	//
	// // Add the reference to the DTDEntity
	// DTDEntity dtdentity = getDTDFile().findEntity(entityName);
	// if (dtdentity != null)
	// {
	// parmEntRef.setEntity(dtdentity);
	// }
	// if (entity.getErrorMessage()!=null)
	// {
	// addErrorMessage(entity.getErrorMessage(), dtdentity);
	// }
	// // System.out.println("adding PE reference: " + declName);
	//
	//
	// super.visitParameterEntityReferenceDecl(entity);
	// }
	// /**
	// * @generated
	// */
	// protected void visitExternalEntityDeclGen(EntityDecl entity) {
	//
	// DTDEntity dtdEntity = createDTDEntity(entity);
	// // System.out.println("adding entity: " + declName);
	// DTDExternalEntity extEntity = getFactory().createDTDExternalEntity();
	// dtdEntity.setContent(extEntity);
	//
	//
	// finishExternalEntity(extEntity, entity);
	// // System.out.println(" ext entity toMof: " );
	// if (dtdEntity.isParameterEntity())
	// {
	//
	//
	// dtdUtil.getPEPool().put("%"+entity.getNodeName()+";", dtdEntity);
	// }
	//
	//
	// super.visitExternalEntityDecl(entity);
	// }
	// /**
	// * @generated
	// */
	// protected void visitInternalEntityDeclGen(EntityDecl entity) {
	//
	// DTDEntity dtdEntity = createDTDEntity(entity);
	//
	//
	// DTDInternalEntity intEntity = getFactory().createDTDInternalEntity();
	// dtdEntity.setContent(intEntity);
	// intEntity.setValue(entity.getValue());
	// // System.out.println(" int entity toMof: " );
	// if (dtdEntity.isParameterEntity())
	// {
	// dtdUtil.getPEPool().put("%"+entity.getNodeName()+";", dtdEntity);
	// }
	//
	//
	// super.visitInternalEntityDecl(entity);
	// }
	// /**
	// * @generated
	// */
	// protected void addAttlistAndContentModelGen(DTD dtd, DTDFile dFile) {
	//
	// //System.out.println("addAttListAndCotentModel - dtd:" +
	// dtd.getName());
	// Enumeration en = dtd.externalElements();
	// while (en.hasMoreElements())
	// {
	// Object e = en.nextElement();
	// if ( e instanceof ElementDecl )
	// {
	// DTDElement dtdelement = (DTDElement) dtdUtil.getElementPool().get(
	// ((ElementDecl)e).getNodeName() );
	// if (dtdelement == null)
	// {
	// dtdelement = getFactory().createDTDElement();
	// dtdFile.getDTDObject().add(dtdelement);
	// }
	// finishElementDecl(dtdelement, (ElementDecl) e);
	// }
	// else if ( e instanceof Attlist )
	// {
	// processAttList((Attlist) e);
	// }
	// }
	// }
	// /**
	// * @generated
	// */
	// protected void processAttListGen(Attlist attList) {
	//
	// DTDElement e = (DTDElement)
	// dtdUtil.getElementPool().get(attList.getNodeName());
	// if ( e != null )
	// {
	// // Element has been added. Add the attributes
	// //System.out.println(" processAttlist - adding Attlist:" +
	// attList.getNodeName());
	// createAttributes(e, attList);
	// }
	// else
	// {
	// //System.out.println("!!!!!!!!! element is null ");
	// }
	// }
	// /**
	// * @generated
	// */
	// protected void finishExternalEntityGen(DTDExternalEntity extEntity,
	// EntityDecl entityDecl) {
	//
	// updateSystemID(extEntity, entityDecl);
	// extEntity.setPublicID(entityDecl.getPublicId());
	//
	//
	// String notationName = entityDecl.getNotationName();
	// if (notationName != null)
	// {
	// DTDNotation dtdNotation = createOrFindNotation(extEntity,
	// notationName,true);
	// extEntity.setNotation(dtdNotation);
	// }
	// }
	// /**
	// * @generated
	// */
	// protected DTDNotation createOrFindNotationGen(DTDExternalEntity
	// extEntity, String name, boolean create) {
	//
	// DTDNotation aNotation =
	// extEntity.getDTDEntity().getDTDFile().findNotation(name);
	// if (aNotation != null)
	// {
	// return aNotation;
	// }
	//
	//
	// //
	// // Create a notation for the reference
	// //
	// if (create)
	// {
	// aNotation = getFactory().createDTDNotation();
	// dtdFile.getDTDObject().add(aNotation);
	// aNotation.setName(name);
	// }
	// return aNotation;
	// }
	// /**
	// * The SystemID attribute is set to whatever the user enters
	// * e.g. com/ibm/b2b/xmimodels/xxx.dtd.xmi
	// *
	// * In the unparse() method, parse out the DTD file name from
	// * the classpath name.
	// * e.g. returns xxx.dtd
	// */
	// protected void updateSystemIDGen(DTDExternalEntity extEntity,
	// EntityDecl entityDecl) {
	//
	// String systemId = entityDecl.getSystemId();
	// String publicId = entityDecl.getPublicId();
	// if (systemId != null)
	// {
	// IdResolver idResolver = new IdResolverImpl(dtd.getName());
	// String uri = idResolver.resolveId(publicId, systemId);
	// ExternalDTDModel ed = dtdUtil.getExternalDTDModel(resources, uri);
	// if (ed != null)
	// {
	// DTDFile referenceDtdFile = ed.getExternalDTDFile();
	// extEntity.setEntityReferencedFromAnotherFile(referenceDtdFile);
	// extEntity.setSystemID(systemId);
	// }
	// else
	// {
	// if (entityDecl.getErrorMessage() == null)
	// {
	// ErrorMessage dtdError = new ErrorMessage();
	// dtdError.setErrorMessage(DTDCoreMessages.getString("_ERROR_INCL_FILE_LOAD_FAILURE")
	// + " '" + systemId + "'");
	// addErrorMessage(dtdError, extEntity.getDTDEntity());
	// }
	//        
	// if (systemId != null)
	// {
	// extEntity.setSystemID(systemId);
	// }
	// else
	// {
	// extEntity.setSystemID("");
	// }
	// }
	// } // end of if ()
	// else
	// {
	// // set the system id to be ""
	// extEntity.setSystemID("");
	// }
	// }
	// /**
	// * @generated
	// */
	// protected DTDEntity createDTDEntityGen(EntityDecl entity) {
	//
	// // create and do what we can to fill in some basic things
	// DTDEntity dtdEntity = getFactory().createDTDEntity();
	// dtdFile.getDTDObject().add(dtdEntity);
	//
	//
	// dtdEntity.setName(entity.getNodeName());
	// dtdEntity.setParameterEntity(entity.isParameter());
	// if (entity.getComment()!=null)
	// {
	// dtdEntity.setComment(entity.getComment());
	// }
	// if (entity.getErrorMessage()!=null)
	// {
	// addErrorMessage(entity.getErrorMessage(), dtdEntity);
	// }
	//
	//
	// return dtdEntity;
	// }
	// /**
	// * @generated
	// */
	// protected void finishElementDeclGen(DTDElement dtdElement, ElementDecl
	// ed) {
	//
	// dtdElement.setName(ed.getNodeName());
	//
	//
	// CMNode cmNode = ed.getContentModelNode();
	//
	//
	// if (ed.getComment()!=null)
	// {
	// dtdElement.setComment(ed.getComment());
	// }
	// if (ed.getErrorMessage()!=null)
	// {
	// addErrorMessage(ed.getErrorMessage(), dtdElement);
	// }
	//
	//
	// if (cmNode instanceof CMBasicNode)
	// {
	// CMBasicNode bn = (CMBasicNode)cmNode;
	// switch (bn.getType())
	// {
	// case CMNodeType.EMPTY :
	// DTDEmptyContent emptyContent = getFactory().createDTDEmptyContent();
	// dtdElement.setContent(emptyContent);
	// break;
	//
	//
	// case CMNodeType.ANY :
	// DTDAnyContent anyContent = getFactory().createDTDAnyContent();
	// dtdElement.setContent(anyContent);
	// break;
	//
	//
	// case CMNodeType.PCDATA:
	// DTDPCDataContent pcData = getFactory().createDTDPCDataContent();
	// dtdElement.setContent(pcData);
	// }
	//
	//
	// }
	// else if (cmNode instanceof CMReferenceNode)
	// {
	// CMReferenceNode rn = (CMReferenceNode) cmNode;
	// if (rn.getType() == CMNodeType.ENTITY_REFERENCE)
	// {
	// String entityName = rn.getName().trim();
	//
	//
	// DTDEntity anEntity = (DTDEntity) dtdUtil.getPEPool().get(entityName);
	// if (anEntity!=null)
	// {
	// //
	// // Create an DTDEntityReference and set its referenced element
	// //
	// DTDEntityReferenceContent enRef =
	// getFactory().createDTDEntityReferenceContent();
	// enRef.setElementReferencedEntity(anEntity);
	//
	// DTDOccurrenceType occurrenceType =
	// DTDOccurrenceType.get(computeMofOccurrence(rn));
	// enRef.setOccurrence(occurrenceType);
	//
	// dtdElement.setContent(enRef);
	// }
	// else
	// { // create default content
	// DTDEmptyContent emptyContent = getFactory().createDTDEmptyContent();
	// dtdElement.setContent(emptyContent);
	// }
	// }
	// else
	// {
	// //
	// // Find the real element for this element references
	// // If the real element does not yet exist, create it
	// //
	// DTDElement anElement = createOrFindElement(rn.getName(), dtdElement);
	//
	//
	// //
	// // Create an DTDElementReference and set its referenced element
	// //
	// DTDElementReferenceContent elemRef =
	// getFactory().createDTDElementReferenceContent();
	// elemRef.setReferencedElement(anElement);
	//
	// DTDOccurrenceType occurrenceType =
	// DTDOccurrenceType.get(computeMofOccurrence(rn));
	// elemRef.setOccurrence(occurrenceType);
	//
	// // setContent to DTDElementReference
	// dtdElement.setContent(elemRef);
	//
	//
	// }
	// }
	// else if (cmNode instanceof CMGroupNode)
	// {
	// CMGroupNode grpNode = (CMGroupNode)cmNode;
	// DTDGroupContent groupContent = getFactory().createDTDGroupContent();
	// DTDGroupKind groupKind =
	// DTDGroupKind.get(computeMofGroupKind(grpNode.getGroupKind()));
	// groupContent.setGroupKind(groupKind);
	// DTDOccurrenceType occurrenceType =
	// DTDOccurrenceType.get(computeMofOccurrence(grpNode));
	// groupContent.setOccurrence(occurrenceType);
	//
	// // just use the locator for the element as the closest guess
	// processGroupContent(groupContent, grpNode);
	// dtdElement.setContent(groupContent);
	// }
	// else if (cmNode == null)
	// {
	// // bad thing happened here, just create a pcdata
	// DTDEmptyContent emptyContent = getFactory().createDTDEmptyContent();
	// dtdElement.setContent(emptyContent);
	// }
	// }
	// /**
	// * Add a new group to the current group
	// * @param parent - the parent node for this group element
	// * @param op1Node - the group
	// * e.g. (a,b)
	// * @param op2Node - set only if called by processCM1op
	// * e.g. (a,b)+
	// */
	// protected void processGroupContentGen(DTDGroupContent parent,
	// CMGroupNode grpNode) {
	//
	// Enumeration children = grpNode.getChildren().elements();
	// DTDFactory factory = getFactory();
	// while (children.hasMoreElements())
	// {
	// CMNode cnode = (CMNode) children.nextElement();
	//
	//
	// if (cnode instanceof CMGroupNode)
	// {
	// CMGroupNode gNode = (CMGroupNode)cnode;
	// DTDGroupContent groupContent = factory.createDTDGroupContent();
	// DTDGroupKind groupKind =
	// DTDGroupKind.get(computeMofGroupKind(gNode.getGroupKind()));
	// groupContent.setGroupKind(groupKind);
	// DTDOccurrenceType occurrenceType =
	// DTDOccurrenceType.get(computeMofOccurrence(gNode));
	// groupContent.setOccurrence(occurrenceType);
	//
	// parent.getContent().add(groupContent);
	// processGroupContent(groupContent,gNode);
	// }
	// else if (cnode instanceof CMBasicNode)
	// {
	// CMBasicNode n = (CMBasicNode)cnode;
	// if (n.getType() == CMNodeType.PCDATA)
	// {
	// // Create a DTDPCDataContent for a leaf PCData node
	// //
	// DTDPCDataContent pcData = factory.createDTDPCDataContent();
	// // Add #PCDATA to the Group, i.e Mixed content model
	// parent.getContent().add(pcData);
	// }
	// }
	// else if (cnode instanceof CMReferenceNode)
	// {
	// CMReferenceNode rn = (CMReferenceNode)cnode;
	// if (rn.getType()==CMNodeType.ELEMENT_REFERENCE)
	// {
	// // System.out.println("CM Element Ref name: " + rn.getName());
	// //
	// // Create an DTDElementReference and set its referenced element
	// //
	// DTDElementReferenceContent elemRef =
	// factory.createDTDElementReferenceContent();
	//
	//
	// //
	// // Find the real element for this element references
	// // If the real element does not yet exist, create it
	// //
	// DTDElement anElement = createOrFindElement(rn.getName(), elemRef);
	//
	//
	// elemRef.setReferencedElement(anElement);
	// DTDOccurrenceType occurrenceType =
	// DTDOccurrenceType.get(computeMofOccurrence(rn));
	// elemRef.setOccurrence(occurrenceType);
	//
	// // Add DTDElementReference to the Group
	// parent.getContent().add(elemRef);
	// }
	// else // PE Reference
	// {
	// String entityName = rn.getName().trim();
	//
	//
	// // System.out.println("CM PE Ref name: " + entityName);
	// DTDEntity anEntity = (DTDEntity) dtdUtil.getPEPool().get(entityName);
	// if (anEntity!=null)
	// {
	// //
	// // Create an DTDEntityReference and set its referenced element
	// //
	// DTDEntityReferenceContent enRef =
	// factory.createDTDEntityReferenceContent();
	// enRef.setElementReferencedEntity(anEntity);
	//             
	// DTDOccurrenceType occurrenceType =
	// DTDOccurrenceType.get(computeMofOccurrence(rn));
	// enRef.setOccurrence(occurrenceType);
	//
	//
	// // Add DTDEntityReference to the Group
	// parent.getContent().add(enRef);
	//
	//
	// }
	// }
	// }
	// }
	// }
	// /**
	// * Find the real element that is referenced by the current element
	// */
	// protected DTDElement createOrFindElementGen(String name, Object obj) {
	//
	// // DTDElement aElement = getDTDFile().findElement(name);
	//
	//
	// DTDElement aElement = (DTDElement) dtdUtil.getElementPool().get(name);
	//
	//
	// if (aElement != null)
	// {
	// return aElement;
	// }
	//
	//
	// String errorMsg =
	// DTDCoreMessages.getString("_ERROR_UNDECLARED_ELEMENT_1");
	// errorMsg += "\"" + name + "\"";
	// errorMsg +=
	// DTDCoreMessages.getString("_UI_ERRORPART_UNDECLARED_ELEMENT_2");
	//
	//
	// ErrorMessage dtdError = new ErrorMessage();
	//     
	// dtdError.setErrorMessage(errorMsg);
	// addErrorMessage(dtdError, obj);
	// // System.out.println(errorMsg);
	// // setDTDErrorMessage(errorMsg);
	// getDTDFile().setParseError(true);
	//
	//
	// //
	// // Create an empty element for the reference to make it valid
	// //
	//
	//
	// DTDFactory factory = getFactory();
	// DTDElement dtdelement = factory.createDTDElement();
	// dtdelement.setName(name);
	//
	//
	// DTDEmptyContent emptyContent = factory.createDTDEmptyContent();
	// dtdelement.setContent(emptyContent);
	//
	//
	// getDTDFile().getDTDObject().add(dtdelement);
	// dtdUtil.getElementPool().put(name,dtdelement);
	// return dtdelement;
	// }
	// /**
	// * Compute the MOF occurrence from the xml4j occurrence
	// */
	// protected int computeMofOccurrenceGen(CMRepeatableNode rnode) {
	//
	// int occurrence = rnode.getOccurrence();
	// int mofoccur = DTDOccurrenceType.ONE;
	//
	//
	// if (occurrence == CMNodeType.ZERO_OR_MORE)
	// {
	// mofoccur = DTDOccurrenceType.ZERO_OR_MORE;
	// }
	// else if (occurrence == CMNodeType.ONE_OR_MORE)
	// {
	// mofoccur = DTDOccurrenceType.ONE_OR_MORE;
	// }
	// else if (occurrence == CMNodeType.OPTIONAL)
	// {
	// mofoccur = DTDOccurrenceType.OPTIONAL;
	// }
	// return mofoccur;
	// }
	// /**
	// * Compute the MOF model group from the xml4j model group
	// */
	// protected int computeMofGroupKindGen(int type) {
	//
	// if (type == CMNodeType.GROUP_CHOICE)
	// {
	// return DTDGroupKind.CHOICE;
	// }
	// else
	// {
	// return DTDGroupKind.SEQUENCE;
	// }
	// }
	// /**
	// * @generated
	// */
	// protected void createAttributesGen(DTDElement element, Attlist attList)
	// {
	//
	// if (attList.getErrorMessage()!=null)
	// {
	// addErrorMessage(attList.getErrorMessage(), element);
	// }
	//
	//
	// for (int i=0;i<attList.size();i++)
	// {
	// AttNode ad = (AttNode) attList.elementAt(i);
	// // DTDAttributeElement dtdAtt= getDTDAttributeElement(a.getName());
	//
	//
	// // only add the AttDef if it is not added yet
	// // ignore the dup AttDef as documented in the XML 1.0 specs
	// //if( dtdAtt==null)
	// addAttribute(element, ad);
	// }
	// }
	// /**
	// * Create a DTDAttribute from the xml4j attribute
	// */
	// protected void addAttributeGen(DTDElement dtdelement, AttNode ad) {
	//
	// DTDAttribute dtdattr = getFactory().createDTDAttribute();
	// dtdelement.getDTDAttribute().add(dtdattr);
	// finishAttribute(dtdattr, ad);
	// }
	// /**
	// * @generated
	// */
	// protected void finishAttributeGen(DTDAttribute dtdattr, AttNode attdef)
	// {
	//
	// boolean parseError = false;
	//
	//
	// if (attdef.name.startsWith("%"))
	// {
	// String peName = attdef.name.trim();
	// DTDEntity en = (DTDEntity) dtdUtil.getPEPool().get(peName);
	// if (en!=null)
	// {
	// dtdattr.setAttributeNameReferencedEntity(en);
	// }
	// }
	//
	//
	// dtdattr.setName(attdef.name);
	//
	//
	// int attrType = attdef.getDeclaredType();
	//
	//
	// if (attrType==AttNode.PEREFERENCE && attdef.type!=null)
	// {
	// String peName = attdef.type.trim();
	// DTDEntity en = (DTDEntity) dtdUtil.getPEPool().get(peName);
	// if (en!=null)
	// {
	// dtdattr.setAttributeTypeReferencedEntity(en);
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_CDATA()); // hack,
	// so we can get back the default value
	// }
	// else //set default type
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_CDATA());
	//
	//
	// }
	// else
	// {
	// switch (attrType)
	// {
	// case AttNode.CDATA:
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_CDATA());
	// break;
	//
	//
	// case AttNode.ENTITIES:
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_ENTITIES());
	// break;
	//
	//
	// case AttNode.ENTITY:
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_ENTITY());
	// break;
	//
	//
	// case AttNode.ID:
	// // check for duplicate ID attribute
	// if (hasIDAttribute(dtdattr))
	// {
	// String errMsg = DTDCoreMessages.getString("_ERROR_DUP_ID_ATTRIBUTE_1");
	// errMsg += attdef.name +
	// DTDCoreMessages.getString("_UI_ERRORPART_DUP_ID_ATTRIBUTE_2");
	// // dtdattr.getDTDElement().getIElement().setDTDErrorMessage(errMsg);
	// // dtdattr.getDTDElement().getDTDFile().setParseError(true);
	// parseError = true;
	// }
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_ID());
	// break;
	//
	//
	// case AttNode.IDREF:
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_IDREF());
	// break;
	//
	//
	// case AttNode.IDREFS:
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_IDREFS());
	// break;
	//
	//
	// case AttNode.ENUMERATION:
	// setAttrDTDType(dtdattr, createDTDEnumeration(dtdattr, attdef,
	// DTDEnumGroupKind.NAME_TOKEN_GROUP));
	// break;
	//
	//
	// case AttNode.NOTATION:
	// setAttrDTDType(dtdattr, createDTDEnumeration(dtdattr, attdef,
	// DTDEnumGroupKind.NOTATION_GROUP));
	// break;
	//
	//
	// case AttNode.NMTOKEN:
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_NMTOKEN());
	// break;
	//
	//
	// case AttNode.NMTOKENS:
	// setAttrDTDType(dtdattr, getFactory().getDTDBasicType_NMTOKENS());
	// break;
	//
	//
	// default:
	// // System.out.println("DTDATTR '" +attdef.name + "'Unknown type..." +
	// attrType);
	// }
	// }
	//
	//
	// int attrDefault = attdef.getDefaultType();
	// int defaultKind = DTDDefaultKind.IMPLIED;
	// switch (attrDefault)
	// {
	// case AttNode.FIXED:
	// defaultKind = DTDDefaultKind.FIXED;
	// break;
	//
	//
	// case AttNode.IMPLIED:
	// defaultKind = DTDDefaultKind.IMPLIED;
	// break;
	//
	//
	// case AttNode.REQUIRED:
	// defaultKind = DTDDefaultKind.REQUIRED;
	// break;
	//
	//
	// case AttNode.NOFIXED:
	// defaultKind = DTDDefaultKind.NOFIXED;
	// break;
	//
	//
	// default:
	// // System.out.println("DTDATTR '" +attdef.name + "' Unknown default
	// type... " + attrDefault);
	// }
	//
	// DTDDefaultKind defaultKindObj = DTDDefaultKind.get(defaultKind);
	// dtdattr.setDefaultKind(defaultKindObj);
	//
	// if (parseError)
	// {
	// return;
	// }
	//
	//
	// String defaultValue = attdef.defaultValue;
	// if (defaultValue != null)
	// {
	// if (attrType == AttNode.ENUMERATION || attrType == AttNode.NOTATION)
	// {
	// if (! isDefaultEnumValueValid(attdef,defaultValue))
	// {
	// String typeString =
	// (attrType==AttNode.ENUMERATION?"enumeration":"notation");
	// String errMsg =
	// DTDCoreMessages.getString("_ERROR_INVALID_DEFAULT_ATTR_VALUE_1");
	// errMsg += typeString +
	// DTDCoreMessages.getString("_UI_ERRORPART_INVALID_DEFAULT_ATTR_VALUE_2");
	// errMsg += attdef.name + "'";
	//
	//
	// // dtdattr.getDTDElement().getIElement().setDTDErrorMessage(errMsg);
	// // dtdattr.getDTDElement().getDTDFile().setParseError(true);
	// return;
	// }
	// }
	// dtdattr.setDefaultValueString(defaultValue);
	// }
	// // System.out.println("DTDAttr - toMof getDefaultValueString " +
	// getDefaultValueString());
	// // System.out.println("DTDAttr - toMof getDefaultValue: " +
	// getDefaultValue());
	// }
	// /**
	// * @generated
	// */
	// protected boolean hasIDAttributeGen(DTDAttribute dtdattr) {
	//
	// boolean hasID = false;
	//
	//
	// DTDElement element = dtdattr.getDTDElement();
	// EList attrs = element.getDTDAttribute();
	//
	//
	// Iterator i = attrs.iterator();
	// while (i.hasNext())
	// {
	// DTDAttribute attr = (DTDAttribute) i.next();
	// DTDType dType = attr.getDTDType();
	// if (dType instanceof DTDBasicType)
	// {
	// if ( ((DTDBasicType)dType).getKind().getValue() == DTDBasicTypeKind.ID)
	// {
	// hasID = true;
	// break;
	// }
	// }
	// }
	// return hasID;
	// }
	// /**
	// * @generated
	// */
	// protected DTDEnumerationType createDTDEnumerationGen(DTDAttribute
	// dtdattr, String[] enumValues, int enumKind) {
	//
	// DTDEnumerationType enum = getFactory().createDTDEnumerationType();
	// // This is gross, but this is what was done before.
	// DTDAttributeImpl dtdattrimpl = (DTDAttributeImpl) dtdattr;
	// // enum.setID("Enum_" +
	// ((DTDElement)dtdattrimpl.getMOFDomain()).getName() + "_" +
	// dtdattr.getName());
	// DTDEnumGroupKind groupKind = DTDEnumGroupKind.get(enumKind);
	// enum.setKind(groupKind);
	// // Enumeration values
	// if (enumValues!=null)
	// {
	// for(int i=0;i<enumValues.length;i++)
	// {
	// EEnumLiteral enumLiteral = createEEnumLiteral();
	// // enumVal.setXMIDocument(dtdattr.getXMIDocument());
	// enumLiteral.refSetLiteral(enumValues[i]);
	// // enumLiteral.setNumber(i);
	// enum.getEnumLiterals().add(enumLiteral);
	// }
	// }
	// dtdattr.getDTDElement().getDTDFile().getDTDEnumerationType().add(enum);
	// return enum;
	// }
	// /**
	// * @generated
	// */
	// protected EEnumLiteral createEEnumLiteralGen() {
	//
	// EcorePackage ePackage =
	// (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
	// return ((EcoreFactory)ePackage.getFactory()).createEEnumLiteral();
	// }
	// /**
	// * @generated
	// */
	// protected DTDEnumerationType createDTDEnumerationGen(DTDAttribute
	// dtdattr, AttNode attdef, int enumKind) {
	//
	// DTDEnumerationType enum = getFactory().createDTDEnumerationType();
	// // This is gross, but this is what was done before.
	// DTDAttributeImpl dtdattrimpl = (DTDAttributeImpl) dtdattr;
	// // enum.setID("Enum_" +
	// ((DTDElement)dtdattrimpl.getMOFDomain()).getName() + "_" +
	// dtdattr.getName());
	// DTDEnumGroupKind groupKind = DTDEnumGroupKind.get(enumKind);
	// enum.setKind(groupKind);
	// dtdattr.getDTDElement().getDTDFile().getDTDEnumerationType().add(enum);
	//
	//
	// // Enumeration values
	// Enumeration tokenIter = attdef.elements();
	// if (tokenIter != null) {
	// int i=0;
	// while (tokenIter.hasMoreElements())
	// {
	// String val = (String)tokenIter.nextElement();
	// EEnumLiteral enumLiteral = createEEnumLiteral();
	// // enumLiteral.setXMIDocument(dtdattr.getXMIDocument());
	//        
	//
	// enumLiteral.refSetLiteral(val);
	// // enumLiteral.setNumber(i++);
	// enum.getEnumLiterals().add(enumLiteral);
	// }
	// }
	//
	//
	// return enum;
	// }
	// /**
	// * @generated
	// */
	// protected boolean isDefaultEnumValueValidGen(AttNode attdef, String
	// defaultValue) {
	//
	// boolean valid = false;
	// boolean containsPercent = false;
	//
	//
	// // Enumeration values
	// Enumeration enumValues = attdef.elements();
	// while (enumValues.hasMoreElements())
	// {
	// String val = (String)enumValues.nextElement();
	// if (val.equals(defaultValue))
	// {
	// valid = true;
	// break;
	// }
	// if (val.indexOf('%') >= 0)
	// {
	// containsPercent = true;
	// }
	// }
	// return valid || containsPercent;
	// }
	// /**
	// * @generated
	// */
	// protected void setAttrDTDTypeGen(DTDAttribute dtdattr, DTDType type) {
	//
	// dtdattr.setDTDType(type);
	// }
	// /**
	// * @generated
	// */
	// protected void addErrorMessageGen(ErrorMessage errMsg, Object obj) {
	//
	// errMsg.setObject(obj);
	// dtdUtil.addErrorMessage(errMsg);
	// }
}
