/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.contentassist;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

/**
 * <p>Content assistant that uses {@link CompoundContentAssistProcessor}s so that multiple
 * processors can be registered for each partition type</p>
 */
public class StructuredContentAssistant extends ContentAssistant {
	/** 
	 * <code>true</code> if a content assist processor has been added to this assistant,
	 * <code>false</code> otherwise
	 */
	private boolean fIsInitalized;
	
	/**
	 * <p>Construct the assistant</p>
	 */
	public StructuredContentAssistant() {
		this.fIsInitalized = false;
	}
	
	/**
	 * <p>Each set content assist processor is placed inside a
	 * CompoundContentAssistProcessor which allows multiple processors per
	 * partition type</p>
	 * 
	 * @param processor
	 *            the content assist processor to register, or
	 *            <code>null</code> to remove an existing one
	 * @param contentType
	 *            the content type under which to register
	 *            
	 * @see org.eclipse.jface.text.contentassist.ContentAssistant#setContentAssistProcessor(org.eclipse.jface.text.contentassist.IContentAssistProcessor, java.lang.String)
	 */
	public void setContentAssistProcessor(IContentAssistProcessor processor, String partitionType) {
		this.fIsInitalized = true;
		
		CompoundContentAssistProcessor compoundProcessor = getExistingContentAssistProcessor(partitionType);
		if(compoundProcessor == null) {
			compoundProcessor = new CompoundContentAssistProcessor();
		}
		
		compoundProcessor.add(processor);
		super.setContentAssistProcessor(compoundProcessor, partitionType);
	}
	
	/**
	 * Returns true if content assist has been initialized with some content
	 * assist processors. False otherwise.
	 * 
	 * @return true if content assistant has been initialized
	 */
	public boolean isInitialized() {
		return this.fIsInitalized;
	}
	
	/**
	 * 
	 * @param partitionType
	 * @return
	 */
	private CompoundContentAssistProcessor getExistingContentAssistProcessor(String partitionType) {
		CompoundContentAssistProcessor compoundContentAssistProcessor = null;
		IContentAssistProcessor processor = super.getContentAssistProcessor(partitionType);
		if (processor != null) {
			if (processor instanceof CompoundContentAssistProcessor) {
				compoundContentAssistProcessor = (CompoundContentAssistProcessor) processor;
			}
		}
		return compoundContentAssistProcessor;
	}
}