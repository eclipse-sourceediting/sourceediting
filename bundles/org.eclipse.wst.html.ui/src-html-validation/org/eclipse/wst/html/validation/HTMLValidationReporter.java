package org.eclipse.wst.html.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.validate.ValidationMessage;
import org.eclipse.wst.sse.core.validate.ValidationReporter;
import org.eclipse.wst.validation.core.IMessage;
import org.eclipse.wst.validation.core.IReporter;
import org.eclipse.wst.validation.core.IValidator;

public class HTMLValidationReporter implements ValidationReporter {

	private IValidator owner = null;
	private IReporter reporter = null;
	private IFile file = null;
	private IStructuredModel model = null;
	private HTMLValidationResult result = null;

	/**
	 */
	public HTMLValidationReporter(IValidator owner, IReporter reporter, IFile file, IStructuredModel model) {
		super();
		this.owner = owner;
		this.reporter = reporter;
		this.file = file;
		this.model = model;
	}

	/**
	 */
	public void clear() {
		if (this.file == null)
			return;

		this.result = null;

		if (this.reporter != null) {
			this.reporter.removeAllMessages(this.owner, this.file);
		} else {
			// remove by myself?
			String id = HTMLValidator.class.getName();
			try {
				// TaskListHelper API changed
				//TaskListHelper.getTaskList().removeAllTasks(id, this.file, null);
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
		if (message == null)
			return;
		if (this.file == null)
			return;

		IMessage mes = translateMessage(message);

		if (this.reporter != null) {
			this.reporter.addMessage(this.owner, mes);
		} else {
			// add by myself?
			String id = HTMLValidator.class.getName();
			String location = Integer.toString(mes.getLineNumber());
			String name = this.file.getFullPath().toString();
			try {
				TaskListHelper.getTaskList().addTask(id, this.file, location,
					mes.getId(), mes.getText(), mes.getSeverity(),
					name, mes.getGroupName(), mes.getOffset(), mes.getLength());
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
			default :
				result.addInformation();
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
}