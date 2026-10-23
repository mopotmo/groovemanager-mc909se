package com.groovemanager.pref;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;

import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * This class represents a PreferencePage for selecting the Theme
 * @author Manu Robledo
 *
 */
public class AppearancePrefPage extends FieldEditorPreferencePage{
	/**
	 * Construct a new AppearancePrefPage using "Appearance" as title
	 *
	 */
	public AppearancePrefPage() {
		this("Appearance");
	}
	/**
	 * Construct a new AppearancePrefPage using the given title
	 * @param title The page title
	 */
	public AppearancePrefPage(String title){
		super(FieldEditorPreferencePage.GRID);
		setTitle(title);
	}
	protected void createFieldEditors() {
		String[][] themes = new String[Theme.THEMES.length][2];
		for (int i = 0; i < themes.length; i++) {
			themes[i][0] = Theme.THEMES[i].getName();
			themes[i][1] = Theme.THEMES[i].getId();
		}
		addField(new RadioGroupFieldEditor(ThemeManager.PREF_KEY, "Theme", 1, themes, getFieldEditorParent(), true));
		addField(new BooleanFieldEditor(ThemeManager.PREF_HIGH_CONTRAST, "High contrast waveforms (opaque, wider slice markers on black or white)", getFieldEditorParent()));
	}
}
