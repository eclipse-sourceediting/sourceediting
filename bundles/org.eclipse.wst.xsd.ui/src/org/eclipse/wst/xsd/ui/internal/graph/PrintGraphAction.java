/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.graph;
             
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


public class PrintGraphAction extends Action
{ 
  protected XSDComponentViewer componentViewer;

  public PrintGraphAction(XSDComponentViewer componentViewer)
  {
    super("Print");
    this.componentViewer = componentViewer;
  } 

  public void run()
  {
    try
    {
      PrintDialog dialog = new PrintDialog(Display.getCurrent().getActiveShell());
      PrinterData data = dialog.open();
      Printer printer = new Printer(data);
                                                   
      Control control = componentViewer.getControl();
      Display display = Display.getCurrent();
      Image graphImage = new Image(display, control.getSize().x, control.getSize().y);
      GC graphGC = new GC(control);
      graphGC.copyArea(graphImage, 0, 0);
        
      ImageData graphImageData = graphImage.getImageData();
      graphImageData.transparentPixel = -1;
        
      Point screenDPI = display.getDPI();
      Point printerDPI = printer.getDPI();
      int scaleFactor = printerDPI.x / screenDPI.x;
      Rectangle trim = printer.computeTrim(0, 0, 0, 0);
      if (printer.startJob("Print XML Schema Graph")) 
      {
        GC gc = new GC(printer);
        if (printer.startPage()) 
        {
          gc.drawImage(
            graphImage,
            0,
            0,
            graphImageData.width,
            graphImageData.height,
            -trim.x,
            -trim.y,
            scaleFactor * graphImageData.width,
            scaleFactor * graphImageData.height);
          printer.endPage();
        }
        printer.endJob();
      }
      printer.dispose();
      graphGC.dispose();
    }    
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
