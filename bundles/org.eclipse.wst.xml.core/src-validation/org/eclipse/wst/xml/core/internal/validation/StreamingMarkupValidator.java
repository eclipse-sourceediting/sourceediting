/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.XMLCoreMessages;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.internal.tasks.XMLLineTokenizer;

public class StreamingMarkupValidator extends AbstractValidator implements IValidator {

	private static final String ANNOTATIONMSG = AnnotationMsg.class.getName();

	private IReporter fReporter;

	private static class Token {
		String type;
		int offset;
		int length;
		int line;
		String text;

		public Token(String type, String text, int offset, int length, int line) {
			this.type = type;
			this.text = text;
			this.offset = offset;
			this.length = length;
			this.line = line;
		}
	}

	public void getAnnotationMsg(IReporter reporter, int problemId, LocalizedMessage message, Object attributeValueText, int len){
		AnnotationMsg annotation = new AnnotationMsg(problemId, attributeValueText,len);
		message.setAttribute(ANNOTATIONMSG, annotation);
		reporter.addMessage(this, message);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void cleanup(IReporter reporter) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void validate(IValidationContext helper, IReporter reporter)	throws ValidationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.AbstractValidator#validate(org.eclipse.core.resources.IResource, int, org.eclipse.wst.validation.ValidationState, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		ValidationResult result = new ValidationResult();
		fReporter = result.getReporter(monitor);
		validateFile((IFile) resource, fReporter);
		return result;
	}

	/**
	 * Gets the line number for a token
	 * @param token the token to find the line of
	 * @return the line in the document where the token can be found
	 */
	private int getLine(Token token) {
		return token.line + 1;
	}

	/**
	 * Checks that a start tag is immediately followed by a tag name
	 * @param token the token to check
	 * @param previousRegion the previous region
	 * @param reporter the reporter
	 */
	private void checkForSpaceBeforeName(Token token, List previousRegion, IReporter reporter) {
		if (previousRegion != null && previousRegion.size() == 1) {
			// Check that the start tag's name comes right after the <
			Token first = (Token) previousRegion.get(0);
			if (DOMRegionContext.XML_TAG_OPEN.equals(first.type) && token.text.trim().length() == 0) {
				final String messageText = XMLCoreMessages.ReconcileStepForMarkup_2;
				final int length = token.length;
				LocalizedMessage message = new LocalizedMessage(getPluginPreference().getInt(XMLCorePreferenceNames.WHITESPACE_BEFORE_TAGNAME) , messageText);
				message.setOffset(token.offset);
				message.setLength(length);
				message.setLineNo(getLine(token));
				getAnnotationMsg(reporter, ProblemIDsXML.SpacesBeforeTagName, message, null, length);
			}
		}
	}

	/**
	 * Check that when a start- or end-tag has been opened it is properly closed
	 * @param previousRegion the previous region
	 * @param reporter the reporter
	 */
	private void checkForTagClose(List previousRegion, IReporter reporter) {
		if (previousRegion != null && previousRegion.size() > 0) {
			final Token first = (Token) previousRegion.get(0);
			// If the previous region was a start- or end-tag, look for the tag close
			if (first.type == DOMRegionContext.XML_TAG_OPEN || first.type == DOMRegionContext.XML_END_TAG_OPEN) {
				final int length = previousRegion.size();
				boolean isClosed = false;
				int textLength = first.length;
				for (int i = 1; i < length; i++) {
					Token t = (Token) previousRegion.get(i);
					// Valid tag closings, EMPTY_TAG_CLOSE only works for a start tag, though
					if ((t.type == DOMRegionContext.XML_EMPTY_TAG_CLOSE && first.type == DOMRegionContext.XML_TAG_OPEN) || t.type == DOMRegionContext.XML_TAG_CLOSE) {
						isClosed = true;
						break;
					}
					else if (t.type == DOMRegionContext.XML_TAG_NAME) {
						textLength += t.length;
					}
				}
				if (!isClosed) {
					String messageText = XMLCoreMessages.ReconcileStepForMarkup_6;

					LocalizedMessage message = new LocalizedMessage(getPluginPreference().getInt(XMLCorePreferenceNames.MISSING_CLOSING_BRACKET) , messageText);
					message.setOffset(first.offset);
					message.setLength(textLength);
					message.setLineNo(getLine(first));
					getAnnotationMsg(reporter, ProblemIDsXML.MissingClosingBracket, message, null, textLength);
				}
			}
		}
	}

