/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui;



import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.core.util.Utilities;
import org.eclipse.wst.sse.ui.util.Assert;
import org.eclipse.wst.sse.ui.view.events.CaretEvent;
import org.eclipse.wst.sse.ui.view.events.ICaretListener;


/**
 * Has the responsibility of listening for key events, and mouse events,
 * deciding if the caret has moved (without a text change), and if so, will
 * notify CaretListeners that the caret has moved. Objects which are
 * interested in ALL caret postion changes will also have to listen for
 * textChanged events.
 */
public class CaretMediator implements org.eclipse.swt.widgets.Listener {

	class CaretMediatorListener implements KeyListener, MouseListener, SelectionListener {

		public void keyPressed(KeyEvent e) {
			internalKeyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			internalKeyReleased(e);
		}

		public void mouseDoubleClick(MouseEvent e) {
			// ignore completely since mouseUp is always sent too
		}

		public void mouseDown(MouseEvent e) {
			// ignore ... even during a swipe select, we're only interested
			// after the select
			// in which case the "caret" postion returned by the widget's
			// getCaretPostion is
			// at the beginning of the selection -- which is what we want.
			internalMouseDown(e);
		}

		public void mouseUp(MouseEvent e) {
			internalMouseUp(e);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			internalWidgetDefaultSelected(e);
		}


		public void widgetSelected(SelectionEvent e) {
			internalWidgetSelected(e);
		}
	}


	final public static class DelayTimer {
		protected boolean fAlive = true;

		/**
		 */
		private Thread fDelayThread;

		/**
		 * A DelayTimer notifies a listener when a specific amount of time has
		 * passed. Based upon org.eclipse.jdt.internal.debug.core.Timer
		 */

		protected Listener fListener;
		protected boolean fStarted = false;
		protected int fTimeout;

		/**
		 * Constructs a new timer
		 */
		public DelayTimer() {
			fTimeout = Integer.MAX_VALUE;
			Runnable r = new Runnable() {
				public void run() {
					while (fAlive) {
						boolean interrupted = false;
						try {
							Thread.sleep(fTimeout);
						} catch (InterruptedException e) {
							interrupted = true;
						}
						if (!interrupted) {
							if (fListener != null) {
								fStarted = false;
								fTimeout = Integer.MAX_VALUE;
								fListener.handleEvent(null);
							}
						}
					}
				}
			};
			fDelayThread = new Thread(r, "Caret Delay Timer");//$NON-NLS-1$
			fDelayThread.setDaemon(true);
			fDelayThread.start();
		}

		/**
		 * Disposes this timer
		 */
		public void dispose() {
			fAlive = false;
			fDelayThread.interrupt();
			fDelayThread = null;
		}

		/**
		 * Immediately cancels any pending requests, and start over, with the
		 * previous value, fTimeout, passed into start. Note: 'restarting' a
		 * stopped timer basically has no effect.
		 */
		public synchronized void restart() {
			stop();
			start(fListener, fTimeout);
		}

		/**
		 * Starts this timer, and notifies the given listener when the time
		 * has passed. A call to <code>stop</code>, before the time
		 * expires, will cancel the the timer and timeout callback. This
		 * method can only be called if this timer is idle (i.e. stopped, or
		 * expired).
		 */
		public synchronized void start(Listener listener, int ms) {
			// if we are already started, we'll ignore the previous request,
			// and "start over" with this new request.
			//if (fStarted) {
			//restart();
			////throw new IllegalStateException();
			//}
			fListener = listener;
			fTimeout = ms;
			fStarted = true;
			fDelayThread.interrupt();
		}

		/**
		 * Stops this timer
		 */
		public synchronized void stop() {
			fTimeout = Integer.MAX_VALUE;
			fStarted = false;
			fDelayThread.interrupt();
		}
	}

	protected int delayMSecs = 300;
	/** used just for debug print outs */
	private long endTime;

