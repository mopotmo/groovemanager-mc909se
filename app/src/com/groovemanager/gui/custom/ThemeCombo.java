package com.groovemanager.gui.custom;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.groovemanager.gui.Spacing;
import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeListener;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * A read-only dropdown that is a native Combo in most themes. Native
 * dropdowns can't be colored on all platforms, so in themes with device
 * buttons it is drawn as a field in the theme´s input colors, which opens a
 * popup menu with the items.
 * @author Manu Robledo
 *
 */
public class ThemeCombo extends Composite implements ThemeListener{
	/**
	 * Horizontal padding of the drawn field and width of the chevron area
	 */
	protected final static int PADDING = 8, CHEVRON = 18;
	/**
	 * The native Combo, <code>null</code> while the dropdown is drawn
	 */
	protected Combo combo;
	/**
	 * The items
	 */
	protected ArrayList items = new ArrayList();
	/**
	 * The index of the selected item, -1 for none
	 */
	protected int selection = -1;
	/**
	 * The registered SelectionListeners
	 */
	protected ArrayList listeners = new ArrayList();
	/**
	 * Construct a new ThemeCombo
	 * @param parent The parent composite
	 */
	public ThemeCombo(Composite parent){
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(combo == null) paintField(e.gc);
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if(combo == null && isEnabled() && e.button == 1) openMenu();
			}
		});
		// keyboard: Tab moves the focus, Space and Return open the menu, the
		// arrow keys select the previous or next item
		addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if(combo != null) return;
				if(e.detail == SWT.TRAVERSE_RETURN){
					e.doit = false;
					openMenu();
				}
				else if(e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) e.doit = true;
			}
		});
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(combo != null) return;
				if(e.character == ' ') openMenu();
				else if(e.keyCode == SWT.ARROW_UP && selection > 0) select(selection - 1, true);
				else if(e.keyCode == SWT.ARROW_DOWN && selection < items.size() - 1) select(selection + 1, true);
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
		getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			public void getRole(AccessibleControlEvent e) {
				e.detail = ACC.ROLE_COMBOBOX;
			}
			public void getValue(AccessibleControlEvent e) {
				e.result = selection >= 0 ? (String)items.get(selection) : "";
			}
		});
		ThemeManager.getDefault().addThemeListener(this, this);
	}
	/**
	 * Switch between the native Combo and the drawn field
	 * @see com.groovemanager.gui.theme.ThemeListener#applyTheme(com.groovemanager.gui.theme.Theme)
	 */
	public void applyTheme(Theme theme) {
		if(theme.hasDeviceButtons()){
			if(combo != null){
				combo.dispose();
				combo = null;
			}
		}
		else if(combo == null){
			combo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
			for (Iterator iter = items.iterator(); iter.hasNext();) {
				combo.add((String) iter.next());
			}
			if(selection >= 0) combo.select(selection);
			combo.setEnabled(isEnabled());
			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					selection = combo.getSelectionIndex();
					fireSelection();
				}
			});
		}
		layout();
		redraw();
	}
	/**
	 * Draw the field: the selected item and a chevron on the theme´s input
	 * colors
	 * @param gc The GC to draw with
	 */
	protected void paintField(GC gc){
		Theme theme = ThemeManager.getDefault().getTheme();
		Rectangle r = getClientArea();
		int arc = 2 * Spacing.RADIUS;
		gc.setAntialias(SWT.ON);
		gc.setBackground(theme.getInputBackground());
		gc.fillRoundRectangle(r.x, r.y, r.width, r.height, arc, arc);
		if(isFocusControl()){
			gc.setLineWidth(2);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
			gc.drawRoundRectangle(r.x + 1, r.y + 1, r.width - 2, r.height - 2, arc, arc);
		}
		else{
			gc.setForeground(theme.getButtonEdge());
			gc.drawRoundRectangle(r.x, r.y, r.width - 1, r.height - 1, arc, arc);
		}
		gc.setForeground(isEnabled() ? theme.getInputForeground() : theme.getButtonText(false));
		if(selection >= 0){
			String s = (String)items.get(selection);
			Point extent = gc.textExtent(s);
			gc.drawText(s, r.x + PADDING, r.y + (r.height - extent.y) / 2, true);
		}
		// a small chevron pointing down
		gc.setLineWidth(2);
		int cx = r.x + r.width - CHEVRON / 2 - 2, cy = r.y + r.height / 2;
		gc.drawPolyline(new int[]{cx - 4, cy - 2, cx, cy + 2, cx + 4, cy - 2});
		gc.setLineWidth(1);
	}
	/**
	 * Open the popup menu with all items below the field
	 */
	protected void openMenu(){
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		for (int i = 0; i < items.size(); i++) {
			MenuItem item = new MenuItem(menu, SWT.RADIO);
			item.setText((String)items.get(i));
			item.setSelection(i == selection);
			final int index = i;
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if(((MenuItem)e.widget).getSelection()) select(index, true);
				}
			});
		}
		Point p = toDisplay(0, getSize().y);
		menu.setLocation(p);
		menu.setVisible(true);
	}
	/**
	 * Select an item
	 * @param index The index of the item
	 * @param notify true, if the SelectionListeners should be notified
	 */
	protected void select(int index, boolean notify){
		if(index == selection) return;
		selection = index;
		if(combo != null) combo.select(index);
		redraw();
		if(notify) fireSelection();
	}
	/**
	 * Select an item without notifying the SelectionListeners, like
	 * Combo.select()
	 * @param index The index of the item
	 */
	public void select(int index){
		select(index, false);
	}
	/**
	 * Get the index of the selected item
	 * @return The index of the selected item, -1 for none
	 */
	public int getSelectionIndex(){
		return selection;
	}
	/**
	 * Add an item at the end
	 * @param string The item to add
	 */
	public void add(String string){
		items.add(string);
		if(combo != null) combo.add(string);
		redraw();
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
	 * Add a SelectionListener, notified when another item is selected
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
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if(combo != null) return combo.computeSize(wHint, hHint, changed);
		GC gc = new GC(this);
		int width = 0;
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			width = Math.max(width, gc.textExtent((String) iter.next()).x);
		}
		gc.dispose();
		width += 2 * PADDING + CHEVRON;
		return new Point(wHint == SWT.DEFAULT ? width : wHint, hHint == SWT.DEFAULT ? ThemeButton.HEIGHT : hHint);
	}
	/**
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(combo != null) combo.setEnabled(enabled);
		redraw();
	}
}
