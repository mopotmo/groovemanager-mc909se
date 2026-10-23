package com.groovemanager.gui.custom;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleActionAdapter;
import org.eclipse.swt.accessibility.AccessibleActionEvent;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.groovemanager.gui.Spacing;
import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeListener;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * A push button that is a native Button in most themes and is drawn like a
 * key of the MC-909 in themes with device buttons. It switches between both
 * when the theme changes.
 * @author Manu Robledo
 *
 */
public class ThemeButton extends Composite implements ThemeListener{
	/**
	 * Height of a device button
	 */
	protected final static int HEIGHT = 28,
	/**
	 * Horizontal padding of the text on a device button
	 */
	PADDING = 12;
	/**
	 * The native button, <code>null</code> while the button is drawn
	 */
	protected Button button;
	/**
	 * The text shown on the button
	 */
	protected String text = "";
	/**
	 * The image shown on the button
	 */
	protected Image image;
	/**
	 * true while the mouse button is held down on a drawn button
	 */
	protected boolean pressed;
	/**
	 * Bold font for the text of drawn buttons
	 */
	protected Font boldFont;
	/**
	 * The registered SelectionListeners
	 */
	protected ArrayList listeners = new ArrayList();
	/**
	 * Construct a new ThemeButton
	 * @param parent The parent composite
	 */
	public ThemeButton(Composite parent){
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(button == null) paintKey(e.gc);
			}
		});
		// keyboard: Tab moves the focus, Space and Return press the button
		addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if(button != null) return;
				if(e.detail == SWT.TRAVERSE_RETURN){
					e.doit = false;
					fireSelection();
				}
				else if(e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) e.doit = true;
			}
		});
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(button == null && e.character == ' ') fireSelection();
			}
			public void keyReleased(KeyEvent e) {
			}
		});
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				redraw();
			}
			public void focusLost(FocusEvent e) {
				redraw();
			}
		});
		// screen readers announce a drawn button like a native one
		getAccessible().addAccessibleListener(new AccessibleAdapter() {
			public void getName(AccessibleEvent e) {
				e.result = text.length() > 0 ? text : getToolTipText();
			}
		});
		getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			public void getRole(AccessibleControlEvent e) {
				e.detail = ACC.ROLE_PUSHBUTTON;
			}
			public void getState(AccessibleControlEvent e) {
				e.detail = ACC.STATE_FOCUSABLE;
				if(isFocusControl()) e.detail |= ACC.STATE_FOCUSED;
				if(!isEnabled()) e.detail |= ACC.STATE_DISABLED;
			}
		});
		getAccessible().addAccessibleActionListener(new AccessibleActionAdapter() {
			public void getActionCount(AccessibleActionEvent e) {
				e.count = 1;
			}
			public void getName(AccessibleActionEvent e) {
				e.result = "press";
			}
			public void doAction(AccessibleActionEvent e) {
				if(isEnabled()) fireSelection();
				e.result = ACC.OK;
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if(button != null || !isEnabled() || e.button != 1) return;
				pressed = true;
				redraw();
			}
			public void mouseUp(MouseEvent e) {
				if(!pressed) return;
				pressed = false;
				redraw();
				Point size = getSize();
				if(e.x >= 0 && e.y >= 0 && e.x < size.x && e.y < size.y) fireSelection();
			}
		});
		ThemeManager.getDefault().addThemeListener(this, this);
	}
	/**
	 * Switch between the native button and the drawn key
	 * @see com.groovemanager.gui.theme.ThemeListener#applyTheme(com.groovemanager.gui.theme.Theme)
	 */
	public void applyTheme(Theme theme) {
		if(theme.hasDeviceButtons()){
			if(button != null){
				button.dispose();
				button = null;
			}
		}
		else if(button == null){
			button = new Button(this, SWT.PUSH);
			button.setText(text);
			button.setImage(image);
			button.setEnabled(isEnabled());
			button.setToolTipText(getToolTipText());
			button.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					fireSelection();
				}
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		layout();
		redraw();
	}
	/**
	 * Draw the button as a flat key in the colors of the MC-909, with the
	 * same thin outline as the frames of the content areas
	 * @param gc The GC to draw with
	 */
	protected void paintKey(GC gc){
		Theme theme = ThemeManager.getDefault().getTheme();
		Rectangle r = getClientArea();
		int arc = 2 * Spacing.RADIUS;
		gc.setAntialias(SWT.ON);
		gc.setBackground(theme.getButtonFace(pressed));
		gc.fillRoundRectangle(r.x, r.y, r.width, r.height, arc, arc);
		gc.setForeground(theme.getButtonEdge());
		gc.drawRoundRectangle(r.x, r.y, r.width - 1, r.height - 1, arc, arc);
		if(isFocusControl()){
			// a focus ring in the system accent color, inside the button
			gc.setLineWidth(2);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
			gc.drawRoundRectangle(r.x + 1, r.y + 1, r.width - 2, r.height - 2, arc, arc);
			gc.setLineWidth(1);
		}
		int cy = r.y + r.height / 2;
		if(image != null){
			Rectangle b = image.getBounds();
			gc.drawImage(image, r.x + (r.width - b.width) / 2, cy - b.height / 2);
		}
		else{
			gc.setFont(getBoldFont());
			gc.setForeground(theme.getButtonText(isEnabled()));
			Point extent = gc.textExtent(text);
			gc.drawText(text, r.x + (r.width - extent.x) / 2, cy - extent.y / 2, true);
		}
	}
	/**
	 * Get the bold font for drawn buttons
	 * @return The bold font
	 */
	protected Font getBoldFont(){
		if(boldFont == null){
			FontData[] fd = getFont().getFontData();
			for (int i = 0; i < fd.length; i++) {
				fd[i].setStyle(SWT.BOLD);
			}
			boldFont = new Font(getDisplay(), fd);
		}
		return boldFont;
	}
	/**
	 * Notify the SelectionListeners
	 */
	protected void fireSelection(){
		Event e = new Event();
		e.widget = this;
		e.display = getDisplay();
		SelectionEvent se = new SelectionEvent(e);
		ArrayList copy = new ArrayList(listeners);
		for (Iterator iter = copy.iterator(); iter.hasNext();) {
			((SelectionListener) iter.next()).widgetSelected(se);
		}
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if(button != null) return button.computeSize(wHint, hHint, changed);
		int width;
		if(image != null) width = image.getBounds().width + 2 * PADDING;
		else{
			GC gc = new GC(this);
			gc.setFont(getBoldFont());
			width = gc.textExtent(text).x + 2 * PADDING;
			gc.dispose();
		}
		return new Point(wHint == SWT.DEFAULT ? width : wHint, hHint == SWT.DEFAULT ? HEIGHT : hHint);
	}
	/**
	 * Add a SelectionListener, notified when the button is pressed
	 * @param l The SelectionListener to add
	 */
	public void addSelectionListener(SelectionListener l){
		listeners.add(l);
	}
	/**
	 * Remove a SelectionListener
	 * @param l The SelectionListener to remove
	 */
	public void removeSelectionListener(SelectionListener l){
		listeners.remove(l);
	}
	/**
	 * Set the text shown on the button
	 * @param text The text
	 */
	public void setText(String text){
		this.text = text == null ? "" : text;
		if(button != null) button.setText(this.text);
		redraw();
	}
	/**
	 * Get the text shown on the button
	 * @return The text
	 */
	public String getText(){
		return text;
	}
	/**
	 * Set the image shown on the button
	 * @param image The image or <code>null</code>
	 */
	public void setImage(Image image){
		this.image = image;
		if(button != null) button.setImage(image);
		redraw();
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
	 */
	public void setToolTipText(String string) {
		super.setToolTipText(string);
		if(button != null) button.setToolTipText(string);
	}
	/**
	 * Push buttons have no selection, kept for compatibility with Button
	 * @param selected Ignored
	 */
	public void setSelection(boolean selected){
		if(button != null) button.setSelection(selected);
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(button != null) button.setEnabled(enabled);
		if(!enabled) pressed = false;
		redraw();
	}
	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		if(boldFont != null) boldFont.dispose();
		super.dispose();
	}
}
