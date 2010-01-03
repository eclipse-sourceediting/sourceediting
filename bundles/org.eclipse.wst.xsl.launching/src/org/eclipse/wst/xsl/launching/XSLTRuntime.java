/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (Intalio) - clean up find bugs
 *******************************************************************************/
package org.eclipse.wst.xsl.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;

public class XSLTRuntime {
	private static void savePreferences() {
		LaunchingPlugin.getDefault().savePluginPreferences();
	}

	private static Preferences getPreferences() {
		return LaunchingPlugin.getDefault().getPluginPreferences();
	}

	/**
	 * Creates a default Output File for the given input file string.
	 * 
	 * @return Returns an IPath for the Output File.
	 * @since 1.0
	 */
	public static IPath defaultOutputFileForInputFile(String inputFileExpression)
			throws CoreException {
		String file = VariablesPlugin.getDefault().getStringVariableManager()
				.performStringSubstitution(inputFileExpression);
		IPath inputFilePath = new Path(file);
		inputFilePath = inputFilePath.removeFileExtension();
		inputFilePath = inputFilePath.addFileExtension("out.xml"); //$NON-NLS-1$
		return inputFilePath;
	}

}
