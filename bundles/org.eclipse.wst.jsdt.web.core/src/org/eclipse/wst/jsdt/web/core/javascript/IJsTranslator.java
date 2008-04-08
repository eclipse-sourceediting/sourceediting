/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 *     
 *     
 *******************************************************************************/


package org.eclipse.wst.jsdt.web.core.javascript;

import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;



/**
 * @author childsb
 *
 */
public interface IJsTranslator extends IDocumentListener{

	public String getJsText();

	public void setBuffer(IBuffer buffer);

	public Position[] getHtmlLocations();

	public int getMissingEndTagRegionStart();

	public Position[] getImportHtmlRanges();

	public String[] getRawImports();

	public void translate();

	public void translateInlineJSNode(IStructuredDocumentRegion container);

	public void translateJSNode(IStructuredDocumentRegion container);

	public void translateScriptImportNode(IStructuredDocumentRegion region);

	public void release();

}