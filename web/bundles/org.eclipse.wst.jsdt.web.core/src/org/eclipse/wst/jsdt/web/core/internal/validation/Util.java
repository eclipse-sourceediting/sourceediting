/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.validation;

import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.jsdt.web.core.internal.provisional.contenttype.ContentTypeIdForEmbededJs;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class Util {
	
	public static boolean isJsType(String fileName) {
		if(fileName==null) return false;
		boolean valid = false;
		IContentType[] types =getJavascriptContentTypes();
		int i = 0;
		while (types!=null && i < types.length && !valid) {
			valid = types[i]!=null && types[i].isAssociatedWith(fileName);
			++i;
		}
		return valid;
		
	}
	
	public static IContentType[] getJavascriptContentTypes() {

		String[] contentTypeIds = ContentTypeIdForEmbededJs.ContentTypeIds;
		ArrayList fContentTypes = new ArrayList();
		
		
		for(int i = 0;i<contentTypeIds.length;i++) {
			IContentType ct =  Platform.getContentTypeManager().getContentType(contentTypeIds[i]);
			if(ct!=null) fContentTypes.add(ct);
		}
		
		return (IContentType[])fContentTypes.toArray(new IContentType[fContentTypes.size()]);
	}
}
