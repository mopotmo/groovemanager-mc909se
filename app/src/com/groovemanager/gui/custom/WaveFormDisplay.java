/*
 * Created on 12.05.2004
 *
 */
package com.groovemanager.gui.custom;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;

import com.groovemanager.sampled.waveform.MarkableWaveFormDisplay;
import com.groovemanager.sampled.waveform.Marker;
import com.groovemanager.sampled.waveform.WaveForm;

/**
 * This class is an implementation of the abstract MarkableWaveFormDisplay
 * using SWT
 * @author Manu Robledo
 *
 */
public class WaveFormDisplay extends MarkableWaveFormDisplay {
	/**
	 * The number of sample frames to move a Marker with one arrow press
	 */
	protected int moveStep = 100;
	/**
	 * Color to use for not selected Markers
	 */
	protected Color markerColor,
	/**
	 * Color to use for the selected Marker
	 */
	markerHighlightColor;
	/**
	 * The main Composite
	 */
	protected Canvas comp;
	/**
	 * Colors used for the different drawing elements
	 */
	protected Color selectionColor, positionColor, channelBackgroundColor, middleLineColor, waveColor, backgroundColor;
	/**
	 * Off-screen image holding the background, the waveform and the middle
	 * lines. It is only rendered again after redraw() has been called or the
	 * size has changed. Position, selection and markers are painted on top of
	 * it in every paint event.
	 */
	protected Image contentBuffer;
	/**
	 * Tells whether the contentBuffer is up to date
	 */
	protected boolean contentValid = false;
	/**
	 * true while the contentBuffer is being rendered
	 */
	protected boolean renderingContent = false;
	/**
	 * The GC to draw with. Only set during a paint event, because drawing on
	 * a Control outside of paint events (and XOR drawing) is not supported on
	 * current platforms (macOS, GTK3).
	 */
	protected GC currentGC;
	/**
	 * If this variable is false, selection events coming from the scrollbar
	 * will be ignored
	 */
	protected boolean reactToScrollBar = true,
	/**
	 * Use a gradient Background for this WaveFormDisplay?
	 */
	gradient,
	/**
	 * Draw rounded corners and a smoothed waveform?
	 */
	soft,
	/**
	 * Draw the markers opaque for a high contrast?
	 */
	highContrast;
	/**
	 * Width of the markers in pixels
	 */
	protected int markerWidth = 1;
	/**
	 * Strength of the vertical gradient of the smoothed waveform: how far
	 * the color at the peaks is blended towards the background, from 0
	 * (no gradient) to 1
	 */
	protected float waveGradient;
	/**
	 * Opacity of the middle line in the smoothed look
	 */
	protected final static int MIDDLE_LINE_ALPHA = 40;
	/**
	 * Opacity of the (not selected) markers in the smoothed look
	 */
	protected final static int MARKER_ALPHA = 140;
	/**
	 * The default cursor
	 */
	protected Cursor normalCursor,
	/**
	 * The cursor to use when moving Markers
	 */
	moveMarkerCursor,
	/**
	 * Cursor to be used when creating new Markers or selecting Markers
	 */
	editMarkerCursor,
	/**
	 * Cursor to be used when deleting Markers
	 */
	deleteMarkerCursor;
	/**
	 * Construct a new WaveFormDisplay
	 * @param parent The parent Composite
	 * @param gradientBackground true, if a gradient background should be used,
	 * false otherwise
	 */
	public WaveFormDisplay(Composite parent, boolean gradientBackground){
		this(parent, SWT.H_SCROLL, gradientBackground);
	}
	/**
	 * Construct a new WaveFormDisplay
	 * @param parent The parent Composite
	 * @param style Combination of SWT.* style constants for the main Composite
	 * @param gradientBackground true, if a gradient background should be used,
	 * false otherwise
	 */
	public WaveFormDisplay(Composite parent, int style, boolean gradientBackground){
		gradient = gradientBackground;
		// paint() covers the whole area, so the background doesn´t need to be
		// erased first. Erasing it makes the display flicker on Windows while
		// the position is updated during playback.
		comp = new Canvas(parent, style | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		normalCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_ARROW);
		moveMarkerCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_SIZEWE);
		editMarkerCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_HAND);
		deleteMarkerCursor = new Cursor(comp.getDisplay(), SWT.CURSOR_CROSS);
		final ScrollBar bar = comp.getHorizontalBar();
		if(bar != null) bar.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(reactToScrollBar){
					int sel = bar.getSelection();
					int max = bar.getMaximum();
					int thumb = bar.getThumb();
					double s = sel / (double)(max - thumb);
					scroll(s);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		comp.addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e) {
				paint(e.gc);
			}
		});
		comp.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if(contentBuffer != null) contentBuffer.dispose();
			}
		});
		initColors();
		
		addMouseListeners(comp);
	}
	/**
	 * Add the needed MouseListeners to the given Composite
	 * @param comp The main Composite
	 */
	protected void addMouseListeners(final Composite comp){
		comp.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				WaveFormDisplay.this.mouseDown(e.x, e.y, (e.stateMask & SWT.SHIFT) > 0, (e.stateMask & SWT.MOD1) > 0);
				if(editMarkers && selectedMarker != null && mouseDown) getComposite().setCursor(moveMarkerCursor);
				else getComposite().setCursor(normalCursor);
			}
			public void mouseUp(MouseEvent e) {
				WaveFormDisplay.this.mouseUp(e.x, e.y, (e.stateMask & SWT.SHIFT) > 0, (e.stateMask & SWT.MOD1) > 0);
				if(editMarkers){
					if((e.stateMask & SWT.MOD1) > 0){
						if(getMarkerFromMousePos(e.x) == null) getComposite().setCursor(editMarkerCursor);
						else getComposite().setCursor(deleteMarkerCursor);
					}
					else if((e.stateMask & SWT.SHIFT) > 0){
						Marker nearest = getMarkerFromMousePos(e.x);
						if(nearest == null) getComposite().setCursor(normalCursor);
						else getComposite().setCursor(moveMarkerCursor);
					}
					else getComposite().setCursor(normalCursor);
				}
				else getComposite().setCursor(normalCursor);
			}
		});
		
		comp.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				WaveFormDisplay.this.mouseMove(e.x, e.y, (e.stateMask & SWT.SHIFT) > 0, (e.stateMask & SWT.MOD1) > 0);
				if(!mouseDown){
					if(editMarkers){
						if((e.stateMask & SWT.MOD1) > 0){
							if(getMarkerFromMousePos(e.x) == null) getComposite().setCursor(editMarkerCursor);
							else getComposite().setCursor(deleteMarkerCursor);
						}
						else if((e.stateMask & SWT.SHIFT) > 0){
							Marker nearest = getMarkerFromMousePos(e.x);
							if(nearest == null) getComposite().setCursor(normalCursor);
							else getComposite().setCursor(moveMarkerCursor);
						}
						else getComposite().setCursor(normalCursor);
					}
					else getComposite().setCursor(normalCursor);
				}
			}
		});
		
		getComposite().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(selectedMarker == null || !editMarkers) return;
				switch (e.keyCode) {
					case SWT.ARROW_LEFT :
						if((e.stateMask & SWT.MOD1) > 0) moveMarker(selectedMarker, Math.max(0, selectedMarker.getPosition() - 1));
						else moveMarker(selectedMarker, Math.max(0, selectedMarker.getPosition() - moveStep));
						break;
					case SWT.ARROW_RIGHT :
						if((e.stateMask & SWT.MOD1) > 0) moveMarker(selectedMarker, Math.min(selectedMarker.getPosition() + 1, getTotalLength()));
						else moveMarker(selectedMarker, Math.min(selectedMarker.getPosition() + moveStep, getTotalLength()));
						break;
					default :
						break;
				}
			}
			public void keyReleased(KeyEvent e) {
			}
		});
	}
	/**
	 * Get the main Composite of this WaveFormDisplay
	 * @return This WaveFormDisplay´s main Composite
	 */
	public Composite getComposite(){
		return comp;
	}
	/**
	 * Set the colors for the different drawing elements to default colors
	 *
	 */
	protected void initColors(){
		Display d = comp.getDisplay();
		markerColor = d.getSystemColor(SWT.COLOR_GREEN);
		markerHighlightColor = d.getSystemColor(SWT.COLOR_DARK_MAGENTA);
		selectionColor = d.getSystemColor(SWT.COLOR_BLUE);
		positionColor = d.getSystemColor(SWT.COLOR_GRAY);
		channelBackgroundColor = d.getSystemColor(SWT.COLOR_DARK_RED);
		middleLineColor = d.getSystemColor(SWT.COLOR_RED);
		waveColor = d.getSystemColor(SWT.COLOR_YELLOW);
		backgroundColor = d.getSystemColor(SWT.COLOR_GRAY);
	}
	/**
	 * Paint this display: render the contentBuffer if needed, copy it to the
	 * screen and paint position, selection and markers on top of it
	 * @param gc The GC of the paint event
	 */
	protected void paint(GC gc){
		Rectangle area = comp.getClientArea();
		if(area.width <= 0 || area.height <= 0) return;
		if(contentBuffer != null){
			Rectangle bounds = contentBuffer.getBounds();
			if(bounds.width != area.width || bounds.height != area.height){
				contentBuffer.dispose();
				contentBuffer = null;
			}
		}
		if(contentBuffer == null){
			contentBuffer = new Image(comp.getDisplay(), area.width, area.height);
			contentValid = false;
		}
		if(!contentValid){
			GC bufferGC = new GC(contentBuffer);
			currentGC = bufferGC;
			renderingContent = true;
			try{
				// the margins around the channels are not drawn otherwise
				fill(backgroundColor, 0, 0, area.width, area.height);
				super.redraw();
			}
			finally{
				renderingContent = false;
				currentGC = null;
				bufferGC.dispose();
			}
			contentValid = true;
		}
		gc.drawImage(contentBuffer, 0, 0);
		if(getUsableDisplayHeight() <= 0 || getUsableDisplayWidth() <= 0) return;
		currentGC = gc;
		try{
			drawGlobalForeground(leftOffset, topOffset, getUsableDisplayWidth(), getUsableDisplayHeight());
		}
		finally{
			currentGC = null;
		}
		if(soft) Frames.clipCorners(gc, area.width, area.height, comp.getParent().getBackground());
		Frames.draw(gc, 0, 0, area.width, area.height);
	}
	/**
	 * Switch between the 2004 look (square display, one line per pixel
	 * column) and rounded displays with a smoothed, anti-aliased waveform
	 * and a faint middle line
	 * @param soft true for the smoothed look
	 */
	public void setSoft(boolean soft){
		this.soft = soft;
		requestPaint();
	}
	/**
	 * Draw the markers opaque, also in the smoothed look
	 * @param highContrast true for opaque markers
	 */
	/**
	 * Set the strength of the vertical gradient of the smoothed waveform. The
	 * waveform keeps its color at the middle line and is blended towards the
	 * background at the peaks.
	 * @param strength 0 for no gradient up to 1
	 */
	public void setWaveGradient(float strength){
		waveGradient = Math.max(0, Math.min(1, strength));
		redraw();
	}
	/**
	 * Draw the markers opaque for a high contrast
	 * @param highContrast true for opaque markers
	 */
	public void setHighContrast(boolean highContrast){
		this.highContrast = highContrast;
		requestPaint();
	}
	/**
	 * Set the width of the markers
	 * @param width The width in pixels
	 */
	public void setMarkerWidth(int width){
		markerWidth = width;
		requestPaint();
	}
	/**
	 * Request a paint event for this display. May be called from any thread.
	 */
	protected void requestPaint(){
		if(comp.isDisposed()) return;
		Display d = comp.getDisplay();
		if(d.getThread() == Thread.currentThread()) comp.redraw();
		else d.asyncExec(new Runnable() {
			public void run() {
				if(!comp.isDisposed()) comp.redraw();
			}
		});
	}
	/**
	 * Fill a rectangle with the given color using the current GC
	 */
	private void fill(Color c, int x, int y, int width, int height){
		currentGC.setBackground(c);
		currentGC.fillRectangle(x, y, width, height);
	}
	/**
	 * Change the Color used for drawing markers
	 * @param c The new Color to be used for drawing markers
	 */
	public void setMarkerColor(Color c){
		markerColor = c;
		requestPaint();
	}
	/**
	 * Change the Color used for drawing highlighted markers
	 * @param c The new Color to be used for drawing highlighted markers
	 */
	public void setHighlightedMarkerColor(Color c){
		markerHighlightColor = c;
		requestPaint();
	}
	/**
	 * Set the Color for drawing the selection
	 * @param c The new selection Color
	 */
	public void setSelectionColor(Color c){
		selectionColor = c;
		requestPaint();
	}
	/**
	 * Set the color used for drawing the position pointer
	 * @param c The Color for the position pointer
	 */
	public void setPositionColor(Color c){
		positionColor = c;
		requestPaint();
	}
	/**
	 * Set the background color used for each channel
	 * @param c The new channel background color
	 */
	public void setChannelBackgroundColor(Color c){
		channelBackgroundColor = c;
		redraw();
	}
	/**
	 * Set the color used for the area around the channels
	 * @param c The new background color
	 */
	public void setBackgroundColor(Color c){
		backgroundColor = c;
		// also shows in the border and behind the scroll bar
		comp.setBackground(c);
		redraw();
	}
	/**
	 * Set the color of the middle line
	 * @param c The middle line´s color
	 */
	public void setMiddleLineColor(Color c){
		middleLineColor = c;
		redraw();
	}
	/**
	 * Set the color used for drawing the WaveForm itself
	 * @param c The waveform´s color
	 */
	public void setWaveColor(Color c){
		waveColor = c;
		redraw();
	}
	// Position, selection and markers are painted from the current state in
	// every paint event, so the draw/erase methods only request a new paint
	// when they are called outside of it.
	protected void drawHighlightedMarker(Marker m) {
		if(currentGC == null) requestPaint();
		else fill(markerHighlightColor, leftOffset + dataToPixel(m.getPosition()) - (markerWidth - 1) / 2, topOffset, markerWidth, getUsableDisplayHeight());
	}
	protected void eraseHighlightedMarker(Marker m) {
		if(currentGC == null) requestPaint();
	}
	protected void drawMarker(Marker m) {
		if(currentGC == null) requestPaint();
		else{
			// in the smoothed look the slices don't cover the waveform; the
			// selected marker stays opaque, so that it stands out
			if(soft && !highContrast) currentGC.setAlpha(MARKER_ALPHA);
			fill(markerColor, leftOffset + dataToPixel(m.getPosition()) - (markerWidth - 1) / 2, topOffset, markerWidth, getUsableDisplayHeight());
			currentGC.setAlpha(255);
		}
	}
	protected void eraseMarker(Marker m) {
		if(currentGC == null) requestPaint();
	}
	protected void drawSelection(int x, int y, int width, int height) {
		if(currentGC == null) requestPaint();
		else if(width > 0){
			currentGC.setAlpha(96);
			fill(selectionColor, x, y, width, height);
			currentGC.setAlpha(255);
		}
	}
	protected void eraseSelection() {
		if(currentGC == null) requestPaint();
	}
	protected void drawPosition(int x, int y, int height) {
		if(currentGC == null) requestPaint();
		else fill(positionColor, x - 1, y, 2, height);
	}
	protected void erasePosition() {
		if(currentGC == null) requestPaint();
	}
	protected void drawGlobalBackground(int x, int y, int width, int height) {
		if(currentGC != null) fill(backgroundColor, x, y, width, height);
	}
	protected void drawChannelBackground(int x, int y, int width, int height) {
		if(currentGC == null) return;
		if(gradient){
			currentGC.setForeground(comp.getForeground());
			currentGC.setBackground(channelBackgroundColor);
			currentGC.fillGradientRectangle(x, y, width, height, true);
		}
		else fill(channelBackgroundColor, x, y, width, height);
	}
	protected void drawGlobalForeground(int x, int y, int width, int height) {
		// painted on top of the contentBuffer in paint()
		if(renderingContent) return;
		drawPosition(x + dataToPixel(pos), y, height);
		int leftPixel = dataToPixel(left);
		drawSelection(x + leftPixel, y, dataToPixel(right) - leftPixel, height);
		for (Iterator iter = markers.iterator(); iter.hasNext();) {
			Marker marker = (Marker) iter.next();
			if(marker == selectedMarker) drawHighlightedMarker(marker);
			else drawMarker(marker);
		}
	}
	protected void drawChannelForeground(int x, int y, int width, int height) {
		if(currentGC == null) return;
		// in the smoothed look the middle line only shines through faintly
		if(soft) currentGC.setAlpha(MIDDLE_LINE_ALPHA);
		currentGC.setForeground(middleLineColor);
		currentGC.drawLine(x, y + height / 2, x + width, y + height / 2);
		currentGC.setAlpha(255);
	}
	/**
	 * Draw the waveform. In the smoothed look, the outline of each channel
	 * is slightly smoothed and filled as one anti-aliased shape instead of
	 * drawing a line per pixel column.
	 * @see com.groovemanager.sampled.waveform.AbstractWaveFormDisplay#drawWave()
	 */
	protected void drawWave() {
		if(!soft || currentGC == null){
			super.drawWave();
			return;
		}
		WaveForm w = getVisibleWaveForm();
		int height = getChannelHeight();
		int columns = getUsableDisplayWidth() + 1;
		float[] min = new float[columns], max = new float[columns];
		currentGC.setAntialias(SWT.ON);
		currentGC.setBackground(waveColor);
		for(int channel = 0; channel < getChannels(); channel++){
			int top = getChannelTop(channel);
			w.rewind();
			int count = 0;
			min[count] = w.getMin(channel);
			max[count] = w.getMax(channel);
			count++;
			while(count < columns && w.next()){
				min[count] = w.getMin(channel);
				max[count] = w.getMax(channel);
				count++;
			}
			Path p = new Path(comp.getDisplay());
			// upper outline from left to right, lower one back
			for(int x = 0; x < count; x++){
				float y = smoothY(top, height, max, x, count);
				if(x == 0) p.moveTo(leftOffset, y);
				else p.lineTo(leftOffset + x, y);
			}
			for(int x = count - 1; x >= 0; x--){
				p.lineTo(leftOffset + x, smoothY(top, height, min, x, count));
			}
			p.close();
			if(waveGradient > 0) fillGradient(p, top, height);
			else currentGC.fillPath(p);
			p.dispose();
		}
		currentGC.setAntialias(SWT.DEFAULT);
	}
	/**
	 * Fill the outline of a channel with a gradient, mirrored at the middle
	 * line: the wave color at the middle line, blended towards the
	 * background at the peaks
	 * @param p The outline of the channel
	 * @param top The top of the channel
	 * @param height The height of the channel
	 */
	protected void fillGradient(Path p, int top, int height){
		Color outer = blend(waveColor, channelBackgroundColor, waveGradient);
		Rectangle clip = currentGC.getClipping();
		int middle = top + height / 2;
		Rectangle[] halves = {
			new Rectangle(clip.x, top, clip.width, middle - top),
			new Rectangle(clip.x, middle, clip.width, top + height - middle)
		};
		for (int i = 0; i < halves.length; i++) {
			int edge = i == 0 ? top : top + height;
			Pattern pattern = new Pattern(comp.getDisplay(), 0, edge, 0, middle, outer, waveColor);
			currentGC.setClipping(halves[i].intersection(clip));
			currentGC.setBackgroundPattern(pattern);
			currentGC.fillPath(p);
			currentGC.setBackgroundPattern(null);
			pattern.dispose();
		}
		currentGC.setClipping(clip);
		outer.dispose();
	}
	/**
	 * Mix two colors
	 * @param c The first color
	 * @param towards The second color
	 * @param amount The share of the second color, from 0 to 1
	 * @return The new Color, to be disposed by the caller
	 */
	protected Color blend(Color c, Color towards, float amount){
		RGB a = c.getRGB(), b = towards == null ? a : towards.getRGB();
		return new Color(comp.getDisplay(), new RGB(
				Math.round(a.red + (b.red - a.red) * amount),
				Math.round(a.green + (b.green - a.green) * amount),
				Math.round(a.blue + (b.blue - a.blue) * amount)));
	}
	/**
	 * Calculate the y coordinate of a waveform outline point, smoothed with
	 * its neighbours (weights 1, 2, 1)
	 * @param top The top of the channel
	 * @param height The height of the channel
	 * @param values The outline values
	 * @param x The index of the point
	 * @param count The number of valid values
	 * @return The y coordinate
	 */
	private static float smoothY(int top, int height, float[] values, int x, int count){
		float prev = values[Math.max(x - 1, 0)], next = values[Math.min(x + 1, count - 1)];
		float v = (prev + 2 * values[x] + next) / 4;
		return top + height / 2f - v / 128f * height / 2f;
	}
	protected void drawWaveLine(int x, int y, int x2, int y2) {
		if(currentGC == null) return;
		currentGC.setForeground(waveColor);
		currentGC.drawLine(x, y, x2, y2);
	}
	protected int getDisplayWidth() {
		return comp.getClientArea().width;
	}
	protected int getDisplayHeight() {
		return comp.getClientArea().height;
	}
	protected void redrawSelection(int x, int y, int width, int height) {
		if(currentGC == null) requestPaint();
		else drawSelection(x, y, width, height);
	}
	/**
	 * Invalidate the rendered waveform and request a new paint. The actual
	 * drawing happens in the next paint event.
	 */
	public synchronized void redraw() {
		contentValid = false;
		requestPaint();
		if(comp.isDisposed() || comp.getDisplay().getThread() != Thread.currentThread()) return;
		ScrollBar b = comp.getHorizontalBar();
		if(b != null){
			if(zoomFactor <= 1.0){
				b.setEnabled(false);
			}
			else{
				b.setEnabled(true);
				reactToScrollBar = false;
				int min = 0;
				int max = (int)Math.round(zoomFactor * 10);
				int pageInc = 10;
				if(b.getMinimum() != min) b.setMinimum(min);
				if(b.getMaximum() != max) b.setMaximum(max);
				if(b.getPageIncrement() != pageInc) b.setPageIncrement(pageInc);
				if(b.getThumb() != pageInc) b.setThumb(pageInc);
				
				int sel = (int)Math.round(scrollFactor * (b.getMaximum() - b.getThumb()));
				if(b.getSelection() != sel) b.setSelection(sel);
				reactToScrollBar = true;
			}
		}
	}
	/**
	 * Set the number of sample frames a Marker should be moved, when an arrow
	 * key is pressed
	 * @param newStep The number of sample frames to move a Marker with the
	 * arrow keys
	 */
	public void setMoveStep(int newStep){
		moveStep = newStep;
	}
}