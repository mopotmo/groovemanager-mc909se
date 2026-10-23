package com.groovemanager.gui.theme;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.groovemanager.gui.custom.WaveFormDisplay;

/**
 * A Theme defines the colors of the custom drawn parts of the editors
 * (waveform displays, MC-909 pads and keys) and the background of the main
 * window. Native widgets are drawn by the platform, a Theme can only switch
 * them between their light and dark appearance.
 * @author Manu Robledo
 *
 */
public class Theme {
	/**
	 * The look of the 2004 release
	 */
	public final static Theme CLASSIC = new Theme("classic", "Classic (2004)", false);
	/**
	 * A dark theme with calm colors
	 */
	public final static Theme MODERN = new Theme("modern", "Modern (dark)", true);
	/**
	 * Silver panel, LCD colored waveforms and orange pads like the MC-909
	 * itself
	 */
	public final static Theme MC909 = new Theme("mc909", "MC-909", false);
	/**
	 * Steel blue panels and blue displays like Roland´s own MC-909 Editor
	 */
	public final static Theme MC909_EDITOR = new Theme("mc909editor", "MC-909 Editor", true);
	/**
	 * All available themes
	 */
	public final static Theme[] THEMES = new Theme[]{CLASSIC, MODERN, MC909, MC909_EDITOR};