	/**
	 * Checks that there is no content before the XML declaration
	 * @param previousRegion the region prior to the processing instruction
	 * @param reporter the reporter
	 */
	private void checkContentBeforeProcessingInstruction(List previousRegion, IReporter reporter) {
		if (previousRegion != null && previousRegion.size() > 0) {
			Token first = (Token) previousRegion.get(0);
			if (first.type == DOMRegionContext.XML_CONTENT && first.offset == 0) {
				// XML declaration only allowed at the start of the document
				String messageText = XMLCoreMessages.ReconcileStepForMarkup_5;

				LocalizedMessage message = new LocalizedMessage(getPluginPreference().getInt(XMLCorePreferenceNames.WHITESPACE_AT_START) , messageText);
				message.setOffset(first.offset);
				message.setLength(first.length);
				message.setLineNo(first.line + 1);
				getAnnotationMsg(reporter, ProblemIDsXML.SpacesBeforePI, message, null, first.length);
			 }
		}
	}

	/**
	 * Checks that the processing instruction name doesn't contain a namespace
	 * @param region the processing instruction region
	 * @param reporter the reporter
	 */
	private void checkNamespacesInProcessingInstruction(List region, IReporter reporter) {
		final int regionLength = region.size();
		for (int i = 0; i < regionLength; i++) {
			Token t = (Token) region.get(i);
			if (t.type == DOMRegionContext.XML_TAG_NAME) {
				int index = t.text.indexOf(":"); //$NON-NLS-1$
				if (index != -1) {
					String messageText = XMLCoreMessages.ReconcileStepForMarkup_4;
					int start = t.offset + index;
					int length = t.text.trim().length() - index;

					LocalizedMessage message = new LocalizedMessage(getPluginPreference().getInt(XMLCorePreferenceNames.NAMESPACE_IN_PI_TARGET) , messageText);
					message.setOffset(start);
					message.setLength(length);
					message.setLineNo(t.line + 1);
					getAnnotationMsg(reporter, ProblemIDsXML.NamespaceInPI, message, null, length);
					break;
				}
			}
		}
	}

	/**
	 * Check that a tag has a name (<> is invalid)
	 * @param token The xml tag close token
	 * @param region the tag region
	 * @param reporter the reporter
	 */
	private void checkEmptyTag(List region, IReporter reporter) {
		if (region.size() == 2) {
			// Check that the tag is not empty
			Token first = (Token)region.get(0);
			if (first.type == DOMRegionContext.XML_TAG_OPEN) {
				String messageText = XMLCoreMessages.ReconcileStepForMarkup_3;
				final int length = first.length + ((Token)region.get(1)).length;
				LocalizedMessage message = new LocalizedMessage(getPluginPreference().getInt(XMLCorePreferenceNames.MISSING_TAG_NAME) , messageText);
				message.setOffset(first.offset);
				message.setLength(length);
				message.setLineNo(first.line + 1);

				getAnnotationMsg(reporter, ProblemIDsXML.EmptyTag, message, null, length);
			}
		}
	}

