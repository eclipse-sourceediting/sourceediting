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
package org.eclipse.wst.sse.core.internal.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.ModelPlugin;
import org.eclipse.wst.sse.core.builder.IBuilderDelegate;
import org.eclipse.wst.sse.core.builder.IBuilderParticipant;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.util.StringUtils;


class BuilderParticipantRegistryReader {

	private static final boolean _debugReader = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/participantregistry")); //$NON-NLS-1$ //$NON-NLS-2$
	private String ATT_CLASS = "class"; //$NON-NLS-1$
	private String ATT_CONTENT_TYPE = "contentType"; //$NON-NLS-1$

	private String PARTICIPANT_EXTENSION_POINT_ID = ModelPlugin.getID() + ".builderparticipant"; //$NON-NLS-1$

	private String DELEGATE_EXTENSION_POINT_ID = ModelPlugin.getID() + ".builderdelegate"; //$NON-NLS-1$

	private final static String CONTENTTYPE_WILDCARD = "*"; //$NON-NLS-1$

	// all of the configuration elements for our extension point
	private IConfigurationElement[] fParticipantElements;
	private IConfigurationElement[] fDelegateElements;

	// a mapping of content types to IBuilderParticipant instances
	private Map fParticipantMap = null;
	// a mapping of content types to IBuilderDelegate instances
	private Map fDelegateMap = null;

	BuilderParticipantRegistryReader() {
		super();
	}

	public IBuilderParticipant[] getBuildParticipants(Object contentTypeID) {
		if (fParticipantElements == null) {
			readRegistry();
		}
		IBuilderParticipant[] participants = (IBuilderParticipant[]) fParticipantMap.get(contentTypeID);
		if (participants == null) {
			// fine the relevant extensions
			List elements = new ArrayList(0);
			IConfigurationElement[] participantElements = fParticipantElements;
			for (int j = 0; j < participantElements.length; j++) {
				String participantContentTypes = participantElements[j].getAttribute(ATT_CONTENT_TYPE);
				String[] participantContentType = StringUtils.unpack(participantContentTypes);
				for (int k = 0; k < participantContentType.length; k++) {
					// allow wildcards to be returned as well
					if (participantContentType[k].equals(contentTypeID) || CONTENTTYPE_WILDCARD.equals(participantContentType[k])) {
						elements.add(participantElements[j]);
					}
				}
			}
			// instantiate and save them
			List participantsList = new ArrayList(elements.size());
			for (int i = 0; i < elements.size(); i++) {
				try {
					IBuilderParticipant participant = (IBuilderParticipant) ((IConfigurationElement) elements.get(i)).createExecutableExtension(ATT_CLASS);
					if (participant != null) {
						participantsList.add(participant);
					}
				}
				catch (CoreException e) {
					Logger.logException("Exception creating builder participant for " + contentTypeID, e); //$NON-NLS-1$
				}
			}
			participants = (IBuilderParticipant[]) participantsList.toArray(new IBuilderParticipant[0]);
			fParticipantMap.put(contentTypeID, participants);
			if (_debugReader) {
				System.out.println("Created " + participants.length + " build participants for " + contentTypeID); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return participants;
	}

	public IBuilderDelegate[] getBuilderDelegates(Object contentTypeID) {
		if (fDelegateElements == null) {
			readRegistry();
		}
		IBuilderDelegate[] delegates = (IBuilderDelegate[]) fDelegateMap.get(contentTypeID);
		if (delegates == null) {
			// fine the relevant extensions
			List elements = new ArrayList(0);
			IConfigurationElement[] delegateElements = fDelegateElements;
			for (int j = 0; j < delegateElements.length; j++) {
				String contentTypes = delegateElements[j].getAttribute(ATT_CONTENT_TYPE);
				String[] contentType = StringUtils.unpack(contentTypes);
				for (int k = 0; k < contentType.length; k++) {
					// allow wildcards to be returned as well
					if (contentType[k].equals(contentTypeID)) {
						elements.add(delegateElements[j]);
					}
				}
			}
			// instantiate and save them
			List delegateList = new ArrayList(elements.size());
			for (int i = 0; i < elements.size(); i++) {
				try {
					IBuilderDelegate delegate = (IBuilderDelegate) ((IConfigurationElement) elements.get(i)).createExecutableExtension(ATT_CLASS);
					if (delegate != null) {
						delegateList.add(delegate);
					}
				}
				catch (CoreException e) {
					Logger.logException("Exception creating builder participant for " + contentTypeID, e); //$NON-NLS-1$
				}
			}
			delegates = (IBuilderDelegate[]) delegateList.toArray(new IBuilderDelegate[0]);
			fDelegateMap.put(contentTypeID, delegates);
			if (_debugReader) {
				System.out.println("Created " + delegates.length + " build participants for " + contentTypeID); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return delegates;
	}

	private void readRegistry() {
		fParticipantMap = new HashMap();
		fDelegateMap = new HashMap();
		// Just remember the elements, so plugins don't have to be activated,
		// unless extension attributes match those of interest
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(PARTICIPANT_EXTENSION_POINT_ID);
		if (point != null) {
			fParticipantElements = point.getConfigurationElements();
		}
		point = Platform.getExtensionRegistry().getExtensionPoint(DELEGATE_EXTENSION_POINT_ID);
		if (point != null) {
			fDelegateElements = point.getConfigurationElements();
		}
	}
}
