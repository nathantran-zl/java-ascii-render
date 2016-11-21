package com.indvd00m.ascii.render.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.indvd00m.ascii.render.Point;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContext;
import com.indvd00m.ascii.render.api.IElement;
import com.indvd00m.ascii.render.api.IPoint;

/**
 * PseudoText element.
 * 
 * <pre>
 *                                                ██                                                                      
 * ██████▒                                        ██           ██████████                         ██                      
 * ██ ░░██▒                                       ██               ██                             ██                      
 * ██   ░██                                       ██               ██                             ██                      
 * ██   ░██  ░▒████▓▒   ░▓███▒    ██    ██   ▒███▒██   ▒████░      ██       ░▓███▒   ▓██░░██▓   ███████                   
 * ██   ░██  ▓█▓░░░▒▓  ░██░░██░   ██    ██  ░██░░███  ░██░░██░     ██      ░██░░██░  ░██▓▓██░     ██                      
 * ██ ░░██▒  ██▒░      ▓█▒  ░█▓   ██    ██  ▓█▒  ▒██  ▓█▒  ▒█▓     ██      ▓█▒  ░█▓   ░████░      ██                      
 * ██████▒   ▓████▓▒   ██░  ░██   ██    ██  ██░  ░██  ██░  ░██     ██      ██░  ░██    ▒██▒       ██                      
 * ██        ░▓█████▒  ████████   ██    ██  ██░  ░██  ██░  ░██     ██      ████████    ░██░       ██                      
 * ██           ░░▓██  ██░        ██░  ░██  ██░  ░██  ██░  ░██     ██      ██░         ▓██▓       ██                      
 * ██             ░██  ▓█▒        ██░  ░██  ▓█▒  ▒██  ▓█▒  ▒█▓     ██      ▓█▒        ▒████▒      ██░                     
 * ██        █▒░░░▓█▓  ░██▒░░▒▓   ▓█▓░░███  ░██░░███  ░██░░██░     ██      ░██▒░░▒▓  ░██▒▒██░     ▓█▒░                    
 * ██        ░▓████▒░   ░▓███▓░   ░▓███░██   ▒███▒██   ▒████▒      ██       ░▓███▓░  ▓██░░██▓     ░▓███
 * </pre>
 * 
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date 2016-Nov-21 12:14:24 PM
 *
 */
public class PseudoText implements IElement {

	String text;
	int x;
	int y;
	int height;
	boolean antialising = true;;

	public PseudoText(String text) {
		super();
		this.text = text;
		this.x = Integer.MIN_VALUE;
		this.y = Integer.MIN_VALUE;
		this.height = Integer.MIN_VALUE;
	}

	public PseudoText(String text, boolean antialising) {
		super();
		this.text = text;
		this.x = Integer.MIN_VALUE;
		this.y = Integer.MIN_VALUE;
		this.height = Integer.MIN_VALUE;
		this.antialising = antialising;
	}

	public PseudoText(String text, int x, int y, int height) {
		super();
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = height;
	}

	public PseudoText(String text, int x, int y, int height, boolean antialising) {
		super();
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = height;
		this.antialising = antialising;
	}

	@Override
	public IPoint draw(ICanvas canvas, IContext context) {
		int x = this.x;
		int y = this.y;
		int height = this.height;

		if (x == Integer.MIN_VALUE)
			x = 0;
		if (y == Integer.MIN_VALUE)
			y = 0;
		if (height == Integer.MIN_VALUE)
			height = canvas.getHeight();

		if (height <= 0)
			return null;

		int width = canvas.getWidth() - x;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		if (antialising)
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		else
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 24);
		FontMetrics fm = graphics.getFontMetrics(font);
		Rectangle2D r2d = fm.getStringBounds(text, graphics);
		float leadingFactor = (float) fm.getLeading() / fm.getHeight();
		float descentFactor = (float) fm.getDescent() / fm.getHeight();
		float size = (float) (font.getSize2D() * height / (r2d.getHeight() - r2d.getHeight() * leadingFactor));
		font = font.deriveFont(size);

		Color fontColor = Color.BLACK;
		Color backgroundColor = Color.WHITE;

		graphics.setFont(font);
		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(fontColor);
		graphics.drawString(text, 0, height - height * descentFactor);

		// writeImageToPNG(image, "/tmp/pseudotext.png");

		for (int imgX = 0; imgX < width; imgX++) {
			for (int imgY = 0; imgY < height; imgY++) {
				int rgb = image.getRGB(imgX, imgY);
				Color color = new Color(rgb);
				if (!backgroundColor.equals(color)) {
					int cx = imgX + x;
					int cy = imgY + y;
					char c = colorToChar(fontColor, backgroundColor, color);
					canvas.draw(cx, cy, c + "");
				}
			}
		}

		return new Point(x, y);
	}

	char colorToChar(Color fontColor, Color backgroundColor, Color pixelColor) {
		if (backgroundColor.equals(pixelColor))
			return ' ';
		if (antialising) {
			double diff = getColorDistancePercentage(fontColor, pixelColor, backgroundColor);
			if (diff > 75d)
				return '░';
			else if (diff > 50d)
				return '▒';
			else if (diff > 25d)
				return '▓';
			else
				return '█';
		} else {
			return '█';
		}
	}

	double getColorDistancePercentage(Color sourceColor, Color targetColor, Color maxDistanceColor) {
		double maxDistance = getColorDistance(sourceColor, maxDistanceColor);
		double distance = getColorDistance(sourceColor, targetColor);
		double diff = distance / maxDistance;
		return diff * 100;
	}

	double getColorDistance(Color c1, Color c2) {
		double rmean = (c1.getRed() + c2.getRed()) / 2;
		int r = c1.getRed() - c2.getRed();
		int g = c1.getGreen() - c2.getGreen();
		int b = c1.getBlue() - c2.getBlue();
		double weightR = 2 + rmean / 256;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256;
		return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
	}

	void writeImageToPNG(BufferedImage image, String path) {
		try {
			ImageIO.write(image, "png", new File(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getText() {
		return text;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}

	public boolean isAntialising() {
		return antialising;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (antialising ? 1231 : 1237);
		result = prime * result + height;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PseudoText other = (PseudoText) obj;
		if (antialising != other.antialising)
			return false;
		if (height != other.height)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PseudoText [");
		if (text != null) {
			builder.append("text=");
			builder.append(text);
			builder.append(", ");
		}
		builder.append("x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append(", height=");
		builder.append(height);
		builder.append(", antialising=");
		builder.append(antialising);
		builder.append("]");
		return builder.toString();
	}

}
