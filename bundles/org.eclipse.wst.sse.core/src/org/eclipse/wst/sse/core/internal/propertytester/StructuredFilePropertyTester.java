/******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.sse.core.internal.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.internal.Logger;

/**
 * A Property Tester that operates on IFiles and validates
 * that the expected content type id matches that of the content
 * type of the file, or any of the base content types.
 * 
 * Based on org.eclipse.core.internal.propertytester.FilePropertyTester
 * 
 */
public class StructuredFilePropertyTester extends PropertyTester {

	/**
	 * A property indicating that we are looking to verify that the file matches
	 * the content type matching the given identifier. The identifier is
	 * provided as the expected value.
	 */
	private static final String CONTENT_TYPE_ID = "contentTypeId"; //$NON-NLS-1$
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ((receiver instanceof IFile) && property.equals(CONTENT_TYPE_ID))
			return testContentType((IFile) receiver, toString(expectedValue));
		return false;
	}
	
	/**
	 * Tests whether the content type for <code>file</code> (or any base content types) 
	 * matches the <code>contentTypeId</code>. It is possible that this method call could
	 * cause the file to be read. It is also possible (through poor plug-in
	 * design) for this method to load plug-ins.
	 * 
	 * @param file
	 *            The file for which the content type should be determined; must
	 *            not be <code>null</code>.
	 * @param contentTypeId
	 *            The expected content type; must not be <code>null</code>.
	 * @return <code>true</code> if the file's content type (or base content types) 
	 *         has an identifier that matches <code>contentTypeId</code>;
	 *         <code>false</code> otherwise.
	 */
	private boolean testContentType(final IFile file, String contentTypeId) {
		final String expectedValue = contentTypeId.trim();

		try {
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription != null) {
				IContentType contentType = contentDescription.getContentType();
				while(contentType != null) {
					if(expectedValue.equals(contentType.getId()))
						return true;
					contentType = contentType.getBaseType();
				}
			}
		} catch (CoreException e) {
			Logger.logException("Core exception while retrieving the content description", e); //$NON-NLS-1$
		}
		return false;
	}
	
	/**
	 * Converts the given expected value to a <code>String</code>.
	 * 
	 * @param expectedValue
	 *            the expected value (may be <code>null</code>).
	 * @return the empty string if the expected value is <code>null</code>,
	 *         otherwise the <code>toString()</code> representation of the
	 *         expected value
	 */
	protected String toString(Object expectedValue) {
		return expectedValue == null ? "" : expectedValue.toString(); //$NON-NLS-1$
	}

}
