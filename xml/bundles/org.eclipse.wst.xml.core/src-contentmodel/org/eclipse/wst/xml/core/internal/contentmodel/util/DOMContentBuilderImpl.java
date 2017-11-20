/*******************************************************************************
 * Copyright (c) 2002, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.util.CMDataTypeValueHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


/**
 * todo... common up this code with 'ContentBuilder'
 */
public class DOMContentBuilderImpl extends CMVisitor implements DOMContentBuilder {
	protected int buildPolicy = BUILD_ALL_CONTENT;
	protected Hashtable propertyTable = new Hashtable();

	protected boolean alwaysVisit = false;
	protected List resultList;
	protected Document document;
	protected Node currentParent;
	protected Node topParent;
	protected Vector visitedCMElementDeclarationList = new Vector();
	protected boolean attachNodesToParent = true;
	protected NamespaceTable namespaceTable;

	protected List namespaceInfoList;
	protected Element rootElement; // this is used only teporarily via
									// createDefaultRootContent
	protected ExternalCMDocumentSupport externalCMDocumentSupport;

	public boolean supressCreationOfDoctypeAndXMLDeclaration;

	protected CMDataTypeValueHelper valueHelper = new CMDataTypeValueHelper();

	protected int numOfRepeatableElements = 1;
	protected Stack cmGroupStack = new Stack();
	protected int depthLimit = -1;

	protected int domLevel;
	private int originalBuildPolicy;
	
	public interface ExternalCMDocumentSupport {
		public CMDocument getCMDocument(Element element, String uri);
	}

	public void setExternalCMDocumentSupport(ExternalCMDocumentSupport externalCMDocumentSupport) {
		this.externalCMDocumentSupport = externalCMDocumentSupport;
	}

	public DOMContentBuilderImpl(Document document) {
		this.document = document;
		namespaceTable = new NamespaceTable(document);
	}

	public void setBuildPolicy(int buildPolicy) {
		this.buildPolicy = buildPolicy;
	}

	public int getBuildPolicy() {
		return buildPolicy;
	}

	protected boolean buildAllContent(int policy) {
		return (policy & BUILD_ALL_CONTENT) == BUILD_ALL_CONTENT;
	}

	protected boolean buildOptionalElements(int policy) {
		return (policy & BUILD_OPTIONAL_ELEMENTS) == BUILD_OPTIONAL_ELEMENTS;
	}

	protected boolean buildOptionalAttributes(int policy) {
		return (policy & BUILD_OPTIONAL_ATTRIBUTES) == BUILD_OPTIONAL_ATTRIBUTES;
	}

	protected boolean buildFirstChoice(int policy) {
		return (policy & BUILD_FIRST_CHOICE) == BUILD_FIRST_CHOICE;
	}

	protected boolean buildTextNodes(int policy) {
		return (policy & BUILD_TEXT_NODES) == BUILD_TEXT_NODES;
	}

	protected boolean buildFirstSubstitution(int policy) {
		return (policy & BUILD_FIRST_SUBSTITUTION) == BUILD_FIRST_SUBSTITUTION;
	}

	public List getResult() {
		return resultList;
	}

	public void setProperty(String propertyName, Object value) {
		propertyTable.put(propertyName, value);
	}

	public Object getProperty(String propertyName) {
		return propertyTable.get(propertyName);
	}

	public void build(Node parent, CMNode child) {
		resultList = new Vector();
		topParent = parent;
		currentParent = parent;
		if (parent instanceof Element) {
			namespaceTable.addElementLineage((Element) parent);
		}
		attachNodesToParent = false;
		alwaysVisit = true;
		visitCMNode(child);
	}

	public void createDefaultRootContent(CMDocument cmDocument, CMElementDeclaration rootCMElementDeclaration, List namespaceInfoList) throws Exception {
		this.namespaceInfoList = namespaceInfoList;
		createDefaultRootContent(cmDocument, rootCMElementDeclaration);
	}

