/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.text;


/**
 * This interface is not intended to be implemented. It defines the
 * partitioning for CSS and all its partitions. Clients should reference the
 * partition type Strings defined here directly.
 * 
 * @since 1.1
 */
public interface ICSSPartitions {

	String STYLE = "org.eclipse.wst.css.STYLE"; //$NON-NLS-1$
	String COMMENT = "org.eclipse.wst.css.COMMENT"; //$NON-NLS-1$
}
