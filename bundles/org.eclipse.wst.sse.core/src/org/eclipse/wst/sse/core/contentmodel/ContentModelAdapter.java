/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.contentmodel;



import org.eclipse.wst.contentmodel.CMDocument;
import org.eclipse.wst.contentmodel.CMNode;

/**
 * ContentModelAdapter interface
 */
public interface ContentModelAdapter extends org.eclipse.wst.sse.core.INodeAdapter {

	/**
	 * getCMDocument method
	 * @return CMDocument
	 *
	 * Returns CMDocument associated to the adapting node.
	 * For exampl :
	 * HTML CMDocument is associated to HTML Document node,
	 * DTD CMDocument is associated to DocumentType node,
	 * DTD/Schema CMDocument is associated to Element node
	 * (sometime with namespace), and
	 * taglib CMDocument is associated to taglib directive Element node.
	 *
	 * INodeNotifier is passed for stateless (singleton) INodeAdapter implementation.
	 */
	CMDocument getCMDocument(org.eclipse.wst.sse.core.INodeNotifier notifier);

	/**
	 * getDeclaration method
	 * @return CMNode
	 *
	 * Returns ElementDefinition or AttributeDefinition
	 * for the adapting node.
	 *
	 * INodeNotifier is passed for stateless (singleton) INodeAdapter implementation.
	 */
	CMNode getDeclaration(org.eclipse.wst.sse.core.INodeNotifier notifier);
}
