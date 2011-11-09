/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.core.internal.validate.MessageFactory;
import org.eclipse.wst.html.core.internal.validation.HTMLValidator;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.validate.ErrorInfo;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.sse.core.internal.validate.ValidationReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

public class HTMLValidationReporter implements ValidationReporter {

	private IValidator owner = null;
	private IReporter reporter = null;
	private IFile file = null;
	private IStructuredModel model = null;
	private HTMLValidationResult result = null;
	private MessageFactory fFactory = null;
	
	/**
	 */
	public HTMLValidationReporter(IValidator owner, IReporter reporter, IFile file, IStructuredModel model) {
		super();
		this.owner = owner;
		this.reporter = reporter;
		this.file = file;
		this.model = model;
		fFactory = new MessageFactory(file != null ? file.getProject() : null);
	}

	/**
	 */
	public void clear() {
		if (this.file == null)
			return;

		this.result = null;

		if (this.reporter != null) {
			this.reporter.removeAllMessages(this.owner, this.file);
		}
		else {
			// remove by myself?
			String id = HTMLValidator.class.getName();
			try {
				// TaskListHelper API changed
				// TaskListHelper.getTaskList().removeAllTasks(id, this.file,
				// null);
				TaskListHelper.getTaskList().removeAllTasks(this.file, id, null);
			}
			catch (CoreException ex) {
			}
		}
	}


	/**
	 */
	public HTMLValidationResult getResult() {
		if (this.result == null)
			this.result = new HTMLValidationResult();
		return this.result;
	}

	/**
	 */
	public void report(ValidationMessage message) {
		if (message == null || message.getSeverity() == ValidationMessage.IGNORE)
			return;
		IMessage mes = translateMessage(message);

		if (this.reporter != null) {
			this.reporter.addMessage(this.owner, mes);
		}
		else {
			if (this.file == null)
				return;

			// add by myself?
			String id = HTMLValidator.class.getName();
			String location = Integer.toString(mes.getLineNumber());
			String name = ""; //$NON-NLS-1$
			IPath filePath = this.file.getFullPath();
			if (filePath != null) {
				name = filePath.toString();
			}
			try {
				TaskListHelper.getTaskList().addTask(id, this.file, location, mes.getId(), mes.getText(), mes.getSeverity(), name, mes.getGroupName(), mes.getOffset(), mes.getLength());
			}
			catch (CoreException ex) {
			}
		}
	}

	/**
	 * Translate ValidationMessage to IMessage and generate result log
	 */
	private IMessage translateMessage(ValidationMessage message) {
		int severity = IMessage.LOW_SEVERITY;
		HTMLValidationResult result = getResult();
		switch (message.getSeverity()) {
			case ValidationMessage.ERROR :
				severity = IMessage.HIGH_SEVERITY;
				result.addError();
				break;
			case ValidationMessage.WARNING :
				severity = IMessage.NORMAL_SEVERITY;
				result.addWarning();
				break;
			case ValidationMessage.INFORMATION :
				result.addInformation();
				break;
			default :
//				result.addInformation();
				break;
		}

		IMessage mes = new LocalizedMessage(severity, message.getMessage(), this.file);
		mes.setOffset(message.getOffset());
		mes.setLength(message.getLength());
		if (this.model != null) {
			IStructuredDocument flatModel = this.model.getStructuredDocument();
			if (flatModel != null) {
				int line = flatModel.getLineOfOffset(message.getOffset());
				mes.setLineNo(line + 1);
			}
		}

		return mes;
	}

	public void report(ErrorInfo info) {
		report(fFactory.createMessage(info));
	}
}