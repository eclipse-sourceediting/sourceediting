/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 224197 - initial API and implementation
 *                    based on work from Apache Xalan 2.7.0
 *******************************************************************************/
/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: XalanTransformState.java,v 1.1 2008/03/27 01:08:56 dacarver Exp $
 */

package org.eclipse.wst.xsl.core.internal.compiler.xslt10.transformer;

import javax.xml.transform.Transformer;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemTemplate;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemTemplateElement;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Before the serializer merge, the TransformState interface was
 * implemented by ResultTreeHandler.
 */
public class XalanTransformState
    implements TransformState {
        
    protected Node m_node = null;
    protected ElemTemplateElement m_currentElement = null;
    protected ElemTemplate m_currentTemplate = null;
    protected ElemTemplate m_matchedTemplate = null;
    protected int m_currentNodeHandle = DTM.NULL;
    protected Node m_currentNode = null;
    protected int m_matchedNode = DTM.NULL;
    protected DTMIterator m_contextNodeList = null;
    protected boolean m_elemPending = false;    
    protected TransformerImpl m_transformer = null;

    /**
     * @see org.apache.xml.serializer.TransformStateSetter#setCurrentNode(Node)
     */
    public void setCurrentNode(Node n) {
        m_node = n;
    }

    /**
     * @see org.apache.xml.serializer.TransformStateSetter#resetState(Transformer)
     */
    public void resetState(Transformer transformer) {
        if ((transformer != null) && (transformer instanceof TransformerImpl)) {
           m_transformer = (TransformerImpl)transformer;
           m_currentElement = m_transformer.getCurrentElement();
           m_currentTemplate = m_transformer.getCurrentTemplate();
           m_matchedTemplate = m_transformer.getMatchedTemplate();
           int currentNodeHandle = m_transformer.getCurrentNode();
           DTM dtm = m_transformer.getXPathContext().getDTM(currentNodeHandle);
           m_currentNode = dtm.getNode(currentNodeHandle);
           m_matchedNode = m_transformer.getMatchedNode();
           m_contextNodeList = m_transformer.getContextNodeList();    
        }       
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getCurrentElement()
     */
    public ElemTemplateElement getCurrentElement() {
      if (m_elemPending)
         return m_currentElement;
      else
         return m_transformer.getCurrentElement();
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getCurrentNode()
     */
    public Node getCurrentNode() {
      if (m_currentNode != null) {
         return m_currentNode;
      } else {
         DTM dtm = m_transformer.getXPathContext().getDTM(m_transformer.getCurrentNode());
         return dtm.getNode(m_transformer.getCurrentNode());
      }
    }
    
    /**
     * @see org.apache.xalan.transformer.TransformState#getCurrentTemplate()
     */
    public ElemTemplate getCurrentTemplate() {
       if (m_elemPending)
         return m_currentTemplate;
       else
         return m_transformer.getCurrentTemplate();
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getMatchedTemplate()
     */
    public ElemTemplate getMatchedTemplate() {
      if (m_elemPending)
         return m_matchedTemplate;
      else
         return m_transformer.getMatchedTemplate();
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getMatchedNode()
     */
    public Node getMatchedNode() {
 
       if (m_elemPending) {
         DTM dtm = m_transformer.getXPathContext().getDTM(m_matchedNode);
         return dtm.getNode(m_matchedNode);
       } else {
         DTM dtm = m_transformer.getXPathContext().getDTM(m_transformer.getMatchedNode());
         return dtm.getNode(m_transformer.getMatchedNode());
       }
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getContextNodeList()
     */
    public NodeIterator getContextNodeList() {
      if (m_elemPending) {
          return new org.apache.xml.dtm.ref.DTMNodeIterator(m_contextNodeList);
      } else {
          return new org.apache.xml.dtm.ref.DTMNodeIterator(m_transformer.getContextNodeList());
      }
    }
    /**
     * @see org.apache.xalan.transformer.TransformState#getTransformer()
     */
    public Transformer getTransformer() {
        return m_transformer;
    }

}
