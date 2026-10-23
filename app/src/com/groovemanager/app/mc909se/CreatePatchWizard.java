/*
 * Created on 15.06.2004
 *
 */
package com.groovemanager.app.mc909se;

import org.eclipse.jface.wizard.Wizard;

/**
 * This class represents the wizard that will be shown in the CreatePatchDialog
 * when the user has exported a sample and wants to create a patch out of it.
 * @author Manu Robledo
 *
 */
public class CreatePatchWizard extends Wizard {
	/**
	 * The first page of this wizard
	 */
	SelectPatchPage selectPatchPage;
	/**
	 * The second page of this wizard
	 */
	WritePatchPage writePatchPage;
	/**
	 * The name of the patch to be created
	 */
	String patchName;
	/**
	 * The names of the patches in the card bank or null if unknown
	 */
	String[] cardPatchNames;
	/**
	 * Create a new CreatePatchWizard
	 * @param patchName The name of the patch
	 * @param cardPatchNames The names of the patches in the card bank or
	 * null if unknown
	 */
	public CreatePatchWizard(String patchName, String[] cardPatchNames) {
		setWindowTitle("Create patch for sample " + patchName);
		this.patchName = patchName;
		this.cardPatchNames = cardPatchNames;
	}
	public void addPages() {
		selectPatchPage = new SelectPatchPage("create", patchName);
		selectPatchPage.cardPatchNames = cardPatchNames;
		writePatchPage = new WritePatchPage("write");
		addPage(selectPatchPage);
		addPage(writePatchPage);
	}
	public boolean performFinish() {
		return true;
	}
}
