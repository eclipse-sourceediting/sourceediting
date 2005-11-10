/*******************************************************************************
 * Copyright (c) 2005 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.domdocument.DOMModelForJSP;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParser;
import org.eclipse.jst.jsp.core.internal.java.jspel.ParseException;
import org.eclipse.jst.jsp.core.internal.java.jspel.Token;
import org.eclipse.jst.jsp.core.internal.java.jspel.TokenMgrError;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


public class JSPELValidator implements IValidator {

	static private class LocalizedMessage extends Message {
		private String _message = null;

		public LocalizedMessage(int severity, String messageText) {
			this(severity, messageText, null);
		}

		public LocalizedMessage(int severity, String messageText, IResource targetObject) {
			this(severity, messageText, (Object) targetObject);
		}

		public LocalizedMessage(int severity, String messageText, Object targetObject) {
			super(null, severity, null);
			setLocalizedMessage(messageText);
			setTargetObject(targetObject);
		}

		public void setLocalizedMessage(String message) {
			_message = message;
		}

		public String getLocalizedMessage() {
			return _message;
		}

		public String getText() {
			return getLocalizedMessage();
		}

		public String getText(ClassLoader cl) {
			return getLocalizedMessage();
		}

		public String getText(Locale l) {
			return getLocalizedMessage();
		}

		public String getText(Locale l, ClassLoader cl) {
			return getLocalizedMessage();
		}
	}

	static boolean shouldValidate(IFile file) {
		IResource resource = file;
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || resource.getName().charAt(0) == '.') {
				return false;
			}
			resource = resource.getParent();
		}
		while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

	/**
	 * Determines if file is jsp fragment or not
	 * 
	 * @param file
	 *            assumes file is not null and exists
	 * @return true if file is jsp fragment, false otherwise
	 */
	private boolean isFragment(IFile file) {
		boolean isFragment = false;
		InputStream is = null;
		try {
			IContentType contentTypeJSP = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT);
			// check this before description, it's less expensive
			if (contentTypeJSP.isAssociatedWith(file.getName())) {

				IContentDescription contentDescription = file.getContentDescription();
				// it can be null
				if (contentDescription == null) {
					is = file.getContents();
					contentDescription = Platform.getContentTypeManager().getDescriptionFor(is, file.getName(), new QualifiedName[]{IContentDescription.CHARSET});
				}
				if (contentDescription != null) {
					String fileCtId = contentDescription.getContentType().getId();
					isFragment = (fileCtId != null && ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT.equals(fileCtId));
				}
			}
		}
		catch (IOException e) {
			// ignore, assume it's invalid JSP
		}
		catch (CoreException e) {
			// ignore, assume it's invalid JSP
		}
		finally {
			// must close input stream in case others need it
			if (is != null)
				try {
					is.close();
				}
				catch (Exception e) {
					// not sure how to recover at this point
				}
		}
		return isFragment;
	}

	/**
	 * Performs extra checks on the file to see if file should really be
	 * validated.
	 * 
	 * @param file
	 *            Assumes shouldValidate was already called on file so it
	 *            should not be null and does exist
	 * @return true if should validate file, false otherwise
	 */
	private boolean shouldValidate2(IFile file) {
		// get preference for validate jsp fragments
		boolean shouldValidate = Platform.getPreferencesService().getBoolean(JSPCorePlugin.getDefault().getBundle().getSymbolicName(), JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true, null);

		/*
		 * if jsp fragments should not be validated, check if file is jsp
		 * fragment
		 */
		if (!shouldValidate) {
			boolean isFragment = isFragment(file);
			shouldValidate = !isFragment;
		}

		return shouldValidate;
	}

	protected void validateRegionContainer(ITextRegionCollection container, IReporter reporter, IFile file) {

		ITextRegionCollection containerRegion = container;
		Iterator regions = containerRegion.getRegions().iterator();
		ITextRegion region = null;
		while (regions.hasNext() && !reporter.isCancelled()) {
			region = (ITextRegion) regions.next();
			String type = region.getType();
			if (type != null && region instanceof ITextRegionCollection) {
				ITextRegionCollection parentRegion = ((ITextRegionCollection) region);
				Iterator childRegions = parentRegion.getRegions().iterator();
				while (childRegions.hasNext() && !reporter.isCancelled()) {
					ITextRegion childRegion = (ITextRegion) childRegions.next();
					if (childRegion.getType() == DOMJSPRegionContexts.JSP_EL_CONTENT)
						validateXMLNode(parentRegion, childRegion, reporter, file);
				}
			}
		}
	}

	protected void validateXMLNode(ITextRegionCollection container, ITextRegion region, IReporter reporter, IFile file) {
		String elText = container.getText(region);
		JSPELParser elParser = JSPELParser.createParser(elText);
		int contentStart = container.getStartOffset(region);
		int contentLength = container.getLength();
		try {
			elParser.Expression();
		}
		catch (ParseException e) {
			Token curTok = e.currentToken;
			int problemStartOffset = contentStart + curTok.beginColumn;
			Message message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, JSPCoreMessages.JSPEL_Syntax);
			message.setOffset(problemStartOffset);
			message.setLength(curTok.endColumn - curTok.beginColumn + 1);
			message.setTargetObject(file);
			reporter.addMessage(this, message);
		}
		catch (TokenMgrError te) {
			Message message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, JSPCoreMessages.JSPEL_Token);
			message.setOffset(contentStart);
			message.setLength(contentLength);
			message.setTargetObject(file);
			reporter.addMessage(this, message);
		}
	}

	public void cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter reporter) {
		// TODO Auto-generated method stub

	}

	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		String[] uris = helper.getURIs();
		reporter.removeAllMessages(this);
		// IValidationContext may return a null array
		if (uris != null) {
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uris[i]));
				if (!shouldValidate(file)) {
					continue;
				}
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=87351
				if (!shouldValidate2(file)) {
					continue;
				}

			    Message message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, NLS.bind(JSPCoreMessages.MESSAGE_JSP_VALIDATING_MESSAGE_UI_, new String[]{file.getFullPath().toString()}));
			    reporter.displaySubtask(this, message);
				
				IStructuredModel model = null;
				try {
					model = StructuredModelManager.getModelManager().getModelForRead(file);
					DOMModelForJSP jspModel = (DOMModelForJSP) model;
					IStructuredDocument structuredDoc = jspModel.getStructuredDocument();
					IStructuredDocumentRegion curNode = structuredDoc.getFirstStructuredDocumentRegion();
					while (null != (curNode = curNode.getNext()) && !reporter.isCancelled()) {
						if (curNode.getType() != DOMRegionContext.XML_COMMENT_TEXT && curNode.getType() != DOMRegionContext.XML_CDATA_TEXT && curNode.getType() != DOMRegionContext.UNDEFINED) {
							validateRegionContainer(curNode, reporter, file);
						}
					}
				}
				catch (Exception e) {
				}
				finally {
					if (null != model)
						model.releaseFromRead();
				}
			}
		}
	}
}
