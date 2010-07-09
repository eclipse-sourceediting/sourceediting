/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.validation;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jst.jsp.core.internal.validation.MarkupValidatorDelegate;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.ui.internal.validation.MarkupValidator;

public class JSPSourceValidator implements MarkupValidatorDelegate {
	
    protected MarkupValidator val = new JSPMarkupValidator();

	public void validate(IResource resource, IReporter reporter) {
		validateFile( (IFile)resource, reporter);
		updateMessages(resource, reporter);
	}
	
	private void updateMessages(IResource resource, IReporter reporter) {
		List msgList = reporter.getMessages();
		if (msgList == null)
			return;
		for (int i = 0; i < msgList.size(); i++) {
			Object msgObject = msgList.get(i);
			if (msgObject instanceof IMessage) {
				IMessage message = (IMessage)msgObject;
				message.setTargetObject(resource);
			}
		}
		
	}

	private void validateFile(IFile file, IReporter reporter) {
		Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, file.getFullPath().toString().substring(1));
		reporter.displaySubtask(val, message);
		
		IStructuredModel model = null;
		try{
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			IStructuredDocument document = null;
			if(model != null) {
				document = model.getStructuredDocument();
				val.connect(document);
				IStructuredDocumentRegion validationRegion = document.getFirstStructuredDocumentRegion();
				while (validationRegion != null) {
					val.validate(validationRegion, reporter);
					validationRegion = validationRegion.getNext();
				}
				val.disconnect(document);
			}
		}
		catch (Exception e) {
			Logger.logException(e);
		}
		
	}
}
