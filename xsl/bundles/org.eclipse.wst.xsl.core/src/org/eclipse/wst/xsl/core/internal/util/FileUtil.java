package org.eclipse.wst.xsl.core.internal.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.wst.xsl.core.XSLCore;

/**
 * This is a general file utility class.
 * 
 * @author dcarver
 *
 */
public class FileUtil {

	/**
	 * Determines if a file is one of the valid XML content types.
	 * @param file The input IFile to check
	 * @return True if it is a XML file, false otherwise.
	 */
	public static boolean isXMLFile(IFile file)
	{
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType[] types = contentTypeManager.findContentTypesFor(file.getName());
		for (IContentType contentType : types)
		{
			if (contentType.isKindOf(contentTypeManager.getContentType("org.eclipse.core.runtime.xml")) || contentType.isKindOf(contentTypeManager.getContentType("org.eclipse.wst.xml.core.xmlsource"))) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if a file is a XSLT File.
	 * @param file The input IFile to check.
	 * @return True if it is a XSLT file, false otherwise.
	 * 
	 */
	public static boolean isXSLFile(IFile file)
	{
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType[] types = contentTypeManager.findContentTypesFor(file.getName());
		for (IContentType contentType : types)
		{
			if (contentType.equals(contentTypeManager.getContentType(XSLCore.XSL_CONTENT_TYPE)))
			{
				return true;
			}
		}
		return false;
	}

}
