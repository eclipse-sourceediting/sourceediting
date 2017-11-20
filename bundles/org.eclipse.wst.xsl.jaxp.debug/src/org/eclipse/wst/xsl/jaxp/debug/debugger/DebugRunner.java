/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (Intalion) - FindBugs cleanup
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.debugger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.wst.xsl.jaxp.debug.invoker.IProcessorInvoker;
import org.eclipse.wst.xsl.jaxp.debug.invoker.PipelineDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformationException;
import org.eclipse.wst.xsl.jaxp.debug.invoker.internal.ConfigurationException;
import org.eclipse.wst.xsl.jaxp.debug.invoker.internal.JAXPSAXProcessorInvoker;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The entry point to the debug process which is responsible for configuring a
 * debugger and then communicating with the Eclipse process via sockets using a
 * common set of commands.
 * 
 * <ul>
 * <li>instantiates an implementation of <code>IXSLDebugger</code>
 * <li>configures the debugger with the transformation pipeline
 * <li>starts the debugger in a separate thread
 * <li>the main thread is then used to listen to incoming requests and call the
 * appropriate debugger methods
 * </ul>
 * 
 * @author Doug Satchwell
 */
public class DebugRunner {
	private static final Log log = LogFactory.getLog(DebugRunner.class);

	private final BufferedReader requestIn;
	private final Writer requestOut;
	private final Writer eventOut;
	private final Writer generatedStream;
	private Socket eventSocket;
	private Socket requestSocket;
	private Socket generateSocket;

	/**
	 * Create a new instance of this using the supplied readers and writers.
	 * 
	 * @param requestIn
	 *            the reader for reading incoming requests
	 * @param requestOut
	 *            the writer for acknowledging requests
	 * @param eventOut
	 *            the writer for publishing debug events
	 */
	public DebugRunner(BufferedReader requestIn, PrintWriter requestOut,
			PrintWriter eventOut, PrintWriter generatedStream) {
		this.requestOut = requestOut;
		this.requestIn = requestIn;
		this.eventOut = eventOut;
		this.generatedStream = generatedStream;
	}

	/**
	 * Create a new instance of this given a request port and an event port.
	 * 
	 * @param requestPort
	 *            the port to listen to requests and send acknowledgements
	 * @param eventPort
	 *            the port for publishing debug events
	 * @throws IOException
	 *             if there was a problem opening a socket
	 */
	public DebugRunner(int requestPort, int eventPort, int generatePort)
			throws IOException {
		requestSocket = getSocket(requestPort);
		eventSocket = getSocket(eventPort);
		generateSocket = getSocket(generatePort);
		requestIn = new BufferedReader(new InputStreamReader(requestSocket
				.getInputStream()));
		requestOut = new PrintWriter(requestSocket.getOutputStream(), true);
		eventOut = new PrintWriter(eventSocket.getOutputStream(), true);
		generatedStream = new BufferedWriter(new PrintWriter(generateSocket
				.getOutputStream(), true));
	}

