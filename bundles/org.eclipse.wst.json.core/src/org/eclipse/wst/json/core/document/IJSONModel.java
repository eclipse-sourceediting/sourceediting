/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.document;

import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

/**
 * The SSE JSON model API.
 *
 */
public interface IJSONModel extends IStructuredModel {

	/**
	 * Returns the JSON Document.
	 * 
	 * @return the JSON Document.
	 */
	IJSONDocument getDocument();

}
