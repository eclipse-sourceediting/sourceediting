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
package org.eclipse.wst.xml.ui.internal.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.ui.wizards.ExampleProjectCreationWizard;

/**
 * Wizard used for creating the HelloWord sample.
 * Most functionality is inherited from ExampleProjectCreationWizard.
 */
public class XMLExampleProjectCreationWizard extends ExampleProjectCreationWizard {

	public XMLExampleProjectCreationWizard() {
		super();
		
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.samplegallery.wizards.ExampleProjectCreationWizard#getImageDescriptor(java.lang.String)
	 */
	protected ImageDescriptor getImageDescriptor(String banner) {
		return XMLWizard.getInstance().getImageDescriptor(banner);
	}
}
