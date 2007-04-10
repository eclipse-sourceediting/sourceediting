/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf.impl;

import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent;

public class DTDIdHelper {
	public DTDIdHelper() {
	}

	public String computeContentId(DTDElementContent content) {
		String id = ""; //$NON-NLS-1$
		if (content instanceof DTDEmptyContent) {
			id = "#EMPTY"; //$NON-NLS-1$
		}
		else if (content instanceof DTDPCDataContent) {
			id = "#PCDATA"; //$NON-NLS-1$
		}
		else if (content instanceof DTDEmptyContent) {
			id = "#EMPTY"; //$NON-NLS-1$
		}
		else if (content instanceof DTDElementReferenceContent) {
			id = "#" + ((DTDElementReferenceContent) content).getReferencedElement().getName(); //$NON-NLS-1$
		}
		else if (content instanceof DTDEntityReferenceContent) {
			id = "#" + ((DTDEntityReferenceContent) content).getElementReferencedEntity().getName(); //$NON-NLS-1$
		}
		else if (content instanceof DTDGroupContent) {
			id = computeGroupId((DTDGroupContent) content);
		}
		return id;
	}

	public String computeGroupId(DTDGroupContent grp) {
		String groupId = "_"; //$NON-NLS-1$
		return groupId;
	}

	/**
	 * @generated
	 */
	protected String computeContentIdGen(DTDElementContent content) {

		String id = ""; //$NON-NLS-1$
		if (content instanceof DTDEmptyContent) {
			id = "#EMPTY"; //$NON-NLS-1$
		}
		else if (content instanceof DTDPCDataContent) {
			id = "#PCDATA"; //$NON-NLS-1$
		}
		else if (content instanceof DTDEmptyContent) {
			id = "#EMPTY"; //$NON-NLS-1$
		}
		else if (content instanceof DTDElementReferenceContent) {
			id = "#" + ((DTDElementReferenceContent) content).getReferencedElement().getName(); //$NON-NLS-1$
		}
		else if (content instanceof DTDEntityReferenceContent) {
			id = "#" + ((DTDEntityReferenceContent) content).getElementReferencedEntity().getName(); //$NON-NLS-1$
		}
		else if (content instanceof DTDGroupContent) {
			id = computeGroupId((DTDGroupContent) content);
		}
		return id;
	}

	/**
	 * @generated
	 */
	protected String computeGroupIdGen(DTDGroupContent grp) {

		String groupId = "_"; //$NON-NLS-1$
		return groupId;
	}
}
