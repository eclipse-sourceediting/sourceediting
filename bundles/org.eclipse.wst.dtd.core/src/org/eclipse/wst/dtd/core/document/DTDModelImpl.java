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
package org.eclipse.wst.dtd.core.document;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.dtd.core.DTDFile;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.Entity;
import org.eclipse.wst.dtd.core.NodeList;
import org.eclipse.wst.dtd.core.event.IDTDFileListener;
import org.eclipse.wst.dtd.core.event.NodesEvent;
import org.eclipse.wst.dtd.core.util.DTDReferenceUpdater;
import org.eclipse.wst.dtd.core.util.LabelValuePair;
import org.eclipse.wst.sse.core.AbstractStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.events.NoChangeEvent;
import org.eclipse.wst.sse.core.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;


public final class DTDModelImpl extends AbstractStructuredModel implements IStructuredDocumentListener, DTDModel {

	public static boolean deleteFile(String fileName) {
		boolean result = false;

		// create the temp File object
		File file = new File(fileName);
		if (file.exists())
			result = file.delete();
		return result;
	}

	private DTDFile document;

	//private List errorMessages = new ArrayList();

	// entity reference names found in the conditional IGNORE sections
	private Vector ignoredEntityRefs;

	private boolean refreshRequired = false;

	protected DTDReferenceUpdater refUpdater = new DTDReferenceUpdater();

	public DTDModelImpl() {
		super();
		document = new DTDFile(this);
		document.addDTDFileListener(new IDTDFileListener() {

			public void nodeChanged(DTDNode node) {
				if (node instanceof Entity) {
					Entity entity = (Entity) node;
					if (entity.isParameterEntity() && entity.isExternalEntity()) {
						// just say they have changed for now
						setReferencedModelsChanged();
					}
				}
			}

			public void nodesAdded(NodesEvent event) {
				checkIfExternalReferencesChanged(event);
			}

			public void nodesRemoved(NodesEvent event) {
				checkIfExternalReferencesChanged(event);
			}
		});
	}

	public void beginRecording(Object requester, String label) {
		super.beginRecording(requester, label);
		// clear reference updater cache
		getReferenceUpdater().clearCache();
	}

	private void checkIfExternalReferencesChanged(NodesEvent event) {
		Iterator iter = event.getNodes().iterator();
		while (iter.hasNext()) {
			DTDNode node = (DTDNode) iter.next();
			if (node instanceof Entity) {
				Entity entity = (Entity) node;
				if (entity.isParameterEntity() && entity.isExternalEntity()) {
					// just say they have changed for now
					setReferencedModelsChanged();
				}
			}
		}
	}

	//
	// The following function helps determine the list of things that
	// can be used in a parameter entity reference content
	// Optional parameter is to allow the currently used DTDEntity to
	// be included in the combobox.
	//
	public LabelValuePair[] createParmEntityContentItems(Entity entity) {
		NodeList entities = getDTDFile().getEntities();

		Vector items = new Vector();

		if (entity != null) {
			String name = "%" + entity.getName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$
			items.addElement(new LabelValuePair(name, name));
		}

		for (Iterator i = entities.getNodes().iterator(); i.hasNext();) {
			Entity entityAt = (Entity) i.next();
			if (entityAt.isParameterEntity() && entityAt.isExternalEntity()) {
				String name = "%" + entityAt.getName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$
				items.addElement(new LabelValuePair(name, name));
			}
		}
		LabelValuePair[] comboArray = new LabelValuePair[items.size()];
		items.copyInto(comboArray);
		return comboArray;
	}

	public void endRecording(Object requester) {
		super.endRecording(requester);
		// clear reference updater cache
		getReferenceUpdater().clearCache();
	}


	public DTDFile getDTDFile() {
		return document;
	}

	// Returns entity reference names that are in
	// the conditional IGNORE sections.
	public Vector getIgnoredEntityRefs() {
		if (ignoredEntityRefs == null)
			ignoredEntityRefs = new Vector();
		return ignoredEntityRefs;
	}

