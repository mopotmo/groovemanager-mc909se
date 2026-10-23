package com.groovemanager.gui.custom;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Draws the transport button symbols as vector shapes with a transparent
 * background, so that they fit any button color and are sharp on high
 * resolution screens. The pixels are computed directly, because drawing
 * on transparent images with a GC is not reliable on all platforms.
 * @author Manu Robledo
 *
 */
public class TransportIcons {
	/**
	 * Constants for the available symbols
	 */
	public final static int PLAY = 0, PAUSE = 1, STOP = 2, REC = 3, LOOP = 4;
	/**
	 * Size of the icons in points
	 */
	public final static int WIDTH = 28, HEIGHT = 16;
	/**
	 * Number of samples per pixel and direction for anti-aliasing
	 */
	private final static int SAMPLES = 4;
	/**
	 * Create a new icon. The caller has to dispose it.
	 * @param d The Display to create the Image on
	 * @param symbol One of the symbol constants
	 * @param color The color of the symbol
	 * @return The new Image
	 */
	public static Image create(Display d, final int symbol, Color color){
		final RGB rgb = color.getRGB();
		return new Image(d, new ImageDataProvider() {
			public ImageData getImageData(int zoom) {
				return render(symbol, rgb, zoom);
			}
		});
	}
	/**
	 * Render a symbol at the given zoom level
	 * @param symbol One of the symbol constants
	 * @param rgb The color of the symbol
	 * @param zoom The zoom level in percent
	 * @return The rendered ImageData
	 */
	protected static ImageData render(int symbol, RGB rgb, int zoom){
		float scale = zoom / 100f;
		int width = Math.round(WIDTH * scale), height = Math.round(HEIGHT * scale);
		ImageData data = new ImageData(width, height, 24, new PaletteData(0xFF0000, 0xFF00, 0xFF));
		int pixel = data.palette.getPixel(rgb);
		byte[] alpha = new byte[width * height];
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				data.setPixel(x, y, pixel);
				int hits = 0;
				for(int sy = 0; sy < SAMPLES; sy++){
					for(int sx = 0; sx < SAMPLES; sx++){
						float px = (x + (sx + 0.5f) / SAMPLES) / scale;
						float py = (y + (sy + 0.5f) / SAMPLES) / scale;
						if(inside(symbol, px, py)) hits++;
					}
				}
				alpha[y * width + x] = (byte)(hits * 255 / (SAMPLES * SAMPLES));
			}
		}
		data.alphaData = alpha;
		return data;
	}
	/**
	 * Tell whether a point belongs to a symbol
	 * @param symbol One of the symbol constants
	 * @param x The x coordinate in points
	 * @param y The y coordinate in points
	 * @return true, if the point is part of the symbol
	 */
	protected static boolean inside(int symbol, float x, float y){
		float dx = x - WIDTH / 2f, dy = y - HEIGHT / 2f;
		switch(symbol){
			case PLAY:
				return dx >= -4 && Math.abs(dy) <= (6 - dx) * 0.6f;
			case PAUSE:
				return Math.abs(dy) <= 5 && Math.abs(dx) >= 1.5f && Math.abs(dx) <= 4.5f;
			case STOP:
				return Math.abs(dx) <= 4.5f && Math.abs(dy) <= 4.5f;
			case REC:
				return dx * dx + dy * dy <= 25;
			case LOOP:
				// two rings next to each other
				return ring(Math.abs(dx) - 4.5f, dy, 4.5f, 3.5f, 1f);
		}
		return false;
	}
	/**
	 * Tell whether a point belongs to an elliptic ring around the origin
	 * @param x The x coordinate relative to the center
	 * @param y The y coordinate relative to the center
	 * @param rx The horizontal radius of the ring´s center line
	 * @param ry The vertical radius of the ring´s center line
	 * @param half Half of the line width
	 * @return true, if the point is part of the ring
	 */
	private static boolean ring(float x, float y, float rx, float ry, float half){
		float outer = (x * x) / ((rx + half) * (rx + half)) + (y * y) / ((ry + half) * (ry + half));
		float inner = (x * x) / ((rx - half) * (rx - half)) + (y * y) / ((ry - half) * (ry - half));
		return outer <= 1 && inner >= 1;
	}
}
