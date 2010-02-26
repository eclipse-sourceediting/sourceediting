/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.contentassist;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * <p>{@link AbstractXMLModelQueryCompletionProposalComputer} used to
 * generate XML tag content assist proposals</p>
 * 
 * <p><b>NOTE:</b> Currently this computer does not filter out any of the 
 * model query results so it will return all proposals from the model query
 * for the current content type.  In the future this may need to change.</p>
 */
public class XMLTagsCompletionProposalComputer extends
		AbstractXMLModelQueryCompletionProposalComputer {

	/** the generated used to generate the proposals */
	protected XMLContentModelGenerator fGenerator;
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#getContentGenerator()
	 */
	protected XMLContentModelGenerator getContentGenerator() {
		if (fGenerator == null) {
			fGenerator = new XMLContentModelGenerator();
		}
		return fGenerator;
	}
	
	/**
	 * <p>Filters out any model query actions that are not specific to XML</p>
	 * <p><b>NOTE:</b> Currently nothing is filtered so this computer returns all
	 * results from the model query for the current content type</p>
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#validModelQueryNode(org.eclipse.wst.xml.core.internal.contentmodel.CMNode)
	 */
	protected boolean validModelQueryNode(CMNode node) {
		return true;
	}
}