	/**
	 * Checks the end-tag region for attributes. There should be no attributes in the end tag
	 * @param first the first token in the region
	 * @param region the end-tag region
	 * @param reporter the reporter
	 */
	private void checkAttributsInEndTag(Token first, List region, IReporter reporter) {
		int errors = 0;
		int start = first.offset, end = first.offset;
		final int regionLength = region.size();

		// Start at one, since we know the first token is an tag-open
		for (int i = 1; (i < regionLength) && (errors < 25); i++) {
			Token t = (Token) region.get(i);
			if ((t.type == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) || (t.type == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) || (t.type == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
				if (start == first.offset) {
					start = t.offset;
				}
				end = t.offset + t.length;
				errors++;
			}
		}

		// create one error for all attributes in the end tag
		if (errors > 0) {
			// Position p = new Position(start, end - start);
			String messageText = XMLCoreMessages.End_tag_has_attributes;
			LocalizedMessage message = new LocalizedMessage(getPluginPreference().getInt(XMLCorePreferenceNames.END_TAG_WITH_ATTRIBUTES), messageText);
			message.setOffset(start);
			message.setLength(end - start);
			message.setLineNo(first.line + 1);

			getAnnotationMsg(reporter, ProblemIDsXML.AttrsInEndTag, message, null, end-start);
			
		}
	}

	/**
	 * Checks that all the attribute in the start-tag have values and that those values are properly quoted
	 * @param region the start-tag region
	 * @param reporter the reporter
	 */
	private void checkAttributes(List region, IReporter reporter) {
		int attrState = 0;
		int errorCount = 0;
		final int regionLength = region.size();

		// Start at one, since we know the first token is an tag-open
		for (int i = 1; i < regionLength && errorCount < 25; i++) {
			Token t = (Token) region.get(i);
			if (t.type == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME || t.type == DOMRegionContext.XML_TAG_CLOSE || t.type == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
				// dangling name and '='
				if ((attrState == 2) && (i >= 2)) {
					// create annotation
					Token nameRegion = (Token) region.get(i - 2);
					Object[] args = {nameRegion.text};
					String messageText = NLS.bind(XMLCoreMessages.Attribute__is_missing_a_value, args);

					int start = nameRegion.offset;
					int end = start + nameRegion.length;

					LocalizedMessage message = new LocalizedMessage(getPluginPreference().getInt(XMLCorePreferenceNames.ATTRIBUTE_HAS_NO_VALUE) , messageText);
					message.setOffset(start);
					message.setLength(nameRegion.length);
					message.setLineNo(nameRegion.line + 1);

					// quick fix info
					Token equalsRegion = (Token) region.get(i - 2 + 1);
					int insertOffset = (equalsRegion.offset + equalsRegion.length) - end;
					Object[] additionalFixInfo = {nameRegion.text, new Integer(insertOffset)};

					getAnnotationMsg(reporter, ProblemIDsXML.MissingAttrValue, message, additionalFixInfo, nameRegion.text.length());
					errorCount++;
				}
				// name but no '=' (XML only)
				else if ((attrState == 1) && (i >= 1)) {
					// create annotation
					Token nameToken = (Token) region.get(i - 1);
					Object[] args = {nameToken.text};
					String messageText = NLS.bind(XMLCoreMessages.Attribute__has_no_value, args);
					int start = nameToken.offset;
					int textLength = nameToken.text.trim().length();
					int lineNo = nameToken.line;

					LocalizedMessage message = new LocalizedMessage(getPluginPreference().getInt(XMLCorePreferenceNames.ATTRIBUTE_HAS_NO_VALUE), messageText);
					message.setOffset(start);
					message.setLength(textLength);
					message.setLineNo(lineNo + 1);

					getAnnotationMsg(reporter, ProblemIDsXML.NoAttrValue, message, nameToken.text, textLength);
					errorCount++;
				}
				attrState = 1;
			}
			else if (t.type == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				attrState = 2;
			}
			else if (t.type == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				attrState = 0;

				// Check that there are quotes around the attribute value and that they match
				final String trimmed = t.text.trim();
				if (trimmed.length() > 0) {
					final char q1 = trimmed.charAt(0), q2 = trimmed.charAt(trimmed.length() - 1);
					if ((q1 == '\'' || q1 == '"') && (q1 != q2 || trimmed.length() == 1)) {
						// missing closing quote
						String message = XMLCoreMessages.ReconcileStepForMarkup_0;
						addAttributeError(message, t.text, t.offset, t.length, t.line, ProblemIDsXML.Unclassified, reporter, getPluginPreference().getInt(XMLCorePreferenceNames.MISSING_CLOSING_QUOTE));
						errorCount++;
					}
					else if (q1 != '\'' && q1 != '"') {
						// missing both
						String message = XMLCoreMessages.ReconcileStepForMarkup_1;
						addAttributeError(message, t.text, t.offset, t.length, t.line, ProblemIDsXML.AttrValueNotQuoted, reporter, getPluginPreference().getInt(XMLCorePreferenceNames.MISSING_CLOSING_QUOTE));
						errorCount++;
					}
				}
				
			}
		}
	}

	private void checkTokens(XMLLineTokenizer tokenizer, IReporter reporter) throws IOException {
		List previousRegion = null;
		String type = null;
		List region = null;
		boolean isClosed = true;
		while ((type = getNextToken(tokenizer)) != null) {

			Token token = new Token(type, tokenizer.yytext(), tokenizer.getOffset(), tokenizer.yylength(), tokenizer.getLine());
			isClosed = false;
			if ((type == DOMRegionContext.XML_CONTENT) || (type == DOMRegionContext.XML_CHAR_REFERENCE) || (type == DOMRegionContext.XML_ENTITY_REFERENCE) || (type == DOMRegionContext.XML_PI_OPEN) || (type == DOMRegionContext.XML_TAG_OPEN) || (type == DOMRegionContext.XML_END_TAG_OPEN) || (type == DOMRegionContext.XML_COMMENT_OPEN) || (type == DOMRegionContext.XML_CDATA_OPEN) || (type == DOMRegionContext.XML_DECLARATION_OPEN)) {
				// Validate the previous
				// Create a new Region
				previousRegion = region;
				region = new ArrayList(0);
				region.add(token);
				if (type == DOMRegionContext.XML_PI_OPEN) {
					checkContentBeforeProcessingInstruction(previousRegion, reporter);
				}
				else if (type == DOMRegionContext.XML_CONTENT) {
					checkForSpaceBeforeName(token, previousRegion, reporter);
				}
				checkForTagClose(previousRegion, reporter);

			}
			else if ((type == DOMRegionContext.XML_TAG_NAME) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) || (type == DOMRegionContext.XML_COMMENT_TEXT) || (type == DOMRegionContext.XML_PI_CONTENT) || (type == DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET)) {
				region.add(token);
			}
			else if ((type == DOMRegionContext.XML_PI_CLOSE) || (type == DOMRegionContext.XML_TAG_CLOSE) || (type == DOMRegionContext.XML_EMPTY_TAG_CLOSE) || (type == DOMRegionContext.XML_COMMENT_CLOSE) || (type == DOMRegionContext.XML_DECLARATION_CLOSE) || (type == DOMRegionContext.XML_CDATA_CLOSE)) {
				region.add(token);
				if (type == DOMRegionContext.XML_PI_CLOSE) {
					checkNamespacesInProcessingInstruction(region, reporter);
				}
				else if (type == DOMRegionContext.XML_TAG_CLOSE || type == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
					checkEmptyTag(region, reporter);
					final int regionLength = region.size();
					if (regionLength > 0) {
						Token first = (Token) region.get(0);
						if (first.type == DOMRegionContext.XML_END_TAG_OPEN) {
							checkAttributsInEndTag(first, region, reporter);
						}
						else if (first.type == DOMRegionContext.XML_TAG_OPEN) {
							checkAttributes(region, reporter);
						}
					}
				}
				isClosed = true;
			}
		}

		if (!isClosed && region != null) {
			// Check some things about the last region, just in case it wasn't properly closed
			final int regionLength = region.size();
			if (regionLength > 0) {
				final Token first = (Token) region.get(0);
				if (first.type == DOMRegionContext.XML_PI_OPEN) {
					checkNamespacesInProcessingInstruction(region, reporter);
				}
				if (first.type == DOMRegionContext.XML_TAG_OPEN) {
					checkForTagClose(region, reporter);
				}
			}
		}
	}

