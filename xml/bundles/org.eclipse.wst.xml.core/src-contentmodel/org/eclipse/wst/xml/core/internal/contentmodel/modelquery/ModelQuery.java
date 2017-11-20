/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.modelquery;

import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtensionManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This class provides an interface for performing higher level queries based on
 * a xml model (DOM) and one or more associated content models (CMDocument).
 *
 * The queries provided can be organized into three groups:
 *
 * 1) DOM Node to CMNode mapping
 *      Given a grammatically valid DOM Node the corresponding CMNode can be determined.
 *      (i.e. Element -> CMElementDeclaration, Attr -> CMAttributeDeclaration, CharacterData -> CMDataType)
 *
 * 2) DOM editing tests ("Can I do this?")
 *      Questions such as canInsert, canRemove, canReplace can assist in the editing of a DOM.
 *
 *      The validityChecking argument determines the strictness of the validity testing that occurs.
 *
 *      - VALIDITY_NONE    : The current content of the Element is ignored.
 *                           Only the content model is considered.
 *                           This is most useful for codeassist related queries.
 *
 *      - VALIDITY_STRICT  : The current content of the Element is considered.
 *                           Returns true only if the operation preserves content validity.
 *                           This is useful when DOM editing needs to be constrained to maintain validity.
 *
 *      - VALIDITY_PARTIAL : Some optimized compromise between the two options above.
 *
 * 3) DOM editing actions ("What can I do here?")
 *      These methods return ModelQueryActions that are relevant at some specified DOM Node.
 *      The actions indicate what kinds of DOM Node can be inserted where (at what index).
 */
public interface ModelQuery
{
  public static final int VALIDITY_NONE = 0;
  public static final int VALIDITY_PARTIAL = 1;
  public static final int VALIDITY_STRICT = 2;

  public static final int INCLUDE_ALL = 0xFF;
  public static final int INCLUDE_ATTRIBUTES = 0x01;
  public static final int INCLUDE_CHILD_NODES = 0x02;
  public static final int INCLUDE_SEQUENCE_GROUPS = 0x04;
  public static final int INCLUDE_TEXT_NODES = 0x08;
  public static final int INCLUDE_ENCLOSING_REPLACE_ACTIONS = 0x10;

  public static final int EDIT_MODE_UNCONSTRAINED = 0;
  public static final int EDIT_MODE_CONSTRAINED_LENIENT= 1;
  public static final int EDIT_MODE_CONSTRAINED_STRICT = 2;


  void setEditMode(int editMode);

  int  getEditMode();

  /**
   * Returns the CMDocument that corresponds to the DOM Node.
   * or null if no CMDocument is appropriate for the DOM Node.
   */
  CMDocument getCorrespondingCMDocument(Node node);

  /**
   * Returns the corresponding CMNode for the DOM Node
   * or null if no CMNode is appropriate for the DOM Node.
   */
  CMNode getCMNode(Node node);

  /**
   * Returns the corresponding CMAttribute for the DOM Node
   * or null if no CMNode is appropriate for the DOM Node.
   */
  CMAttributeDeclaration getCMAttributeDeclaration(Attr attr);

  /**
   * Returns the corresponding CMAttribute for the DOM Node
   * or null if no CMNode is appropriate for the DOM Node.
   */
  CMElementDeclaration getCMElementDeclaration(Element element);

  /**
   * Returns true if the content of the element is valid
   */
  boolean isContentValid(Element element);

  /**
   * Returns the CMNode of the parent element's content model
   * that corresponds to the node
   */
  CMNode getOrigin(Node node);

  /**
   * Returns an array of CMNodes of the parent element's content model
   * that corresponds to the node
   */
  CMNode[] getOriginArray(Element element);

  /**
   * Returns a list of all CMNode 'meta data' that may be potentially added to the element.
   */
  List getAvailableContent(Element element, CMElementDeclaration ed, int includeOptions);

  /**
   * Can a DOM Node corresponding to the CMNode 'meta data' be added to the parent
   */
  boolean canInsert(Element parent, CMNode cmNode, int index, int validityChecking);

  /**
   * Can multiple DOM Nodes corresponding to the list of CMNode 'meta data' be added to the parent
   */
  boolean canInsert(Element parent, List cmNodeList, int index, int validityChecking);

  /**
   * Can the DOM Node be removed
   */
  boolean canRemove(Node node, int validityChecking);

  /**
   * Can the list of DOM Nodes be removed
   */
  boolean canRemove(List nodeList, int validityChecking);

  /**
   * Can the children within the indicated indices be replaced with a DOM Node corresponding to the CMNode 'meta data'
   */
  boolean canReplace(Element parent, int startIndex, int endIndex, CMNode cmNode, int validityChecking);

  /**
   * Can the children within the indicated indices be replaced with multiple DOM Nodes corresponding to the list of CMNode 'meta data'
   */
  boolean canReplace(Element parent, int startIndex, int endIndex, List cmNodeList, int validityChecking);

  /**
   *
   */
  void getInsertActions(Element parent, CMElementDeclaration ed, int index, int includeOptions, int validityChecking, List actionList);

  /**
   *
   */
  void getInsertActions(Document parent, CMDocument cmDocument, int index, int includeOptions, int validityChecking, List actionList);

  /**
   * Return a list of replace actions that can be performed on the parent's content
   */
  void getReplaceActions(Element parent, CMElementDeclaration ed, int includeOptions, int validityChecking, List actionList);

  /**
   * Return a list of replace actions that can be performed on the selected children of that parent 
   */
  void getReplaceActions(Element parent, CMElementDeclaration ed, List selectedChildren, int includeOptions, int validityChecking, List actionList);

                        
  /** 
   *  @deprecated - use getPossibleDataTypeValues()
   */
  List getDataTypeValues(Element element, CMNode cmNode);

  /**
   * This methods return an array of possible values corresponding to the datatype of the CMNode (either an CMAttributeDeclaration or a CMElementDeclaration)
   */
  String[] getPossibleDataTypeValues(Element element, CMNode cmNode);

  /**
   * This method may return null if a CMDocumentManager is not used by the ModelQuery
   */
  CMDocumentManager getCMDocumentManager();                                       

  /**
   * This method may return null the ModelQuery doesn't support the use of extensions
   */
  ModelQueryExtensionManager getExtensionManager();   
}
