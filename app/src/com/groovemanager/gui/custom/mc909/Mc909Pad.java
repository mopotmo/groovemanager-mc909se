package com.groovemanager.gui.custom.mc909;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.groovemanager.gui.Spacing;
import com.groovemanager.gui.custom.CustomComposite;
import com.groovemanager.gui.custom.KeyboardKey;
import com.groovemanager.gui.custom.KeyboardKeyListener;
import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * A Mc909Pad is an implementation of the KeyboardKey the gives a visual
 * representation of one pad of the Roland MC-909. It can be wrapped into a
 * Mc909Key to have a black or white key surrounding the pad just like on the
 * MC-909 itself.
 * @author Manu Robledo
 *
 */
public class Mc909Pad extends CustomComposite implements KeyboardKey{
	/**
	 * true, if this pad is currently pressed, false otherwise
	 */
	private boolean pressed = false;
	/**
	 * Constant value for a pad being surrounded by a white key
	 */
	public static int WHITE = 1,
	/**
	 * Constant value for a pad being surrounded by a white key
	 */
	BLACK = 2;
	/**
	 * Constant for the line width
	 */
	private final static int LINE_WIDTH = 3;
	/**
	 * The type of this Pad (either <code>BLACK</code> or <code>WHITE</code>
	 */
	private final int type;
	/**
	 * If this value is true, a pad has to be clicked a second time to be
	 * released. otherwise it will be released on mouse release.
	 */
	private boolean hold;
	/**
	 * The Key value assigned to this pad
	 */
	private final int key;
	/**
	 * The KeyboardKeyListeners registered with this pad
	 */
	private ArrayList listeners = new ArrayList();
	/**
	 * Construct a new Mc909Pad
	 * @param parent The parent Composite
	 * @param type The pad type (either <code>BLACK</code> or
	 * <code>WHITE</code>)
	 * @param key The key value assigned to this pad from 0 to 127
	 */
	public Mc909Pad(Composite parent, int type, int key){
		super(parent);
		this.key = key;
		this.type = type;
	}
	/**
	 * 
	 * @see com.groovemanager.gui.custom.KeyboardKey#press()
	 */
	public void press() {
		if(pressed) return;
		pressed = true;
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			KeyboardKeyListener listener = (KeyboardKeyListener) iter.next();
			listener.keyPressed(key);
		}
		getComposite().redraw();
	}
	/**
	 * 
	 * @see com.groovemanager.gui.custom.KeyboardKey#isPressed()
	 */
	public boolean isPressed() {
		return pressed;
	}
	/**
	 * 
	 * @see com.groovemanager.gui.custom.KeyboardKey#release()
	 */
	public void release() {
		if(!pressed) return;
		pressed = false;
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			KeyboardKeyListener listener = (KeyboardKeyListener) iter.next();
			listener.keyReleased(key);
		}
		getComposite().redraw();
	}
	/**
	 * 
	 * @see com.groovemanager.gui.custom.KeyboardKey#setHold(boolean)
	 */
	public void setHold(boolean hold){
		this.hold = hold;
	}
	/**
	 * @see com.groovemanager.gui.custom.KeyboardKey#addKeyListener(com.groovemanager.gui.custom.KeyboardKeyListener)
	 */
	public void addKeyListener(KeyboardKeyListener listener) {
		listeners.add(listener);
	}
	/**
	 * @see com.groovemanager.gui.custom.KeyboardKey#removeKeyListener(com.groovemanager.gui.custom.KeyboardKeyListener)
	 */
	public void removeKeyListener(KeyboardKeyListener listener) {
		listeners.remove(listener);
	}
	/**
	 * @see com.groovemanager.gui.custom.KeyboardKey#getKey()
	 */
	public int getKey() {
		return key;
	}
	/**
	 * @see com.groovemanager.gui.custom.CustomComposite#createComposite(org.eclipse.swt.widgets.Composite, int)
	 */
	protected Composite createComposite(Composite parent, int style) {
		Canvas comp = new Canvas(parent, SWT.NONE);

		comp.addPaintListener(new PaintListener(){

			public void paintControl(PaintEvent event) {
				GC gc = event.gc;
				Theme theme = ThemeManager.getDefault().getTheme();
				Rectangle rect = getComposite().getClientArea();
				gc.setLineWidth(LINE_WIDTH);
				if(type == WHITE) gc.setBackground(theme.getKeyWhite());
				else gc.setBackground(theme.getKeyBlack());
				
				// Hintergrund
				gc.fillRectangle(rect);
				
				// Pad-Inhalt
				gc.setBackground(theme.getPad(pressed));
				gc.fillRoundRectangle(rect.x + 1, rect.y + 1, rect.width - 1, rect.height - 1, 2 * Spacing.RADIUS, 2 * Spacing.RADIUS);
				
				// Pad-Rahmen
				gc.setForeground(theme.getPadFrame());
				gc.drawRoundRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, 2 * Spacing.RADIUS, 2 * Spacing.RADIUS);
			}
			
		});
		
		comp.addMouseListener(new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if((!pressed)) press();
				else release();
			}
			public void mouseUp(MouseEvent e) {
				if(!hold) release();
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
	/**
	 * @see com.groovemanager.gui.custom.KeyboardKey#getHold()
	 */
	public boolean getHold() {
		return hold;
	}
}
