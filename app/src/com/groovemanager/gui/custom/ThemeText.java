package com.groovemanager.gui.custom;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.groovemanager.gui.Spacing;
import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeListener;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * A single line text field that is a native bordered Text in most themes.
 * In themes with device buttons the native border can't be colored, so the
 * Text is created without border on a drawn field in the theme´s input
 * colors. The Text is created again when the theme changes, keeping its
 * content and listeners.
 * @author Manu Robledo
 *
 */
public class ThemeText extends Composite implements ThemeListener{
	/**
	 * Space between the drawn field and the text
	 */
	protected final static int PADDING_X = 6, PADDING_Y = 4;
	/**
	 * The current Text
	 */
	protected Text text;
	/**
	 * The style of the Text without SWT.BORDER
	 */
	protected final int style;
	/**
	 * true while the field is drawn
	 */
	protected boolean drawn;
	/**
	 * The maximum number of characters
	 */
	protected int limit = Text.LIMIT;
	/**
	 * The registered ModifyListeners and FocusListeners
	 */
	protected ArrayList modifyListeners = new ArrayList(), focusListeners = new ArrayList();
	/**
	 * Construct a new ThemeText
	 * @param parent The parent composite
	 * @param style The style of the Text, e.g. SWT.RIGHT
	 */
	public ThemeText(Composite parent, int style){
		super(parent, SWT.NONE);
		this.style = style & ~SWT.BORDER;
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paintField(e.gc);
			}
		});
		ThemeManager.getDefault().addThemeListener(this, this);
	}
	/**
	 * Create the Text in the style of the theme
	 * @see com.groovemanager.gui.theme.ThemeListener#applyTheme(com.groovemanager.gui.theme.Theme)
	 */
	public void applyTheme(Theme theme) {
		boolean d = theme.hasDeviceButtons();
		if(text == null || d != drawn){
			String content = "";
			boolean focus = false;
			if(text != null){
				content = text.getText();
				focus = text.isFocusControl();
				text.dispose();
			}
			drawn = d;
			text = new Text(this, drawn ? style : style | SWT.BORDER);
			text.setText(content);
			text.setTextLimit(limit);
			text.setEnabled(isEnabled());
			// the listeners are added after setText, so that they don't
			// see the copied content as a modification
			for (Iterator iter = modifyListeners.iterator(); iter.hasNext();) {
				text.addModifyListener((ModifyListener) iter.next());
			}
			for (Iterator iter = focusListeners.iterator(); iter.hasNext();) {
				text.addFocusListener((FocusListener) iter.next());
			}
			text.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					redraw();
				}
				public void focusLost(FocusEvent e) {
					redraw();
				}
			});
			FillLayout fl = new FillLayout();
			if(drawn){
				fl.marginWidth = PADDING_X;
				fl.marginHeight = PADDING_Y;
			}
			// the native border on macOS needs room for the frame drawn
			// around it
			else if(SWT.getPlatform().equals("cocoa")) fl.marginWidth = fl.marginHeight = 1;
			setLayout(fl);
			if(focus) text.setFocus();
		}
		if(drawn){
			text.setBackground(theme.getInputBackground());
			text.setForeground(theme.getInputForeground());
		}
		layout();
		redraw();
	}
	/**
	 * Draw the field behind a borderless Text, or a frame around the native
	 * Text on macOS, whose own border is barely visible
	 * @param gc The GC to draw with
	 */
	protected void paintField(GC gc){
		Rectangle r = getClientArea();
		gc.setAntialias(SWT.ON);
		if(!drawn){
			if(!SWT.getPlatform().equals("cocoa")) return;
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			gc.drawRoundRectangle(r.x, r.y, r.width - 1, r.height - 1, 8, 8);
			return;
		}
		Theme theme = ThemeManager.getDefault().getTheme();
		int arc = 2 * Spacing.RADIUS;
		gc.setBackground(theme.getInputBackground());
		gc.fillRoundRectangle(r.x, r.y, r.width, r.height, arc, arc);
		if(text != null && text.isFocusControl()){
			gc.setLineWidth(2);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
			gc.drawRoundRectangle(r.x + 1, r.y + 1, r.width - 2, r.height - 2, arc, arc);
			gc.setLineWidth(1);
		}
		else{
			gc.setForeground(theme.getButtonEdge());
			gc.drawRoundRectangle(r.x, r.y, r.width - 1, r.height - 1, arc, arc);
		}
	}
	/**
	 * Get the text
	 * @return The text
	 */
	public String getText(){
		return text.getText();
	}
	/**
	 * Set the text
	 * @param string The new text
	 */
	public void setText(String string){
		text.setText(string);
	}
	/**
	 * Set the maximum number of characters
	 * @param limit The maximum number of characters
	 */
	public void setTextLimit(int limit){
		this.limit = limit;
		text.setTextLimit(limit);
	}
	/**
	 * Add a ModifyListener, notified when the text changes
	 * @param l The ModifyListener to add
	 */
	public void addModifyListener(ModifyListener l){
		modifyListeners.add(l);
		text.addModifyListener(l);
	}
	/**
	 * Remove a ModifyListener
	 * @param l The ModifyListener to remove
	 */
	public void removeModifyListener(ModifyListener l){
		modifyListeners.remove(l);
		text.removeModifyListener(l);
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#addFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	public void addFocusListener(FocusListener l) {
		focusListeners.add(l);
		text.addFocusListener(l);
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#removeFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	public void removeFocusListener(FocusListener l) {
		focusListeners.remove(l);
		text.removeFocusListener(l);
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		text.setEnabled(enabled);
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#setFocus()
	 */
	public boolean setFocus() {
		return text.setFocus();
	}
}