	public void createDefaultRootContent(CMDocument cmDocument, CMElementDeclaration rootCMElementDeclaration) throws Exception {
		String grammarFileName = cmDocument.getNodeName();
		if (!supressCreationOfDoctypeAndXMLDeclaration) {
			// TODO cs... investigate to see if this code path is ever used,
			// doesn't seem to be
			// for now I'm setting the encoding to UTF-8 just incase this code
			// path is used somewhere
			//
			String piValue = "version=\"1.0\""; //$NON-NLS-1$
			String encoding = "UTF-8"; //$NON-NLS-1$
			piValue += " encoding=\"" + encoding + "\""; //$NON-NLS-1$ //$NON-NLS-2$      
			ProcessingInstruction pi = document.createProcessingInstruction("xml", piValue); //$NON-NLS-1$
			document.appendChild(pi);

			// if we have a 'dtd' then add a DOCTYPE tag
			//
			if (grammarFileName != null && grammarFileName.endsWith("dtd")) //$NON-NLS-1$
			{
				DOMImplementation domImpl = document.getImplementation();
				DocumentType documentType = domImpl.createDocumentType(rootCMElementDeclaration.getElementName(), grammarFileName, grammarFileName);
				document.appendChild(documentType);
			}
		}

		// if we have a schema add an xsi:schemaLocation attribute
		//
		if (grammarFileName != null && grammarFileName.endsWith("xsd") && namespaceInfoList != null) //$NON-NLS-1$
		{
			DOMNamespaceInfoManager manager = new DOMNamespaceInfoManager();
			String name = rootCMElementDeclaration.getNodeName();
			if (namespaceInfoList.size() > 0) {
				NamespaceInfo info = (NamespaceInfo) namespaceInfoList.get(0);
				if (info.prefix != null && info.prefix.length() > 0) {
					name = info.prefix + ":" + name; //$NON-NLS-1$
				}
			}
			rootElement = createElement(rootCMElementDeclaration, name, document);
			manager.addNamespaceInfo(rootElement, namespaceInfoList, true);
		}
		createDefaultContent(document, rootCMElementDeclaration);
	}

	public void createDefaultContent(Node parent, CMElementDeclaration ed) {
		currentParent = parent;
		alwaysVisit = true;
		originalBuildPolicy = buildPolicy;
		visitCMElementDeclaration(ed);
	}

	public String computeName(CMNode cmNode, Node parent) {
		String prefix = null;
		return DOMNamespaceHelper.computeName(cmNode, parent, prefix, namespaceTable);
	}

	// overide the following 'create' methods to control how nodes are created
	//
	protected Element createElement(CMElementDeclaration ed, String name, Node parent) {
		return document.createElement(name);
	}

	protected Attr createAttribute(CMAttributeDeclaration ad, String name, Node parent) {
		return document.createAttribute(name);
	}

	protected Text createTextNode(CMDataType dataType, String value, Node parent) {
		return document.createTextNode(value);
	}

	protected void handlePushParent(Element parent, CMElementDeclaration ed) {
	  domLevel++;
	}

	protected void handlePopParent(Element element, CMElementDeclaration ed) {
      domLevel--;
	}

	// The range must be between 1 and 99.
	public void setNumOfRepeatableElements(int i) {
		numOfRepeatableElements = i;
	}

	protected int getNumOfRepeatableElements() {
		return numOfRepeatableElements;
	}

