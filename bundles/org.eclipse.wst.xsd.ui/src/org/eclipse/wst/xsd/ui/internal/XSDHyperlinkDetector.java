/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * Detects hyperlinks for XSD files
 */
public class XSDHyperlinkDetector implements IHyperlinkDetector {
	/**
	 * Gets the xsd schema from document
	 * 
	 * @param document
	 * @return XSDSchema or null of one does not exist yet for document
	 */
	private XSDSchema getXSDSchema(IDocument document) {
		XSDSchema schema = null;
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
		if (model != null) {
			try {
				if (model instanceof IDOMModel) {
					IDOMDocument domDoc = ((IDOMModel) model).getDocument();
					if (domDoc != null) {
						XSDModelAdapter modelAdapter = (XSDModelAdapter) domDoc.getExistingAdapter(XSDModelAdapter.class);
						/*
						 * ISSUE: Didn't want to go through initializing
						 * schema if it does not already exist, so just
						 * attempted to get existing adapter. If doesn't
						 * exist, just don't bother working.
						 */
						if (modelAdapter != null)
							schema = modelAdapter.getSchema();
					}
				}
			}
			finally {
				model.releaseFromRead();
			}
		}
		return schema;
	}

	/**
	 * 
	 * @param xsdSchema
	 *            cannot be null
	 * @param node
	 *            cannot be null
	 * @return XSDConcreteComponent
	 */
	private XSDConcreteComponent getXSDComponent(XSDSchema xsdSchema, Node node) {
		XSDConcreteComponent objectToReveal = null;

		XSDConcreteComponent xsdComp = xsdSchema.getCorrespondingComponent((Node) node);
		if (xsdComp instanceof XSDElementDeclaration) {
			XSDElementDeclaration elementDecl = (XSDElementDeclaration) xsdComp;
			if (elementDecl.isElementDeclarationReference()) {
				objectToReveal = elementDecl.getResolvedElementDeclaration();
			}
			else {
				XSDConcreteComponent typeDef = null;
				if (elementDecl.getAnonymousTypeDefinition() == null) {
					typeDef = elementDecl.getTypeDefinition();
				}

				XSDConcreteComponent subGroupAffiliation = elementDecl.getSubstitutionGroupAffiliation();

				if (typeDef != null && subGroupAffiliation != null) {
					// we have 2 things we can navigate to, if the
					// cursor is anywhere on the substitution
					// attribute
					// then jump to that, otherwise just go to the
					// typeDef.
					if (node instanceof Attr && ((Attr) node).getLocalName().equals(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE)) {
						objectToReveal = subGroupAffiliation;
					}
					else {
						// try to reveal the type now. On success,
						// then we return true.
						// if we fail, set the substitution group
						// as
						// the object to reveal as a backup plan.
						// ISSUE: how to set backup?
						// if (revealObject(typeDef)) {
						objectToReveal = typeDef;
						// }
						// else {
						// objectToReveal = subGroupAffiliation;
						// }
					}
				}
				else {
					// one or more of these is null. If the
					// typeDef is
					// non-null, use it. Otherwise
					// try and use the substitution group
					objectToReveal = typeDef != null ? typeDef : subGroupAffiliation;
				}
			}
		}
		else if (xsdComp instanceof XSDModelGroupDefinition) {
			XSDModelGroupDefinition elementDecl = (XSDModelGroupDefinition) xsdComp;
			if (elementDecl.isModelGroupDefinitionReference()) {
				objectToReveal = elementDecl.getResolvedModelGroupDefinition();
			}
		}
		else if (xsdComp instanceof XSDAttributeDeclaration) {
			XSDAttributeDeclaration attrDecl = (XSDAttributeDeclaration) xsdComp;
			if (attrDecl.isAttributeDeclarationReference()) {
				objectToReveal = attrDecl.getResolvedAttributeDeclaration();
			}
			else if (attrDecl.getAnonymousTypeDefinition() == null) {
				objectToReveal = attrDecl.getTypeDefinition();
			}
		}
		else if (xsdComp instanceof XSDAttributeGroupDefinition) {
			XSDAttributeGroupDefinition attrGroupDef = (XSDAttributeGroupDefinition) xsdComp;
			if (attrGroupDef.isAttributeGroupDefinitionReference()) {
				objectToReveal = attrGroupDef.getResolvedAttributeGroupDefinition();
			}
		}
		else if (xsdComp instanceof XSDIdentityConstraintDefinition) {
			XSDIdentityConstraintDefinition idConstraintDef = (XSDIdentityConstraintDefinition) xsdComp;
			if (idConstraintDef.getReferencedKey() != null) {
				objectToReveal = idConstraintDef.getReferencedKey();
			}
		}
		else if (xsdComp instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition typeDef = (XSDSimpleTypeDefinition) xsdComp;
			objectToReveal = typeDef.getItemTypeDefinition();
			if (objectToReveal == null) {
				// if itemType attribute is not set, then check
				// for memberType
				List memberTypes = typeDef.getMemberTypeDefinitions();
				if (memberTypes != null && memberTypes.size() > 0) {
					objectToReveal = (XSDConcreteComponent) memberTypes.get(0);
				}
			}
		}
		else if (xsdComp instanceof XSDTypeDefinition) {
			XSDTypeDefinition typeDef = (XSDTypeDefinition) xsdComp;
			objectToReveal = typeDef.getBaseType();
		}
		else if (xsdComp instanceof XSDSchemaDirective) {
			XSDSchemaDirective directive = (XSDSchemaDirective) xsdComp;
			// String schemaLocation =
			// URIHelper.removePlatformResourceProtocol(directive.getResolvedSchema().getSchemaLocation());
			// openXSDEditor(schemaLocation);
			// return false;
			objectToReveal = directive.getResolvedSchema();
		}
		return objectToReveal;
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		// for now, only capable of creating 1 hyperlink
		List hyperlinks = new ArrayList(0);

		if (region != null && textViewer != null) {
			IDocument document = textViewer.getDocument();
			Node node = getCurrentNode(document, region.getOffset());
			if (node != null) {
				XSDSchema xsdSchema = getXSDSchema(textViewer.getDocument());
				if (xsdSchema != null) {
					XSDConcreteComponent objectToReveal = getXSDComponent(xsdSchema, node);
					// now reveal the object if this isn't null
					if (objectToReveal != null) {
						IRegion nodeRegion = region;
						if (node instanceof IndexedRegion) {
							IndexedRegion indexed = (IndexedRegion) node;
							int start = indexed.getStartOffset();
							int end = indexed.getEndOffset();
							nodeRegion = new Region(start, end - start);
						}
						hyperlinks.add(new XSDHyperlink(nodeRegion, objectToReveal));
					}
				}
			}
		}

		if (hyperlinks.size() == 0)
			return null;
		return (IHyperlink[]) hyperlinks.toArray(new IHyperlink[0]);
	}

	/**
	 * Returns the node the cursor is currently on in the document. null if no
	 * node is selected
	 * 
	 * @param offset
	 * @return Node either element, doctype, text, or null
	 */
	private Node getCurrentNode(IDocument document, int offset) {
		// get the current node at the offset (returns either: element,
		// doctype, text)
		IndexedRegion inode = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			inode = sModel.getIndexedRegion(offset);
			if (inode == null)
				inode = sModel.getIndexedRegion(offset - 1);
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}

		if (inode instanceof Node) {
			return (Node) inode;
		}
		return null;
	}
}