	protected ICaretListener[] fCaretListeners;
	protected CaretMediatorListener internalListener;
	/** used just for debug print outs */
	private long startTime;
	protected StyledText textWidget;
	protected DelayTimer timer;

	/**
	 * CaretMediator constructor comment.
	 */
	public CaretMediator() {
		super();
	}

	/**
	 * CaretMediator constructor comment. Must always provide the widget its
	 * supposed to listen to.
	 */
	public CaretMediator(StyledText textWidget) {
		this();
		setTextWidget(textWidget);
	}

	public synchronized void addCaretListener(ICaretListener listener) {

		if (Debug.debugStructuredDocument) {
			System.out.println("CaretMediator::addCaretListener. Request to add an instance of " + listener.getClass() + " as a listener on caretlistner.");//$NON-NLS-2$//$NON-NLS-1$
		}
		// make sure listener is not already in listening array
		// (and if it is, print a warning to aid debugging, if needed)

		if (Utilities.contains(fCaretListeners, listener)) {
			if (Debug.displayWarnings) {
				System.out.println("CaretMediator::addCaretListener. listener " + listener + " was added more than once. ");//$NON-NLS-2$//$NON-NLS-1$
			}
		} else {
			if (Debug.debugStructuredDocument) {
				System.out.println("CaretMediator::addCaretListener. Adding an instance of " + listener.getClass() + " as a listener on caret mediator.");//$NON-NLS-2$//$NON-NLS-1$
			}
			int oldSize = 0;
			if (fCaretListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fCaretListeners.length;
			}
			int newSize = oldSize + 1;
			ICaretListener[] newListeners = new ICaretListener[newSize];
			if (fCaretListeners != null) {
				System.arraycopy(fCaretListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fCaretListeners = newListeners;

		}
	}

	protected void fireCaretEvent(CaretEvent event) {
		if (fCaretListeners != null) {
			// we must assign listeners to local variable to be thread safe,
			// since the add and remove listner methods
			// can change this object's actual instance of the listener array
			// from another thread
			// (and since object assignment is atomic, we don't need to
			// synchronize
			ICaretListener[] holdListeners = fCaretListeners;
			//
			for (int i = 0; i < holdListeners.length; i++) {
				holdListeners[i].caretMoved(event);

			}
		}
	}

	public void handleEvent(Event e) {
		Display display = null;
		// timer should never be null when this fires,
		// since the handleEvent should be called from the other thread.
		Assert.isNotNull(timer);

		if (Debug.debugCaretMediator) {
			endTime = System.currentTimeMillis();
			System.out.println("Timer fired: " + (endTime - startTime)); //$NON-NLS-1$
		}

		// check if 'okToUse'
		if (textWidget != null && !textWidget.isDisposed()) {
			display = textWidget.getDisplay();
			if ((display != null) && (!display.isDisposed())) {
				display.asyncExec(new Runnable() {
					public void run() {
						if (textWidget != null && !textWidget.isDisposed()) {
							fireCaretEvent(new CaretEvent(textWidget, textWidget.getCaretOffset()));
						}
					}
				});
			}
		}
	}

	protected void internalKeyPressed(KeyEvent e) {
		//	stopTimer(e);
	}

	protected void internalKeyReleased(KeyEvent e) {
		switch (e.keyCode) {
			case SWT.ARROW_DOWN :
			case SWT.ARROW_UP :
			case SWT.ARROW_LEFT :
			case SWT.ARROW_RIGHT :
			case SWT.HOME :
			case SWT.END :
			case SWT.PAGE_DOWN :
			case SWT.PAGE_UP : {
				startTimer(e);
				break;
			}
			default : {
				// always update cursor postion, even during normal typing
				// (the logic may look funny, since we always to the same
				// thing, but we haven't always done the same thing, so I
				// wanted to leave that fact documented via code.)
				startTimer(e);
			}
		}
	}

	protected void internalMouseDown(MouseEvent e) {
		stopTimer(e);
	}

	protected void internalMouseUp(MouseEvent e) {
		// Note, even during a swipe select, when the mouse button goes up,
		// and the widget is
		// queried for the current caret postion, it always returns the
		// beginning of the selection,
		// which is desirable (at least for the known use of this feature,
		// which is to signal
		// that the property sheet can update itself.
		startTimer(e);
	}

	protected void internalWidgetDefaultSelected(SelectionEvent event) {
		// What to do here?
		//System.out.println("Double: " + event.x + " " + event.y + " " +
		// event.width + " " + event.item);
	}

	protected void internalWidgetSelected(SelectionEvent event) {
		// TODO: be sure "current caret postion is updated with event.x
		// (beginnging of selection)
		// and that your 'end' is set to event.y
		//System.out.println("Single: " + event.x + " " + event.y + " " +
		// event.width + " " + event.item);
	}

	public void release() {
		if (timer != null) {
			timer.dispose();
			timer = null;
		}
		if (textWidget != null && !textWidget.isDisposed()) {
			textWidget.removeKeyListener(internalListener);
			textWidget.removeMouseListener(internalListener);
			textWidget.removeSelectionListener(internalListener);
			textWidget = null;
		}

	}

	public synchronized void removeCaretListener(ICaretListener listener) {

		if ((fCaretListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fCaretListeners, listener)) {
				int oldSize = fCaretListeners.length;
				int newSize = oldSize - 1;
				ICaretListener[] newListeners = new ICaretListener[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fCaretListeners[i] == listener) { // ignore
					} else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fCaretListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fCaretListeners = newListeners;
			}
		}
	}

	public void setTextWidget(StyledText newTextWidget) {
		// unhook from previous, if any
		if (this.textWidget != null) {
			stopTimer(null);
			this.textWidget.removeKeyListener(internalListener);
			this.textWidget.removeMouseListener(internalListener);
			this.textWidget.removeSelectionListener(internalListener);
		}

		//		Object oldWidget = this.textWidget;
		this.textWidget = newTextWidget;

		if (internalListener == null) {
			internalListener = new CaretMediatorListener();
		}

		if (this.textWidget != null) {
			this.textWidget.addKeyListener(internalListener);
			this.textWidget.addMouseListener(internalListener);
			this.textWidget.addSelectionListener(internalListener);
		}
		//		else if(oldWidget != null) {
		//			Logger.log(Logger.WARNING, "CaretMediator constructor. textWidget
		// was null, so keys and mouse events won't be listened
		// to");//$NON-NLS-1$
		//		}
	}

	/**
	 * The TypedEvent is expected to be of type KeyEvent or MouseEvent.
	 */
	protected void startTimer(TypedEvent e) {
		if (timer == null) {
			timer = new DelayTimer();
			if (Debug.debugCaretMediator) {
				startTime = System.currentTimeMillis();
				System.out.println("Timer created: " + startTime); //$NON-NLS-1$
			}
		}
		if (Debug.debugCaretMediator) {

			endTime = System.currentTimeMillis();
			System.out.println("Timer started/restarted: after " + (endTime - startTime)); //$NON-NLS-1$
			startTime = System.currentTimeMillis();
		}
		timer.start(this, delayMSecs);
	}

	/**
	 * The TypedEvent is expected to be of type KeyEvent or MouseEvent.
	 */
	protected void stopTimer(TypedEvent e) {
		if (timer != null) {
			timer.stop();
			if (Debug.debugCaretMediator) {
				endTime = System.currentTimeMillis();
				System.out.println("Timer stopped: " + (endTime - startTime)); //$NON-NLS-1$
				startTime = System.currentTimeMillis();
			}
		} else if (Debug.debugCaretMediator) {
			System.out.println("No Timer to stop: " + System.currentTimeMillis()); //$NON-NLS-1$
		}

	}
}
