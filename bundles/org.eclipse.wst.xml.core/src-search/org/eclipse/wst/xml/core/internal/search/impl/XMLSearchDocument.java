/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.search.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.core.search.document.Entry;
import org.eclipse.wst.common.core.search.document.SearchDocument;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.search.XMLSearchParticipant;

public class XMLSearchDocument extends SearchDocument {
	
	IDOMModel model;
	Map entries = new HashMap(); // category -> set (entry)	

	public XMLSearchDocument(String documentPath, XMLSearchParticipant participant) {
		super(documentPath, participant);
	}

	public Object getModel() {
		if(model == null){
            //System.out.println("creating DOM for " + getPath());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(getPath()));
			if(file != null){
				try {
					model = (IDOMModel)StructuredModelManager.getModelManager().getModelForEdit(file);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			
			}
		}
		return model;
	}

	public Entry[] getEntries(String category, String key, int matchRule)
	{
		// TODO use matchRule
		Set results = new HashSet();
		if(category != null){
			Set values = (Set)entries.get(category);
			if(values == null){
				return new Entry[0];
			}
			if(key == null || "".equals(key) || "*".equals(key)){ //$NON-NLS-1$ //$NON-NLS-2$
				// entries with any key in the given category
				results.addAll(values);
			}
			else{
				// entries with the specified key in the given category
				for (Iterator iter = values.iterator(); iter.hasNext();)
				{
					Entry entry = (Entry) iter.next();
					if(key.equals(entry.getKey())){
						results.add(entry);
					}
				}
			}
			
		}
		return (Entry[]) results.toArray(new Entry[results.size()]);
	}

	public void putEntry(Entry entry)
	{
		if(entry.getCategory() != null){
			Set values = (Set)entries.get(entry.getCategory());
			if(values == null){
				entries.put(entry.getCategory(), values=new HashSet());
			}
			values.add(entry);
		}
		
	}

    public void dispose()
    {     
      if (model != null)
      {  
        model.releaseFromEdit();        
      }  
    }
}
