package org.eclipse.jst.jsp.core.internal.validation;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

public interface MarkupValidatorDelegate {
	void validate(IResource resource, IReporter reporter);
}
