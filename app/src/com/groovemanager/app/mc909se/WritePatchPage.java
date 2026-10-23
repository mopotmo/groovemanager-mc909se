/*
 * Created on 15.06.2004
 *
 */
package com.groovemanager.app.mc909se;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.groovemanager.core.FileManager;
import com.groovemanager.gui.custom.ThemeButton;

/**
 * This class represents a wizard page that is displayed to the user to tell him
 * that he has to write the patch now manually on the Mc909.
 * @author Manu Robledo
 *
 */
public class WritePatchPage extends WizardPage {
	/**
	 * The part the patch was created on. 
	 */
	private int part;
	/**
	 * The location of the patch on which the new patch is based
	 */
	private String location = "";
	/**
	 * Sends the patch again, if set
	 */
	Runnable resend;
	/**
	 * Create a new WritePatchPage
	 * @param pageName The page´s name
	 */
	public WritePatchPage(String pageName) {
		super(pageName);
	}
	/**
	 * Create a new WritePatchPage
	 * @param pageName The page´s name
	 * @param title The page title
	 * @param titleImage The title image
	 */
	public WritePatchPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}
	public void createControl(Composite parent) {
		updateMessage();
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, true));
		final Image writeImg = new Image(comp.getDisplay(), FileManager.getDefault().getRootPath("909screens/write.jpg"));
		
		Composite write = new Composite(comp, SWT.NONE);
		write.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(writeImg, 0, 0);
			}
		});
		GridData gd = new GridData();
		gd.heightHint = writeImg.getBounds().height;
		gd.widthHint = writeImg.getBounds().width;
		write.setLayoutData(gd);
		
		final ThemeButton retry = new ThemeButton(comp);
		retry.setText("Send again");
		retry.setToolTipText("Send the patch to the MC-909 again, e.g. after a MIDI problem");
		retry.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(resend == null) return;
				retry.setEnabled(false);
				BusyIndicator.showWhile(retry.getDisplay(), resend);
				retry.setEnabled(true);
			}
		});
		
		Label loadLabel = new Label(comp, SWT.WRAP);
		loadLabel.setText("In order to use the exportet samples, you will have to load the samples into RAM. This can be done either by rebooting the MC-909 or by selecting the samples in the Sample List and pressing Load (Shift + F3).");
		gd = new GridData();
		gd.widthHint = 400;
		loadLabel.setLayoutData(gd);
		
		final Image loadImg = new Image(comp.getDisplay(), FileManager.getDefault().getRootPath("909screens/load.gif"));
		Composite load = new Composite(comp, SWT.NONE);
		load.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(loadImg, 0, 0);
			}
		});
		gd = new GridData();
		gd.heightHint = loadImg.getBounds().height;
		gd.widthHint = loadImg.getBounds().width;
		load.setLayoutData(gd);
		
		setControl(comp);
	}
	/**
	 * Set the part and the location of the patch that has been created
	 * @param part The part (between 0 and 15) on which the patch has been created
	 * @param card true if the patch location is in the card bank, false for
	 * the user bank
	 * @param number The patch number (from 0)
	 */
	public void setPatch(int part, boolean card, int number){
		if(part > 15) part = 15;
		else if(part < 0) part = 0;
		this.part = part;
		location = (card ? "Card" : "User") + " patch " + (number + 1);
		updateMessage();
	}
	/**
	 * Tell the user where the patch is and how to keep it
	 */
	private void updateMessage(){
		setMessage("Part " + (part + 1) + " (MIDI channel " + (part + 1) + ") now plays the new patch, which is not stored yet. "
			+ "To keep it, write it on the MC-909, e.g. to " + location + ". Then click Finish.\n"
			+ "If part " + (part + 1) + " didn't change: check that it receives on MIDI channel " + (part + 1) + " and that the MC-909 accepts SysEx messages, then click \"Send again\".");
	}
}
