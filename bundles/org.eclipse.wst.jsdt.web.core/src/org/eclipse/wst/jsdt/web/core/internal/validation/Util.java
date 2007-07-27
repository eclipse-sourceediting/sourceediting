/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.jsdt.web.core.internal.provisional.contenttype.ContentTypeIdForEmbededJs;

/**
 * @author childsb
 *
 */
public class Util {
	
	public static boolean isJsType(String fileName) {
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
		IContentType[] fContentTypes = new IContentType[contentTypeIds.length];
		
		
		for(int i = 0;i<contentTypeIds.length;i++) {
			fContentTypes[i] =  Platform.getContentTypeManager().getContentType(contentTypeIds[i]);
		}
		
		return fContentTypes;
	}
}
