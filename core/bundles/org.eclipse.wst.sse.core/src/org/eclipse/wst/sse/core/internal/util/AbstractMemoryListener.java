/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * This responds to memory events.
 * 
 * Create an instance of a child of this class with the events you are interested in.
 * Then call connect() to start listening. To stop listening call disconnect();
 */
public abstract class AbstractMemoryListener implements EventHandler {
	/**
	 * The event that indicates that memory is running low at the lowest severity.
	 * Listeners are requested to release caches that can easily be recomputed.
	 * The Java VM is not seriously in trouble, but process size is getting higher than 
	 * is deemed acceptable.
	 */
	public static final String SEV_NORMAL = "org/eclipse/equinox/events/MemoryEvent/NORMAL"; //$NON-NLS-1$
	
	/**
	 * The event that indicates that memory is running low at medium severity. 
	 * Listeners are requested to release intermediate build results, complex models, etc.
	 * Memory is getting low and may cause operating system level stress, such as swapping.
	 */
	public static final String SEV_SERIOUS = "org/eclipse/equinox/events/MemoryEvent/SERIOUS"; //$NON-NLS-1$
	
	/**
	 * The event that indicates that memory is running low at highest severity.
	 * Listeners are requested to do things like close editors and perspectives, close database connections, etc.
	 * Restoring these resources and caches constitutes lots of work, but memory is so low that
	 * drastic measures are required.
	 */
	public static final String SEV_CRITICAL = "org/eclipse/equinox/events/MemoryEvent/CRITICAL"; //$NON-NLS-1$
	
	/**
	 * All of the valid memory severities
	 */
	public static final String[] SEV_ALL = { SEV_NORMAL, SEV_SERIOUS, SEV_CRITICAL };

	/**
	 * Used to register the {@link EventAdmin} listener
	 */
	private static BundleContext CONTEXT =
		(SSECorePlugin.getDefault() != null) ?
				SSECorePlugin.getDefault().getBundle().getBundleContext() : null;

	/**
	 * the severities that will be reacted to
	 */
	private final List fSeverities;
	
	/**
	 * service used to register this listener
	 */
	private ServiceRegistration fRegisterService;

	/**
	 * Will listen to all memory events
	 */
	public AbstractMemoryListener() {
		this(AbstractMemoryListener.SEV_ALL);
	}

	/**
	 * Will listen to memory events of the given <code>severity</code>
	 * 
	 * @param severity listen for memory events of this severity
	 */
	public AbstractMemoryListener(String severity) {
		Assert.isNotNull(severity, "Severity can not be null"); //$NON-NLS-1$
		
		List severities = new ArrayList(1);
		severities.add(severity);
		fSeverities = severities;
	}
	
	/**
	 * Will listen to memory events of the given <code>severities</code>
	 * 
	 * @param severities listen for memory events for any of these severities
	 */
	public AbstractMemoryListener(String[] severities) {
		Assert.isNotNull(severities, "Severities can not be null"); //$NON-NLS-1$
		Assert.isLegal(severities.length > 0, "Severities must specify at least one severity"); //$NON-NLS-1$
		
		fSeverities = Arrays.asList(severities);
	}

	/**
	 * Will listen to memory events of the given <code>severities</code>
	 * 
	 * @param severities listen for memory events for any of these severities
	 */
	public AbstractMemoryListener(List severities) {
		Assert.isNotNull(severities, "Severities can not be null"); //$NON-NLS-1$
		Assert.isLegal(!severities.isEmpty(), "Severities must specify at least one severity"); //$NON-NLS-1$
		fSeverities = severities;
	}

	/**
	 * Connect this listener to the {@link EventAdmin}
	 */
	public final void connect() {
		if (CONTEXT != null) {
			// NOTE: This is TEMPORARY CODE needed to load the plugin
			// until its done automatically by the product
			// TODO: Remove me
			Bundle b = Platform.getBundle("org.eclipse.equinox.event"); //$NON-NLS-1$
			if (b != null && b.getState() == Bundle.RESOLVED) {
				try {
					b.start(Bundle.START_TRANSIENT);
				}
				catch (BundleException e) {
					e.printStackTrace();
				}
			}
			// end remove me
			
			//register this handler
			String[] severities = (String[])fSeverities.toArray(new String[fSeverities.size()]);
			Hashtable prop = new Hashtable(1);
			prop.put(EventConstants.EVENT_TOPIC, severities);
			fRegisterService = CONTEXT.registerService(EventHandler.class.getName(), this, prop);
			
			//call any implementer specific connect code
			doConnect();
		} else {
			Logger.log(Logger.WARNING, "Error accessing bundle context. Is Platform running? Not tracking memory events. "); //$NON-NLS-1$
		}
	}

	/**
	 * Disconnect this listener to the {@link EventAdmin}
	 */
	public final void disconnect() {
		if (fRegisterService != null) {
			fRegisterService.unregister();
			fRegisterService = null;
		}
		
		//call any implementer specific disconnect code
		doDisconnect();
	}

	/**
	 * <p>Filter out any events that are not of the type that this listener handles</p>
	 * 
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	public final void handleEvent(Event event) {
		if (fSeverities.contains(event.getTopic())) {
			handleMemoryEvent(event);
		}
	}

	/**
	 * Implementing child classes may assume that only {@link Event}s of the types
	 * given to the constructor will be given to this method.
	 * 
	 * @param event the {@link Event} with a topic equal to one of the memory
	 * severities that this listener is listening for
	 */
	protected abstract void handleMemoryEvent(Event event);
	
	/**
	 * Implementers may overrun this method to do setup after connection of this listener
	 */
	protected void doConnect() {
		//do nothing by default
	}
	
	/**
	 * Implementers may overrun this method to do tear down after disconnection of this listener
	 */
	protected void doDisconnect() {
		//do nothing by default
	}
}
