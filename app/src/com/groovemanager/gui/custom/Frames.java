package com.groovemanager.gui.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.groovemanager.gui.Spacing;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * Draws the frames around the content areas of the editors (file lists,
 * property panels, waveform displays), so that all of them look the same
 * instead of using the different native borders
 * @author Manu Robledo
 *
 */
public class Frames {
	/**
	 * Room a framed Composite must leave between its border and its
	 * children, e.g. as layout margin
	 */
	public final static int INSET = 3;
	/**
	 * Draw the theme´s frame along the border of a Composite instead of a
	 * native border. The layout of the Composite must leave
	 * <code>INSET</code> pixels of room around its children.
	 * @param c The Composite to frame
	 */
	public static void add(final Composite c){
		c.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle r = c.getClientArea();
				draw(e.gc, r.x, r.y, r.width, r.height);
			}
		});
		c.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}
			public void controlResized(ControlEvent e) {
				c.redraw();
			}
		});
	}
	/**
	 * Draw a frame along the inside of the given area
	 * @param gc The GC to draw with
	 * @param x The x coordinate of the area
	 * @param y The y coordinate of the area
	 * @param width The width of the area
	 * @param height The height of the area
	 */
	public static void draw(GC gc, int x, int y, int width, int height){
		Color frame = ThemeManager.getDefault().getTheme().getFrame();
		if(frame == null) return;
		int antialias = gc.getAntialias();
		gc.setAntialias(SWT.ON);
		gc.setLineWidth(1);
		gc.setForeground(frame);
		gc.drawRoundRectangle(x, y, width - 1, height - 1, 2 * Spacing.RADIUS, 2 * Spacing.RADIUS);
		gc.setAntialias(antialias);
	}
	/**
	 * Fill the corners of an area outside of the rounded frame with the
	 * given color, so that square content looks rounded
	 * @param gc The GC to draw with
	 * @param width The width of the area
	 * @param height The height of the area
	 * @param outside The color around the area
	 */
	public static void clipCorners(GC gc, int width, int height, Color outside){
		if(outside == null) return;
		int r = Spacing.RADIUS;
		Path p = new Path(Display.getCurrent());
		p.addRectangle(0, 0, width, height);
		// the rounded rectangle, the even-odd rule leaves only the corners
		p.moveTo(r, 0);
		p.lineTo(width - r, 0);
		p.addArc(width - 2 * r, 0, 2 * r, 2 * r, 90, -90);
		p.lineTo(width, height - r);
		p.addArc(width - 2 * r, height - 2 * r, 2 * r, 2 * r, 0, -90);
		p.lineTo(r, height);
		p.addArc(0, height - 2 * r, 2 * r, 2 * r, 270, -90);
		p.lineTo(0, r);
		p.addArc(0, 0, 2 * r, 2 * r, 180, -90);
		p.close();
		int antialias = gc.getAntialias();
		gc.setAntialias(SWT.ON);
		gc.setFillRule(SWT.FILL_EVEN_ODD);
		gc.setBackground(outside);
		gc.fillPath(p);
		gc.setAntialias(antialias);
		p.dispose();
	}
}
