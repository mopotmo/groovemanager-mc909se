package com.groovemanager.gui.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

import com.groovemanager.gui.Spacing;
import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeListener;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * A section of the window (file list, properties, transport...) with one
 * child as its content. In themes with section headers it is a box with a
 * dark header bar carrying the title, like the labelled sections on the
 * MC-909. In the other themes it is either framed like before or not
 * visible at all.
 * @author Manu Robledo
 *
 */
public class Section extends Composite implements ThemeListener{
	/**
	 * Vertical padding of the title in the header bar and its minimum
	 * distance from the border
	 */
	protected final static int TITLE_PADDING_Y = 3, TITLE_PADDING_X = 8;
	/**
	 * The title shown in the header bar
	 */
	protected final String title;
	/**
	 * true, if the section is framed also without header
	 */
	protected final boolean framed;
	/**
	 * true while the header is shown
	 */
	protected boolean header;
	/**
	 * The bold font of the title
	 */
	protected Font titleFont;
	/**
	 * Construct a new Section
	 * @param parent The parent composite
	 * @param title The title for the header bar
	 * @param framed true, if the content should be framed in themes without
	 * section headers (e.g. lists), false for groups of buttons, which stay
	 * as they are in these themes
	 */
	public Section(Composite parent, String title, boolean framed){
		super(parent, SWT.NONE);
		this.title = title.toUpperCase();
		this.framed = framed;
		FontData[] fd = getFont().getFontData();
		for (int i = 0; i < fd.length; i++) {
			fd[i].setStyle(SWT.BOLD);
			fd[i].setHeight(Math.max(fd[i].getHeight() - 2, 9));
		}
		titleFont = new Font(getDisplay(), fd);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				titleFont.dispose();
			}
		});
		setLayout(new SectionLayout());
		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				redraw();
			}
		});
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paintSection(e.gc);
			}
		});
		ThemeManager.getDefault().addThemeListener(this, this);
	}
	/**
	 * Show or hide the header and set the background of the box
	 * @see com.groovemanager.gui.theme.ThemeListener#applyTheme(com.groovemanager.gui.theme.Theme)
	 */
	public void applyTheme(Theme theme) {
		header = theme.hasSectionHeaders();
		setBackground(header ? theme.getSectionBackground() : null);
		layout(true);
		redraw();
	}
	/**
	 * Get the height of the header bar
	 * @return The height of the header bar, 0 if there is none
	 */
	protected int getHeaderHeight(){
		if(!header) return 0;
		GC gc = new GC(this);
		gc.setFont(titleFont);
		int height = gc.getFontMetrics().getHeight();
		gc.dispose();
		return height + 2 * TITLE_PADDING_Y;
	}
	/**
	 * Get the room between the border and the content
	 * @return The margin in pixels
	 */
	protected int getMargin(){
		if(framed) return Frames.INSET;
		return header ? Spacing.CONTROL : 0;
	}
	/**
	 * Draw the box, the frame and the header bar
	 * @param gc The GC to draw with
	 */
	protected void paintSection(GC gc){
		if(!header && !framed) return;
		Rectangle r = getClientArea();
		Theme theme = ThemeManager.getDefault().getTheme();
		// the background of the box has square corners
		if(header) Frames.clipCorners(gc, r.width, r.height, theme.getPanelBackground());
		Frames.draw(gc, r.x, r.y, r.width, r.height);
		if(!header) return;
		int h = getHeaderHeight(), arc = 2 * Spacing.RADIUS;
		gc.setAntialias(SWT.ON);
		gc.setBackground(theme.getHeader(false));
		// rounded at the top like the frame, square at the bottom
		gc.fillRoundRectangle(r.x, r.y, r.width, h, arc, arc);
		gc.fillRectangle(r.x, r.y + h - Spacing.RADIUS, r.width, Spacing.RADIUS);
		gc.setFont(titleFont);
		gc.setForeground(theme.getHeader(true));
		// centered like the section labels on the device
		int x = r.x + Math.max(TITLE_PADDING_X, (r.width - gc.textExtent(title).x) / 2);
		gc.drawText(title, x, r.y + TITLE_PADDING_Y, true);
	}
	/**
	 * Places the first child below the header bar, leaving the margin
	 * @author Manu Robledo
	 *
	 */
	protected class SectionLayout extends Layout{
		/**
		 * @see org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite, int, int, boolean)
		 */
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			Control[] children = composite.getChildren();
			int margin = getMargin(), top = getHeaderHeight();
			Point size = new Point(0, 0);
			if(children.length > 0){
				int w = wHint == SWT.DEFAULT ? SWT.DEFAULT : Math.max(0, wHint - 2 * margin);
				int h = hHint == SWT.DEFAULT ? SWT.DEFAULT : Math.max(0, hHint - 2 * margin - top);
				size = children[0].computeSize(w, h, flushCache);
			}
			return new Point(wHint == SWT.DEFAULT ? size.x + 2 * margin : wHint, hHint == SWT.DEFAULT ? size.y + 2 * margin + top : hHint);
		}
		/**
		 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite, boolean)
		 */
		protected void layout(Composite composite, boolean flushCache) {
			Control[] children = composite.getChildren();
			if(children.length == 0) return;
			Rectangle r = composite.getClientArea();
			int margin = getMargin(), top = getHeaderHeight();
			children[0].setBounds(r.x + margin, r.y + margin + top, Math.max(0, r.width - 2 * margin), Math.max(0, r.height - 2 * margin - top));
		}
	}
}
