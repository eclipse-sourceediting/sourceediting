/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.wst.html.core.internal.provisional;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;

/**
 * @deprecated not used in WTP
 */
public interface HTMLFilesPreferenceNames {

	static final String DEFAULT_SUFFIX = "defaultSuffix";//$NON-NLS-1$
	static final String HTML_SUFFIX = "html";//$NON-NLS-1$
	static final String GENERATE_DOCUMENT_TYPE = "generateDocumentType";//$NON-NLS-1$
	static final String GENERATE_GENERATOR = "generateGenerator";//$NON-NLS-1$
	// added this as a potential way to handle changing
	// product names "up" the stack. Note, not declared final 
	// to avoid getting 'inlined' by compiler.
	static IProduct product = Platform.getProduct();
	// Platform.getProduct() is spec'd so it might return null.
	// Its expected for any final product it would not be, so we'll return 
	// "WTP" for development/interim builds. No need to translate. 
	static String GENERATOR = (product == null) ? "WTP": product.getName(); //$NON-NLS-1$
}
