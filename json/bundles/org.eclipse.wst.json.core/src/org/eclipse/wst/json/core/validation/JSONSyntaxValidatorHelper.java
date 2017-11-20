/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.validation;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.json.core.internal.JSONCoreMessages;
import org.eclipse.wst.json.core.internal.parser.JSONLineTokenizer;
import org.eclipse.wst.json.core.internal.validation.ProblemIDsJSON;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.core.util.JSONUtil;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

public class JSONSyntaxValidatorHelper {

	/**
	 * The error threshold - sometimes, after you get so many errors, it's not
	 * worth seeing the others
	 */
	private static final int ERROR_THRESHOLD = 25;

	/**
	 * A token from the tokenizer
	 */
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

		@Override
		public String toString() {
			return new StringBuilder(type).append("[").append(line).append("-")
					.append(offset).append("]: ").append(text).toString();
		}
	}

	public static void validate(JSONLineTokenizer tokenizer,
			IReporter reporter, IValidator validator, ISeverityProvider provider) {
		List previousRegions = null;
		String type = null;
		Stack<Token> tagStack = new Stack<Token>();
		List<Token> region = null;
		boolean isClosed = true;
		int tagErrorCount = 0;
		Token previousRegion = null;
		while ((type = getNextToken(tokenizer)) != null) {
			// System.err.println(type);
			Token token = new Token(type, tokenizer.yytext(),
					tokenizer.getOffset(), tokenizer.yylength(),
					tokenizer.getLine());
			isClosed = false;
			boolean hasError = checkExpectedRegion(token, previousRegion,
					tagStack, reporter, validator, provider);
			if (type == JSONRegionContexts.JSON_OBJECT_OPEN
					|| type == JSONRegionContexts.JSON_ARRAY_OPEN) {
				tagStack.push(token);
			} else if (type == JSONRegionContexts.JSON_OBJECT_CLOSE) {
				if (tagStack.isEmpty()) {
					createMissingTagError(token, false, reporter,
							tagErrorCount, tagStack, validator, provider);
				} else {
					Token lastToken = tagStack.peek();
					if (lastToken.type == JSONRegionContexts.JSON_OBJECT_OPEN) {
						tagStack.pop();
					}
				}
			} else if (type == JSONRegionContexts.JSON_ARRAY_CLOSE) {
				if (tagStack.isEmpty()) {
					createMissingTagError(token, false, reporter,
							tagErrorCount, tagStack, validator, provider);
				} else {
					Token lastToken = tagStack.peek();
					if (lastToken.type == JSONRegionContexts.JSON_ARRAY_OPEN) {
						if (!tagStack.isEmpty())
							tagStack.pop();
					}
				}
			} else if (type.equalsIgnoreCase(JSONRegionContexts.UNDEFINED)) {
				if ("{".equals(token.text)) {
					tagStack.push(token);
				} else if ("}".equals(token.text)) {
					if (!tagStack.isEmpty()) {
						tagStack.pop();
					}
				} else if ("[".equals(token.text)) {
					tagStack.push(token);
				} else if ("]".equals(token.text)) {
					if (!tagStack.isEmpty()) {
						tagStack.pop();
					}
				} else {
					if (!hasError) {
						String messageText = "Unexpected token";
						LocalizedMessage message = createMessage(token,
								messageText,
								JSONCorePreferenceNames.MISSING_BRACKET,
								provider);
						getAnnotationMsg(reporter,
								ProblemIDsJSON.MissingEndBracket, message,
								null, token.length, validator);
					}
				}
			}
			/*
			 * else if (check &&
			 * type.equalsIgnoreCase(JSONRegionContexts.UNDEFINED)) { String
			 * messageText = "Unexpected token"; LocalizedMessage message =
			 * createMessage(token, messageText,
			 * JSONCorePreferenceNames.MISSING_BRACKET, provider);
			 * getAnnotationMsg(reporter, ProblemIDsJSON.MissingEndBracket,
			 * message, null, token.length, validator);
			 * 
			 * }
			 */
			if (!isIgnoreRegion(type)) {
				previousRegion = token;
			}
		}

		if (!tagStack.isEmpty()) {
			while (!tagStack.isEmpty()) {
				createMissingTagError(tagStack.pop(), true, reporter,
						tagErrorCount, tagStack, validator, provider);
			}
		}
	}

	private static boolean isIgnoreRegion(String type) {
		return type == JSONRegionContexts.JSON_COMMENT
				|| type == JSONRegionContexts.WHITE_SPACE
				|| type.equalsIgnoreCase(JSONRegionContexts.UNDEFINED);
	}

	private static boolean checkExpectedRegion(Token current, Token previous,
			Stack<Token> tagStack, IReporter reporter, IValidator validator,
			ISeverityProvider provider) {
		if (previous == null || isIgnoreRegion(current.type)) {
			return false;
		}
		if (previous.type == JSONRegionContexts.JSON_OBJECT_OPEN) {
			if (current.type != JSONRegionContexts.JSON_OBJECT_CLOSE
					&& current.type != JSONRegionContexts.JSON_OBJECT_KEY) {
				String messageText = "Expected object key but found "
						+ current.type;
				LocalizedMessage message = createMessage(current, messageText,
						JSONCorePreferenceNames.MISSING_BRACKET, provider);
				getAnnotationMsg(reporter, ProblemIDsJSON.MissingEndBracket,
						message, null, current.length, validator);
				return true;
			}
		} else if (previous.type == JSONRegionContexts.JSON_OBJECT_KEY) {
			if (current.type != JSONRegionContexts.JSON_COLON) {
				String messageText = "Expected colon but found " + current.type;
				LocalizedMessage message = createMessage(current, messageText,
						JSONCorePreferenceNames.MISSING_BRACKET, provider);
				getAnnotationMsg(reporter, ProblemIDsJSON.MissingEndBracket,
						message, null, current.length, validator);
				return true;
			}
		} else if (previous.type == JSONRegionContexts.JSON_COLON) {
			if (!JSONUtil.isJSONSimpleValue(current.type)
					&& !JSONUtil.isStartJSONStructure(current.type)) {
				String messageText = "Expected JSON value but found "
						+ current.type;
				LocalizedMessage message = createMessage(current, messageText,
						JSONCorePreferenceNames.MISSING_BRACKET, provider);
				getAnnotationMsg(reporter, ProblemIDsJSON.MissingEndBracket,
						message, null, current.length, validator);
				return true;
			}
		} else if (previous.type == JSONRegionContexts.JSON_COMMA) {
			if (tagStack.isEmpty()) {
				String messageText = "Unexpected comma";
				LocalizedMessage message = createMessage(current, messageText,
						JSONCorePreferenceNames.MISSING_BRACKET, provider);
				getAnnotationMsg(reporter, ProblemIDsJSON.MissingEndBracket,
						message, null, current.length, validator);
				return true;
			} else {
				if (tagStack.peek().type == JSONRegionContexts.JSON_ARRAY_OPEN) {
					// inside array, previous token must be a JSON value
					if (!JSONUtil.isJSONSimpleValue(current.type)
							&& current.type != JSONRegionContexts.JSON_ARRAY_OPEN
							&& current.type != JSONRegionContexts.JSON_OBJECT_OPEN) {
						String messageText = "Expected JSON value but found "
								+ current.type;
						LocalizedMessage message = createMessage(current,
								messageText,
								JSONCorePreferenceNames.MISSING_BRACKET,
								provider);
						getAnnotationMsg(reporter,
								ProblemIDsJSON.MissingEndBracket, message,
								null, current.length, validator);
						return true;
					}
				} else {
					// inside object, previous token must be a JSON key
					if (current.type != JSONRegionContexts.JSON_OBJECT_KEY) {
						String messageText = "Expected JSON key but found "
								+ current.type;
						LocalizedMessage message = createMessage(current,
								messageText,
								JSONCorePreferenceNames.MISSING_BRACKET,
								provider);
						getAnnotationMsg(reporter,
								ProblemIDsJSON.MissingEndBracket, message,
								null, current.length, validator);
						return true;
					}
				}
			}
		}
		return false;
	}

	private static void createMissingTagError(Token token, boolean isStartTag,
			IReporter reporter, int tagErrorCount, Stack<Token> tagStack,
			IValidator validator, ISeverityProvider provider) {
		boolean isArray = (token.type == JSONRegionContexts.JSON_ARRAY_OPEN || token.type == JSONRegionContexts.JSON_ARRAY_CLOSE);
		Object[] args = { token.text };
		String messageText = NLS.bind(getMessage(isStartTag, isArray), args);

		LocalizedMessage message = createMessage(token, messageText,
				JSONCorePreferenceNames.MISSING_BRACKET, provider);

		Object fixInfo = /*
						 * isStartTag ? (Object) getStartEndFixInfo(token.text,
						 * token) :
						 */token.text;
		getAnnotationMsg(reporter,
				isStartTag ? ProblemIDsJSON.MissingEndBracket
						: ProblemIDsJSON.MissingStartBracket, message, fixInfo,
				token.length, validator);

		if (++tagErrorCount > ERROR_THRESHOLD) {
			tagStack.clear();
		}
	}

	private static LocalizedMessage createMessage(Token token,
			String messageText, String severityPreference,
			ISeverityProvider provider) {
		LocalizedMessage message = new LocalizedMessage(
				provider.getSeverity(severityPreference), messageText);
		message.setOffset(token.offset);
		message.setLength(token.length);
		message.setLineNo(getLine(token));
		return message;
	}

	private static void getAnnotationMsg(IReporter reporter, int problemId,
			LocalizedMessage message, Object attributeValueText, int len,
			IValidator validator) {
		AnnotationMsg annotation = new AnnotationMsg(problemId,
				attributeValueText, len);
		message.setAttribute(AnnotationMsg.ID, annotation);
		reporter.addMessage(validator, message);
	}

	private static String getMessage(boolean isStartTag, boolean isArray) {
		if (isArray) {
			return isStartTag ? JSONCoreMessages.Missing_end_array
					: JSONCoreMessages.Missing_start_array;
		}
		return isStartTag ? JSONCoreMessages.Missing_end_object
				: JSONCoreMessages.Missing_start_object;
	}

	/**
	 * Gets the line number for a token
	 * 
	 * @param token
	 *            the token to find the line of
	 * @return the line in the document where the token can be found
	 */
	private static int getLine(Token token) {
		return token.line + 1;
	}

	/**
	 * Gets the next token from the tokenizer.
	 * 
	 * @param tokenizer
	 *            the JSON tokenizer for the file being validated
	 * @return the next token type from the tokenizer, or null if it's at the
	 *         end of the file
	 */
	private static String getNextToken(JSONLineTokenizer tokenizer) {
		String token = null;
		try {
			if (!tokenizer.isEOF()) {
				token = tokenizer.primGetNextToken();
			}
		} catch (IOException e) {
		}
		return token;
	}
}
