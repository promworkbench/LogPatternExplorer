package org.processmining.lip.model.pattern.vis;

import java.awt.Color;

/**
 * 
 * 
 * @author xlu
 *
 */
public class ColorUtil {

	public static Color getContrastColor(Color color) {
		double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue())
				/ 1000;
		return y >= 128 ? Color.black : Color.white;
	}

	public static String convertToHEXColor(Color c) {
		String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(),
				c.getBlue());
		return hex;
	}
}
