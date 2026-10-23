import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * Draws the app icon: a waveform with slice markers above four pads, in the
 * colors of the editor. Writes icon.png (1024 px), icon.ico (Windows) and an
 * iconset that iconutil turns into icon.icns (macOS).
 *
 * java packaging/icon/IconGenerator.java packaging/icon
 * iconutil -c icns packaging/icon/icon.iconset -o packaging/icon/icon.icns
 */
public class IconGenerator {
	static BufferedImage draw(int size) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.scale(size / 1024.0, size / 1024.0);

		// body: macOS icon grid (824 px square with 100 px margin)
		RoundRectangle2D body = new RoundRectangle2D.Double(100, 100, 824, 824, 185, 185);
		g.setPaint(new GradientPaint(0, 100, new Color(0x3a3d42), 0, 924, new Color(0x1c1e21)));
		g.fill(body);
		g.setStroke(new BasicStroke(4));
		g.setColor(new Color(255, 255, 255, 40));
		g.draw(body);

		// screen with the waveform
		RoundRectangle2D screen = new RoundRectangle2D.Double(170, 180, 684, 400, 40, 40);
		g.setColor(Color.BLACK);
		g.fill(screen);
		Shape oldClip = g.getClip();
		g.clip(screen);
		double mid = 380;
		Random r = new Random(909);
		int[] slices = {170, 330, 470, 640, 854};
		Path2D wave = new Path2D.Double();
		wave.moveTo(170, mid);
		java.util.List<double[]> pts = new ArrayList<>();
		for (int x = 170; x <= 854; x += 6) {
			// decaying hits at each slice start
			double env = 0.12;
			for (int i = 0; i < slices.length - 1; i++) if (x >= slices[i]) env = Math.max(env, Math.exp(-(x - slices[i]) / 55.0));
			double amp = 175 * env * (0.55 + 0.45 * r.nextDouble());
			pts.add(new double[]{x, amp});
		}
		for (double[] p : pts) wave.lineTo(p[0], mid - p[1]);
		for (int i = pts.size() - 1; i >= 0; i--) wave.lineTo(pts.get(i)[0], mid + pts.get(i)[1]);
		wave.closePath();
		g.setColor(new Color(0xffe600));
		g.fill(wave);
		g.setColor(new Color(0xff2a2a));
		g.setStroke(new BasicStroke(5));
		g.draw(new Line2D.Double(170, mid, 854, mid));
		g.setColor(new Color(0x39ff6a));
		g.setStroke(new BasicStroke(7));
		for (int i = 1; i < slices.length - 1; i++) g.draw(new Line2D.Double(slices[i], 180, slices[i], 580));
		g.setClip(oldClip);

		// four pads, the second one lit
		for (int i = 0; i < 4; i++) {
			double x = 170 + i * 176;
			RoundRectangle2D pad = new RoundRectangle2D.Double(x, 650, 156, 200, 34, 34);
			g.setColor(new Color(0x0b0c0d));
			g.fill(new RoundRectangle2D.Double(x - 8, 642, 172, 216, 42, 42));
			if (i == 1) g.setPaint(new GradientPaint(0, 650, new Color(0xfff27a), 0, 850, new Color(0xffc400)));
			else g.setPaint(new GradientPaint(0, 650, new Color(0xe6dfdc), 0, 850, new Color(0xb9b1ae)));
			g.fill(pad);
		}
		g.dispose();
		return img;
	}

	static byte[] png(BufferedImage img) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, "png", out);
		return out.toByteArray();
	}

	public static void main(String[] args) throws IOException {
		Path dir = Paths.get(args.length > 0 ? args[0] : ".");
		Files.write(dir.resolve("icon.png"), png(draw(1024)));

		Path iconset = dir.resolve("icon.iconset");
		Files.createDirectories(iconset);
		for (int s : new int[]{16, 32, 128, 256, 512}) {
			Files.write(iconset.resolve("icon_" + s + "x" + s + ".png"), png(draw(s)));
			Files.write(iconset.resolve("icon_" + s + "x" + s + "@2x.png"), png(draw(s * 2)));
		}

		// ICO with PNG-compressed entries (Windows Vista and later)
		int[] sizes = {16, 24, 32, 48, 64, 128, 256};
		java.util.List<byte[]> images = new ArrayList<>();
		for (int s : sizes) images.add(png(draw(s)));
		ByteArrayOutputStream ico = new ByteArrayOutputStream();
		DataOutputStream d = new DataOutputStream(ico);
		d.writeShort(0); d.writeShort(Short.reverseBytes((short) 1)); d.writeShort(Short.reverseBytes((short) sizes.length));
		int offset = 6 + 16 * sizes.length;
		for (int i = 0; i < sizes.length; i++) {
			d.writeByte(sizes[i] == 256 ? 0 : sizes[i]); d.writeByte(sizes[i] == 256 ? 0 : sizes[i]);
			d.writeByte(0); d.writeByte(0);
			d.writeShort(Short.reverseBytes((short) 1)); d.writeShort(Short.reverseBytes((short) 32));
			d.writeInt(Integer.reverseBytes(images.get(i).length)); d.writeInt(Integer.reverseBytes(offset));
			offset += images.get(i).length;
		}
		for (byte[] b : images) d.write(b);
		Files.write(dir.resolve("icon.ico"), ico.toByteArray());
	}
}
