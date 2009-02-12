/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.encoding;

import org.eclipse.core.runtime.content.IContentDescription;


/**
 * This class is to simply hold information and data about the type of
 * encoding found for a resource. It not only includes names, etc., but also
 * gives hints about the algorithm, or rule, that the encodng was determined.
 * Having all this info in a central object, associated with the Document
 * (technically, IStructuredDocument), allows for better user error messages,
 * and better handling of knowing how to dump a file, given we know how it was
 * loaded.
 * 
 * Note: the data in this class is only valid if its has actually gone through
 * the loading or dumping sequence. It is not accurate, for example, if a
 * structuredDocument is simply created and then setText called. In this type
 * of case, accuracy for loading and dumping is not required, since its all
 * re-discovered. One limitation is that structuredDocument's created "from
 * scratch" this way, don't have any encoding information to count on, and
 * would have to arrange the processing to be done. (And it is done,
 * automatically if going through loader or dumper, but perhaps not in future
 * new uses. TODO: this can be inproved in future versions.)
 * 
 * isInitialized is set when the loader or dumper processes have been used,
 * but even this can't be counted on 100% if the document has been modified
 * since.
 * 
 */
public class EncodingMemento implements Cloneable {

	public final static String CLONED = "cloned"; //$NON-NLS-1$
	public final static String DEFAULTS_ASSUMED_FOR_EMPTY_INPUT = "DefaultsAssumedForEmptyInput"; //$NON-NLS-1$
	public final static String DEFAULTS_USED_DUE_TO_SMALL_STREAM = "defaultsUsedDueToSmallStream"; //$NON-NLS-1$


	/*
	 * Strings to be used for tracing. TODO: need to clean this up, we no
	 * longer use all of them
	 */
	public final static String DETECTED_STANDARD_UNICODE_BYTES = "detectedStandardUnicodeBytes"; //$NON-NLS-1$
	public final static String FOUND_ENCODING_IN_CONTENT = "foundEncodingInContent"; //$NON-NLS-1$
	public final static String FOUND_ENCODING_IN_STREAM = "foundEncodingInStream"; //$NON-NLS-1$
	public final static String FOUND_ENCODING_IN_STRUCTURED_DOCUMENT = "foundEncodingInStructuredDocument"; //$NON-NLS-1$
	public final static String GUESSED_ENCODING_FROM_STREAM = "GuessEncodingFromStream"; //$NON-NLS-1$
	public final static String JAVA_NAME_FOUND_AS_IANA_NAME = "noMappingFoundButJavaNameFoundToBeIANAName"; //$NON-NLS-1$
	public final static String JAVA_NAME_FOUND_IN_ALIAS_NAME = "noMappingFoundButJavaNameFoundInAliasTable"; //$NON-NLS-1$
	public final static String NO_IANA_NAME_FOUND = "noMappingFoundFromJavaNameToIANAName"; //$NON-NLS-1$
	public final static String USED_CONTENT_TYPE_DEFAULT = "UsedContentTypeDefault"; //$NON-NLS-1$
	public final static String USED_JAVA_DEFAULT = "UsedJavaDefault"; //$NON-NLS-1$
	public final static String USED_MEMENTO_FROM_LOAD = "usedMementoFromLoad"; //$NON-NLS-1$
	public final static String USED_PROPERTY_SETTINGS = "USED_PROPERTY_SETTINGS"; //$NON-NLS-1$
	public final static String USED_USER_SPECIFIED_PREFERENCE = "UsedUserSpecifiedPreference"; //$NON-NLS-1$
	public final static String USED_WORKSPACE_DEFAULT = "UsedWorkspaceDefault"; //$NON-NLS-1$
	public final static String USER_IS_USING_JAVA_ENCODING = "UserIsUsingJavaEncoding"; //$NON-NLS-1$
	private String fAppropriateDefault;
	private String fDetectedCharsetName;
	private String fInvalidEncoding;


	private String fJavaCharsetName;
	private boolean fUnicodeStream;
	private boolean fUTF83ByteBOMUsed;
	
	private byte[] fBOM;

	public EncodingMemento() {
		super();
	}

	/**
	 * Returns a clone of this object.
	 */
	public Object clone() {
		EncodingMemento object = null;
		try {
			object = (EncodingMemento) super.clone();
		}
		catch (CloneNotSupportedException e) {
			// impossible, since we're implementing here
		}

		return object;

	}

