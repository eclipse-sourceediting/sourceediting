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
package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class MakeAnonymousGlobal extends CreateElementAction
{
	XSDSchema xsdSchema;
	Element type;
	
	public MakeAnonymousGlobal(String text, Element type, XSDSchema xsdSchema)
	{
		super(text);
		this.xsdSchema = xsdSchema;
		this.type = type;
		isGlobal = true;
	}

	public Element createAndAddNewChildElement()
	{
		// create the new global type
		Element childNode = super.createAndAddNewChildElement();
    // add the anonymous type's children to the new global type
		if (type.hasChildNodes())
		{        
			NodeList nodes = type.getChildNodes();
			// use clones so we don't have a refresh problem
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Node node = nodes.item(i);       
				childNode.appendChild(node.cloneNode(true));
			}
		}

    // clean up the element whose type was anonymous
    // and set its type attribute to the new global type
		TypesHelper helper = new TypesHelper(xsdSchema);
		String prefix = helper.getPrefix(xsdSchema.getTargetNamespace(), true);
		helper = null;
		
		Element parentElementOfAnonymousType = (Element)type.getParentNode();

		parentElementOfAnonymousType.removeChild(type);
		parentElementOfAnonymousType.setAttribute(XSDConstants.TYPE_ATTRIBUTE, prefix + childNode.getAttribute(XSDConstants.NAME_ATTRIBUTE));

		formatChild(childNode);
		
    return childNode;
	}

}
