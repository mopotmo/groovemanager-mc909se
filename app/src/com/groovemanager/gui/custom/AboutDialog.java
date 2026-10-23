package com.groovemanager.gui.custom;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.groovemanager.gui.Spacing;

/**
 * Dialog showing the name, version, history and license of the application,
 * with links to the project page and to the Ko-fi page for donations
 * @author Manu Robledo
 *
 */
public class AboutDialog extends Dialog {
	/**
	 * The project page
	 */
	public final static String PROJECT_URL = "https://github.com/mopotmo/groovemanager-mc909se";
	/**
	 * The Ko-fi page for donations
	 */
	public final static String KOFI_URL = "https://ko-fi.com/manurobledo";
	/**
	 * ID of the Ko-fi button
	 */
	protected final static int KOFI_ID = IDialogConstants.CLIENT_ID + 1;
	/**
	 * The name of the application
	 */
	protected final String appName;
	/**
	 * The bold, larger font of the name
	 */
	protected Font titleFont;
	/**
	 * Construct a new AboutDialog
	 * @param parentShell The parent Shell
	 * @param appName The name of the application
	 */
	public AboutDialog(Shell parentShell, String appName) {
		super(parentShell);
		this.appName = appName;
	}
	/**
	 * Get the version of the application, which the build writes into the
	 * manifest of the jar
	 * @return The version, or null when not run from a jar
	 */
	public static String getVersion(){
		return AboutDialog.class.getPackage().getImplementationVersion();
	}
	/**
	 * Open a web page in the default browser
	 * @param url The address of the page
	 */
	public static void openURL(String url){
		Program.launch(url);
	}
	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("About " + appName);
	}
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout layout = (GridLayout)comp.getLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 3 * Spacing.CONTROL;
		layout.verticalSpacing = Spacing.CONTROL;

		// the window icon of the application
		Image icon = getParentShell() == null ? null : getParentShell().getImage();
		Label iconLabel = new Label(comp, SWT.NONE);
		if(icon != null) iconLabel.setImage(icon);
		GridData gd = new GridData(SWT.CENTER, SWT.BEGINNING, false, false);
		gd.verticalSpan = 5;
		iconLabel.setLayoutData(gd);

		Label name = new Label(comp, SWT.NONE);
		name.setText(appName);
		FontData[] fd = name.getFont().getFontData();
		for (int i = 0; i < fd.length; i++) {
			fd[i].setStyle(SWT.BOLD);
			fd[i].setHeight(fd[i].getHeight() + 4);
		}
		titleFont = new Font(name.getDisplay(), fd);
		name.setFont(titleFont);
		name.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				titleFont.dispose();
			}
		});

		String version = getVersion();
		new Label(comp, SWT.NONE).setText(version == null ? "Development build" : "Version " + version);

		Label history = new Label(comp, SWT.WRAP);
		history.setText("Written in 2004 by Manu Robledo as the practical part of a diploma thesis "
				+ "at the Hochschule der Medien Stuttgart, revived in 2026 to run on current Java.");
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.widthHint = 360;
		history.setLayoutData(gd);

		new Label(comp, SWT.NONE).setText("Copyright 2004 Manu Robledo, MIT License");

		Link links = new Link(comp, SWT.NONE);
		links.setText("<a href=\"" + PROJECT_URL + "\">Project page on GitHub</a>");
		links.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openURL(e.text);
			}
		});
		return comp;
	}
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, KOFI_ID, "Support on Ko-fi", false);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if(buttonId == KOFI_ID) openURL(KOFI_URL);
		else super.buttonPressed(buttonId);
	}
}
