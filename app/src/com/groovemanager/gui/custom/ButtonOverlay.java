package com.groovemanager.gui.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.groovemanager.gui.theme.Theme;
import com.groovemanager.gui.theme.ThemeListener;
import com.groovemanager.gui.theme.ThemeManager;

/**
 * Shows a native push button of a dialog as a drawn ThemeButton in themes
 * with device buttons. The buttons of JFace dialogs and wizards (OK,
 * Cancel, Next, Apply...) are created by JFace and must stay native buttons,
 * so the native button is only hidden. The drawn button lies at its place
 * and passes clicks on to it, and takes over its text and enabled state.
 * @author Manu Robledo
 *
 */
public class ButtonOverlay implements ThemeListener, Runnable{
	/**
	 * Key of the Button data holding the ButtonOverlay
	 */
	protected final static String KEY = "com.groovemanager.gui.custom.buttonOverlay";
	/**
	 * Interval for taking over text and enabled state in milliseconds.
	 * SWT has no event for changes of them.
	 */
	protected final static int SYNC_INTERVAL = 150;
	/**
	 * Room between the drawn button and the bounds of the native one, which
	 * include the bezel on macOS
	 */
	protected final static int INSET = 2;
	/**
	 * true, once the Return key filter has been installed
	 */
	private static boolean returnFilter;
	/**
	 * The native button
	 */
	protected final Button button;
	/**
	 * The drawn button
	 */
	protected final ThemeButton face;
	/**
	 * The text and tooltip last taken over
	 */
	protected String text, toolTip;
	/**
	 * Show a native push button as drawn button in themes with device
	 * buttons. Only buttons laid out by a GridLayout (like the button bars of
	 * JFace and most dialogs) are supported, others stay native.
	 * @param b The native button
	 */
	public static void add(Button b){
		if(b.isDisposed() || b.getData(KEY) != null || (b.getStyle() & SWT.PUSH) == 0) return;
		if(b.getParent() instanceof ThemeButton || !(b.getParent().getLayout() instanceof GridLayout)) return;
		b.setData(KEY, new ButtonOverlay(b));
		if(!returnFilter){
			returnFilter = true;
			b.getDisplay().addFilter(SWT.Traverse, new Listener() {
				public void handleEvent(Event e) {
					pressDefaultButton(e);
				}
			});
		}
	}
	/**
	 * Construct a new ButtonOverlay
	 * @param b The native button
	 */
	protected ButtonOverlay(Button b){
		button = b;
		face = new ThemeButton(b.getParent());
		GridData gd = new GridData();
		gd.exclude = true;
		face.setLayoutData(gd);
		face.moveAbove(b);
		face.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!button.isDisposed() && button.isEnabled()) button.notifyListeners(SWT.Selection, new Event());
			}
		});
		b.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
				place();
			}
			public void controlResized(ControlEvent e) {
				place();
			}
		});
		b.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if(!face.isDisposed()) face.dispose();
			}
		});
		run();
		ThemeManager.getDefault().addThemeListener(face, this);
	}
	/**
	 * Show either the native or the drawn button
	 * @see com.groovemanager.gui.theme.ThemeListener#applyTheme(com.groovemanager.gui.theme.Theme)
	 */
	public void applyTheme(Theme theme) {
		if(button.isDisposed()) return;
		boolean drawn = theme.hasDeviceButtons();
		button.setVisible(!drawn);
		face.setVisible(drawn);
		place();
	}
	/**
	 * Lay the drawn button over the native one, as high as the drawn
	 * buttons of the main window
	 */
	protected void place(){
		if(button.isDisposed() || face.isDisposed()) return;
		Rectangle r = button.getBounds();
		int height = Math.min(r.height, ThemeButton.HEIGHT);
		face.setBounds(r.x + INSET, r.y + (r.height - height) / 2, Math.max(0, r.width - 2 * INSET), height);
	}
	/**
	 * Take over text and enabled state of the native button
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if(button.isDisposed() || face.isDisposed()) return;
		// mnemonics are not shown on the drawn button
		String t = button.getText().replaceAll("&(.)", "$1");
		if(!t.equals(text)){
			text = t;
			face.setText(t);
		}
		if(face.getEnabled() != button.isEnabled()) face.setEnabled(button.isEnabled());
		String tip = button.getToolTipText();
		if(tip == null ? toolTip != null : !tip.equals(toolTip)){
			toolTip = tip;
			face.setToolTipText(tip);
		}
		button.getDisplay().timerExec(SYNC_INTERVAL, this);
	}
	/**
	 * Press the default button of a dialog on Return while it is hidden
	 * behind a drawn button, as the platform only presses visible buttons
	 * @param e The traverse event
	 */
	protected static void pressDefaultButton(Event e){
		if(e.detail != SWT.TRAVERSE_RETURN || !(e.widget instanceof Control)) return;
		Control c = (Control)e.widget;
		// drawn buttons and dropdowns use Return themselves
		if(c instanceof ThemeButton || c instanceof ThemeCombo || c.getParent() instanceof ThemeButton) return;
		if((c.getStyle() & SWT.MULTI) != 0) return;
		Shell shell = c.getShell();
		Button b = shell.getDefaultButton();
		if(b == null || b.isDisposed() || b.getVisible() || b.getData(KEY) == null || !b.isEnabled()) return;
		e.doit = false;
		e.detail = SWT.TRAVERSE_NONE;
		b.notifyListeners(SWT.Selection, new Event());
	}
}
