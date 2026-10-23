package com.groovemanager.gui.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.groovemanager.gui.Spacing;
import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeListener;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * Placeholder shown instead of an empty list or waveform, telling that
 * audio files can be dropped there. A dashed frame surrounds a title and a
 * smaller second line. A click on it is reported as selection, so that it
 * can open the file dialog.
 * @author Manu Robledo
 *
 */
public class DropHint extends Canvas implements ThemeListener{
	/**
	 * Style for the place of a list, using the colors of lists
	 */
	public final static int LIST = 0;
	/**
	 * Style for the place of a waveform, using the colors of the waveform
	 */
	public final static int WAVE = 1;
	/**
	 * Length of the dashes and room between the frame and the border
	 */
	protected final static int DASH = 6, INSET = 6;
	/**
	 * Which colors to use, LIST or WAVE
	 */
	protected final int kind;
	/**
	 * The title and the second line
	 */
	protected final String title, detail;
	/**
	 * The bold font of the title
	 */
	protected Font titleFont;
	/**
	 * Construct a new DropHint
	 * @param parent The parent Composite
	 * @param kind LIST or WAVE
	 * @param title The title, like "Drop audio files here"
	 * @param detail The smaller second line or <code>null</code>
	 */
	public DropHint(Composite parent, int kind, String title, String detail){
		super(parent, SWT.NONE);
		this.kind = kind;
		this.title = title;
		this.detail = detail;
		FontData[] fd = getFont().getFontData();
		for (int i = 0; i < fd.length; i++) {
			fd[i].setStyle(SWT.BOLD);
			if(kind == WAVE) fd[i].setHeight(fd[i].getHeight() + 4);
		}
		titleFont = new Font(getDisplay(), fd);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				titleFont.dispose();
			}
		});
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paint(e.gc);
			}
		});
		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				redraw();
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				if(e.button == 1 && getClientArea().contains(e.x, e.y)) notifyListeners(SWT.Selection, new Event());
			}
		});
		setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		setToolTipText(detail);
		ThemeManager.getDefault().addThemeListener(this, this);
	}
	/**
	 * Add a listener that is informed about clicks on this DropHint
	 * @param listener The SelectionListener
	 */
	public void addSelectionListener(final SelectionListener listener){
		addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				listener.widgetSelected(new SelectionEvent(e));
			}
		});
	}
	/**
	 * @see com.groovemanager.gui.theme.ThemeListener#applyTheme(com.groovemanager.gui.theme.Theme)
	 */
	public void applyTheme(Theme theme) {
		setBackground(getBack(theme));
		redraw();
	}
	/**
	 * Get the background color for the current theme
	 * @param theme The current theme
	 * @return The background color
	 */
	protected Color getBack(Theme theme){
		Color c = kind == WAVE ? theme.getWaveBackground() : theme.getInputBackground();
		return c == null ? getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND) : c;
	}
	/**
	 * Get the text color for the current theme
	 * @param theme The current theme
	 * @return The text color
	 */
	protected Color getFore(Theme theme){
		Color c = kind == WAVE ? theme.getWaveColor() : theme.getInputForeground();
		return c == null ? getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND) : c;
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(wHint == SWT.DEFAULT ? 100 : wHint, hHint == SWT.DEFAULT ? 60 : hHint);
	}
	/**
	 * Draw the frame and the centered text
	 * @param gc The GC to draw with
	 */
	protected void paint(GC gc){
		Theme theme = ThemeManager.getDefault().getTheme();
		Rectangle r = getClientArea();
		gc.setAntialias(SWT.ON);
		gc.setBackground(getBack(theme));
		gc.fillRectangle(r);
		gc.setForeground(getFore(theme));
		gc.setLineWidth(kind == WAVE ? 2 : 1);
		gc.setLineDash(new int[]{DASH, DASH});
		int arc = 2 * Spacing.RADIUS;
		gc.drawRoundRectangle(r.x + INSET, r.y + INSET, r.width - 2 * INSET - 1, r.height - 2 * INSET - 1, arc, arc);
		gc.setLineDash(null);
		gc.setLineWidth(1);

		// both lines are wrapped at the width inside the frame
		int width = Math.max(1, r.width - 4 * INSET);
		gc.setFont(titleFont);
		String[] t = wrap(gc, title, width);
		int lineT = gc.getFontMetrics().getHeight();
		gc.setFont(getFont());
		String[] d = detail == null ? new String[0] : wrap(gc, detail, width);
		int lineD = gc.getFontMetrics().getHeight();
		int height = t.length * lineT + (d.length > 0 ? Spacing.CONTROL + d.length * lineD : 0);
		int y = r.y + Math.max(2 * INSET, (r.height - height) / 2);
		gc.setFont(titleFont);
		for (int i = 0; i < t.length; i++, y += lineT) {
			gc.drawText(t[i], r.x + (r.width - gc.textExtent(t[i]).x) / 2, y, true);
		}
		y += Spacing.CONTROL;
		gc.setFont(getFont());
		for (int i = 0; i < d.length; i++, y += lineD) {
			gc.drawText(d[i], r.x + (r.width - gc.textExtent(d[i]).x) / 2, y, true);
		}
	}
	/**
	 * Break a text into lines at spaces, so that each line fits the width
	 * @param gc The GC with the font of the text
	 * @param text The text
	 * @param width The available width
	 * @return The lines
	 */
	protected static String[] wrap(GC gc, String text, int width){
		java.util.ArrayList lines = new java.util.ArrayList();
		String[] words = text.split(" ");
		String line = "";
		for (int i = 0; i < words.length; i++) {
			String next = line.length() == 0 ? words[i] : line + " " + words[i];
			if(line.length() > 0 && gc.textExtent(next).x > width){
				lines.add(line);
				line = words[i];
			}
			else line = next;
		}
		if(line.length() > 0) lines.add(line);
		return (String[])lines.toArray(new String[lines.size()]);
	}
}
