/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.dtd.core.internal.emf.DTDConstants;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDFactory;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.impl.DTDFactoryImpl;
import org.eclipse.wst.dtd.core.internal.saxparser.DTD;
import org.eclipse.wst.dtd.core.internal.saxparser.DTDParser;
import org.eclipse.wst.dtd.core.internal.saxparser.ErrorMessage;

/**
 * Create MOF objects from the parsed DTD If no DTD file is specifed, create
 * the root MOF object
 */
public class DTDUtil {
	DTDFactory factory;
	EList extent;
	Resource resource;
	DTDFile dtdFile;
	String dtdModelFile;
	boolean expandEntityReferences;

	private Hashtable externalDTDModels = new Hashtable();
	private Hashtable pePool = new Hashtable();
	private Hashtable elementPool = new Hashtable();

	private DTDParser parser;

	private Vector errorMsgs = new Vector();

	private static Hashtable utilCache = new Hashtable();

	static public DTDUtil getDTDUtilFor(String filename) {
		DTDUtil util = (DTDUtil) utilCache.get(filename);
		if (util == null) {
			util = new DTDUtil();
			utilCache.put(filename, util);
		}

		return util;
	}


	public void parse(String uri) {
		parse(new ResourceSetImpl(), uri);
	}

	/**
	 * Invoke parse to parse a .dtd file into a MOF object model This is
	 * invoked by the RegisteredDTDParser that registers this parser for MOF
	 * to use against the .dtd type
	 * 
	 * @param filename -
	 *            the fully qualifed name of a .dtd file
	 */
	public void parse(ResourceSet resources, String filename) {
		// Get the dtd name from the file name
		Path path = new Path(filename);
		IPath iPath = path.removeFileExtension();
		String dtdName = iPath.toFile().getName();

		try {
			parser = new DTDParser(false);
			if (expandEntityReferences) {
				parser.setExpandEntityReferences(expandEntityReferences);
			}
			parser.parse(filename);
		}
		catch (IOException ex) {
			ex.printStackTrace(System.err);
		}

		dtdModelFile = filename;

		factory = DTDFactoryImpl.instance();

		extent = new BasicEList();

		dtdFile = factory.createDTDFile();
		extent.add(dtdFile);

		dtdFile.setName(dtdName);

		populateDTD(resources, expandEntityReferences);
	}

	public DTDFactory getFactory() {
		return factory;
	}

	public DTDFile getDTDFile() {
		return dtdFile;
	}

	public Hashtable getPEPool() {
		return pePool;
	}

	public Hashtable getElementPool() {
		return elementPool;
	}

	public EList getContents() {
		return extent;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Resource getResource(Resource resource) {
		return resource;
	}

	public void setexpandEntityReferences(boolean expandEntityReferences) {
		this.expandEntityReferences = expandEntityReferences;
	}

	public boolean getExpandEntityReferences() {
		return expandEntityReferences;
	}

	/**
	 * Return true if the model has been modified
	 */
	public boolean isModelDirty() {
		if (resource != null) {
			return resource.isModified();
		}
		return false;
	}

	/**
	 * Save the .dtd file
	 */
	public boolean save() {
		try {
			Map options = new HashMap();
			resource.save(options);
			return true;
		}
		catch (Exception ex) {
			System.out.println("Save model exception " + ex); //$NON-NLS-1$
			ex.printStackTrace();
			return false;
		}
	}

	public boolean saveAs(String newFilename) {
		resource.setURI(URI.createURI(newFilename));
		boolean ok = save();

		if (ok) {
			Path path = new Path(newFilename);
			IPath iPath = path.removeFileExtension();
			String dtdName = iPath.toFile().getName();

			dtdFile.setName(dtdName);
		}
		return ok;
	}

	public boolean saveDTDFile(String filename) {
		boolean result = true;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(filename), true);
			DTDPrinter printer = new DTDPrinter(true);
			printer.visitDTDFile(dtdFile);

			String s = printer.getBuffer().toString();
			pw.println(s);
			pw.close();
		}

