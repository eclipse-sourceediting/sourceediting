/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.document;



import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface XMLModelNotifier {

	/**
	 * attrReplaced method
	 * @param element org.w3c.dom.Element
	 * @param newAttr org.w3c.dom.Attr
	 * @param oldAttr org.w3c.dom.Attr
	 */
	void attrReplaced(Element element, Attr newAttr, Attr oldAttr);

	/**
	 */
	void beginChanging();

	/**
	 */
	void beginChanging(boolean newModel);

	/**
	 * childReplaced method
	 * @param parentNode org.w3c.dom.Node
	 * @param newChild org.w3c.dom.Node
	 * @param oldChild org.w3c.dom.Node
	 */
	void childReplaced(Node parentNode, Node newChild, Node oldChild);

	/**
	 */
	void endChanging();

	/**
	 */
	void endTagChanged(Element element);

	/**
	 */
	boolean hasChanged();

	/**
	 */
	boolean isChanging();

	/**
	 */
	void propertyChanged(Node node);

	/**
	 */
	void startTagChanged(Element element);

	/**
	 */
	void editableChanged(Node node);

	/**
	 */
	void structureChanged(Node node);

	/**
	 * valueChanged method
	 * @param node org.w3c.dom.Node
	 */
	void valueChanged(Node node);

	/**
	 * Cancel pending notifications. This is called in the context
	 * of "reinitialization" so is assumed ALL notifications can 
	 * be safely canceled, assuming that once factories and adapters
	 * are re-initialized they will be re-notified as text is set in 
	 * model, if still appropriate.
	 */
	void cancelPending();

}
