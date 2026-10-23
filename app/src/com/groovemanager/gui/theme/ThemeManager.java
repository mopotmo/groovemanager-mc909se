package com.groovemanager.gui.theme;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.groovemanager.core.ConfigManager;
import com.groovemanager.core.Log;

/**
 * The ThemeManager holds the current Theme, which is stored in the
 * preferences under <code>PREF_KEY</code>, and notifies ThemeListeners when
 * it changes. The default instance can be accessed through
 * <code>ThemeManager.getDefault()</code> once a Display exists.
 * @author Manu Robledo
 *
 */
public class ThemeManager implements IPropertyChangeListener{
	/**
	 * The preference key for the id of the selected theme
	 */
	public final static String PREF_KEY = "gui_theme";
	/**
	 * The default instance
	 */
	private static ThemeManager defaultManager;
	/**
	 * The current theme
	 */
	protected Theme theme;
	/**
	 * The registered ThemeListeners
	 */
	protected ArrayList listeners = new ArrayList();
	/**
	 * Key of the Shell data marking a Shell whose background and labels
	 * follow the theme
	 */
	protected final static String THEMED_SHELL = "com.groovemanager.gui.theme.themedShell";
	/**
	 * Construct a new ThemeManager using the theme from the default
	 * ConfigManager
	 */
	protected ThemeManager(){
		ConfigManager.getDefault().getPrefStore().setDefault(PREF_KEY, Theme.MC909.getId());
		ConfigManager.getDefault().getPrefStore().addPropertyChangeListener(this);
		theme = Theme.forId(ConfigManager.getDefault().getPrefStore().getString(PREF_KEY));
		Display d = Display.getDefault();
		setDarkAppearance(d, theme.isDark());
		// sent for every new widget and on reskin()
		d.addListener(SWT.Skin, new Listener() {
			public void handleEvent(Event e) {
				if(e.widget instanceof Label) skinLabel((Label)e.widget);
				else if(e.widget instanceof List) skinList((List)e.widget);
			}
		});
	}
	/**
	 * Let the background of a Shell and the color of its labels follow the
	 * theme. Input fields and native buttons keep their own colors.
	 * @param shell The Shell
	 */
	public void addShell(final Shell shell){
		shell.setData(THEMED_SHELL, Boolean.TRUE);
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		addThemeListener(shell, new ThemeListener() {
			public void applyTheme(Theme theme) {
				shell.setBackground(theme.getPanelBackground());
				shell.reskin(SWT.ALL);
			}
		});
	}
	/**
	 * Apply the theme´s text color to a label of a themed Shell. Labels on
	 * custom drawn Canvases (like the pad numbers) have their own colors.
	 * @param l The label
	 */
	protected void skinLabel(Label l){
		if(l.isDisposed() || l.getParent() instanceof Canvas) return;
		if(l.getShell().getData(THEMED_SHELL) == null) return;
		l.setForeground(theme.getPanelForeground());
	}
	/**
	 * Apply the theme´s input colors to a list of a themed Shell
	 * @param l The list
	 */
	protected void skinList(List l){
		if(l.isDisposed() || l.getShell().getData(THEMED_SHELL) == null) return;
		l.setBackground(theme.getInputBackground());
		l.setForeground(theme.getInputForeground());
	}
	/**
	 * Switch the native widgets between their light and dark appearance
	 * @param d The Display
	 * @param dark true for the dark appearance
	 */
	protected void setDarkAppearance(Display d, boolean dark){
		d.setDarkThemePreferred(dark);
		if(!SWT.getPlatform().equals("cocoa")) return;
		// setDarkThemePreferred(false) lets the application follow the
		// system appearance, which may be dark. Set the light or dark
		// appearance of the application explicitly instead. SWT on macOS also
		// reads the system colors (e.g. the background of lists, tables and
		// windows) only on start and when the system appearance changes, so
		// let it read them again. The cocoa classes only exist in the macOS
		// SWT, so they are used via reflection.
		try {
			Class nsString = Class.forName("org.eclipse.swt.internal.cocoa.NSString");
			Class nsAppearance = Class.forName("org.eclipse.swt.internal.cocoa.NSAppearance");
			Class os = Class.forName("org.eclipse.swt.internal.cocoa.OS");
			Object name = nsString.getMethod("stringWith", new Class[]{String.class}).invoke(null, new Object[]{dark ? "NSAppearanceNameDarkAqua" : "NSAppearanceNameAqua"});
			Object appearance = nsAppearance.getMethod("appearanceNamed", new Class[]{nsString}).invoke(null, new Object[]{name});
			long appearanceId = nsAppearance.getField("id").getLong(appearance);
			long appearanceClass = ((Long)os.getMethod("objc_getClass", new Class[]{String.class}).invoke(null, new Object[]{"NSAppearance"})).longValue();
			long selector = ((Long)os.getMethod("sel_registerName", new Class[]{String.class}).invoke(null, new Object[]{"setCurrentAppearance:"})).longValue();
			Method msgSend = os.getMethod("objc_msgSend", new Class[]{long.class, long.class, long.class});
			msgSend.invoke(null, new Object[]{Long.valueOf(appearanceClass), Long.valueOf(selector), Long.valueOf(appearanceId)});
			Class nsApplication = Class.forName("org.eclipse.swt.internal.cocoa.NSApplication");
			Object app = nsApplication.getMethod("sharedApplication", new Class[0]).invoke(null, new Object[0]);
			long appId = nsApplication.getField("id").getLong(app);
			long setAppearance = ((Long)os.getMethod("sel_registerName", new Class[]{String.class}).invoke(null, new Object[]{"setAppearance:"})).longValue();
			msgSend.invoke(null, new Object[]{Long.valueOf(appId), Long.valueOf(setAppearance), Long.valueOf(appearanceId)});
			Method initColors = Display.class.getDeclaredMethod("initColors", new Class[0]);
			initColors.setAccessible(true);
			initColors.invoke(d, new Object[0]);
		} catch (Exception e) {
			if(Log.active) Log.log("Could not update the system colors: " + e, Log.TYPE_ERROR);
		}
	}
	/**
	 * Let input widgets take the default colors of the new appearance. They
	 * keep the colors they were created with otherwise.
	 * @param c The control to start with
	 */
	protected void resetDefaultColors(Control c){
		if(c instanceof List || c instanceof Table || c instanceof Tree || c instanceof Text || c instanceof Combo){
			c.setBackground(null);
			c.setForeground(null);
		}
		if(c instanceof List) skinList((List)c);
		// SWT on macOS stores the text color of the old appearance in the
		// tab titles, setting the titles again uses the new one
		if(c instanceof TabFolder){
			TabItem[] items = ((TabFolder)c).getItems();
			for (int i = 0; i < items.length; i++) {
				String text = items[i].getText();
				items[i].setText("");
				items[i].setText(text);
			}
		}
		if(c instanceof Composite){
			Control[] children = ((Composite)c).getChildren();
			for (int i = 0; i < children.length; i++) {
				resetDefaultColors(children[i]);
			}
		}
	}
	/**
	 * Get the default ThemeManager. Must be called from the UI thread. Call
	 * it before creating any window, so that all widgets are created in the
	 * right appearance.
	 * @return The default ThemeManager
	 */
	public static ThemeManager getDefault(){
		if(defaultManager == null) defaultManager = new ThemeManager();
		return defaultManager;
	}
	/**
	 * Get the current theme
	 * @return The current theme
	 */
	public Theme getTheme(){
		return theme;
	}
	/**
	 * Change the current theme and notify all ThemeListeners
	 * @param newTheme The new theme
	 */
	public void setTheme(Theme newTheme){
		if(newTheme == theme) return;
		theme = newTheme;
		Display display = Display.getCurrent();
		setDarkAppearance(display, theme.isDark());
		// first the native colors, so that the listeners can override them
		Shell[] shells = display.getShells();
		for (int i = 0; i < shells.length; i++) {
			resetDefaultColors(shells[i]);
		}
		ArrayList copy = new ArrayList(listeners);
		for (Iterator iter = copy.iterator(); iter.hasNext();) {
			ThemeListener l = (ThemeListener) iter.next();
			l.applyTheme(theme);
		}
		for (int i = 0; i < shells.length; i++) {
			// icons and drawn buttons change their preferred sizes
			shells[i].layout(true, true);
			shells[i].redraw(0, 0, shells[i].getSize().x, shells[i].getSize().y, true);
		}
	}
	/**
	 * Register a ThemeListener. It is called once immediately with the
	 * current theme.
	 * @param l The ThemeListener to add
	 */
	public void addThemeListener(ThemeListener l){
		listeners.add(l);
		l.applyTheme(theme);
	}
	/**
	 * Register a ThemeListener that is removed again when the given widget
	 * is disposed. It is called once immediately with the current theme.
	 * @param w The widget the listener belongs to
	 * @param l The ThemeListener to add
	 */
	public void addThemeListener(Widget w, final ThemeListener l){
		addThemeListener(l);
		w.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeThemeListener(l);
			}
		});
	}
	/**
	 * Remove a ThemeListener
	 * @param l The ThemeListener to remove
	 */
	public void removeThemeListener(ThemeListener l){
		listeners.remove(l);
	}
	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(!event.getProperty().equals(PREF_KEY)) return;
		final Theme t = Theme.forId(String.valueOf(event.getNewValue()));
		Display d = Display.getDefault();
		if(d.getThread() == Thread.currentThread()) setTheme(t);
		else d.asyncExec(new Runnable() {
			public void run() {
				setTheme(t);
			}
		});
	}
}
