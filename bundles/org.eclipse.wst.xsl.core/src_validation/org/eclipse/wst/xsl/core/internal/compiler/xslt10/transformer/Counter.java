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
 * Copyright 1999-2004 The Apache Software Foundation.
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
 * $Id: Counter.java,v 1.2 2008/03/28 02:38:16 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.transformer;

import javax.xml.transform.TransformerException;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemNumber;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathContext;

/**
 * A class that does incremental counting for support of xsl:number. This class
 * stores a cache of counted nodes (m_countNodes). It tries to cache the counted
 * nodes in document order... the node count is based on its position in the
 * cache list
 * 
 * @xsl.usage internal
 */
public class Counter {

	/**
	 * Set the maximum ammount the m_countNodes list can grow to.
	 */
	public static final int MAXCOUNTNODES = 500;

	/**
	 * The start count from where m_countNodes counts from. In other words, the
	 * count of a given node in the m_countNodes vector is node position +
	 * m_countNodesStartCount.
	 */
	protected int m_countNodesStartCount = 0;

	/**
	 * A vector of all nodes counted so far.
	 */
	protected NodeSetDTM m_countNodes;

	/**
	 * The node from where the counting starts. This is needed to find a counter
	 * if the node being counted is not immediatly found in the m_countNodes
	 * vector.
	 */
	protected int m_fromNode = DTM.NULL;

	/**
	 * The owning xsl:number element.
	 */
	protected ElemNumber m_numberElem;

	/**
	 * Value to store result of last getCount call, for benifit of returning val
	 * from CountersTable.getCounterByCounted, who calls getCount.
	 */
	protected int m_countResult;

	/**
	 * Construct a counter object.
	 * 
	 * @param numberElem
	 *            The owning xsl:number element.
	 * @param countNodes
	 *            A vector of all nodes counted so far.
	 * 
	 * @throws TransformerException
	 */
	public Counter(ElemNumber numberElem, NodeSetDTM countNodes)
			throws TransformerException {
		m_countNodes = countNodes;
		m_numberElem = numberElem;
	}

	/**
	 * Construct a counter object.
	 * 
	 * @param numberElem
	 *            The owning xsl:number element.
	 * 
	 * @throws TransformerException
	 * 
	 * Counter(ElemNumber numberElem) throws TransformerException { m_numberElem =
	 * numberElem; }
	 */

	/**
	 * Try and find a node that was previously counted. If found, return a
	 * positive integer that corresponds to the count.
	 * 
	 * @param support
	 *            The XPath context to use
	 * @param node
	 *            The node to be counted.
	 * 
	 * @return The count of the node, or -1 if not found.
	 */
	public int getPreviouslyCounted(XPathContext support, int node) {

		int n = m_countNodes.size();

		m_countResult = 0;

		for (int i = n - 1; i >= 0; i--) {
			int countedNode = m_countNodes.elementAt(i);

			if (node == countedNode) {

				// Since the list is in backwards order, the count is
				// how many are in the rest of the list.
				m_countResult = i + 1 + m_countNodesStartCount;

				break;
			}

			DTM dtm = support.getDTM(countedNode);

			// Try to see if the given node falls after the counted node...
			// if it does, don't keep searching backwards.
			if (dtm.isNodeAfter(countedNode, node))
				break;
		}

		return m_countResult;
	}

	/**
	 * Get the last node in the list.
	 * 
	 * @return the last node in the list.
	 */
	public int getLast() {

		int size = m_countNodes.size();

		return (size > 0) ? m_countNodes.elementAt(size - 1) : DTM.NULL;
	}
}
