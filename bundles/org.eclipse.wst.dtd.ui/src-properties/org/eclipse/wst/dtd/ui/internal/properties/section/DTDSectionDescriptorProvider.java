/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.properties.section;

import org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor;
import org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptorProvider;

public class DTDSectionDescriptorProvider implements ISectionDescriptorProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptorProvider#getSectionDescriptors()
	 */
	public ISectionDescriptor[] getSectionDescriptors() {
		ISectionDescriptor[] descriptors = new ISectionDescriptor[11];

		descriptors[0] = new NameSectionDescriptor();
		descriptors[1] = new ContentModelTypeSectionDescriptor();
		descriptors[2] = new TypeSectionDescriptor();
		descriptors[3] = new AttributeDefaultSectionDescriptor();
		descriptors[4] = new ContentModelGroupSectionDescriptor();
		descriptors[5] = new ContentModelNameSectionDescriptor();
		descriptors[6] = new OccurrenceSectionDescriptor();
		descriptors[7] = new CommentSectionDescriptor();
		descriptors[8] = new DocumentSectionDescriptor();
		// descriptors[9] = new EntityTypeSectionDescriptor();
		// descriptors[10]= new EntityValueSectionDescriptor();
		descriptors[9] = new NotationSectionDescriptor();
		descriptors[10] = new NewEntitySectionDescriptor();
		// descriptors[11]= new EmptySectionDescriptor();
		return descriptors;
	}

}