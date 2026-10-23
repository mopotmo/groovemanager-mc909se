package com.groovemanager.gui;

import org.eclipse.swt.layout.GridLayout;

/**
 * Common spacing values, so that all parts of the editors use the same
 * margins and gaps
 * @author Manu Robledo
 *
 */
public class Spacing {
	/**
	 * Margin between the window border and its contents
	 */
	public final static int MARGIN = 8,
	/**
	 * Gap between the main areas of a window
	 */
	GAP = 8,
	/**
	 * Spacing between the controls inside an area
	 */
	CONTROL = 6,
	/**
	 * Spacing between buttons stacked on top of each other
	 */
	BUTTON = 4,
	/**
	 * Corner radius of all rounded elements (pads, buttons, frames)
	 */
	RADIUS = 4;
	/**
	 * Create a GridLayout without margins, using the CONTROL spacing
	 * @param columns The number of columns
	 * @return The new GridLayout
	 */
	public static GridLayout grid(int columns){
		GridLayout gl = new GridLayout(columns, false);
		gl.marginWidth = gl.marginHeight = 0;
		gl.horizontalSpacing = gl.verticalSpacing = CONTROL;
		return gl;
	}
}
