/******************************************************************************
 * Copyright (c) 2010, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.css.ui.tests.contentassist;

import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;
import org.eclipse.wst.css.ui.internal.contentassist.IProposalInfo;

public class ProposalInfo extends BrowserInformationControlInput implements IProposalInfo {

	private CSSMMNode fNode;

	public ProposalInfo() {
		super(null);
	}

	public Object getInputElement() {
		return fNode;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.css.ui.internal.contentassist.IProposalInfo#setInputElement(org.eclipse.wst.css.core.internal.metamodel.CSSMMNode)
	 */
	public void setInputElement(CSSMMNode node) {
		fNode = node;
	}

	public String getHtml() {
		return null;//fNode != null ?  "<b>" + fNode.getName() + "</b>" : "";
	}

	public String getInputName() {
		return fNode != null ? fNode.getName() : "";
	}

}
