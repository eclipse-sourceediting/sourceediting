/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.preferences;

class ComboData {
	private String fKey;
	private int[] fSeverities;
	private int fIndex;
	int originalSeverity = -2;
	
	public ComboData(String key, int[] severities, int index) {
		fKey = key;
		fSeverities = severities;
		fIndex = index;
	}
	
	public String getKey() {
		return fKey;
	}
	
	public void setIndex(int index) {
		fIndex = index;
	}
	
	public int getIndex() {
		return fIndex;
	}
	
	/**
	 * Sets the severity index based on <code>severity</code>.
	 * If the severity doesn't exist, the index is set to -1.
	 * 
	 * @param severity the severity level
	 */
	public void setSeverity(int severity) {
		for(int i = 0; fSeverities != null && i < fSeverities.length; i++) {
			if(fSeverities[i] == severity) {
				fIndex = i;
				return;
			}
		}
		
		fIndex = -1;
	}
	
	public int getSeverity() {
		return (fIndex >= 0 && fSeverities != null && fIndex < fSeverities.length) ? fSeverities[fIndex] : -1;
	}
	
	boolean isChanged() {
		return fSeverities[fIndex] != originalSeverity;
	}
}
