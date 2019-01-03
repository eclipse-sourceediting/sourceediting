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
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.ltk.parser;



import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public interface StructuredDocumentRegionHandler {

	// Sent when a IStructuredDocumentRegion is first parsed
	public void nodeParsed(IStructuredDocumentRegion aCoreStructuredDocumentRegion);

	// Sent when the calling parser's model undergoes a full reset
	//   and any information based upon the old model should be
	//   cleared
	public void resetNodes();
}