	/**
	 * Returns the appropriateDefault. This is only set if an invalid encoding
	 * was found, and contains an charset appropriate to use as a default
	 * value, if, for example, the user decides to load the document anyway,
	 * even though the charset was found to be invalid.
	 * 
	 * @return String
	 */
	public String getAppropriateDefault() {
		if (fAppropriateDefault == null) {
			fAppropriateDefault = NonContentBasedEncodingRules.useDefaultNameRules(null);
		}
		return fAppropriateDefault;
	}

	/**
	 * Returns the charset name, if it is different from the charset name
	 * found in getJavaCharsetName. This can happen, for example, if there are
	 * differences in case. This method might return SHIFT_JIS, and the the
	 * getJavaCharsetName might return Shift_JIS -- if SHIFT_JIS was detected
	 * in file/document. If the original file contained the correct case, then
	 * this method would return null. The getJavaCharsetName is typically the
	 * one that should always be used, and this one only used for certain
	 * error conditions, or or if when creating a "duplicate" resource, it was
	 * desired to use exactly the charset name as in the original document. As
	 * an example of this later case, the original document might contain
	 * ISO-8859-9, but the detected charset name might contain ISO-8859-9-I.
	 * 
	 * @return String
	 */
	public String getDetectedCharsetName() {
		return fDetectedCharsetName;
	}

	/**
	 * Returns a charset name that was detected, but not found to be a charset
	 * suppoorted by the VM.
	 * 
	 * @return String
	 */
	public String getInvalidEncoding() {
		return fInvalidEncoding;
	}

	/**
	 * Returns the java cononical charset name.
	 * 
	 * @return String
	 */
	public String getJavaCharsetName() {
		return fJavaCharsetName;
	}

	/**
	 * Note: we may be able to remove this method, if it turns out this work
	 * is done by "text" type.
	 * 
	 * @deprecated -
	 */
	public byte[] getUnicodeBOM() {
		byte[] bom = null;
		if (isUTF83ByteBOMUsed())
			bom = IContentDescription.BOM_UTF_8;
		else if (isUnicodeStream())
			bom = fBOM;
		return bom;
	}

	/**
	 * Note: in our implementation, the stream is a unicode stream if the
	 * charset is UTF-16, UTF-16LE, or UTF-16BE. A stream with 3 byte BOM is
	 * not considered unicode stream here.
	 * 
	 * @return returns true if is a unicode (UTF-16) stream
	 */
	public boolean isUnicodeStream() {
		return fUnicodeStream;
	}

	/**
	 * Note: in our implementation, the stream is a unicode stream if the
	 * charset is UTF-16, UTF-16LE, or UTF-16BE. A stream with 3 byte BOM is
	 * not considered unicode stream here.
	 * 
	 * Set during load, can be used by dumper to write 3 byte BOM, which Java
	 * does not normally do. This helps maintain compatibility with other
	 * programs (those that wrote the 3 byte BOM there to begin with.
	 * 
	 * @return boolean
	 */
	public boolean isUTF83ByteBOMUsed() {
		return fUTF83ByteBOMUsed;
	}

	public boolean isValid() {
		return getInvalidEncoding() == null;
	}

	/**
	 * Sets the appropriateDefault.
	 * 
	 * @param appropriateDefault
	 *            The appropriateDefault to set
	 */
	public void setAppropriateDefault(String appropriateDefault) {
		fAppropriateDefault = appropriateDefault;
	}


	public void setDetectedCharsetName(String detectedCharsetName) {
		fDetectedCharsetName = detectedCharsetName;
	}

	public void setInvalidEncoding(String invalidEncoding) {
		fInvalidEncoding = invalidEncoding;
	}

	/**
	 * Sets the javaEncodingName.
	 * 
	 * @param javaEncodingName
	 *            The javaEncodingName to set
	 */
	public void setJavaCharsetName(String javaCharsetName) {
		fJavaCharsetName = javaCharsetName;
	}

	/**
	 * @param b
	 */
	public void setUnicodeStream(boolean unicodeStream) {
		fUnicodeStream = unicodeStream;

	}

	/**
	 * Sets the uTF83ByteBOMfound.
	 * 
	 * @param uTF83ByteBOMfound
	 *            The uTF83ByteBOMfound to set
	 */
	public void setUTF83ByteBOMUsed(boolean uTF83ByteBOMUsed) {
		fUTF83ByteBOMUsed = uTF83ByteBOMUsed;
	}

	public void setUnicodeBOM(byte[] bom) {
		fBOM = bom;
	}
}
