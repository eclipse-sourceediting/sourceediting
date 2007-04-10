/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.modelquery;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;

/**
 */
public interface DocumentQuery {

	/**
	 * isRenderRoot() returns true if - node is BODY element (both explicit
	 * and implicit) - node is portalhtml:body element - node is Document node
	 * and the document is treated as fragment (with BODY context) Note that
	 * in editing a fragment file, Document node should be treated as a
	 * substitute of BODY element.
	 */
	boolean isRenderRoot(Node node);

	/**
	 * isHeadCorrespondent() returns true if - node is HEAD element (both
	 * explicit and implicit) - node is portalhtml:head element - node is
	 * Document node and the document is treated as fragment (with HEAD
	 * context) Note that in editing a fragment file (with HEAD context),
	 * Document node should be treated as a substitute of HEAD element.
	 */
	boolean isHeadCorrespondent(Node node);

	/**
	 * Implicit BODY element will be gone in V6 SED model. So page designer
	 * provides an API to get a range whose content nodes are rendered in
	 * design view. getRenderRootRange() returns - a range from BODY's first
	 * to its last (if the document has BODY element) - a range form
	 * Document's first to its last (if the document is fragment with BODY
	 * context) - null (if the document is fragment with HEAD context) [The
	 * following cases will be supported since V6] - a range from a custom
	 * tag's first to its last (if the document has a custom tag which
	 * generates BODY element at runtime) - a range from Document's
	 * appropirate offset to its last (if the document does not have explicit
	 * BODY/HTML) - a range from HTML element's appropriate offset to its last
	 * (if the document does not have explicit BODY but have explicit HTML)
	 * 
	 * @param doc
	 * @return
	 */
	Range getRenderRootRange(Document doc);

	/**
	 * Implicit HEAD element will be gone in V6 SED model. So page designer
	 * provides an API to get a range whose content nodes are treated as HEAD
	 * element's child. getHeadCorrespondentRange() returns - a range from
	 * HEAD's first to its last (if the document has HEAD element) - a range
	 * form Document's first to its last (if the document is fragment with
	 * HEAD context) - null (if the document is fragment with BODY context)
	 * [The following cases will be supported since V6] - a range from a
	 * custom tag's first to its last (if the document has a custom tag which
	 * generates HEAD element at runtime) - a range from Document's first to
	 * appropirate offset (if the document does not have explicit HEAD/HTML) -
	 * a range from HTML element's first to appropriate offset (if the
	 * document does not have explicit HEAD but have explicit HTML)
	 * 
	 * @param doc
	 * @return
	 */
	Range getHeadCorrespondentRange(Document doc);

	/**
	 * getRenderRootNode() with [create=false] returns - BODY element if this
	 * document is not fragment and has BODY element - null if this document
	 * is not fragment and does not have BODY element - Document node if this
	 * document is fragment with BODY context - null if this document is
	 * fragment with HEAD context [The following cases will be supported since
	 * V6] - a custom tag which generates BODY tag at runtime - Document node
	 * or HTML element if this document is not fragment but does not have
	 * explicit BODY element getRenderRootNode() with [create=true] returns -
	 * BODY element if this document is not fragment and has BODY element (no
	 * modifictation) - newly created BODY element if this document is not
	 * fragment but does not have BODY element - Document node if this
	 * document is fragment with BODY context (no modifictation) [The
	 * following cases will be supported since V6] - a custom tag which
	 * generates BODY tag at runtime (no modifictation) - newly created BODY
	 * element if this document is not fragment but does not have explicit
	 * BODY element getRenderRootNode() throws HTMLCommandException (since V6)
	 * if - this document is fragment with HEAD context and - "create"
	 * parameter is true Note that in editing a fragment file, Document node
	 * should be treated as a substitute of BODY element.
	 * 
	 * @param childOrDocument
	 * @param create
	 * @return
	 */
	Node getRenderRootNode(Node childOrDocument, boolean create);

	/**
	 * getHeadCorrespondentNode() with [create=false] returns - HEAD element
	 * if this document is not fragment and has HEAD element - null if this
	 * document is not fragment and does not have HEAD element - Document node
	 * if this document is fragment with HEAD context - null if this document
	 * is fragment with BODY context [The following cases will be supported
	 * since V6] - a custom tag which generates HEAD tag at runtime - Document
	 * node or HTML element if this document is not fragment but does not have
	 * explicit HEAD element getHeadCorrespondentNode() with [create=true]
	 * returns - HEAD element if this document is not fragment and has HEAD
	 * element (no modifictation) - newly created HEAD element if this
	 * document is not fragment but does not have HEAD element - Document node
	 * if this document is fragment with HEAD context (no modifictation) [The
	 * following cases will be supported since V6] - a custom tag which
	 * generates HEAD tag at runtime (no modifictation) - newly created HEAD
	 * element if this document is not fragment but does not have explicit
	 * HEAD element getHeadCorrespondentNode() throws HTMLCommandException
	 * (since V6) if - this document is fragment with BODY context and -
	 * "create" parameter is true Note that in editing a fragment file,
	 * Document node should be treated as a substitute of HEAD element.
	 * 
	 * @param childOrDocument
	 * @param create
	 * @return
	 */
	Node getHeadCorrespondentNode(Node childOrDocument, boolean create);

	/**
	 * getHtmlCorrespondentNode() throws HTMLCommandException (since V6) if -
	 * this document is fragment and "create" parameter is true
	 * 
	 * @param childOrDocument
	 * @param create
	 * @return
	 */
	Node getHtmlCorrespondentNode(Node childOrDocument, boolean create);

	/**
	 * This inner class is intended for insertion target. please use this like
	 * the following : DocumentQuery.InsertionTarget ins;
	 * ins.getParent().insertBefore(youInsertionNode, ins.getRef());
	 */
	public class InsertionTarget {
		private final Node parent;
		private final Node ref;

		public InsertionTarget(Node parent, Node ref) {
			this.parent = parent;
			this.ref = ref;
		}

		public Node getParent() {
			return parent;
		}

		public Node getRef() {
			return ref;
		}
	}

	/**
	 * getHeadInsertionTarget() returns appropriate insetion target location
	 * for HEAD child tags such as <script>, <style>, <meta>etc. Basically
	 * this function returns <HEAD>tag's the last position. Note that this
	 * would not create actual <HEAD>tag when the document does not have it.
	 * <HEAD>is omittable tag so this function returns appropriate position
	 * to which implicit <HEAD>can be inserted, if the document has no
	 * <HEAD>.
	 * 
	 * @param doc
	 * @return
	 */
	InsertionTarget getHeadInsertionTarget(Document doc);

	/**
	 * getPageInsertionTarget() returns appropriate insetion target location
	 * for page-level markups, such as JSP directives, usebean tags or <html>
	 * tag. Basically this function returns just before <HTML>tag. Note that
	 * this would not create actual <HTML>tag when the document does not have
	 * it. In such case, this function returns a position just before the
	 * meaningful tags such as HTML/JSP elements.
	 * 
	 * @param doc
	 * @return
	 */
	InsertionTarget getPageInsertionTarget(Document doc);

	/**
	 * isFragment() returns whether the document is fragment or complete
	 * document
	 * 
	 * @param doc
	 * @return
	 */
	boolean isFragment(Document doc);

}