	static{
		CLASSIC.waveBackground = new RGB(192, 192, 192);
		CLASSIC.waveChannelBackground = new RGB(0, 0, 0);
		CLASSIC.wave = new RGB(255, 255, 0);
		CLASSIC.waveMiddleLine = new RGB(255, 0, 0);
		CLASSIC.waveSelection = new RGB(0, 0, 255);
		CLASSIC.wavePosition = new RGB(192, 192, 192);
		CLASSIC.waveMarker = new RGB(0, 255, 0);
		CLASSIC.waveMarkerHighlight = new RGB(128, 0, 128);
		CLASSIC.overviewBackground = new RGB(192, 192, 192);
		// null: the platform's widget background
		CLASSIC.overviewChannelBackground = null;
		CLASSIC.overviewWave = new RGB(0, 0, 0);
		CLASSIC.overviewMiddleLine = new RGB(255, 0, 0);
		CLASSIC.overviewSelection = new RGB(255, 255, 0);
		CLASSIC.overviewPosition = new RGB(0, 0, 0);
		CLASSIC.panelBackground = null;
		// the pad section uses the light window background
		CLASSIC.stripBackground = null;
		CLASSIC.keyPanel = null;
		CLASSIC.keyWhite = new RGB(255, 255, 255);
		CLASSIC.keyBlack = new RGB(0, 0, 0);
		CLASSIC.keyLine = new RGB(0, 0, 0);
		CLASSIC.pad = new RGB(220, 212, 208);
		CLASSIC.padPressed = new RGB(255, 144, 18);
		CLASSIC.padFrame = new RGB(0, 0, 0);
		CLASSIC.numBackground = new RGB(0, 0, 0);
		CLASSIC.numForeground = new RGB(255, 255, 255);
		// the transport buttons use the GIFs from root/icons/trans
		CLASSIC.bitmapIcons = true;
		CLASSIC.frame = new RGB(160, 160, 160);

		MODERN.waveBackground = new RGB(18, 20, 24);
		MODERN.waveChannelBackground = new RGB(26, 29, 35);
		MODERN.wave = new RGB(86, 200, 245);
		// a tone of the waveform instead of white, so that the middle line
		// doesn't form a grid with the slice markers
		MODERN.waveMiddleLine = new RGB(60, 140, 180);
		MODERN.waveSelection = new RGB(245, 176, 65);
		MODERN.wavePosition = new RGB(240, 242, 245);
		MODERN.waveMarker = new RGB(126, 224, 129);
		MODERN.waveMarkerHighlight = new RGB(255, 92, 138);
		MODERN.overviewBackground = new RGB(18, 20, 24);
		MODERN.overviewChannelBackground = new RGB(22, 25, 30);
		MODERN.overviewWave = new RGB(61, 126, 166);
		MODERN.overviewMiddleLine = new RGB(60, 140, 180);
		// the overview selection shows the visible part, so keep it calm
		MODERN.overviewSelection = new RGB(120, 132, 150);
		MODERN.overviewPosition = new RGB(240, 242, 245);
		// SWT reads the window background only on start, so it has to be
		// set explicitly to follow a later switch to the dark appearance
		MODERN.panelBackground = new RGB(30, 32, 38);
		MODERN.stripBackground = new RGB(18, 20, 24);
		MODERN.keyPanel = new RGB(18, 20, 24);
		MODERN.keyWhite = new RGB(86, 93, 108);
		MODERN.keyBlack = new RGB(14, 16, 20);
		MODERN.keyLine = new RGB(14, 16, 20);
		MODERN.pad = new RGB(124, 133, 151);
		MODERN.padPressed = new RGB(245, 176, 65);
		MODERN.padFrame = new RGB(14, 16, 20);
		MODERN.numBackground = new RGB(36, 40, 47);
		MODERN.numForeground = new RGB(160, 168, 180);
		MODERN.icon = new RGB(226, 230, 236);
		MODERN.iconDisabled = new RGB(88, 94, 106);
		MODERN.iconActive = new RGB(245, 176, 65);
		MODERN.iconRecord = new RGB(240, 84, 84);
		MODERN.frame = new RGB(48, 52, 60);
		MODERN.softWave = true;

		// colors taken from Roland's product photo of the MC-909: a cool
		// blue-silver panel, a pale blue backlit LCD with navy ink, blue-grey
		// pads and keys
		MC909.waveBackground = new RGB(204, 216, 239);
		MC909.waveChannelBackground = new RGB(204, 216, 239);
		MC909.wave = new RGB(52, 60, 120);
		// a tone of the ink instead of white, so that the middle line doesn't
		// form a grid with the red slice markers
		MC909.waveMiddleLine = new RGB(96, 110, 190);
		MC909.waveSelection = new RGB(132, 150, 220);
		MC909.wavePosition = new RGB(30, 36, 90);
		MC909.waveMarker = new RGB(214, 60, 70);
		MC909.waveMarkerHighlight = new RGB(255, 144, 18);
		MC909.overviewBackground = new RGB(194, 206, 232);
		MC909.overviewChannelBackground = new RGB(194, 206, 232);
		MC909.overviewWave = new RGB(52, 60, 120);
		MC909.overviewMiddleLine = new RGB(96, 110, 190);
		MC909.overviewSelection = new RGB(132, 150, 220);
		MC909.overviewPosition = new RGB(30, 36, 90);
		// the blue-silver of the top panel
		MC909.panelBackground = new RGB(168, 177, 194);
		MC909.panelForeground = new RGB(30, 34, 42);
		// the velocity pads sit on a slightly darker strip
		MC909.stripBackground = new RGB(146, 157, 174);
		MC909.keyPanel = new RGB(146, 157, 174);
		MC909.keyWhite = new RGB(234, 238, 244);
		MC909.keyBlack = new RGB(40, 48, 56);
		MC909.keyLine = new RGB(40, 48, 56);
		MC909.pad = new RGB(150, 168, 198);
		MC909.padPressed = new RGB(250, 72, 82);
		MC909.padFrame = new RGB(36, 44, 58);
		MC909.numBackground = new RGB(47, 53, 59);
		MC909.numForeground = new RGB(230, 234, 240);
		MC909.icon = new RGB(33, 53, 83);
		MC909.iconDisabled = new RGB(120, 132, 150);
		MC909.iconActive = new RGB(255, 144, 18);
		MC909.iconRecord = new RGB(214, 60, 70);
		MC909.frame = new RGB(96, 108, 124);
		MC909.softWave = true;
		// thin red lines get lost on the light display
		MC909.markerWidth = 2;
		// flat buttons in the pale blue-grey of the sequencer keys
		MC909.deviceButtons = true;
		MC909.buttonFace = new RGB(184, 197, 218);
		MC909.buttonFacePressed = new RGB(166, 180, 204);
		MC909.buttonEdge = new RGB(96, 108, 124);
		MC909.buttonText = new RGB(33, 53, 83);
		MC909.buttonTextDisabled = new RGB(120, 132, 150);
		// text fields, dropdowns and lists look like small LCDs
		MC909.inputBackground = new RGB(214, 224, 242);
		MC909.inputForeground = new RGB(33, 53, 83);
		// sections in darker boxes with black header bars like on the device
		MC909.sectionBackground = new RGB(156, 166, 184);
		MC909.headerBackground = new RGB(40, 45, 54);
		MC909.headerForeground = new RGB(236, 240, 246);

		// after Roland's MC-909 Editor: deep steel blue panels with white
		// labels, navy displays with cyan waveforms. It uses the dark native
		// widgets, so that input fields don't stand out as white boxes. All
		// text has a contrast of at least 4.5:1, outlines at least 3:1.
		MC909_EDITOR.waveBackground = new RGB(16, 26, 78);
		MC909_EDITOR.waveChannelBackground = new RGB(16, 26, 78);
		MC909_EDITOR.wave = new RGB(120, 210, 255);
		// a tone of the waveform instead of white, so that the middle line
		// doesn't form a grid with the slice markers
		MC909_EDITOR.waveMiddleLine = new RGB(70, 140, 200);
		MC909_EDITOR.waveSelection = new RGB(150, 180, 255);
		MC909_EDITOR.wavePosition = new RGB(255, 255, 255);
		// amber slices are told apart from the cyan waveform by their hue
		MC909_EDITOR.waveMarker = new RGB(255, 190, 70);
		MC909_EDITOR.waveMarkerHighlight = new RGB(255, 96, 110);
		MC909_EDITOR.overviewBackground = new RGB(14, 22, 66);
		MC909_EDITOR.overviewChannelBackground = new RGB(14, 22, 66);
		MC909_EDITOR.overviewWave = new RGB(96, 176, 232);
		MC909_EDITOR.overviewMiddleLine = new RGB(70, 140, 200);
		MC909_EDITOR.overviewSelection = new RGB(150, 180, 255);
		MC909_EDITOR.overviewPosition = new RGB(255, 255, 255);
		MC909_EDITOR.panelBackground = new RGB(52, 62, 80);
		MC909_EDITOR.panelForeground = new RGB(236, 240, 246);
		MC909_EDITOR.stripBackground = new RGB(40, 48, 62);
		MC909_EDITOR.keyPanel = new RGB(40, 48, 62);
		MC909_EDITOR.keyWhite = new RGB(178, 190, 208);
		MC909_EDITOR.keyBlack = new RGB(22, 26, 34);
		MC909_EDITOR.keyLine = new RGB(22, 26, 34);
		MC909_EDITOR.pad = new RGB(104, 120, 146);
		MC909_EDITOR.padPressed = new RGB(250, 72, 82);
		MC909_EDITOR.padFrame = new RGB(22, 26, 34);
		MC909_EDITOR.numBackground = new RGB(22, 26, 34);
		MC909_EDITOR.numForeground = new RGB(236, 240, 246);
		MC909_EDITOR.icon = new RGB(236, 240, 246);
		MC909_EDITOR.iconDisabled = new RGB(138, 150, 168);
		MC909_EDITOR.iconActive = new RGB(255, 190, 70);
		MC909_EDITOR.iconRecord = new RGB(255, 96, 110);
		MC909_EDITOR.frame = new RGB(122, 138, 164);
		MC909_EDITOR.softWave = true;
		// flat buttons like the ones of the editor
		MC909_EDITOR.deviceButtons = true;
		MC909_EDITOR.buttonFace = new RGB(78, 92, 116);
		MC909_EDITOR.buttonFacePressed = new RGB(64, 76, 98);
		MC909_EDITOR.buttonEdge = new RGB(122, 138, 164);
		MC909_EDITOR.buttonText = new RGB(236, 240, 246);
		MC909_EDITOR.buttonTextDisabled = new RGB(138, 150, 168);
		// navy display fields with cyan text like in the editor
		MC909_EDITOR.inputBackground = new RGB(16, 26, 78);
		MC909_EDITOR.inputForeground = new RGB(150, 220, 255);
		MC909_EDITOR.sectionBackground = new RGB(42, 51, 68);
		MC909_EDITOR.headerBackground = new RGB(20, 24, 32);
		MC909_EDITOR.headerForeground = new RGB(236, 240, 246);
	}
	/**
	 * The key under which the theme is stored in the preferences
	 */
	private final String id,
	/**
	 * The name shown to the user
	 */
	name;
	/**
	 * true, if native widgets should use their dark appearance
	 */
	private final boolean dark;
	/**
	 * Colors of the main waveform display
	 */
	RGB waveBackground, waveChannelBackground, wave, waveMiddleLine,
		waveSelection, wavePosition, waveMarker, waveMarkerHighlight;
	/**
	 * Colors of the overview waveform display
	 */
	RGB overviewBackground, overviewChannelBackground, overviewWave,
		overviewMiddleLine, overviewSelection, overviewPosition;
	/**
	 * Background and text color of the main window, <code>null</code> for
	 * the platform's default
	 */
	RGB panelBackground, panelForeground;
	/**
	 * Colors of the MC-909 key and pad strip
	 */
	RGB stripBackground, keyPanel, keyWhite, keyBlack, keyLine, pad, padPressed,
		padFrame, numBackground, numForeground;
	/**
	 * true, if the transport buttons should use the 2004 bitmaps instead of
	 * the TransportIcons
	 */
	boolean bitmapIcons;
	/**
	 * Colors of the TransportIcons
	 */
	RGB icon, iconDisabled, iconActive, iconRecord;
	/**
	 * true, if the buttons of the main window should be drawn like the keys
	 * of the MC-909 instead of using native buttons
	 */
	boolean deviceButtons;
	/**
	 * Colors of the device buttons
	 */
	RGB buttonFace, buttonFacePressed, buttonEdge, buttonText, buttonTextDisabled;
	/**
	 * Colors of text fields, dropdowns and lists, <code>null</code> for the
	 * native colors
	 */
	RGB inputBackground, inputForeground;
	/**
	 * Colors of the sections (file list, properties, transport, zoom) and
	 * their header bars, <code>null</code> for sections without header
	 */
	RGB sectionBackground, headerBackground, headerForeground;
	/**
	 * Frame around the content areas (lists, panels, waveform displays),
	 * <code>null</code> for none
	 */
	RGB frame;
	/**
	 * true for rounded waveform displays with a smoothed waveform
	 */
	boolean softWave;
	/**
	 * Width of the slice markers in pixels
	 */
	int markerWidth = 1;
	/**
	 * Colors already created, by RGB
	 */
	private HashMap colors = new HashMap();
	/**
	 * Construct a new Theme
	 * @param id The key under which the theme is stored in the preferences
	 * @param name The name shown to the user
	 * @param dark true, if native widgets should use their dark appearance
	 */
	private Theme(String id, String name, boolean dark){
		this.id = id;
		this.name = name;
		this.dark = dark;
	}
	/**
	 * Get the theme with the given id
	 * @param id The theme´s id
	 * @return The theme with the given id or MC909, if there is none
	 */
	public static Theme forId(String id){
		for (int i = 0; i < THEMES.length; i++) {
			if(THEMES[i].id.equals(id)) return THEMES[i];
		}
		return MC909;
	}
	/**
	 * Get the key under which this theme is stored in the preferences
	 * @return This theme´s id
	 */
	public String getId(){
		return id;
	}
	/**
	 * Get the name of this theme
	 * @return The name shown to the user
	 */
	public String getName(){
		return name;
	}
	/**
	 * Tells whether native widgets should use their dark appearance
	 * @return true for a dark theme
	 */
	public boolean isDark(){
		return dark;
	}
	/**
	 * Get the Color for the given RGB value
	 * @param rgb The RGB value or <code>null</code>
	 * @return The Color or <code>null</code>, if rgb is <code>null</code>
	 */
	protected Color color(RGB rgb){
		if(rgb == null) return null;
		Color c = (Color)colors.get(rgb);
		if(c == null){
			c = new Color(Display.getCurrent(), rgb);
			colors.put(rgb, c);
		}
		return c;
	}
	/**
	 * Apply the colors of the main waveform display
	 * @param d The WaveFormDisplay to apply the colors to
	 */
	public void applyWaveColors(WaveFormDisplay d){
		boolean hc = ThemeManager.getDefault().isHighContrast();
		RGB contrast = hc ? contrastBackground(wave) : null;
		d.setBackgroundColor(color(hc ? contrast : waveBackground));
		d.setChannelBackgroundColor(color(hc ? contrast : waveChannelBackground));
		d.setWaveColor(color(wave));
		d.setMiddleLineColor(color(waveMiddleLine));
		d.setSelectionColor(color(waveSelection));
		d.setPositionColor(color(wavePosition));
		d.setMarkerColor(color(waveMarker));
		d.setHighlightedMarkerColor(color(waveMarkerHighlight));
		d.setSoft(softWave);
		d.setMarkerWidth(hc ? Math.max(2, markerWidth) : markerWidth);
		d.setHighContrast(hc);
	}
	/**
	 * Apply the colors of the overview waveform display
	 * @param d The WaveFormDisplay to apply the colors to
	 */
	public void applyOverviewColors(WaveFormDisplay d){
		boolean hc = ThemeManager.getDefault().isHighContrast();
		Color channel = color(hc ? contrastBackground(overviewWave) : overviewChannelBackground);
		if(channel == null) channel = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		d.setBackgroundColor(hc ? channel : color(overviewBackground));
		d.setChannelBackgroundColor(channel);
		d.setWaveColor(color(overviewWave));
		d.setMiddleLineColor(color(overviewMiddleLine));
		d.setSelectionColor(color(overviewSelection));
		d.setPositionColor(color(overviewPosition));
		d.setMarkerColor(color(waveMarker));
		d.setHighlightedMarkerColor(color(waveMarkerHighlight));
		d.setSoft(softWave);
		d.setMarkerWidth(hc ? Math.max(2, markerWidth) : markerWidth);
		d.setHighContrast(hc);
	}
	/**
	 * Get black or white, whichever contrasts more with the given color.
	 * Used as display background in high contrast mode.
	 * @param rgb The color of the waveform
	 * @return Black or white
	 */
	protected static RGB contrastBackground(RGB rgb){
		double l = luminance(rgb);
		// contrast against white: 1.05 / (l + 0.05), against black: (l + 0.05) / 0.05
		return 1.05 / (l + 0.05) > (l + 0.05) / 0.05 ? new RGB(255, 255, 255) : new RGB(0, 0, 0);
	}
	/**
	 * Get the relative luminance of a color as defined by WCAG 2
	 * @param rgb The color
	 * @return The luminance from 0 (black) to 1 (white)
	 */
	protected static double luminance(RGB rgb){
		int[] c = new int[]{rgb.red, rgb.green, rgb.blue};
		double[] l = new double[3];
		for (int i = 0; i < 3; i++) {
			double v = c[i] / 255.0;
			l[i] = v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
		}
		return 0.2126 * l[0] + 0.7152 * l[1] + 0.0722 * l[2];
	}
	/**
	 * Get the background of the main window
	 * @return The background Color or <code>null</code> for the platform´s
	 * default
	 */
	public Color getPanelBackground(){
		return color(panelBackground);
	}
	/**
	 * Get the text color of the main window
	 * @return The text Color or <code>null</code> for the platform´s default
	 */
	public Color getPanelForeground(){
		return color(panelForeground);
	}
	/**
	 * Get the background of the area around the MC-909 keys
	 * @return The strip background Color
	 */
	public Color getStripBackground(){
		return color(stripBackground);
	}
	/**
	 * Get the color around the keys and between the pad number bars
	 * @return The key panel Color
	 */
	public Color getKeyPanel(){
		return color(keyPanel);
	}
	/**
	 * Get the color of the white keys
	 * @return The white key Color
	 */
	public Color getKeyWhite(){
		return color(keyWhite);
	}
	/**
	 * Get the color of the black keys
	 * @return The black key Color
	 */
	public Color getKeyBlack(){
		return color(keyBlack);
	}
	/**
	 * Get the color of the lines between the keys
	 * @return The key line Color
	 */
	public Color getKeyLine(){
		return color(keyLine);
	}
	/**
	 * Get the color of a pad
	 * @param pressed true for the color of a pressed pad
	 * @return The pad Color
	 */
	public Color getPad(boolean pressed){
		return color(pressed ? padPressed : pad);
	}
	/**
	 * Get the color of the pad frames
	 * @return The pad frame Color
	 */
	public Color getPadFrame(){
		return color(padFrame);
	}
	/**
	 * Get the background of the pad numbers
	 * @return The pad number background Color
	 */
	public Color getNumBackground(){
		return color(numBackground);
	}
	/**
	 * Get the color of the pad numbers
	 * @return The pad number Color
	 */
	public Color getNumForeground(){
		return color(numForeground);
	}
	/**
	 * Tells whether the transport buttons should use the 2004 bitmaps
	 * @return true for bitmaps, false for TransportIcons
	 */
	public boolean hasBitmapIcons(){
		return bitmapIcons;
	}
	/**
	 * Get the color of the frames around the content areas
	 * @return The frame Color or <code>null</code> for no frames
	 */
	public Color getFrame(){
		return color(frame);
	}
	/**
	 * Tells whether the waveform displays should be rounded and show a
	 * smoothed waveform
	 * @return true for soft waveform displays
	 */
	public boolean hasSoftWave(){
		return softWave;
	}
	/**
	 * Tells whether the buttons should be drawn like the keys of the MC-909
	 * @return true for device buttons, false for native buttons
	 */
	public boolean hasDeviceButtons(){
		return deviceButtons;
	}
	/**
	 * Get a color of the device buttons
	 * @param pressed true for the face of a pressed button
	 * @return The face Color
	 */
	public Color getButtonFace(boolean pressed){
		return color(pressed ? buttonFacePressed : buttonFace);
	}
	/**
	 * Tells whether sections have a header bar with their title
	 * @return true for section headers
	 */
	public boolean hasSectionHeaders(){
		return headerBackground != null;
	}
	/**
	 * Get the background of the section boxes
	 * @return The background Color or <code>null</code> for the panel´s
	 */
	public Color getSectionBackground(){
		return color(sectionBackground);
	}
	/**
	 * Get the colors of the section header bars
	 * @param text true for the text color, false for the bar
	 * @return The Color
	 */
	public Color getHeader(boolean text){
		return color(text ? headerForeground : headerBackground);
	}
	/**
	 * Get the background of text fields, dropdowns and lists
	 * @return The background Color or <code>null</code> for the native one
	 */
	public Color getInputBackground(){
		return color(inputBackground);
	}
	/**
	 * Get the text color of text fields, dropdowns and lists
	 * @return The text Color or <code>null</code> for the native one
	 */
	public Color getInputForeground(){
		return color(inputForeground);
	}
	/**
	 * Get the color of the outline of the device buttons
	 * @return The outline Color
	 */
	public Color getButtonEdge(){
		return color(buttonEdge);
	}
	/**
	 * Get the text color of the device buttons
	 * @param enabled true, if the button is enabled
	 * @return The text Color
	 */
	public Color getButtonText(boolean enabled){
		return color(enabled ? buttonText : buttonTextDisabled);
	}
	/**
	 * Get the color of the transport button symbol
	 * @param enabled true, if the button is enabled
	 * @param active true, if the button´s function is currently active
	 * @param record true for the record button
	 * @return The symbol Color
	 */
	public Color getIconColor(boolean enabled, boolean active, boolean record){
		if(!enabled) return color(iconDisabled);
		// the record symbol is always red, like on most devices
		if(active || record) return color(record ? iconRecord : iconActive);
		return color(icon);
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
	}
}
