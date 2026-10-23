package com.groovemanager.gui.custom.mc909;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.groovemanager.gui.Spacing;
import com.groovemanager.gui.custom.CustomComposite;
import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeListener;
import com.groovemanager.gui.theme.ThemeManager;


/**
 * An Mc909Key represents a Composite surrounding a Mc909Pad. A number of
 * Mc909Keys can be packed into a Mc909KeyComposite.
 * @author Manu Robledo
 *
 */
public class Mc909Key extends CustomComposite{
	/**
	 * The Mc909Pad contained in this Key
	 */
	private Mc909Pad pad;
	/**
	 * Constants for different types of keys
	 */
	public final static int WHITE_BEGIN = 1, WHITE_MIDDLE = 2, WHITE_END = 3, BLACK_LEFT= 4, BLACK_MIDDLE = 5, BLACK_RIGHT = 6;
	/**
	 * The constant for the line width
	 */
	private final static int LINE_WIDTH = 3;
	/**
	 * Note names
	 */
	private final static String[] notes = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	/**
	 * The Label containing the note name and octave
	 */
	private Label note;
	/**
	 * true, if this key draws the left or right edge of the keyboard
	 */
	private boolean outerLeft, outerRight;
	/**
	 * Construct a new Mc909Key
	 * @param parent The parent Composite
	 * @param type The type of the key (one of the type constants, e.g.
	 * <code>WHITE_BEGIN</code>
	 * @param key The key number in the range from 0 to 127
	 */
	public Mc909Key(Composite parent, int type, int key){
		super(parent);
		drawKey(type);
		
		if(type == WHITE_BEGIN || type == WHITE_MIDDLE || type == WHITE_END) pad = new Mc909Pad(getComposite(), Mc909Pad.WHITE, key);
		else  pad = new Mc909Pad(getComposite(), Mc909Pad.BLACK, key);

		FormData fd = new FormData();
		fd.right = new FormAttachment(90, 0);
		fd.left = new FormAttachment(10, 0);
		fd.top = new FormAttachment(8, 0);
		fd.bottom = new FormAttachment(75, 0);
		
		pad.getComposite().setLayoutData(fd);
		
		note.setText(notes[key % 12] + " " + (key / 12 - 1));
	}
	/**
	 * Internal method for adding the PaintListener to the main composite
	 * @param type The type of this Key
	 */
	private void drawKey(final int type){
		getComposite().addPaintListener(new PaintListener(){

			public void paintControl(PaintEvent event) {
				GC gc = event.gc;
				Theme theme = ThemeManager.getDefault().getTheme();
				// all lines are filled rectangles, so that they are exactly
				// LINE_WIDTH pixels wide and meet the lines of the neighbour
				// keys without gaps
				gc.setBackground(theme.getKeyLine());
				Rectangle rect = getComposite().getClientArea();
				int l = LINE_WIDTH;
				int blackHeight = (int)Math.round(rect.height * 0.8);
				// obere Linie
				gc.fillRectangle(rect.x, rect.y, rect.width, l);
				// untere Linie
				gc.fillRectangle(rect.x, rect.y + rect.height - l, rect.width, l);
				
				int split = -1;
				switch(type){
					case WHITE_BEGIN:
						// Linie links, die rechte Taste zeichnet den Rest
						gc.fillRectangle(rect.x, rect.y, l - l / 2, rect.height);
						break;
						
					case WHITE_END:
					// Linie rechts
					gc.fillRectangle(rect.x + rect.width - l / 2, rect.y, l / 2, rect.height);
					break;
			
					case BLACK_LEFT:
					split = (int)Math.round(rect.width * 0.66);
					break;

					case BLACK_RIGHT:
					split = (int)Math.round(rect.width * 0.33);
					break;

					case BLACK_MIDDLE:
					split = (int)Math.round(rect.width * 0.5);
					break;
				}
				if(split >= 0){
					// Schwarze Taste und Trennlinie darunter
					gc.setBackground(theme.getKeyBlack());
					gc.setAntialias(SWT.ON);
					gc.fillRoundRectangle(rect.x, rect.y, rect.width, blackHeight, 2 * Spacing.RADIUS, 2 * Spacing.RADIUS);
					gc.setAntialias(SWT.OFF);
					gc.setBackground(theme.getKeyLine());
					gc.fillRectangle(rect.x + split - l / 2, rect.y + blackHeight, l, rect.height - blackHeight);
				}
				// outer edges of the whole keyboard
				if(outerLeft) gc.fillRectangle(rect.x, rect.y, l, rect.height);
				if(outerRight) gc.fillRectangle(rect.x + rect.width - l, rect.y, l, rect.height);
			}
		});
		
	}
	/**
	 * Draw the left edge of the keyboard on this key
	 * @param outer true, if this is the first key of the keyboard
	 */
	public void setOuterLeft(boolean outer){
		outerLeft = outer;
		getComposite().redraw();
	}
	/**
	 * Draw the right edge of the keyboard on this key
	 * @param outer true, if this is the last key of the keyboard
	 */
	public void setOuterRight(boolean outer){
		outerRight = outer;
		getComposite().redraw();
	}
	/**
	 * Set the visible status of the note name
	 * @param visible true, if the note name and octave should be visible,
	 * false otherwise
	 */
	public void setNoteVisible(boolean visible){
		note.setVisible(visible);
	}
	/**
	 * Get the Mc909Pad contained in this Key
	 * @return The Mc909Pad contained in this Key
	 */
	public Mc909Pad getPad(){
		return pad;
	}
	/**
	 * @see com.groovemanager.gui.custom.CustomComposite#createComposite(org.eclipse.swt.widgets.Composite, int)
	 */
	protected Composite createComposite(Composite parent, int style) {
		Composite comp = new Canvas(parent, SWT.NONE);
		
		comp.setLayout(new FormLayout());
		
		note = new Label(comp, SWT.CENTER);
		FormData lfd = new FormData();
		lfd.right = new FormAttachment(90, 0);
		lfd.left = new FormAttachment(10, 0);
		lfd.top = new FormAttachment(83, 0);
		note.setLayoutData(lfd);

		final Composite c = comp;
		ThemeManager.getDefault().addThemeListener(comp, new ThemeListener() {
			public void applyTheme(Theme theme) {
				c.setBackground(theme.getKeyWhite());
				note.setBackground(theme.getKeyWhite());
				note.setForeground(theme.getKeyLine());
			}
		});

		return comp;
	}
	/**
	 * @see com.groovemanager.gui.custom.CustomComposite#getPossibleStyles()
	 */
	protected int getPossibleStyles() {
		return SWT.NONE;
	}
	/**
	 * @see com.groovemanager.gui.custom.CustomComposite#getListenerTypes()
	 */
	protected int[] getListenerTypes() {
		return new int[0];
	}
}