	public void visitCMElementDeclaration(CMElementDeclaration ed) {
		int forcedMin = (buildOptionalElements(buildPolicy) || alwaysVisit) ? 1 : 0;
		int min = Math.max(ed.getMinOccur(), forcedMin);

		// Correct the min value if the element is contained in
		// a group.
		if (!cmGroupStack.isEmpty()) {
			CMGroup group = (CMGroup) cmGroupStack.peek();
			int gmin = group.getMinOccur();
			if (gmin == 0)
				if (buildOptionalElements(buildPolicy)) { 
					/* do nothing: min = min */
				}
				else {
					min = min * gmin; // min = 0
				}
			else {
				min = min * gmin;
			}
		}

		int max = Math.min(ed.getMaxOccur(), getNumOfRepeatableElements());
		if (max < min)
			max = min;

		alwaysVisit = false;

		// Note - ed may not be abstract but has substitutionGroups
		// involved.
		if (buildFirstSubstitution(buildPolicy) || isAbstract(ed)) // leave
																	// this
																	// for
																	// backward
																	// compatibility
																	// for now
		{
			// Note - To change so that if ed is optional, we do not
			// generate anything here.
			ed = getSubstitution(ed);

			// Note - the returned ed may be an abstract element in
			// which case the xml will be invalid.
		}

		if (min > 0 && !visitedCMElementDeclarationList.contains(ed)) {
			visitedCMElementDeclarationList.add(ed);
			for (int i = 1; i <= max; i++) {
				// create an Element for each
				Element element = null;
				if (rootElement != null) {
					element = rootElement;
					rootElement = null;
				}
				else {
					element = createElement(ed, computeName(ed, currentParent), currentParent);
				}

				// visit the children of the GrammarElement
				Node oldParent = currentParent;
				currentParent = element;
				handlePushParent(element, ed);

				namespaceTable.addElement(element);

				boolean oldAttachNodesToParent = attachNodesToParent;
				attachNodesToParent = true;

				// instead of calling super.visitCMElementDeclaration()
				// we duplicate the code with some minor modifications
				CMNamedNodeMap nodeMap = ed.getAttributes();
				int size = nodeMap.getLength();
				for (int j = 0; j < size; j++) {
					visitCMNode(nodeMap.item(j));
				}

				CMContent content = ed.getContent();
				if (content != null) {
					visitCMNode(content);
				}

				if (ed.getContentType() == CMElementDeclaration.PCDATA) {
					CMDataType dataType = ed.getDataType();
					if (dataType != null) {
						visitCMDataType(dataType);
					}
				}
				// end duplication
				attachNodesToParent = oldAttachNodesToParent;
				handlePopParent(element, ed);
				currentParent = oldParent;
				linkNode(element);
			}
			int size = visitedCMElementDeclarationList.size();
			visitedCMElementDeclarationList.remove(size - 1);
		}
	}


	public void visitCMDataType(CMDataType dataType) {
		Text text = null;
		String value = null;

		// For backward compatibility:
		// Previous code uses a property value but new one uses
		// buildPolicy.
		if (getProperty(PROPERTY_BUILD_BLANK_TEXT_NODES) != null && getProperty(PROPERTY_BUILD_BLANK_TEXT_NODES).equals("true")) //$NON-NLS-1$
			buildPolicy = buildPolicy ^ BUILD_TEXT_NODES;

		if (buildTextNodes(buildPolicy)) {
			value = valueHelper.getValue(dataType);
			if (value == null) {
				if (currentParent != null && currentParent.getNodeType() == Node.ELEMENT_NODE) {
					value = currentParent.getNodeName();
				}
				else {
					value = "pcdata"; //$NON-NLS-1$
				}
			}
		}
		else {
			value = ""; //$NON-NLS-1$
		}
		text = createTextNode(dataType, value, currentParent);
		linkNode(text);
	}

	public void visitCMNode(CMNode node) {
		if (depthLimit != -1) {
			if (domLevel > depthLimit) {
				buildPolicy = buildPolicy &= ~BUILD_OPTIONAL_ELEMENTS;
			} else {
				buildPolicy = originalBuildPolicy;
			}
		}
		super.visitCMNode(node);
	}

