/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.provisional.document;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * 
 * ISourceGenerator allows DOM models to generate source appropriate for their
 * parameter, relative to the model that provides the source generator.
 * 
 */

public interface ISourceGenerator {

	/**
	 * Generate attribute name.
	 * 
	 * @param attr -
	 *            the Attr
	 * @return String - the string generated
	 */
	String generateAttrName(Attr attr);

	/**
	 * generateAttrValue
	 * 
	 * @param attr -
	 *            the Attr
	 * @return String - the string generated
	 */
	String generateAttrValue(Attr attr);

	/**
	 * generateAttrValue
	 * 
	 * @param attr -
	 *            the Attr
	 * @param char -
	 *            the type of quote desired (' or ").
	 * @return String - the string generated
	 */
	String generateAttrValue(Attr attr, char quote);

	/**
	 * generateAttrValue
	 * 
	 * @param value
	 *            the String value
	 * @param char -
	 *            the type of quote desired (' or ").
	 * @return String - the string generated
	 */
	String generateAttrValue(String value, char quote);

	/**
	 * generateCDATASection method
	 * 
	 * @param comment
	 *            CDATASection
	 * @return String - the string generated
	 */
	String generateCDATASection(CDATASection cdata);

	/**
	 * generateChild method
	 * 
	 * @return String
	 * @param Node
	 * @return String - the string generated
	 */
	String generateChild(Node parentNode);

	/**
	 * generateCloseTag
	 * 
	 * @param node -
	 *            the Node
	 * @return String - the string generated
	 */
	String generateCloseTag(Node node);

	/**
	 * generateComment method
	 * 
	 * @param comment
	 *            org.w3c.dom.Comment
	 * @return String - the string generated
	 */
	String generateComment(Comment comment);

	/**
	 * generateDoctype method
	 * 
	 * @param docType
	 *            DocumentType
	 * @return String - the string generated
	 */
	String generateDoctype(DocumentType docType);

	/**
	 * generateElement method
	 * 
	 * @param element -
	 *            Element
	 * @return String - the string generated
	 */
	String generateElement(Element element);

	/**
	 * generateEndTag method
	 * 
	 * @param element -
	 *            Element
	 * @return String - the string generated
	 */
	String generateEndTag(Element element);

	/**
	 * generateEntityRef method
	 * 
	 * @param entityRef
	 *            EntityReference
	 * @return String - the string generated
	 */
	String generateEntityRef(EntityReference entityRef);

	/**
	 * generatePI method
	 * 
	 * @param pi -
	 *            ProcessingInstruction
	 * @return String - the string generated
	 */
	String generatePI(ProcessingInstruction pi);

	/**
	 * generateSource method
	 * 
	 * @param node -
	 *            the Node
	 * @return String - the string generated
	 */
	String generateSource(Node node);

	/**
	 * generateStartTag method
	 * 
	 * @param element
	 *            Element
	 * @return String - the string generated
	 */
	String generateStartTag(Element element);

	/**
	 * Generate tag name.
	 * 
	 * @param element -
	 *            element
	 * @return String - the string generated
	 */
	String generateTagName(Element element);

	/**
	 * generateText method
	 * 
	 * @param text -
	 *            the Text
	 * @return String - the string generated
	 */
	String generateText(Text text);

	/**
	 * generate text data
	 * 
	 * @param text -
	 *            the Text
	 * @param data -
	 *            the data
	 * @return String - the string generated
	 */
	String generateTextData(Text text, String data);
}