	/**
	 * This method starts the given debugger in its own thread, and blocks while
	 * waiting for incoming requests from the request port, until there are no
	 * more requests.
	 * 
	 * @param debugger
	 *            the debugger to start in a thread
	 * @throws TransformationException
	 *             if a problem occurred while transforming
	 * @throws IOException
	 */
	public void loop(IXSLDebugger debugger) throws TransformationException,
			IOException {
		debugger.setEventWriter(eventOut);
		debugger.setGeneratedWriter(generatedStream);
		String inputLine, response;
		// signal we are ready to receive requests
		eventOut.write("ready\n"); //$NON-NLS-1$
		eventOut.flush();
		log.debug("entering loop"); //$NON-NLS-1$
		try {
			while ((inputLine = requestIn.readLine()) != null) {
				response = inputLine;
				log.debug("REQUEST:" + inputLine); //$NON-NLS-1$
				Thread debuggerThread = null;
				if (DebugConstants.REQUEST_START.equals(inputLine)) {
					debuggerThread = new Thread(debugger, "debugger"); //$NON-NLS-1$
					debuggerThread.start();
				}
				/*
				 * else if (REQUEST_QUIT.equals(inputLine)) { }
				 */
				else if (DebugConstants.REQUEST_STEP_INTO.equals(inputLine)) {
					debugger.stepInto();
				} else if (DebugConstants.REQUEST_STEP_OVER.equals(inputLine)) {
					debugger.stepOver();
				} else if (DebugConstants.REQUEST_STEP_RETURN.equals(inputLine)) {
					debugger.stepReturn();
				} else if (DebugConstants.REQUEST_SUSPEND.equals(inputLine)) {
					debugger.suspend();
				} else if (DebugConstants.REQUEST_RESUME.equals(inputLine)) {
					debugger.resume();
				} else if (DebugConstants.REQUEST_STACK.equals(inputLine)) {
					response = debugger.stack();
				} else if (inputLine
						.startsWith(DebugConstants.REQUEST_VARIABLE)) {
					String data = inputLine
							.substring(DebugConstants.REQUEST_VARIABLE.length() + 1);
					int id = Integer.parseInt(data);
					Variable var = debugger.getVariable(id);
					log.debug("var " + id + " = " + var); //$NON-NLS-1$ //$NON-NLS-2$
					response = var.getScope() + "&" + var.getName(); //$NON-NLS-1$
				} else if (inputLine.startsWith(DebugConstants.REQUEST_VALUE)) {
					String data = inputLine
							.substring(DebugConstants.REQUEST_VALUE.length() + 1);
					int id = Integer.parseInt(data);
					Variable var = debugger.getVariable(id);
					response = var.getType() + "&" + var.getValueFirstLine(); //$NON-NLS-1$
				} else if (inputLine
						.startsWith(DebugConstants.REQUEST_ADD_BREAKPOINT)) {
					int index = inputLine.lastIndexOf(' ');
					String file = inputLine.substring(
							DebugConstants.REQUEST_ADD_BREAKPOINT.length() + 1,
							index);
					String line = inputLine.substring(index + 1);
					BreakPoint breakpoint = new BreakPoint(file, Integer
							.parseInt(line));
					debugger.addBreakpoint(breakpoint);
				} else if (inputLine
						.startsWith(DebugConstants.REQUEST_REMOVE_BREAKPOINT)) {
					int index = inputLine.lastIndexOf(' ');
					String file = inputLine
							.substring(DebugConstants.REQUEST_REMOVE_BREAKPOINT
									.length() + 1, index);
					String line = inputLine.substring(index + 1);
					BreakPoint breakpoint = new BreakPoint(file, Integer
							.parseInt(line));
					debugger.removeBreakpoint(breakpoint);
				} else {
					response = "What?"; //$NON-NLS-1$
				}
				// confirm request
				log.debug("RESPONSE:" + response); //$NON-NLS-1$
				requestOut.write(response + "\n"); //$NON-NLS-1$
				requestOut.flush();

				/*
				 * if (REQUEST_QUIT.equals(inputLine)) {
				 * waitForFinish(debuggerThread); break; }
				 */
			}
		} catch (IOException e) {
			throw new TransformationException(e.getMessage(), e);
		}
		log.debug("exited loop"); //$NON-NLS-1$
		eventOut.write("terminated\n"); //$NON-NLS-1$
		eventOut.flush();
	}

	/**
	 * Dispose of this - close all open sockets.
	 * 
	 * @throws IOException
	 */
	public void dispose() throws IOException {
		if (requestIn != null) {
			try {
				requestIn.close();
			} catch (IOException e) {
				log.error("Could not close request input stream", e); //$NON-NLS-1$
			}
		}
		if (requestOut != null) {
			requestOut.close();
		}
		if (eventOut != null) {
			eventOut.close();
		}
		if (requestSocket != null) {
			try {
				requestSocket.close();
			} catch (IOException e) {
				log.error("Could not close request socket", e); //$NON-NLS-1$
			}
		}
		if (eventSocket != null) {
			try {
				eventSocket.close();
			} catch (IOException e) {
				log.error("Could not close event socket", e); //$NON-NLS-1$
			}
		}
	}

	private static Socket getSocket(int port) throws IOException {
		InetAddress localhost = InetAddress.getByName("localhost"); //$NON-NLS-1$
		ServerSocket serverSocket = new ServerSocket(port, 5, localhost);
		Socket clientSocket = serverSocket.accept();
		serverSocket.close();
		return clientSocket;
	}

