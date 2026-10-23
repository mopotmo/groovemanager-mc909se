package com.groovemanager.gui.theme;

/**
 * A ThemeListener is notified when the user selects another Theme
 * @author Manu Robledo
 *
 */
public interface ThemeListener {
	/**
	 * Apply the given theme. Called once on registration and again whenever
	 * the theme changes.
	 * @param theme The theme to apply
	 */
	public void applyTheme(Theme theme);
}
