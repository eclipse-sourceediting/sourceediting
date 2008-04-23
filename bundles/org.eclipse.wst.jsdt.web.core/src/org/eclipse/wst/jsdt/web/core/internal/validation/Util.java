/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.validation;

import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.jsdt.web.core.internal.provisional.contenttype.ContentTypeIdForEmbededJs;

/**
 * @author childsb
 *
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
