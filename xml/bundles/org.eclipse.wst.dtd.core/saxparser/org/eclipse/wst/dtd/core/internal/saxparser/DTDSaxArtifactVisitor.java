/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.saxparser;

import java.util.Enumeration;



public class DTDSaxArtifactVisitor {
	public void visitDTD(DTD dtd) {
		Enumeration en = dtd.externalElements();

		while (en.hasMoreElements()) {
			Object e = en.nextElement();
			if (e instanceof EntityDecl) {
				visitEntityDecl((EntityDecl) e);
			}
			else if (e instanceof ElementDecl) {
				visitElementDecl((ElementDecl) e);
			}
			else if (e instanceof NotationDecl) {
				visitNotationDecl((NotationDecl) e);
			}
		}
	}

	public void visitNotationDecl(NotationDecl notation) {
	}

	public void visitElementDecl(ElementDecl element) {
	}

	public void visitEntityDecl(EntityDecl entity) {
		if (entity.isEntityReferenced()) {
			visitParameterEntityReferenceDecl(entity);
		}
		else {
			if (entity.isExternal()) {
				visitExternalEntityDecl(entity);
			}
			else {
				visitInternalEntityDecl(entity);
			}
		}
	}

	public void visitParameterEntityReferenceDecl(EntityDecl entity) {
	}

	public void visitExternalEntityDecl(EntityDecl entity) {
	}

	public void visitInternalEntityDecl(EntityDecl entity) {
	}
}