	public void visitCMGroup(CMGroup e) {
		cmGroupStack.push(e);

		int forcedMin = (buildOptionalElements(buildPolicy) || alwaysVisit) ? 1 : 0;
		int min = Math.max(e.getMinOccur(), forcedMin);

		int max = 0;
		if (e.getMaxOccur() == -1) // unbounded
			max = getNumOfRepeatableElements();
		else
			max = Math.min(e.getMaxOccur(), getNumOfRepeatableElements());

		if (max < min)
			max = min;

		alwaysVisit = false;

		for (int i = 1; i <= max; i++) {
			if (e.getOperator() == CMGroup.CHOICE && buildFirstChoice(buildPolicy)) {
				CMNode hintNode = null;

				// todo... the CMGroup should specify the hint... but it seems
				// as though
				// the Yamato guys are making the CMElement specify the hint.
				// I do it that way for now until... we should fix this post
				// GA
				//    
				int listSize = visitedCMElementDeclarationList.size();
				if (listSize > 0) {
					CMElementDeclaration ed = (CMElementDeclaration) visitedCMElementDeclarationList.get(listSize - 1);
					Object contentHint = ed.getProperty("contentHint"); //$NON-NLS-1$
					if (contentHint instanceof CMNode) {
						hintNode = (CMNode) contentHint;
					}
				}

				// see if this hint corresponds to a valid choice
				//
				CMNode cmNode = null;

				if (hintNode != null) {
					CMNodeList nodeList = e.getChildNodes();
					int nodeListLength = nodeList.getLength();
					for (int j = 0; j < nodeListLength; j++) {
						if (hintNode == nodeList.item(j)) {
							cmNode = hintNode;
						}
					}
				}

				// if no cmNode has been determined from the hint, just use
				// the first choice
				//
				if (cmNode == null) {
					CMNodeList nodeList = e.getChildNodes();
					if (nodeList.getLength() > 0) {
						cmNode = nodeList.item(0);
					}
				}

				if (cmNode != null) {
					// Bug 330260
					// Problem - Add child element also adds optional grand-child elements
					// This assumes 'e' is a model group choice, case 1. However 'e' could be a model group definition, case 2, where the
					// first child is a model group. In the first case (choice), the first child is an
					// element. Upon visiting the element (visitCMElementDeclaration), the minOccurs of the
					// choice is ALSO considered. If its minOccurs is 0, then the first element is not added as a child.
					// However, in the second case (model group definition), the first child is a choice, but the multiplicity is [1,1],
					// meaning, it is required. So the first element is then added as child, even though
					// the model group definition reference is optional. (minOccurs is not checked in this method, visitCMGroup)
					// Visit the node only if it is not a GROUP (model group). If it is an element, then visit it.
					if (!(cmNode.getNodeType() == CMNode.GROUP && min > 0)) {
						visitCMNode(cmNode);
					}
				}
			}
			else if (e.getOperator() == CMGroup.ALL // ALL
						|| e.getOperator() == CMGroup.SEQUENCE) // SEQUENCE
			{
				// visit all of the content
				super.visitCMGroup(e);
			}
		}

		cmGroupStack.pop();
	}

	static int count = 0;

	public void visitCMAttributeDeclaration(CMAttributeDeclaration ad) {
		if (alwaysVisit || buildOptionalAttributes(buildPolicy) || ad.getUsage() == CMAttributeDeclaration.REQUIRED) {
			alwaysVisit = false;
			String name = computeName(ad, currentParent);
			String value = valueHelper.getValue(ad, namespaceTable);
			Attr attr = createAttribute(ad, name, currentParent);
			attr.setValue(value != null ? value : ""); //$NON-NLS-1$
			linkNode(attr);
		}
	}

	protected boolean isAbstract(CMNode ed) {
		boolean result = false;
		if (ed != null) {
			Object value = ed.getProperty("Abstract"); //$NON-NLS-1$
			result = (value == Boolean.TRUE);
		}
		return result;
	}

	protected CMElementDeclaration getSubstitution(CMElementDeclaration ed) {
		CMElementDeclaration result = ed;
		CMNodeList l = (CMNodeList) ed.getProperty("SubstitutionGroup"); //$NON-NLS-1$
		if (l != null) {
			for (int i = 0; i < l.getLength(); i++) {
				CMNode candidate = l.item(i);
				if (!isAbstract(candidate) && (candidate instanceof CMElementDeclaration)) {
					result = (CMElementDeclaration) candidate;
					break;
				}
			}
		}
		return result;
	}

	protected CMElementDeclaration getParentCMElementDeclaration() {
		CMElementDeclaration ed = null;
		int listSize = visitedCMElementDeclarationList.size();
		if (listSize > 0) {
			ed = (CMElementDeclaration) visitedCMElementDeclarationList.get(listSize - 1);
		}
		return ed;
	}

