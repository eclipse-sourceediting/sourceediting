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
 * Wizard used for creating the XML samples.
 * Most functionality is inherited from ExampleProjectCreationWizard.
 */
public class XMLExampleProjectCreationWizard extends ExampleProjectCreationWizard {
	
	public static String EXAMPLE_WIZARD_XP_ID = "org.eclipse.wst.xml.ui.XMLExampleProjectCreationWizardExtension";  //$NON-NLS-1$


	public XMLExampleProjectCreationWizard() {
		super();
		
	}
	

	protected ImageDescriptor getImageDescriptor(String banner) {
		return XMLWizard.getInstance().getImageDescriptor(banner);
	}
	
	public String getWizardExtensionId(){
		return EXAMPLE_WIZARD_XP_ID;
	}
}
