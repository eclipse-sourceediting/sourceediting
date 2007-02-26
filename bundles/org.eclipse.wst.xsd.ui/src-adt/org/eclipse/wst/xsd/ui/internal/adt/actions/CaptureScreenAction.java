/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
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
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class CaptureScreenAction extends Action
{

  private static String last_screen_capture_path = System.getProperty("user.home");
  private static String last_screen_capture_file_name = Messages._UI_ACTION_CAPTURE_SCREEN_DEFAULT_FILE_NAME;
  private static String last_screen_capture_file_extension = ".jpeg";

  public CaptureScreenAction()
  {
    setText(Messages._UI_CAPTURE_SCREEN_ACTION_TEXT);
    setToolTipText(Messages._UI_CAPTURE_SCREEN_ACTION_TOOLTIPTEXT);
    setImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/etool16/capturescreen.gif"));
  }

  public void run()
  {
    ImageExporter imageExporter = new ImageExporter();
    imageExporter.save(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), (GraphicalViewer) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getAdapter(GraphicalViewer.class));

  }

  public class ImageExporter
  {
    public boolean save(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath, int format)
    {
      Assert.isNotNull(editorPart, "null editorPart passed to ImageExporter.save"); //$NON-NLS-1$
      Assert.isNotNull(viewer, "null viewer passed to ImageExporter.save"); //$NON-NLS-1$
      Assert.isNotNull(saveFilePath, "null saveFilePath passed to ImageExporter.save"); //$NON-NLS-1$

      if (format != SWT.IMAGE_BMP && format != SWT.IMAGE_JPEG && format != SWT.IMAGE_ICO)
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

      String saveFilePath = getSaveFilePath(editorPart, viewer, -1);
      if (saveFilePath == null)
        return false;

      File file = new File(saveFilePath);
      if (file.exists() && file.isFile())
      {
        if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages._UI_ACTION_CAPTURE_SCREEN_OVERWRITE_CONFIRMATION_QUESTION, Messages._UI_ACTION_CAPTURE_SCREEN_OVERWRITE_CONFIRMATION_1 + " "
            + saveFilePath + " " + Messages._UI_ACTION_CAPTURE_SCREEN_OVERWRITE_CONFIRMATION_2))
        {
          return false;
        }
      }

      int format = SWT.IMAGE_JPEG;
      if (saveFilePath.endsWith(".jpeg")) //$NON-NLS-1$
        format = SWT.IMAGE_JPEG;
      else if (saveFilePath.endsWith(".bmp")) //$NON-NLS-1$
        format = SWT.IMAGE_BMP;
      else if (saveFilePath.endsWith(".ico")) //$NON-NLS-1$
        format = SWT.IMAGE_ICO;
      else if (saveFilePath.endsWith(".png")) //$NON-NLS-1$
        format = SWT.IMAGE_PNG;
      else if (saveFilePath.endsWith(".gif")) //$NON-NLS-1$
        format = SWT.IMAGE_GIF;
      else if (saveFilePath.endsWith(".tiff")) //$NON-NLS-1$
        format = SWT.IMAGE_TIFF;

      return save(editorPart, viewer, saveFilePath, format);

    }

    private String getSaveFilePath(IEditorPart editorPart, GraphicalViewer viewer, int format)
    {
      String filePath;

      FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);

      String[] filterExtensions = new String[] { "*.jpeg", "*.bmp", "*.tif" /*
                                                                             * ,"*.ico",
                                                                             * "*.png",
                                                                             * "*.gif"
                                                                             */}; //$NON-NLS-1$  //$NON-NLS-1$  //$NON-NLS-1$
      if (format == SWT.IMAGE_BMP)
        filterExtensions = new String[] { "*.bmp" }; //$NON-NLS-1$
      else if (format == SWT.IMAGE_JPEG)
        filterExtensions = new String[] { "*.jpeg" }; //$NON-NLS-1$
      else if (format == SWT.IMAGE_ICO)
        filterExtensions = new String[] { "*.ico" }; //$NON-NLS-1$
      else if (format == SWT.IMAGE_PNG)
        filterExtensions = new String[] { "*.png" }; //$NON-NLS-1$
      else if (format == SWT.IMAGE_GIF)
        filterExtensions = new String[] { "*.gif" }; //$NON-NLS-1$
      else if (format == SWT.IMAGE_TIFF)
        filterExtensions = new String[] { "*.tiff" }; //$NON-NLS-1$

      fileDialog.setFileName(obtainNextFileName());
      fileDialog.setFilterExtensions(filterExtensions);

      filePath = fileDialog.open();

      last_screen_capture_path = fileDialog.getFilterPath();
      String fileName = fileDialog.getFileName();
      last_screen_capture_file_name = fileName.substring(0, fileName.indexOf('.'));
      last_screen_capture_file_extension = fileName.substring(fileName.indexOf('.'));

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
      Rectangle rootFigureBounds = rootFigure.getBounds();

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

      int aux = last_screen_capture_file_name.length() - 1;
      while (Character.isDigit(last_screen_capture_file_name.charAt(aux)))
      {
        aux--;
      }

      String nonNumeratedfileName = last_screen_capture_file_name.substring(0, aux + 1);
      String filePath = last_screen_capture_path + System.getProperty("file.separator") + nonNumeratedfileName + last_screen_capture_file_extension;

      int counter = 1;
      File file = new File(filePath);
      while (file.exists())
      {
        filePath = last_screen_capture_path + System.getProperty("file.separator") + nonNumeratedfileName + counter++ + last_screen_capture_file_extension;
        file = new File(filePath);
      }

      return filePath;
    }

  }
}
