/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.design.layouts.FillLayout;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class CaptureScreenAction extends Action
{

  private static String LAST_SCREEN_CAPTURE_PATH = System.getProperty("user.home"); //$NON-NLS-1$
  private static String LAST_SCREEN_CAPTURE_FILE_NAME = Messages._UI_ACTION_CAPTURE_SCREEN_DEFAULT_FILE_NAME;
  private static String LAST_SCREEN_CAPTURE_FILE_EXTENSION = ".jpg"; //$NON-NLS-1$

  public CaptureScreenAction()
  {
    setText(Messages._UI_CAPTURE_SCREEN_ACTION_TEXT);
    setToolTipText(Messages._UI_CAPTURE_SCREEN_ACTION_TOOLTIPTEXT);
    setImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/etool16/capturescreen.gif")); //$NON-NLS-1$
    setDisabledImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/dtool16/capturescreen.gif")); //$NON-NLS-1$
    setAccelerator(SWT.CTRL | SWT.SHIFT | 'X'); //$NON-NLS-1$
  }

  public void run()
  {
    ImageExporter imageExporter = new ImageExporter();
    imageExporter.save(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), (GraphicalViewer) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getAdapter(GraphicalViewer.class));
  }

  public class ImageExporter
  {
    private static final String FILE_FORMATS = "*.jpeg;*.jfif;*.jpg;*.jpe;*.png;*.bmp;*.ico"; //$NON-NLS-1$
  	private static final String FILE_SEPARATOR = "file.separator"; //$NON-NLS-1$

	  public boolean save(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath, int format)
    {
      Assert.isNotNull(editorPart, "null editorPart passed to ImageExporter.save"); //$NON-NLS-1$
      Assert.isNotNull(viewer, "null viewer passed to ImageExporter.save"); //$NON-NLS-1$
      Assert.isNotNull(saveFilePath, "null saveFilePath passed to ImageExporter.save"); //$NON-NLS-1$

      if (format != SWT.IMAGE_BMP && format != SWT.IMAGE_JPEG && format != SWT.IMAGE_ICO && format != SWT.IMAGE_PNG)
        throw new IllegalArgumentException(Messages._UI_ACTION_CAPTURE_SCREEN_FORMAT_NOT_SUPPORTED);

      try
      {
        saveEditorContentsAsImage(editorPart, viewer, saveFilePath, format);
      }
      catch (Exception ex)
      {
        MessageDialog.openError(editorPart.getEditorSite().getShell(), Messages._UI_ACTION_CAPTURE_SCREEN_ERROR_TITLE, Messages._UI_ACTION_CAPTURE_SCREEN_ERROR_DESCRIPTION);
        return false;
      }

      return true;
    }

    public boolean save(IEditorPart editorPart, GraphicalViewer viewer)
    {
      Assert.isNotNull(editorPart, "null editorPart passed to ImageExporter.save"); //$NON-NLS-1$
      Assert.isNotNull(viewer, "null viewer passed to ImageExporter.save"); //$NON-NLS-1$

      String saveFilePath = getSaveFilePath(editorPart, viewer);
      if (saveFilePath == null)
        return false;

      File file = new File(saveFilePath);
      if (file.exists() && file.isFile())
      {
        if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages._UI_ACTION_CAPTURE_SCREEN_OVERWRITE_CONFIRMATION_QUESTION, Messages._UI_ACTION_CAPTURE_SCREEN_OVERWRITE_CONFIRMATION.replace("{0}", saveFilePath))) //$NON-NLS-1$
        {
          return false;
        }
      }

      int format = -1;
      String saveFilePathLowerCase = saveFilePath.toLowerCase(); 
      if (saveFilePathLowerCase.endsWith(".jpeg") || saveFilePathLowerCase.endsWith(".jpg") || saveFilePathLowerCase.endsWith(".jpe") || saveFilePathLowerCase.endsWith(".jfif")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    	format = SWT.IMAGE_JPEG;
      else if (saveFilePathLowerCase.endsWith(".bmp")) //$NON-NLS-1$
        format = SWT.IMAGE_BMP;
      else if (saveFilePathLowerCase.endsWith(".ico")) //$NON-NLS-1$
        format = SWT.IMAGE_ICO;
      else if (saveFilePathLowerCase.endsWith(".png")) //$NON-NLS-1$
        format = SWT.IMAGE_PNG;
      else if (saveFilePathLowerCase.endsWith(".gif")) //$NON-NLS-1$
        format = SWT.IMAGE_GIF;

      if(format != -1) {
    	  return save(editorPart, viewer, saveFilePath, format);
      } else {
    	  return false;
      }

    }

    private String getSaveFilePath(IEditorPart editorPart, GraphicalViewer viewer)
    {
      String filePath;

      FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);

      String[] filterExtensions = new String[] {FILE_FORMATS}; //$NON-NLS-1$

      fileDialog.setFileName(obtainNextFileName());
      fileDialog.setFilterExtensions(filterExtensions);
      fileDialog.setFilterNames(new String[] {FILE_FORMATS});
      fileDialog.setText(Messages._UI_ACTION_CAPTURE_SCREEN_FILE_SAVE_DIALOG_TITLE);
      
      filePath = fileDialog.open();

      if (filePath != null)
      {
	      LAST_SCREEN_CAPTURE_PATH = fileDialog.getFilterPath();
	      String fileName = fileDialog.getFileName();
	      if (fileName.indexOf('.') > 0) {
		      LAST_SCREEN_CAPTURE_FILE_NAME = fileName.substring(0, fileName.indexOf('.'));
		      LAST_SCREEN_CAPTURE_FILE_EXTENSION = fileName.substring(fileName.indexOf('.'));
	      }
	      else
	      {
		      LAST_SCREEN_CAPTURE_FILE_NAME = fileName;
		      LAST_SCREEN_CAPTURE_FILE_EXTENSION = ""; //$NON-NLS-1$
	      }
      }
      return filePath;
    }

    private void saveEditorContentsAsImage(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath, int format)
    {
      /*
       * 1. First get the figure whose visuals we want to save as image. So we
       * would like to save the rooteditpart which actually hosts all the
       * printable layers.
       * 
       * NOTE: ScalableRootEditPart manages layers and is registered
       * graphicalviewer's editpartregistry with the key LayerManager.ID ...
       * well that is because ScalableRootEditPart manages all layers that are
       * hosted on a FigureCanvas. Many layers exist for doing different things
       */
      ScalableRootEditPart rootEditPart = (ScalableRootEditPart) viewer.getEditPartRegistry().get(LayerManager.ID);
      IFigure rootFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);// rootEditPart.getFigure();
    
      Rectangle rootFigureBounds = new Rectangle(new Point(0,0),rootFigure.getPreferredSize());
      List rootEditPartChildren = rootEditPart.getChildren();
      Iterator rootEditPartChildrenIterator = rootEditPartChildren.iterator();
      while(rootEditPartChildrenIterator.hasNext()) {
    	  Object object = rootEditPartChildrenIterator.next();
    	  if(object instanceof AbstractGraphicalEditPart) {
    		  AbstractGraphicalEditPart childAbstractGraphicalEditPart = (AbstractGraphicalEditPart)object;
    		  List grandChildren = childAbstractGraphicalEditPart.getChildren();
    		  Iterator grandChildrenIterator = grandChildren.iterator();
    		  while(grandChildrenIterator.hasNext()) {
    			  AbstractGraphicalEditPart grandChildAbstractGraphicalEditPart = (AbstractGraphicalEditPart)grandChildrenIterator.next();
    			  IFigure figure = grandChildAbstractGraphicalEditPart.getFigure();
    			  LayoutManager layoutManager = figure.getLayoutManager();
    			  if(layoutManager instanceof FillLayout) {
    				  rootFigureBounds = rootFigure.getBounds();
    			  }
    		  }
    	  }
      }

      /*
       * 2. Now we want to get the GC associated with the control on which all
       * figures are painted by SWTGraphics. For that first get the SWT Control
       * associated with the viewer on which the rooteditpart is set as contents
       */
      Control figureCanvas = viewer.getControl();
      GC figureCanvasGC = new GC(figureCanvas);

      /*
       * 3. Create a new Graphics for an Image onto which we want to paint
       * rootFigure
       */
      Image img = new Image(null, rootFigureBounds.width, rootFigureBounds.height);
      GC imageGC = new GC(img);
      imageGC.setBackground(figureCanvasGC.getBackground());
      imageGC.setForeground(figureCanvasGC.getForeground());
      imageGC.setFont(figureCanvasGC.getFont());
      imageGC.setLineStyle(figureCanvasGC.getLineStyle());
      imageGC.setLineWidth(figureCanvasGC.getLineWidth());
      imageGC.setXORMode(figureCanvasGC.getXORMode());
      Graphics imgGraphics = new SWTGraphics(imageGC);

      /* 4. Draw rootFigure onto image. After that image will be ready for save */
      rootFigure.paint(imgGraphics);

      /* 5. Save image */
      ImageData[] imgData = new ImageData[1];
      imgData[0] = img.getImageData();

      ImageLoader imgLoader = new ImageLoader();
      imgLoader.data = imgData;
      imgLoader.save(saveFilePath, format);

      /* release OS resources */
      figureCanvasGC.dispose();
      imageGC.dispose();
      img.dispose();

    }

    String obtainNextFileName()
    {

      int aux = LAST_SCREEN_CAPTURE_FILE_NAME.length() - 1;
      while (Character.isDigit(LAST_SCREEN_CAPTURE_FILE_NAME.charAt(aux)))
      {
        aux--;
      }

      String nonNumeratedfileName = LAST_SCREEN_CAPTURE_FILE_NAME.substring(0, aux + 1);
      String filePath = LAST_SCREEN_CAPTURE_PATH + System.getProperty(FILE_SEPARATOR) + nonNumeratedfileName + LAST_SCREEN_CAPTURE_FILE_EXTENSION; //$NON-NLS-1$

      int counter = 1;
      File file = new File(filePath);
      while (file.exists())
      {
        filePath = LAST_SCREEN_CAPTURE_PATH + System.getProperty(FILE_SEPARATOR) + nonNumeratedfileName + counter++ + LAST_SCREEN_CAPTURE_FILE_EXTENSION; //$NON-NLS-1$
        file = new File(filePath);
      }

      return filePath;
    }

  }
}
