/*******************************************************************************
 * Copyright (c) 2004, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.contentassist.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

/**
 * Cache for the filenames and filename extensions registered for a content
 * type and all of its descendants
 */
public class ContentTypeSpecs {
	/**
	 * @param contentTypeId - the registered content type's ID
	 * @return
	 */
	public static ContentTypeSpecs createFor(String contentTypeId) {
		return createFor(new IContentType[]{Platform.getContentTypeManager().getContentType(contentTypeId)});
	}

	public static ContentTypeSpecs createFor(IContentType[] contentTypes) {
//		long startTime = System.currentTimeMillis();
		List<String> filenameExtensions = new ArrayList<String>();
		Set<String> filenames = new HashSet<>();
		for (IContentType baseContentType: contentTypes) {
			String[] baseExtensions = baseContentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
			for (int i = 0; i < baseExtensions.length; i++) {
				filenameExtensions.add(baseExtensions[i]);
			}
			String[] names = baseContentType.getFileSpecs(IContentType.FILE_NAME_SPEC);
			for (int j = 0; j < names.length; j++) {
				filenames.add(names[j]);
			}
			Arrays.sort(baseExtensions);
			IContentType[] allContentTypes1 = Platform.getContentTypeManager().getAllContentTypes();
			for (int i = 0, length = allContentTypes1.length; i < length; i++) {
				if (allContentTypes1[i].isKindOf(baseContentType)) {
					String[] fileExtension = allContentTypes1[i].getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
					for (int j = 0; j < fileExtension.length; j++) {
						if (!filenameExtensions.contains(fileExtension[j])) {
							filenameExtensions.add(fileExtension[j]);
						}
					}
					names = allContentTypes1[i].getFileSpecs(IContentType.FILE_NAME_SPEC);
					for (int j = 0; j < names.length; j++) {
						filenames.add(names[j]);
					}
				}
			}
		}
		String[] stringExtensions = filenameExtensions.toArray(new String[filenameExtensions.size()]);
//		System.out.println("Discovered content types for " + contentTypeId + " in " + (System.currentTimeMillis() - startTime) + "ms");
		return new ContentTypeSpecs(filenames.toArray(new String[filenames.size()]), stringExtensions);
	}

	String[] fFilenames = new String[0];
	String[] fExtensions = new String[0];

	private ContentTypeSpecs(String[] fileNames, String[] extensions) {
		super();
		fFilenames = fileNames;
		fExtensions = extensions;
		Arrays.sort(fileNames);
	}

	public boolean matches(String filename) {
		if (Arrays.binarySearch(fFilenames, filename) >= 0) {
			return true;
		}
		for (int i = 0; i < fExtensions.length; i++) {
			if (filename.length() > fExtensions[i].length() + 1
					&& filename.charAt(filename.length() - fExtensions[i].length() - 1) == '.'
					&& filename.endsWith(fExtensions[i])) {
				return true;
			}
		}
		return false;
	}

	public void addFilename(String filename) {
		List<String> combinedList = new ArrayList<String>(fFilenames.length + 1);
		for (int i = 0; i < fFilenames.length; i++) {
			combinedList.add(fFilenames[i]);
		}
		combinedList.add(filename);
		String[] combinedArray = combinedList.toArray(new String[combinedList.size()]);
		Arrays.sort(combinedArray);
		fFilenames = combinedArray;
	}

	public void addFilenameExtension(String extension) {
		List<String> combinedList = new ArrayList<String>(fExtensions.length + 1);
		for (int i = 0; i < fExtensions.length; i++) {
			combinedList.add(fExtensions[i]);
		}
		combinedList.add(extension);
		String[] combinedArray = combinedList.toArray(new String[combinedList.size()]);
		fExtensions = combinedArray;
	}
}
