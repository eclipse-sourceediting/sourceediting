/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.css.core.internal.contentmodel;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;

public class JSPedCSSTaglibController implements IDocumentSetupParticipant {

	public void setup(IDocument document) {
		// ignore TLD in content type css jsp
	}

}
