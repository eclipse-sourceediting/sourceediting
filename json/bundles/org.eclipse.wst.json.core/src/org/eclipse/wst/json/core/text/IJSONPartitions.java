/**
 *  Copyright (c) 2013-2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.text;

/**
 * This interface is not intended to be implemented. It defines the partitioning
 * for JSON and all its partitions. Clients should reference the partition type
 * Strings defined here directly.
 */
public interface IJSONPartitions {

	String JSON = "org.eclipse.wst.json.JSON"; //$NON-NLS-1$
	String COMMENT = "org.eclipse.wst.json.COMMENT"; //$NON-NLS-1$
}