	public IndexedRegion getIndexedRegion(int offset) {
		if (this.document == null)
			return null;
		//    System.out.println("getNode at " + offset + " returning = " +
		// this.document.getNodeAt(offset));

		return this.document.getNodeAt(offset);
	}

	public DTDReferenceUpdater getReferenceUpdater() {
		return refUpdater;
	}

	public boolean isReferencedModelsChanged() {
		return refreshRequired;
	}

	public boolean isRefreshRequired() {
		return refreshRequired;
	}

	public void newModel(NewDocumentEvent flatModelEvent) {
		document.newModel(flatModelEvent);
		//    System.out.println("\nnewmodel");
		outputStructuredDocument(flatModelEvent);
	}

	public void noChange(NoChangeEvent flatModelEvent) {
		//    System.out.println("\nnochange");
		outputStructuredDocument(flatModelEvent);

	}

	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent flatModelEvent) {
		//    System.out.println("\nnodesreplaced");
		document.nodesReplaced(flatModelEvent);
		outputStructuredDocument(flatModelEvent);

	}

	public void outputStructuredDocument(StructuredDocumentEvent flatModelEvent) {
		//      System.out.println("structuredDocument source = '" +
		// flatModelEvent.getStructuredDocument().getText() + "'");
		//      System.out.println("new String = '" +
		// flatModelEvent.getOriginalChanges() +"'");
		//      System.out.println("deleted String = '" +
		// flatModelEvent.getDeletedText() +"'");
		//      Enumeration e =
		// flatModelEvent.getStructuredDocument().getNodes().elements();
		//      int i = 0;
		//      for (; e.hasMoreElements(); i++)
		//      {
		//        BasicStructuredDocumentRegion node =
		// (BasicStructuredDocumentRegion) e.nextElement();
		//        outputStructuredDocumentRegion(node);
		//        System.out.println(" " + i +". " + node.hashCode() + " '"
		// +node.getText() + "'");
		//        }
	}

	public void outputStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		//      int size = flatNode.getNumberOfRegions();
		//      for (int i = 0; i < size; i++)
		//      {
		//        Region region = (Region) flatNode.getRegions().get(i);
		//        System.out.println(i + ". " + region.getType());

		//      } // end of for ()

	}

	public void regionChanged(RegionChangedEvent flatModelEvent) {
		//    System.out.println("\nregion changed");
		document.regionChanged(flatModelEvent);
		//      System.out.println("= " +
		// flatModelEvent.getStructuredDocumentRegion().getText());
		//      System.out.println("region changed " +
		// flatModelEvent.getRegion().hashCode() + " = " +
		// flatModelEvent.getRegion());

		outputStructuredDocument(flatModelEvent);
	}

	public void regionsReplaced(RegionsReplacedEvent flatModelEvent) {
		//    System.out.println("\nregion replaced");
		document.regionsReplaced(flatModelEvent);
		outputStructuredDocument(flatModelEvent);
	}

	public void setReferencedModelsChanged() {
		refreshRequired = true;
	}

	public void setRefreshRequired(boolean value) {
		refreshRequired = value;
	}


	/**
	 * @param newStructuredDocument
	 *            com.ibm.sed.structured.text.IStructuredDocument
	 */
	public void setStructuredDocument(IStructuredDocument newStructuredDocument) {
		IStructuredDocument oldStructuredDocument = super.getStructuredDocument();
		if (newStructuredDocument == oldStructuredDocument)
			return; // noting to do

		if (oldStructuredDocument != null)
			oldStructuredDocument.removeDocumentChangingListener(this);
		super.setStructuredDocument(newStructuredDocument);
		if (newStructuredDocument.getLength() > 0) {
			newModel(new NewDocumentEvent(newStructuredDocument, this));
		}
		if (newStructuredDocument != null)
			newStructuredDocument.addDocumentChangingListener(this);
		document.setStructuredDocument(newStructuredDocument);
	}

}
