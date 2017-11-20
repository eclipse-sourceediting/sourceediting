/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.validation.internal.core.IMessageAccess;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

public class MockReporter implements IReporter {
	List list = new ArrayList();

	public MockReporter() {
		super();
	}

	public void addMessage(IValidator origin, IMessage message) {
		list.add(message);
	}

	public void displaySubtask(IValidator validator, IMessage message) {
		/* do not need to implement */
	}

	public IMessageAccess getMessageAccess() {
		return null;
	}

	public boolean isCancelled() {
		return false;
	}

	public void removeAllMessages(IValidator origin, Object object) { // do
		/* do not need to implement */
	}

	public void removeAllMessages(IValidator origin) {
		/* do not need to implement */
	}

	public void removeMessageSubset(IValidator validator, Object obj,
			String groupName) {// do
		/* do not need to implement */
	}

	public List getMessages() {
		return list;
	}

}
