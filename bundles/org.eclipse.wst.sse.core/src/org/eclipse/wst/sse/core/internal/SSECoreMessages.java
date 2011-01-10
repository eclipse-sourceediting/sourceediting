/**********************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.sse.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by SSE Core
 * 
 * @plannedfor 1.0
 */
public class SSECoreMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.sse.core.internal.SSECorePluginResources";//$NON-NLS-1$

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, SSECoreMessages.class);
	}

	private SSECoreMessages() {
		// cannot create new instance
	}

	public static String A_model_s_id_can_not_be_nu_EXC_;
	public static String Program_Error__ModelManage_EXC_;
	public static String Original_Error__UI_;
	public static String Text_Change_UI_;
	public static String TaskScanner_0;
	public static String TaskScanningJob_0;
	public static String TaskScanningJob_1;
	public static String Migrate_Charset;
	
	public static String IndexManager_0_starting;
	public static String IndexManager_0_starting_1;
	public static String IndexManager_0_Indexing_1_Files;	
	public static String IndexManager_processing_deferred_resource_changes;
	public static String IndexManager_Processing_entire_workspace_for_the_first_time;
	public static String IndexManager_0_Processing_entire_workspace_for_the_first_time;
	public static String IndexManager_processing_recent_resource_changes;
	public static String IndexManager_0_resources_to_go_1;
	public static String IndexManager_Waiting_for_0;
	public static String IndexManager_0_Processing_resource_events;
}
