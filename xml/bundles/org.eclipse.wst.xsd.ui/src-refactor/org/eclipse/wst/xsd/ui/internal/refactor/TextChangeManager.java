/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;

/**
 * A <code>TextChangeManager</code> manages associations between <code>IFile</code> and <code>TextChange</code> objects.
 */
public class TextChangeManager {
	
	private Map fMap= new HashMap(10); // IFile -> TextChange
	
	private final boolean fKeepExecutedTextEdits;
	
	public TextChangeManager() {
		this(false);
	}

	public TextChangeManager(boolean keepExecutedTextEdits) {
		fKeepExecutedTextEdits= keepExecutedTextEdits;
	}
	
	/**
	 * Adds an association between the given file and the passed
	 * change to this manager.
	 * 
	 * @param file the file (key)
	 * @param change the change associated with the file
	 */
	public void manage(IFile file, TextChange change) {
		fMap.put(file, change);
	}
	
	/**
	 * Returns the <code>TextChange</code> associated with the given file.
	 * If the manager does not already manage an association it creates a one.
	 * 
	 * @param file the file for which the text buffer change is requested
	 * @return the text change associated with the given file. 
	 */
	public TextChange get(IFile file) {
		TextChange result= (TextChange)fMap.get(file);
		if (result == null) {
			result= new TextFileChange(file.toString(), file);
			result.setKeepPreviewEdits(fKeepExecutedTextEdits);
			result.initializeValidationData(new NullProgressMonitor());
			fMap.put(file, result);
		}
		return result;
	}
	
	/**
	 * Removes the <tt>TextChange</tt> managed under the given key
	 * <code>unit<code>.
	 * 
	 * @param unit the key determining the <tt>TextChange</tt> to be removed.
	 * @return the removed <tt>TextChange</tt>.
	 */
	public TextChange remove(IFile unit) {
		return (TextChange)fMap.remove(unit);
	}
	
	/**
	 * Returns all text changes managed by this instance.
	 * 
	 * @return all text changes managed by this instance
	 */
	public TextChange[] getAllChanges(){
		return (TextChange[])fMap.values().toArray(new TextChange[fMap.values().size()]);
	}

	/**
	 * Returns all files managed by this instance.
	 * 
	 * @return all files managed by this instance
	 */	
	public IFile[] getAllCompilationUnits(){
		return (IFile[]) fMap.keySet().toArray(new IFile[fMap.keySet().size()]);
	}
	
	/**
	 * Clears all associations between resources and text changes.
	 */
	public void clear() {
		fMap.clear();
	}

	/**
	 * Returns if any text changes are managed for the specified file.
	 * 
	 * @param file the file
	 * @return <code>true</code> if any text changes are managed for the specified file and <code>false</code> otherwise
	 */		
	public boolean containsChangesIn(IFile file){
		return fMap.containsKey(file);
	}
}


