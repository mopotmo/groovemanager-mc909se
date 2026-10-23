/*
 * Created on 15.06.2004
 *
 */
package com.groovemanager.app.mc909se;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * This class is used in the CreatePatchWizard as first page 
 * @author Manu Robledo
 *
 */
public class SelectPatchPage extends WizardPage {
	/**
	 * Popup for selection of the part
	 */
	Combo partSelect,
	/**
	 * Popup for selection of the bank
	 */
	bankSelect,
	/**
	 * Popup for selection of the patch number
	 */
	patchSelect;
	/**
	 * The patch´s name
	 */
	String patchName;
	/**
	 * The names of the patches in the card bank or null if unknown
	 */
	String[] cardPatchNames;
	/**
	 * Hint about the shown patch names
	 */
	Label namesHint;
	/**
	 * Create a new SelectPatchPage
	 * @param pageName The page´s name
	 * @param patchName The patch´s name
	 */
	public SelectPatchPage(String pageName, String patchName) {
		super(pageName);
		this.patchName = patchName;
	}
	/**
	 * Create a new SelectPatchPage
	 * @param pageName The page´s name
	 * @param title The page title
	 * @param titleImage The title image
	 * @param patchName The patch´s name
	 */
	public SelectPatchPage(String pageName, String title,
			ImageDescriptor titleImage, String patchName) {
		super(pageName, title, titleImage);
		this.patchName = patchName;
	}
	public void createControl(Composite parent) {
		setMessage("Exit USB mode on the MC-909 and select the MIDI out port in the settings.\nThen select the part and the patch location of the patch to be created for Sample " + patchName);
		setTitle("Create patch for " + patchName);
		
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		
		new Label(comp, SWT.NONE).setText("Part (MIDI channel)");
		
		partSelect = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
		for(int i = 0; i < 16; i++){
			partSelect.add("" + (i + 1));
		}
		partSelect.select(0);
		
		new Label(comp, SWT.NONE).setText("Patch location");
		
		Composite patchComp = new Composite(comp, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		patchComp.setLayout(gl);
		patchComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		bankSelect = new Combo(patchComp, SWT.READ_ONLY | SWT.DROP_DOWN);
		bankSelect.add("User");
		bankSelect.add("Card");
		// Prefer the card if its patches are known, so that a free slot can
		// be suggested
		bankSelect.select(cardPatchNames != null ? 1 : 0);
		
		patchSelect = new Combo(patchComp, SWT.READ_ONLY | SWT.DROP_DOWN);
		patchSelect.add("1");
		patchSelect.select(0);
		
		new Label(comp, SWT.NONE);
		namesHint = new Label(comp, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		namesHint.setLayoutData(gd);
		
		bankSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updatePatchList();
			}
		});
		updatePatchList();
		setControl(comp);
	}
	/**
	 * Show the names of the patches of the selected bank, as far as they
	 * are known
	 */
	private void updatePatchList(){
		boolean card = bankSelect.getSelectionIndex() == 1;
		Mc909SoundFile.fillCombo(patchSelect, 256, card ? cardPatchNames : null, Mc909SoundFile.EMPTY_PATCH);
		if(!card) namesHint.setText("The names of the user patches can't be read from the MC-909.");
		else if(cardPatchNames == null) namesHint.setText("The card patches couldn't be read from the memory card.");
		else namesHint.setText("Card patch names as read from the memory card.");
		namesHint.getParent().layout(true, true);
	}
}
