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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor;
import org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptorProvider;


public class XSDSectionDescriptorProvider implements ISectionDescriptorProvider
{
  /**
   * 
   */
  public XSDSectionDescriptorProvider()
  {
    super();
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptorProvider#getSectionDescriptors()
   */
  public ISectionDescriptor[] getSectionDescriptors()
  {
    ISectionDescriptor[] descriptors = new ISectionDescriptor[18];
    descriptors[0] = new NameSectionDescriptor();
    descriptors[1] = new TypesSectionDescriptor();
    descriptors[2] = new OtherAttributesSectionDescriptor();
    descriptors[3] = new AttributesViewSectionDescriptor();
    descriptors[4] = new ModelGroupSectionDescriptor();
    descriptors[5] = new NamespaceProcessContentsSectionDescriptor();
    descriptors[6] = new ReferenceSectionDescriptor();
    descriptors[7] = new ComplexTypeSectionDescriptor();
    descriptors[8] = new ValueSectionDescriptor();
    descriptors[9] = new PatternSectionDescriptor();
    descriptors[10] = new AnnotationSectionDescriptor();
    descriptors[11] = new SimpleTypeSectionDescriptor();
    descriptors[12] = new FacetsSectionDescriptor();
    descriptors[13] = new EnumerationsSectionDescriptor();
    descriptors[14] = new NamespaceSectionDescriptor();
    descriptors[15] = new SchemaLocationDescriptor();
    descriptors[16] = new NamespaceAndSchemaLocationDescriptor();
    descriptors[17] = new MinMaxSectionDescriptor();
    
//  descriptors[18] = new SimpleTypeUnionSectionDescriptor();
//  descriptors[19] = new FixedDefaultSectionDescriptor();    
    return descriptors;
  }

}
