/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.internal.deployables;

import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipant;

public interface IFlatDeployable {
	/**
	 * Get a list of current flatten participants for this deployable
	 * @return
	 */
	public IFlattenParticipant[] getParticipants();

	/**
	 * Get a list of current flatten participant id's for this deployable
	 * @return
	 */
	public String[] getParticipantIds();

	/**
	 * Get a list of default flatten participants involved
	 * for this module type
	 * 
	 * @return
	 */
	public String[] getDefaultFlattenParticipantIDs();
	
	/**
	 * Add the flatten participant of the given id
	 * and persist it in the project settings.
	 * 
	 * @param id
	 * @param position
	 * @return
	 */
	public void addFlattenParticipant(String id, int position);
	
	/**
	 * Remove the flatten participant of the given id
	 * and persist it in the project settings.
	 * 
	 * @param id
	 * @return
	 */
	public void removeFlattenParticipant(String id);
	

}