		catch (Exception e) {
			result = false;
		}
		return result;
	}

	public String getDTDSource(boolean includeError) {
		return dtdFile.unparse(includeError); // true = include error lines
	}

	/**
	 * Create MOF objects from DTD
	 */
	private void populateDTD(ResourceSet resources, boolean expandEntityReferences) {
		Vector dtdList = parser.getDTDList();

		// create XMIModel for the included DTDs
		if (dtdList.size() > 0) {
			if (!expandEntityReferences) {
				for (int i = dtdList.size() - 1; i > 0; i--) {
					DTD dtd = (DTD) dtdList.elementAt(i);
					loadIncludedDTD(resources, dtd);
				}

				DTD dtd = (DTD) dtdList.elementAt(0);
				populateDTD(resources, dtd, dtdFile); // populate the main
														// DTD
				// validateWithModel(dtd);
			}
			else {
				for (int i = dtdList.size() - 1; i >= 0; i--) {
					DTD dtd = (DTD) dtdList.elementAt(i);
					populateDTD(resources, dtd, dtdFile);
					// validateWithModel(dtd);
				}
			}
		}
	}

	// return the instance unique to this DTDUtil file that represents an
	// externally loaded dtd (will create if one doesn't exist yet)
	public ExternalDTDModel getExternalDTDModel(ResourceSet resources, String uri) {
		if (expandEntityReferences) {
			return null;
		}

		// DefaultMsgLogger.write("External DTD name: " + uri);

		if (externalDTDModels.containsKey(uri)) {
			return (ExternalDTDModel) externalDTDModels.get(uri);
		}
		else {
			ExternalDTDModel extModel = new ExternalDTDModel();
			if (extModel.loadModel(resources, uri)) {
				externalDTDModels.put(uri, extModel);
				return extModel;
			}
		}
		return null;
	}

	// load xmiModels for included DTDs
	private void loadIncludedDTD(ResourceSet resources, DTD dtd) {
		ExternalDTDModel extModel = getExternalDTDModel(resources, dtd.getName());
		if (extModel != null) {
			// now fill our hash tables for elements and parameter entities
			// so that we know what can be referenced by our main dtd
			DTDFile file = extModel.getExternalDTDFile();
			Collection elementList = file.listDTDElement();
			Collection entityList = file.listDTDEntity();

			Iterator i = elementList.iterator();
			while (i.hasNext()) {
				DTDObject object = (DTDObject) i.next();
				elementPool.put(getBaseName(object), object);
			}

			i = entityList.iterator();
			while (i.hasNext()) {
				DTDEntity entity = (DTDEntity) i.next();
				if (entity.isParameterEntity()) {
					pePool.put(getBaseName(entity), entity);
				}
			}
		}
	}

	/**
	 * Create MOF objects from DTD
	 * 
	 * 1) will create the corresponding DTD Mof object for EntityDecl,
	 * NotationDecl and ElementDecl declarations during the 1st pass 2) During
	 * the 2nd pass, it will add the contentModel and Attlist to all
	 * ElementDecl.
	 * 
	 */
	private void populateDTD(ResourceSet resources, DTD dtd, DTDFile dFile) {
		DTDModelBuilder modelBuilder = new DTDModelBuilder(resources, this, dtd, dFile);

		modelBuilder.visitDTD(dtd);
	}


	public void emptyErrorMessages() {
		errorMsgs.removeAllElements();
	}

	public void addErrorMessage(ErrorMessage error) {
		int vectorSize = errorMsgs.size();
		boolean add = true;

		if (vectorSize != 0) {
			int index = 0;

			while (add == true && index < vectorSize) {
				if (((ErrorMessage) errorMsgs.elementAt(index)).equals(error)) {
					add = false;
				}
				else {
					index++;
				}
			}
		}

		if (add) {
			errorMsgs.addElement(error);
		}
	}

	public Vector getErrors() {
		return errorMsgs;
	}

	// This gets the name without any pseudo namespace prefix
	public static String getBaseName(DTDObject obj) {
		return getName(obj, null);
	}

	// This gets the name with pseudo namespace prefixes if dtdFile is not
	// null
	public static String getName(DTDObject obj, DTDFile dtdFile) {
		String name = ""; //$NON-NLS-1$
		if (obj instanceof DTDEntity) {
			DTDEntity entity = (DTDEntity) obj;
			if (dtdFile != null && !entity.getDTDFile().equals(dtdFile)) {
				name = new Path(entity.getDTDFile().getName()).lastSegment() + ": "; //$NON-NLS-1$
			}
			name += "%" + ((DTDEntity) obj).getName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (obj instanceof DTDElement) {
			DTDElement element = (DTDElement) obj;
			if (dtdFile != null && !element.getDTDFile().equals(dtdFile)) {
				name = new Path(element.getDTDFile().getName()).lastSegment() + ": "; //$NON-NLS-1$
			}
			name += ((DTDElement) obj).getName();
		}
		else if (obj instanceof DTDElementContent) {
			return ((DTDElementContent) obj).getContentName();
		}
		return name;
	}

	public static String getGroupType(int groupKind, int occurrence) {
		String type = null;

		switch (groupKind) {
			case DTDGroupKind.SEQUENCE :
				type = "DTDSequence"; //$NON-NLS-1$
				break;
			case DTDGroupKind.CHOICE :
				type = "DTDChoice"; //$NON-NLS-1$
				break;
		}

		type += getRepeatableTypeSuffix(occurrence);
		return type;
	}

	public static String getReferenceType(int occurrence) {
		String type = "DTDReference"; //$NON-NLS-1$
		type += getRepeatableTypeSuffix(occurrence);
		return type;
	}

	private static String getRepeatableTypeSuffix(int occurrence) {
		return "(" + (char) occurrence + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void main(String args[]) {
		System.out.println("\nStarting ..."); //$NON-NLS-1$

		if (args.length != 1)
			System.out.println("usage: DtdUtil inputfile.dtd"); //$NON-NLS-1$

		java.io.File inputFile = new File(args[0]);

		String dtdFileName = ""; //$NON-NLS-1$
		try {
			dtdFileName = inputFile.getCanonicalPath();
		}
		catch (IOException ex) {
		}

		String dtdModelName = dtdFileName;

		if (DTDConstants.DTD_EXTENSION.equals(new Path(dtdFileName).getFileExtension())) {
			dtdModelName = new Path(dtdFileName).removeFileExtension().lastSegment();
		}
		DTDUtil d2m = new DTDUtil();
		// TODO: fix port
		// d2m.parse(dtdFileName);
		try {
			d2m.saveAs(dtdModelName + "." + DTDConstants.DTD_XMI_EXTENSION); //$NON-NLS-1$
		}
		catch (Exception e) {
			System.out.println("Exception thrown during model save: " + e); //$NON-NLS-1$
		}
		System.out.println("Done."); //$NON-NLS-1$
	}

	public org.eclipse.core.runtime.IPath getPath() {
		Path currentDTDPath = new Path(dtdModelFile);
		if (currentDTDPath.segmentCount() > 1) {
			return currentDTDPath.removeLastSegments(1).addTrailingSeparator();
		}

		return new Path(""); //$NON-NLS-1$
	}

	// /**
	// * @generated
	// */
	// protected static DTDUtil getDTDUtilForGen(String filename) {
	//
	// DTDUtil util = (DTDUtil) utilCache.get(filename);
	// if (util == null)
	// {
	// util = new DTDUtil();
	// utilCache.put(filename, util);
	// }
	//    
	// return util;
	// }
	// /**
	// * @generated
	// */
	// protected void parseGen(String uri) {
	//                           
	// parse(new ResourceSetImpl(), uri);
	// }
	// /**
	// * Invoke parse to parse a .dtd file into a MOF object model
	// * This is invoked by the RegisteredDTDParser that registers this parser
	// for MOF to use
	// * against the .dtd type
	// * @param filename - the fully qualifed name of a .dtd file
	// */
	// protected void parseGen(ResourceSet resources, String filename) {
	//
	// // Get the dtd name from the file name
	// Path path = new Path(filename);
	// IPath iPath = path.removeFileExtension();
	// String dtdName = iPath.toFile().getName();
	//
	//
	// try
	// {
	// parser = new DTDParser(false);
	// if (expandEntityReferences)
	// {
	// parser.setExpandEntityReferences(expandEntityReferences);
	// }
	// parser.parse(filename);
	// }
	// catch(IOException ex)
	// {
	// ex.printStackTrace(System.err);
	// }
	//
	//
	// dtdModelFile = filename;
	//
	//
	// factory = DTDFactoryImpl.instance();
	//
	//
	// extent = new BasicEList();
	//
	//
	// dtdFile = factory.createDTDFile();
	// extent.add(dtdFile);
	//
	//
	// dtdFile.setName(dtdName);
	//
	//
	// populateDTD(resources, expandEntityReferences);
	// }
	// /**
	// * @generated
	// */
	// protected DTDFactory getFactoryGen() {
	//
	// return factory;
	// }
	// /**
	// * @generated
	// */
	// protected DTDFile getDTDFileGen() {
	//
	// return dtdFile;
	// }
	// /**
	// * @generated
	// */
	// protected Hashtable getPEPoolGen() {
	//
	// return pePool;
	// }
	// /**
	// * @generated
	// */
	// protected Hashtable getElementPoolGen() {
	//
	// return elementPool;
	// }
	// /**
	// * @generated
	// */
	// protected EList getExtentGen() {
	//
	// return extent;
	// }
	// /**
	// * @generated
	// */
	// protected void setResourceGen(Resource resource) {
	//
	// this.resource = resource;
	// }
	// /**
	// * @generated
	// */
	// protected Resource getResourceGen(Resource resource) {
	//
	// return resource;
	// }
	// /**
	// * @generated
	// */
	// protected void setexpandEntityReferencesGen(boolean
	// expandEntityReferences) {
	//
	// this.expandEntityReferences = expandEntityReferences;
	// }
	// /**
	// * @generated
	// */
	// protected boolean getExpandEntityReferencesGen() {
	//
	// return expandEntityReferences;
	// }
	// /**
	// * Return true if the model has been modified
	// */
	// protected boolean isModelDirtyGen() {
	//
	// if (resource != null)
	// {
	// return resource.isModified();
	// }
	// return false;
	// }
	// /**
	// * Save the .dtd file
	// */
	// protected boolean saveGen() {
	//
	// try
	// {
	// resource.save();
	// return true;
	// }
	// catch (Exception ex)
	// {
	// System.out.println("Save model exception " + ex);
	// ex.printStackTrace();
	// return false;
	// }
	// }
	// /**
	// * @generated
	// */
	// protected boolean saveAsGen(String newFilename) {
	//
	// resource.setURI(newFilename);
	// boolean ok = save();
	//
	//
	// if (ok)
	// {
	// Path path = new Path(newFilename);
	// IPath iPath = path.removeFileExtension();
	// String dtdName = iPath.toFile().getName();
	//
	//
	// dtdFile.setName(dtdName);
	// }
	// return ok;
	// }
	// /**
	// * @generated
	// */
	// protected boolean saveDTDFileGen(String filename) {
	//
	// boolean result=true;
	// try
	// {
	// PrintWriter pw = new PrintWriter(new FileWriter(filename),true);
	// DTDPrinter printer = new DTDPrinter(true);
	// printer.visitDTDFile(dtdFile);
	//
	//
	// String s = printer.getBuffer().getName().toString();
	// pw.println(s);
	// pw.close();
	// }
	//
	//
	// catch (Exception e)
	// {
	// result = false;
	// }
	// return result;
	// }
	// /**
	// * @generated
	// */
	// protected String getDTDSourceGen(boolean includeError) {
	//
	// return dtdFile.unparse(includeError); // true = include error lines
	// }
	// /**
	// * Create MOF objects from DTD
	// */
	// protected void populateDTDGen(ResourceSet resources, boolean
	// expandEntityReferences) {
	//
	// Vector dtdList = parser.getDTDList();
	//
	//
	// // create XMIModel for the included DTDs
	// if ( dtdList.size() > 0)
	// {
	// if (!expandEntityReferences)
	// {
	// for (int i=dtdList.size() - 1; i > 0; i--)
	// {
	// DTD dtd = (DTD) dtdList.elementAt(i);
	// loadIncludedDTD(resources, dtd);
	// }
	//
	//
	// DTD dtd = (DTD) dtdList.elementAt(0);
	// populateDTD(resources, dtd, dtdFile); // populate the main DTD
	// // validateWithModel(dtd);
	// }
	// else
	// {
	// for (int i = dtdList.size() - 1; i >= 0; i--)
	// {
	// DTD dtd = (DTD) dtdList.elementAt(i);
	// populateDTD(resources, dtd, dtdFile);
	// // validateWithModel(dtd);
	// }
	// }
	// }
	// }
	// /**
	// * @generated
	// */
	// protected ExternalDTDModel getExternalDTDModelGen(ResourceSet
	// resources, String uri) {
	//
	// if (expandEntityReferences)
	// {
	// return null;
	// }
	//       
	// DefaultMsgLogger.write("External DTD name: " + uri);
	//
	//
	// if (externalDTDModels.containsKey(uri))
	// {
	// return (ExternalDTDModel) externalDTDModels.get(uri);
	// }
	// else
	// {
	// ExternalDTDModel extModel = new ExternalDTDModel();
	// if (extModel.loadModel(resources, uri))
	// {
	// externalDTDModels.put(uri, extModel);
	// return extModel;
	// }
	// }
	// return null;
	// }
	// /**
	// * @generated
	// */
	// protected void loadIncludedDTDGen(ResourceSet resources, DTD dtd) {
	//
	// ExternalDTDModel extModel = getExternalDTDModel(resources,
	// dtd.getName());
	// if (extModel != null)
	// {
	// // now fill our hash tables for elements and parameter entities
	// // so that we know what can be referenced by our main dtd
	// DTDFile file = extModel.getExternalDTDFile();
	// Collection elementList = file.listDTDElement();
	// Collection entityList = file.listDTDEntity();
	//
	//
	// Iterator i = elementList.iterator();
	// while (i.hasNext())
	// {
	// DTDObject object = (DTDObject) i.next();
	// elementPool.put(getBaseName(object), object);
	// }
	//      
	// i = entityList.iterator();
	// while (i.hasNext())
	// {
	// DTDEntity entity = (DTDEntity) i.next();
	// if (entity.isParameterEntity())
	// {
	// pePool.put(getBaseName(entity), entity);
	// }
	// }
	// }
	// }
	// /**
	// * Create MOF objects from DTD
	// *
	// * 1) will create the corresponding DTD Mof object for EntityDecl,
	// NotationDecl
	// * and ElementDecl declarations during the 1st pass
	// * 2) During the 2nd pass, it will add the contentModel and Attlist to
	// all
	// * ElementDecl.
	// *
	// */
	// protected void populateDTDGen(ResourceSet resources, DTD dtd, DTDFile
	// dFile) {
	//
	// DTDModelBuilder modelBuilder = new DTDModelBuilder(resources, this,
	// dtd, dFile);
	//
	//
	// modelBuilder.visitDTD(dtd);
	// }
	// /**
	// * @generated
	// */
	// protected void emptyErrorMessagesGen() {
	//
	// errorMsgs.removeAllElements();
	// }
	// /**
	// * @generated
	// */
	// protected void addErrorMessageGen(ErrorMessage error) {
	//
	// int vectorSize = errorMsgs.size();
	// boolean add = true;
	//
	//
	// if (vectorSize != 0)
	// {
	// int index = 0;
	//
	//
	// while (add == true && index < vectorSize)
	// {
	// if (((ErrorMessage) errorMsgs.elementAt(index)).equals(error))
	// {
	// add =false;
	// }
	// else
	// {
	// index++;
	// }
	// }
	// }
	//
	//
	// if (add)
	// {
	// errorMsgs.addElement(error);
	// }
	// }
	// /**
	// * @generated
	// */
	// protected Vector getErrorsGen() {
	//
	// return errorMsgs;
	// }
	// /**
	// * @generated
	// */
	// protected boolean validateDTDGen() {
	//
	// String tempDTDFile=getPath().toOSString()+"tempValidatingFileName.dtd";
	// try
	// {
	// saveDTDFile(tempDTDFile);
	//
	//
	// parser = new DTDParser(false);
	// if (FileUtility.fileExists(tempDTDFile))
	// {
	// parser.parse(tempDTDFile);
	// }
	// Vector dtdList = parser.getDTDList();
	// if ( dtdList.size() > 0)
	// {
	// // reset the errors and update what we have in the element pool
	// emptyErrorMessages();
	// updateElementHashtable();
	//
	//
	// // TODO: do we just validate the current one?
	// validateWithModel((DTD)dtdList.elementAt(0));
	// }
	// }
	// catch(IOException ex)
	// {
	// ex.printStackTrace(System.err);
	// FileUtility.delete(tempDTDFile);
	// }
	// FileUtility.delete(tempDTDFile);
	//
	//
	// return dtdFile.isParseError();
	// }
	// /**
	// * @generated
	// */
	// protected void validateWithModelGen(DTD dtd) {
	//
	// Enumeration en = dtd.externalElements();
	// while (en.hasMoreElements())
	// {
	// Object e = en.nextElement();
	//
	//
	// if ( e instanceof EntityDecl )
	// {
	// EntityDecl entity = (EntityDecl) e;
	// ErrorMessage errMsg = entity.getErrorMessage();
	// if ( errMsg!= null)
	// {
	// addErrorMessage(errMsg);
	// // dtdFile.setParseError(true);
	// }
	// }
	// else if ( e instanceof ElementDecl )
	// {
	// ElementDecl elem = (ElementDecl) e;
	// ErrorMessage errMsg = elem.getErrorMessage();
	// DTDElement dtdelement = (DTDElement)
	// getElementPool().get(elem.getNodeName());
	//
	//
	// if ( errMsg!= null)
	// {
	// addErrorMessage(errMsg);
	// }
	// checkForMissingElementReferences(dtdelement);
	// }
	// else if ( e instanceof NotationDecl )
	// {
	// NotationDecl notation = (NotationDecl) e;
	// ErrorMessage errMsg = notation.getErrorMessage();
	// if ( errMsg!= null)
	// {
	// addErrorMessage(errMsg);
	// }
	// }
	// else if ( e instanceof Attlist )
	// {
	// Attlist attList = (Attlist) e;
	// ErrorMessage errMsg = attList.getErrorMessage();
	// if ( errMsg!= null)
	// {
	// addErrorMessage(errMsg);
	// }
	// }
	// }
	//
	//
	// checkForDuplicateIDAttribute();
	// }
	// /**
	// * @generated
	// */
	// protected void updateElementHashtableGen() {
	//
	// elementPool.clear();
	// CreateListItems itemsHelper = new CreateListItems(dtdFile);
	//
	//
	// Vector allElements = itemsHelper.getElements();
	// // go through all the dtdFiles and list the elements
	// int numElements = allElements.size();
	//
	//
	// for (int i = 0; i < numElements; i++)
	// {
	// DTDElement element = (DTDElement) allElements.elementAt(i);
	// elementPool.put(getBaseName(element), element);
	// }
	// }
	// /**
	// * @generated
	// */
	// protected void updateEntityHashtableGen() {
	//
	// updateHashTable(pePool, dtdFile.listDTDEntity());
	// }
	// /**
	// * @generated
	// */
	// protected void updateHashTableGen(Hashtable h, Collection dtdObjects) {
	//
	// h.clear();
	// Iterator i = dtdObjects.iterator();
	// while (i.hasNext())
	// {
	// String name = "";
	// DTDObject o = (DTDObject) i.next();
	// name = getBaseName(o);
	// h.put(name,o);
	// }
	// }
	// /**
	// * @generated
	// */
	// protected static String getBaseNameGen(DTDObject obj) {
	//
	// return getName(obj, null);
	// }
	// /**
	// * @generated
	// */
	// protected static String getNameGen(DTDObject obj, DTDFile dtdFile) {
	//
	// String name = "";
	// if (obj instanceof DTDEntity)
	// {
	// DTDEntity entity = (DTDEntity) obj;
	// if (dtdFile != null &&
	// !entity.getDTDFile().equals(dtdFile))
	// {
	// name = new Path(entity.getDTDFile().getName()).lastSegment() + ": ";
	// }
	// name += "%" + ((DTDEntity)obj).getName() + ";";
	// }
	// else if (obj instanceof DTDElement)
	// {
	// DTDElement element = (DTDElement) obj;
	// if (dtdFile != null &&
	// !element.getDTDFile().equals(dtdFile))
	// {
	// name = new Path(element.getDTDFile().getName()).lastSegment() + ": ";
	// }
	// name += ((DTDElement)obj).getName();
	// }
	// else if (obj instanceof DTDElementContent)
	// {
	// return ((DTDElementContent) obj).getContentName();
	// }
	// return name;
	// }
	// /**
	// * @generated
	// */
	// protected void checkForMissingElementReferencesGen(DTDElement element)
	// {
	//
	// String dtdString = null;
	// if ( element == null )
	// return;
	//
	//
	// Vector elementRefs = new Vector();
	// findElementReferences(element.getContent(),elementRefs);
	//
	//
	// Enumeration en = elementRefs.elements();
	// while (en.hasMoreElements())
	// {
	// String elementRefName = "";
	// DTDObject dtdObject = (DTDObject) en.nextElement();
	//
	//
	// if ( dtdObject instanceof DTDElement)
	// {
	// elementRefName = ((DTDElement)dtdObject).getName();
	// if ( getElementPool().get(elementRefName) == null)
	// {
	// if (dtdString == null)
	// {
	// dtdString = getDTDSource(false);
	// }
	// String errorMsg = "Element \"" + element.getName() + "\", refers to
	// undeclared element "
	// + "\"" + elementRefName + "\", in content model.\n";
	// ErrorMessage dtdError = new ErrorMessage();
	// dtdError.setErrorMessage(errorMsg);
	// SourceLocationHelper.LineColumn lineColumn =
	// SourceLocationHelper.offsetToLineColumn(dtdString,
	// element.getStartOffset());
	// dtdError.setErrorLine(lineColumn.getLine());
	// dtdError.setErrorColumn(lineColumn.getColumn());
	// addErrorMessage(dtdError);
	// }
	// }
	// }
	// }
	// /**
	// * @generated
	// */
	// protected void findElementReferencesGen(DTDElementContent content,
	// Vector result) {
	//
	//
	// if (content instanceof DTDElementReferenceContent)
	// {
	// result.addElement(((DTDElementReferenceContent)content).getReferencedElement());
	// }
	// else if (content instanceof DTDEntityReferenceContent)
	// {
	// result.addElement(((DTDEntityReferenceContent)content).getElementReferencedEntity());
	// }
	// else if (content instanceof DTDGroupContent)
	// {
	// DTDGroupContent group = (DTDGroupContent)content;
	// EList contents = group.getContent();
	// Iterator i = contents.iterator();
	// while (i.hasNext())
	// {
	// findElementReferences((DTDElementContent) i.next(), result);
	// }
	// }
	// }
	// /**
	// * @generated
	// */
	// protected void checkForDuplicateIDAttributeGen() {
	//
	// String dtdString = null;
	// Collection elements = dtdFile.listDTDElement();
	// Iterator i = elements.iterator();
	// while (i.hasNext())
	// {
	// DTDElement element = (DTDElement) i.next();
	// Iterator k = element.getDTDAttribute().iterator();
	// boolean saw1stID = false;
	// while (k.hasNext())
	// {
	// DTDAttribute attr = (DTDAttribute) k.next();
	// DTDType dType = attr.getDTDType();
	// if (dType instanceof DTDBasicType)
	// {
	// if ( ((DTDBasicType)dType).getKind().intValue() == DTDBasicTypeKind.ID)
	// {
	// if (saw1stID)
	// {
	// if (dtdString == null)
	// {
	// dtdString = getDTDSource(false);
	// }
	// String errMsg = DTDPlugin.getDTDString("_ERROR_DUP_ID_ATTRIBUTE_1");
	// errMsg += attr.getName() +
	// DTDPlugin.getDTDString("_UI_ERRORPART_DUP_ID_ATTRIBUTE_2");
	//              
	// ErrorMessage dtdError = new ErrorMessage();
	// dtdError.setErrorMessage(errMsg);
	// SourceLocationHelper.LineColumn lineColumn =
	// SourceLocationHelper.offsetToLineColumn(dtdString,
	// attr.getStartOffset());
	// dtdError.setErrorLine(lineColumn.getLine());
	// dtdError.setErrorColumn(lineColumn.getColumn());
	// dtdError.setObject(attr);
	// addErrorMessage(dtdError);
	// // dtdFile.setParseError(true);
	// break;
	// }
	// else
	// {
	// saw1stID = true;
	// }
	// }
	// }
	// }
	// }
	// }
	// /**
	// * @generated
	// */
	// protected static String getGroupTypeGen(int groupKind, int occurrence)
	// {
	//
	// String type = null;
	//
	//
	// switch (groupKind)
	// {
	// case DTDGroupKind.SEQUENCE:
	// type = "DTDSequence";
	// break;
	// case DTDGroupKind.CHOICE:
	// type = "DTDChoice";
	// break;
	// }
	//
	//
	// type += getRepeatableTypeSuffix(occurrence);
	// return type;
	// }
	// /**
	// * @generated
	// */
	// protected static String getReferenceTypeGen(int occurrence) {
	//
	// String type = "DTDReference";
	// type += getRepeatableTypeSuffix(occurrence);
	// return type;
	// }
	// /**
	// * @generated
	// */
	// protected static String getRepeatableTypeSuffixGen(int occurrence) {
	//
	// return "(" + (char)occurrence + ")";
	// }
	// /**
	// * @generated
	// */
	// protected static void mainGen(String[] args) {
	//
	// System.out.println("\nStarting ...");
	//
	//
	// if (args.length != 1)
	// System.out.println("usage: DtdUtil inputfile.dtd");
	//
	//
	// java.io.File inputFile = new File(args[0]);
	//
	//
	// String dtdFileName = "";
	// try
	// {
	// dtdFileName = inputFile.getCanonicalPath();
	// }
	// catch(IOException ex)
	// {}
	//
	//
	// String dtdModelName = dtdFileName;
	//
	//
	// if (DTDConstants.DTD_EXTENSION.equals(new
	// Path(dtdFileName).getFileExtension()))
	// {
	// dtdModelName= new
	// Path(dtdFileName).removeFileExtension().lastSegment();
	// }
	// DTDUtil d2m = new DTDUtil();
	// // TODO: fix port
	// // d2m.parse(dtdFileName);
	// try
	// {
	// d2m.saveAs(dtdModelName + "." + DTDConstants.DTD_XMI_EXTENSION);
	// }
	// catch (Exception e)
	// {
	// System.out.println("Exception thrown during model save: " + e);
	// }
	// System.out.println("Done.");
	// }
	// /**
	// * @generated
	// */
	// protected org.eclipse.core.runtime.IPath getPathGen() {
	//
	// Path currentDTDPath = new Path(dtdModelFile);
	// if (currentDTDPath.segmentCount() > 1)
	// {
	// return currentDTDPath.removeLastSegments(1).addTrailingSeparator();
	// }
	//    
	// return new Path("");
	// }
}