	/**
	 * Gets the next token from the tokenizer.
	 * @param tokenizer the XML tokenizer for the file being validated
	 * @return the next token type from the tokenizer, or null if it's at the end of the file
	 */
	private String getNextToken(XMLLineTokenizer tokenizer) {
		String token = null;
		try {
			if (!tokenizer.isEOF()) {
				token = tokenizer.primGetNextToken();
			}
		}
		catch (IOException e) {
		}
		return token;
	}

	private void validateFile(IFile file, IReporter reporter) {
		Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, file.getFullPath().toString().substring(1));
		reporter.displaySubtask(StreamingMarkupValidator.this, message);

		XMLLineTokenizer tokenizer = null;
		try {
			tokenizer = new XMLLineTokenizer(new BufferedReader(new InputStreamReader(file.getContents(true), getCharset(file))));
			checkTokens(tokenizer, reporter);
		} catch (UnsupportedEncodingException e) {
		} catch (CoreException e) {
		} catch (IOException e) {
		}
	}

	private String getCharset(IFile file) {
		if (file != null && file.isAccessible()) {
			try {
				return file.getCharset(true);
			} catch (CoreException e) {
			}
		}
		return ResourcesPlugin.getEncoding();
	}

	private Preferences getPluginPreference(){
		return XMLCorePlugin.getDefault().getPluginPreferences();
	}

	/**
	 * Creates an error related to an attribute
	 */
	private void addAttributeError(String messageText, String attributeValueText, int start, int length, int line, int problemId, IReporter reporter, int messageSeverity) {
		LocalizedMessage message = new LocalizedMessage(messageSeverity, messageText);
		message.setOffset(start);
		message.setLength(length);
		message.setLineNo(line + 1);
		getAnnotationMsg(reporter, problemId, message, attributeValueText,length);
	}
}
