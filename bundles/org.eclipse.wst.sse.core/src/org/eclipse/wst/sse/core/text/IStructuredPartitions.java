/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.text;

/**
 * This interface is not intended to be implemented.
 * It defines the partitioning for StructuredDocuments.
 * Clients should reference the partition type Strings defined here directly.
 * 
 * @since 1.1
 */
public interface IStructuredPartitions {

	String DEFAULT_PARTITION = "org.eclipse.wst.sse.ST_DEFAULT"; //$NON-NLS-1$
	String UNKNOWN_PARTITION = "org.eclipse.wst.sse.UNKNOWN_PARTITION_TYPE"; //$NON-NLS-1$
}
