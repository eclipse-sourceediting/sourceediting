/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.document;



import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public interface XMLGenerator {

	/**
	 */
	String generateAttrName(Attr attr);

	/**
	 */
	String generateAttrValue(Attr attr);

	/**
	 */
	String generateAttrValue(Attr attr, char quote);

	/**
	 */
	String generateAttrValue(String value, char quote);

	/**
	 * generateCDATASection method
	 * 
	 * @return java.lang.String
	 * @param comment
	 *            org.w3c.dom.CDATASection
	 */
	String generateCDATASection(CDATASection cdata);

	/**
	 * generateChild method
	 * 
	 * @return java.lang.String
	 * @param org.w3c.dom.Node
	 */
	String generateChild(Node parentNode);

	/**
	 */
	String generateCloseTag(Node node);

	/**
	 * generateComment method
	 * 
	 * @return java.lang.String
	 * @param comment
	 *            org.w3c.dom.Comment
	 */
	String generateComment(Comment comment);

	/**
	 * generateDoctype method
	 * 
	 * @return java.lang.String
	 * @param docType
	 *            org.w3c.dom.DocumentType
	 */
	String generateDoctype(DocumentType docType);

	/**
	 * generateElement method
	 * 
	 * @return java.lang.String
	 * @param element
	 *            Element
	 */
	String generateElement(Element element);

	/**
	 * generateEndTag method
	 * 
	 * @return java.lang.String
	 * @param element
	 *            org.w3c.dom.Element
	 */
	String generateEndTag(Element element);

	/**
	 * generateEntityRef method
	 * 
	 * @return java.lang.String
	 * @param entityRef
	 *            org.w3c.dom.EntityReference
	 */
	String generateEntityRef(EntityReference entityRef);

	/**
	 * generatePI method
	 * 
	 * @return java.lang.String
	 * @param pi
	 *            org.w3c.dom.ProcessingInstruction
	 */
	String generatePI(ProcessingInstruction pi);

	/**
	 * generateSource method
	 * 
	 * @return java.lang.String
	 * @param node
	 *            org.w3c.dom.Node
	 */
	String generateSource(Node node);

	/**
	 * generateStartTag method
	 * 
	 * @return java.lang.String
	 * @param element
	 *            Element
	 */
	String generateStartTag(Element element);

	/**
	 */
	String generateTagName(Element element);

	/**
	 * generateText method
	 * 
	 * @return java.lang.String
	 * @param text
	 *            org.w3c.dom.Text
	 */
	String generateText(Text text);

	/**
	 */
	String generateTextData(Text text, String data);
}