	/**
	 * Expected arguments:
	 * 
	 * <ol>
	 * <li>the class name of the invoker
	 * <li>the file name of the XML launch configuration file
	 * <li>the URL of the source document
	 * <li>the file of the output document
	 * <li>not used (anything)
	 * <li>the class name of the <code>IXSLDebugger</code> instance
	 * <li>the port used for requests
	 * <li>the port used for debug events
	 * <li>the port used for generate events
	 * </ol>
	 * 
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws SAXException, ParserConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("javax.xml.transform.TransformerFactory=" + System.getProperty("javax.xml.transform.TransformerFactory")); //$NON-NLS-1$//$NON-NLS-2$
		log.info("java.endorsed.dirs=" + System.getProperty("java.endorsed.dirs")); //$NON-NLS-1$//$NON-NLS-2$

		String invokerClassName = args[0];
		File launchFile = new File(args[1]);
		String src = args[2];
		String target = args[3];
		String debuggerClassName = args[5];

		log.info("src: " + src); //$NON-NLS-1$
		log.info("target: " + target); //$NON-NLS-1$
		log.info("launchFile: " + launchFile); //$NON-NLS-1$
		log.info("debugger: " + debuggerClassName); //$NON-NLS-1$

		DebugRunner debugRunner = null;
		try {
			final IXSLDebugger debugger = createDebugger(debuggerClassName);
			// create the invoker
			IProcessorInvoker invoker = new JAXPSAXProcessorInvoker() {

				@Override
				protected TransformerFactory createTransformerFactory() {
					TransformerFactory tFactory = super
							.createTransformerFactory();
					debugger.setTransformerFactory(tFactory);
					return tFactory;
				}

				@Override
				public void addStylesheet(URL stylesheet, Map parameters,
						Properties outputProperties, URIResolver resolver)
						throws TransformerConfigurationException {
					InputSource inputsource = new InputSource(stylesheet
							.toString());
					// if required in future, parse the document with line
					// numbers (to get the end line numbers)
					// XMLReaderWrapper reader = new
					// XMLReaderWrapper(createReader());
					// SAXSource source = new SAXSource(reader,inputsource);
					addStylesheet(new SAXSource(inputsource), resolver,
							parameters, outputProperties);
				}

				@Override
				protected Transformer addStylesheet(Source source,
						URIResolver resolver, Map parameters,
						Properties outputProperties)
						throws TransformerConfigurationException {
					Transformer transformer = super.addStylesheet(source,
							resolver, parameters, outputProperties);
					debugger.addTransformer(transformer);
					return transformer;
				}
			};

			if (args.length == 9) {
				int requestPort = Integer.parseInt(args[6]);
				int eventPort = Integer.parseInt(args[7]);
				int generatePort = Integer.parseInt(args[8]);

				log.debug("requestPort: " + requestPort); //$NON-NLS-1$
				log.debug("eventPort: " + eventPort); //$NON-NLS-1$
				log.debug("generatePort: " + generatePort); //$NON-NLS-1$

				try {
					debugRunner = new DebugRunner(requestPort, eventPort,
							generatePort);
				} catch (Exception e) {
					handleFatalError(
							"Could not instantiate invoker: " + invokerClassName, e); //$NON-NLS-1$
				}
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				debugRunner = new DebugRunner(br, new PrintWriter(System.out),
						new PrintWriter(System.err), null);
				System.out.println("xsl>"); //$NON-NLS-1$
			}

			PipelineDefinition pipeline = new PipelineDefinition(launchFile);
			pipeline.configure(invoker);

			debugger.setInvoker(invoker);
			debugger.setSource(new URL(src));
			debugger.setTarget(new FileWriter(new File(target)));

			debugRunner.loop(debugger);
		} catch (IOException e) {
			handleFatalError(e.getMessage(), e);
		} catch (TransformationException e) {
			handleFatalError(e.getMessage(), e);
		} catch (ConfigurationException e) {
			handleFatalError(e.getMessage(), e);
		}
		
		finally {
			if (debugRunner != null) {
				try {
					debugRunner.dispose();
				} catch (IOException e) {
					handleFatalError(e.getMessage(), e);
				}
			}
		}
	}

	private static IXSLDebugger createDebugger(String classname)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class clazz = Class.forName(classname);
		return (IXSLDebugger) clazz.newInstance();
	}

	private static void handleFatalError(String msg, Throwable t) {
		log.fatal(msg, t);
		System.exit(1);
	}
}