	public void visitCMAnyElement(CMAnyElement anyElement) {
		// ingnore buildPolicy for ANY elements... only create elements if
		// absolutely needed
		//
		int forcedMin = alwaysVisit ? 1 : 0;
		int min = Math.max(anyElement.getMinOccur(), forcedMin);
		alwaysVisit = false;

		String uri = anyElement.getNamespaceURI();
		String targetNSProperty = "http://org.eclipse.wst/cm/properties/targetNamespaceURI"; //$NON-NLS-1$
		CMDocument parentCMDocument = (CMDocument) anyElement.getProperty("CMDocument"); //$NON-NLS-1$
		CMElementDeclaration ed = null;

		// System.out.println("parentCMDocument = " + parentCMDocument);
		// //$NON-NLS-1$
		if (parentCMDocument != null) {
			if (uri == null || uri.startsWith("##") || uri.equals(parentCMDocument.getProperty(targetNSProperty))) //$NON-NLS-1$
			{
				ed = getSuitableElement(getParentCMElementDeclaration(), parentCMDocument);
			}
		}


		if (ed == null && externalCMDocumentSupport != null && uri != null && !uri.startsWith("##") && currentParent instanceof Element) //$NON-NLS-1$
		{
			CMDocument externalCMDocument = externalCMDocumentSupport.getCMDocument((Element) currentParent, uri);
			if (externalCMDocument != null) {
				ed = getSuitableElement(null, externalCMDocument);
			}
		}

		for (int i = 1; i <= min; i++) {
			if (ed != null) {
				visitCMElementDeclaration(ed);
			}
			else {
				Element element = document.createElement("ANY-ELEMENT"); //$NON-NLS-1$
				linkNode(element);
			}
		}
	}

	protected CMElementDeclaration getSuitableElement(CMNamedNodeMap nameNodeMap) {
		CMElementDeclaration result = null;
		int size = nameNodeMap.getLength();
		for (int i = 0; i < size; i++) {
			CMElementDeclaration candidate = (CMElementDeclaration) nameNodeMap.item(i);
			if (!visitedCMElementDeclarationList.contains(candidate)) {
				result = candidate;
				break;
			}
		}
		return result;
	}

	protected CMElementDeclaration getSuitableElement(CMElementDeclaration ed, CMDocument cmDocument) {
		CMElementDeclaration result = null;

		if (ed != null) {
			result = getSuitableElement(ed.getLocalElements());
		}

		if (result == null && cmDocument != null) {
			result = getSuitableElement(cmDocument.getElements());
		}

		return result;
	}


	public void linkNode(Node node) {
		if (attachNodesToParent && currentParent != null) {
			if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				((Element) currentParent).setAttributeNode((Attr) node);
			}
			else {
				currentParent.appendChild(node);
			}
		}
		else if (resultList != null) {
			resultList.add(node);
		}
	}

	public static void testPopulateDocumentFromGrammarFile(Document document, String grammarFileName, String rootElementName, boolean hack) {
		try {
			CMDocument cmDocument = ContentModelManager.getInstance().createCMDocument(grammarFileName, null);
			CMNamedNodeMap elementMap = cmDocument.getElements();
			CMElementDeclaration element = (CMElementDeclaration) elementMap.getNamedItem(rootElementName);

			DOMContentBuilderImpl contentBuilder = new DOMContentBuilderImpl(document);
			contentBuilder.supressCreationOfDoctypeAndXMLDeclaration = hack;
			contentBuilder.createDefaultRootContent(cmDocument, element);

			System.out.println();
			System.out.println("-----------------------------"); //$NON-NLS-1$
			DOMWriter writer = new DOMWriter();
			if (hack) {
				writer.print(document, grammarFileName);
			}
			else {
				writer.print(document);
			}
			System.out.println("-----------------------------"); //$NON-NLS-1$
		}
		catch (Exception e) {
			System.out.println("Error: " + e); //$NON-NLS-1$
			e.printStackTrace();
		}
	}
	
	public void setOptionalElementDepthLimit(int depth) {
		depthLimit = depth;
	}
	

	// test
	//
	/*
	 * public static void main(String arg[]) { if (arg.length >= 2) { try {
	 * CMDocumentFactoryRegistry.getInstance().registerCMDocumentBuilderWithClassName("org.eclipse.wst.xml.core.internal.contentmodel.mofimpl.CMDocumentBuilderImpl");
	 * 
	 * String grammarFileName = arg[0]; String rootElementName = arg[1];
	 * 
	 * Document document =
	 * (Document)Class.forName("org.apache.xerces.dom.DocumentImpl").newInstance();
	 * testPopulateDocumentFromGrammarFile(document, grammarFileName,
	 * rootElementName, true); } catch (Exception e) {
	 * System.out.println("DOMContentBuilderImpl error"); e.printStackTrace(); } }
	 * else { System.out.println("Usage : java
	 * org.eclipse.wst.xml.util.DOMContentBuildingCMVisitor grammarFileName
	 * rootElementName"); } }
	 */
}
