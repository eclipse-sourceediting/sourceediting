/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.ProposalInfo;
import org.eclipse.jdt.internal.ui.text.java.ResultCollector;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;



public class JavaTypeResultCollector extends JSPCompletionRequestor {

	protected final boolean fShowClasses = true;
	protected boolean fShowInterfaces = true;

	protected int fReplacementStart = 0;
	protected int fReplacementLength = 0;

	protected ImageDescriptorRegistry fRegistry = JavaPlugin.getImageDescriptorRegistry();

	public JavaTypeResultCollector() {
		super();
	}

	public JavaTypeResultCollector(boolean showInterfaces) {
		super();
		fShowInterfaces = showInterfaces;
	}

	protected boolean getShowClasses() {
		return fShowClasses;
	}

	protected void setShowClasses(boolean showClasses) {
	}

	protected boolean getShowInterfaces() {
		return fShowInterfaces;
	}

	protected void setShowInterfaces(boolean showInterfaces) {
		fShowInterfaces = showInterfaces;
	}

	public int getReplacementLength() {
		return fReplacementLength;
	}

	public void setReplacementLength(int replacementLength) {
		fReplacementLength = replacementLength;
	}

	public int getReplacementStart() {
		return fReplacementStart;
	}

	public void setReplacementStart(int replacementStart) {
		fReplacementStart = replacementStart;
	}

	/**
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptClass(char[],
	 *      char[], char[], int, int, int, int)
	 */
	public void acceptClass(char[] packageName, char[] className, char[] completionName, int modifiers, int completionStart, int completionEnd, int relevance) {
		if (fShowClasses)
			super.acceptClass(packageName, className, completionName, modifiers, completionStart, completionEnd, relevance);
	}

	/**
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptInterface(char[], char[], char[], int,
	 *      int, int, int)
	 */
	public void acceptInterface(char[] packageName, char[] interfaceName, char[] completionName, int modifiers, int completionStart, int completionEnd, int relevance) {
		if (fShowInterfaces)
			super.acceptInterface(packageName, interfaceName, completionName, modifiers, completionStart, completionEnd, relevance);
	}

	/**
	 * @see ResultCollector#createTypeCompletion(int, int, String,
	 *      ImageDescriptor, String, String, ProposalInfo, int)
	 */
	protected JavaCompletionProposal createTypeCompletion(int start, int end, String completion, ImageDescriptor descriptor, String typeName, String containerName, ProposalInfo proposalInfo, int relevance) {
		String qualifiedName = null;
		if (containerName != null && containerName.length() > 0)
			qualifiedName = containerName + "." + typeName; //$NON-NLS-1$
		else
			qualifiedName = typeName;
		Image image = null;
		if (descriptor != null)
			image = fRegistry.get(descriptor);
		return new JavaTypeCompletionProposal("\"" + qualifiedName + "\"", fReplacementStart, fReplacementLength, qualifiedName, image, typeName, containerName, null, relevance); //$NON-NLS-1$ //$NON-NLS-2$
		//	return super.createTypeCompletion(start, end, completion,
		// descriptor, typeName, containerName, proposalInfo, relevance);
	}

}