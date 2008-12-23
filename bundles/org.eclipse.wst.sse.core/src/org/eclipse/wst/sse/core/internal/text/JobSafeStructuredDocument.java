/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.text;

import java.util.Stack;

import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.wst.sse.core.internal.IExecutionDelegate;
import org.eclipse.wst.sse.core.internal.ILockable;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;



public class JobSafeStructuredDocument extends BasicStructuredDocument implements IExecutionDelegatable, ILockable {

	private Stack fExecutionDelegates = new Stack();
	private ILock fLockable = Job.getJobManager().newLock();

	public JobSafeStructuredDocument() {
		super();
	}


	public JobSafeStructuredDocument(RegionParser parser) {
		super(parser);
	}


	/**
	 * 
	 */
	protected final void acquireLock() {
		getLockObject().acquire();
	}

	private IExecutionDelegate getExecutionDelegate() {
		if (!fExecutionDelegates.isEmpty())
			return (IExecutionDelegate) fExecutionDelegates.peek();
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.ILockable#getLock()
	 */

	public ILock getLockObject() {
		return fLockable;
	}


	/**
	 * 
	 */
	protected final void releaseLock() {
		getLockObject().release();
	}
	
	/*
	 * @see org.eclipse.jface.text.IDocument.replace(int, int, String)
	 */
	public void replace(final int offset, final int length, final String text) throws BadLocationException {
		IExecutionDelegate delegate = getExecutionDelegate();
		if (delegate == null) {
			super.replace(offset, length, text);
		}
		else {
			final Object[] resultSlot = new Object[1];
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						JobSafeStructuredDocument.super.replace(offset, length, text);
					}
					catch (Throwable e) {
						resultSlot[0] = e;
					}
				}
			};
			delegate.execute(runnable);
		}
	}
	
	/*
	 * @see org.eclipse.jface.text.IDocumentExtension4.replace(int, int, String, long)
	 */
	public void replace(final int offset, final int length, final String text, final long modificationStamp) throws BadLocationException {
		IExecutionDelegate delegate = getExecutionDelegate();
		if (delegate == null) {
			super.replace(offset, length, text, modificationStamp);
		}
		else {
			final Object[] resultSlot = new Object[1];
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						JobSafeStructuredDocument.super.replace(offset, length, text, modificationStamp);
					}
					catch (Throwable e) {
						resultSlot[0] = e;
					}
				}
			};
			delegate.execute(runnable);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument#replaceText(java.lang.Object, int, int, java.lang.String)
	 */
	public StructuredDocumentEvent replaceText(final Object requester, final int start, final int replacementLength, final String changes) {
		StructuredDocumentEvent event = null;
		IExecutionDelegate delegate = getExecutionDelegate();
		if (delegate == null) {
			event = super.replaceText(requester, start, replacementLength, changes);
		}
		else {
			final Object[] resultSlot = new Object[1];
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						resultSlot[0] = JobSafeStructuredDocument.super.replaceText(requester, start, replacementLength, changes);
					}
					catch (Throwable e) {
						resultSlot[0] = e;
					}
				}
			};
			delegate.execute(runnable);
			if (resultSlot[0] instanceof Throwable) {
				throw new RuntimeException((Throwable) resultSlot[0]);
			}
			else {
				event = (StructuredDocumentEvent) resultSlot[0];
			}
		}
		return event;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument#replaceText(java.lang.Object, int, int, java.lang.String, boolean)
	 */
	public StructuredDocumentEvent replaceText(final Object requester, final int start, final int replacementLength, final String changes, final boolean ignoreReadOnlySettings) {
		StructuredDocumentEvent event = null;
		IExecutionDelegate delegate = getExecutionDelegate();
		if (delegate == null) {
			// if the delegate has not been set, we execute on current
			// thread, like "normal". This is the case for normal
			// non-editor
			// use (which should still, ultimately, be protected by
			// a scheduling rule).
			event = super.replaceText(requester, start, replacementLength, changes, ignoreReadOnlySettings);
		}
		else {
			// If document is being used by an editor, a runnable is
			// created,
			// even if later (in the execution delegate instance) it is
			// decided
			// nothing special is needed (that is, in fact being called
			// from
			// editor's display thread, in which case its just executed)
			final Object[] resultSlot = new Object[1];
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						resultSlot[0] = JobSafeStructuredDocument.super.replaceText(requester, start, replacementLength, changes, ignoreReadOnlySettings);
					}
					catch (Throwable e) {
						resultSlot[0] = e;
					}
				}
			};
			// strongly assumes synchronous call, to "wait" for
			// results
			// basically just routes to Display.synchExec(runnable),
			// if not already running on display thread.
			delegate.execute(runnable);

			// this remembering and re-throwing of exception is just to
			// get an
			// exception that occurred on one thread, to be thrown on
			// callers
			// thread.
			if (resultSlot[0] instanceof Throwable) {
				throw new RuntimeException((Throwable) resultSlot[0]);
			}
			else {
				event = (StructuredDocumentEvent) resultSlot[0];
			}
		}
		return event;
	}

	public void setExecutionDelegate(IExecutionDelegate delegate) {
		if (delegate != null)
			fExecutionDelegates.push(delegate);
		else if (!fExecutionDelegates.isEmpty())
			fExecutionDelegates.pop();
	}

	public StructuredDocumentEvent setText(final Object requester, final String theString) {
		StructuredDocumentEvent event = null;
		IExecutionDelegate executionDelegate = getExecutionDelegate();
		if (executionDelegate == null) {
			// if the delegate has not been set, we execute on current
			// thread, like "normal". This is the case for normal
			// non-editor
			// use (which should still, ultimately, be protected by
			// a scheduling rule).
			event = super.setText(requester, theString);
		}
		else {
			// If document is being used by an editor, a runnable is
			// created,
			// even if later (in the execution delegate instance) it is
			// decided
			// nothing special is needed (that is, in fact being called
			// from
			// editor's display thread, in which case its just executed)
			final Object[] resultSlot = new Object[1];
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						resultSlot[0] = JobSafeStructuredDocument.super.setText(requester, theString);
					}
					catch (Throwable e) {
						resultSlot[0] = e;
					}
				}
			};
			// strongly assumes synchronous call, to "wait" for
			// results
			// basically just routes to Display.synchExec(runnable),
			// if not already running on display thread.
			executionDelegate.execute(runnable);

			// this remembering and re-throwing of exception is just to
			// get an
			// exception that occurred on one thread, to be thrown on
			// callers
			// thread.
			if (resultSlot[0] instanceof Throwable) {
				throw new RuntimeException((Throwable) resultSlot[0]);
			}
			else {
				event = (StructuredDocumentEvent) resultSlot[0];
			}
		}
		return event;
	}

	public DocumentRewriteSession startRewriteSession(DocumentRewriteSessionType sessionType) throws IllegalStateException {
		DocumentRewriteSession session = null;
		IExecutionDelegate executionDelegate = getExecutionDelegate();
		if (executionDelegate == null) {
			// if the delegate has not been set, we execute on current
			// thread, like "normal". This is the case for normal
			// non-editor
			// use (which should still, ultimately, be protected by
			// a scheduling rule).
			session = internalStartRewriteSession(sessionType);
		}
		else {
			// If document is being used by an editor, a runnable is
			// created,
			// even if later (in the execution delegate instance) it is
			// decided
			// nothing special is needed (that is, in fact being called
			// from
			// editor's display thread, in which case its just executed)
			final Object[] resultSlot = new Object[1];
			final DocumentRewriteSessionType finalSessionType = sessionType;
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						resultSlot[0] = internalStartRewriteSession(finalSessionType);

					}
					catch (Throwable e) {
						resultSlot[0] = e;
					}
				}
			};
			// strongly assumes synchronous call, to "wait" for
			// results
			// basically just routes to Display.synchExec(runnable),
			// if not already running on display thread.
			executionDelegate.execute(runnable);

			// this remembering and re-throwing of exception is just to
			// get an
			// exception that occurred on one thread, to be thrown on
			// callers
			// thread.
			if (resultSlot[0] instanceof Throwable) {
				throw new RuntimeException((Throwable) resultSlot[0]);
			}
			else {
				session = (DocumentRewriteSession) resultSlot[0];
			}
		}
		return session;
	}

	public void stopRewriteSession(DocumentRewriteSession session) {
		IExecutionDelegate executionDelegate = getExecutionDelegate();
		if (executionDelegate == null) {
			// if the delegate has not been set, we execute on current
			// thread, like "normal". This is the case for normal
			// non-editor
			// use (which should still, ultimately, be protected by
			// a scheduling rule).
			internalStopRewriteSession(session);
		}
		else {
			// If document is being used by an editor, a runnable is
			// created,
			// even if later (in the execution delegate instance) it is
			// decided
			// nothing special is needed (that is, in fact being called
			// from
			// editor's display thread, in which case its just executed)
			final Object[] resultSlot = new Object[1];
			final DocumentRewriteSession finalSession = session;
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						internalStopRewriteSession(finalSession);

					}
					catch (Throwable e) {
						resultSlot[0] = e;
					}
				}
			};
			// strongly assumes synchronous call, to "wait" for
			// results
			// basically just routes to Display.synchExec(runnable),
			// if not already running on display thread.
			executionDelegate.execute(runnable);

			// this remembering and re-throwing of exception is just to
			// get an
			// exception that occurred on one thread, to be thrown on
			// callers
			// thread.
			if (resultSlot[0] instanceof Throwable) {
				throw new RuntimeException((Throwable) resultSlot[0]);
			}
		}
	}


}
