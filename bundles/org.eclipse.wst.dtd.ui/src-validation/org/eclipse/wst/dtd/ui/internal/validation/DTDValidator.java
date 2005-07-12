/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.ui.internal.validation;


import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IRuleGroup;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;


public class DTDValidator implements IValidator {
	private final String GET_FILE = "getFile"; //$NON-NLS-1$
	public final String GET_PROJECT_FILES = "getAllFiles"; //$NON-NLS-1$

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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wtp.validation.core.IValidator#cleanup(org.eclipse.wtp.validation.core.IReporter)
	 */
	public void cleanup(IReporter reporter) {
	}

	public void validate(IFile file) {
		ValidateDTDAction validateAction = new ValidateDTDAction(file, false);
		validateAction.setValidator(this);
		validateAction.run();
	}

	public void validate(IFile file, IReporter reporter, int ruleGroup) {
		ValidateDTDAction validateAction = new ValidateDTDAction(file, false);
		validateAction.setValidator(this);
		validateAction.run();
	}

	/**
	 * This is the method which performs the validation on the MOF model. <br>
	 * <br>
	 * <code>context</code> and <code>reporter</code> may not be null.
	 * <code>changedFiles</code> may be null, if a full build is desired.
	 * <br>
	 * <br>
	 * <code>helper</code> returns the ifile for the given information in
	 * the IFileDelta array <br>
	 * <br>
	 * <code>reporter</code> is an instance of an IReporter interface, which
	 * is used for interaction with the user. <br>
	 * <br>
	 * <code>changedFiles</code> is an array of file names which have
	 * changed since the last validation. If <code>changedFiles</code> is
	 * null, or if it is an empty array, then a full build is performed.
	 * Otherwise, validation on just the files listed in the Vector is
	 * performed.
	 */
	public void validate(IValidationContext context, IReporter reporter) throws ValidationException {
		String[] changedFiles = context.getURIs();
		if (changedFiles != null && changedFiles.length > 0) {
			for (int i = 0; i < changedFiles.length; i++) {
				String changedFileName = changedFiles[i];
				if (changedFileName != null) {
					Object[] parms = {changedFileName};

					IFile file = (IFile) context.loadModel(GET_FILE, parms);
					if (file != null && shouldValidate(file)) {
						validateIfNeeded(file, context, reporter);
					}
				}
			}
		}
		else {
			Object[] parms = {this.getClass().getName()};
			Collection files = (Collection) context.loadModel(GET_PROJECT_FILES, parms);
			Iterator iter = files.iterator();
			while (iter.hasNext()) {
				IFile file = (IFile) iter.next();
				if(shouldValidate(file)) {
					validateIfNeeded(file, context, reporter);
				}
			}
		}
	}


	protected void validateIfNeeded(IFile file, IValidationContext context, IReporter reporter) {
		Integer ruleGroupInt = (Integer) context.loadModel(IRuleGroup.PASS_LEVEL, null);
		/*
		 * pass in a "null" so that loadModel doesn't attempt to cast the
		 * result into a RefObject
		 */
		int ruleGroup = (ruleGroupInt == null) ? IRuleGroup.PASS_FULL : ruleGroupInt.intValue();

		validate(file, reporter, ruleGroup);
	}
}
