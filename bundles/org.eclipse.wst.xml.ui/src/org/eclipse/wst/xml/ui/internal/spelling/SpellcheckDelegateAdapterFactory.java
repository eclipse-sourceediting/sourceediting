/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.spelling;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.spelling.ISpellcheckDelegate;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;

/**
 * An <code>IAdapterFactory</code> to adapt an <code>IDOMModel</code> to a
 * <code>ISpellcheckDelegate</code>.
 */
public class SpellcheckDelegateAdapterFactory implements IAdapterFactory {
	/**
	 * This list of classes this factory adapts to.
	 */
	private static final Class[] ADAPTER_LIST = new Class[]{ISpellcheckDelegate.class};
	
	/**
	 * The <code>ISpellcheckDelegate</code> this factory adapts to
	 */
	private static final ISpellcheckDelegate DELEGATE = new XMLSpellcheckDelegate();
	
	/**
	 * Implementation of <code>ISpellcheckDelegate</code> for XML type documents.
	 */
	private static class XMLSpellcheckDelegate implements ISpellcheckDelegate {
		
		/**
		 * If the region in the given <code>model</code> at the given <code>offset</code> is a
		 * <code>Comment</code> region then it should be spell checked, otherwise it should not.
		 * 
		 * @see org.eclipse.wst.sse.ui.internal.spelling.ISpellcheckDelegate#shouldSpellcheck(org.eclipse.wst.sse.core.internal.provisional.IndexedRegion)
		 */
		public boolean shouldSpellcheck(int offset, IStructuredModel model) {
			boolean shouldSpellcheck = true;
			
			IndexedRegion region = model.getIndexedRegion(offset);
			if(region != null) {
				shouldSpellcheck = (region instanceof Comment || region instanceof Text);
			}
			
			return shouldSpellcheck;
		}
		
	}
	
	/** 
	 * Adapts <code>IDOMModel</code> to <code>ISpellcheckDelegate</code>.
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		ISpellcheckDelegate decision = null;
		
		if (adaptableObject instanceof IDOMModel && ISpellcheckDelegate.class.equals(adapterType)) {
			decision = DELEGATE;
		}
		
		return decision;
	}

	/**
	 *  This adapter only adapts to <code>ISpellcheckDelegate</code>
	 *  
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
