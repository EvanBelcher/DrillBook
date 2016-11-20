package main.java.com.evanbelcher.DrillSweet2.display;

import java.awt.Rectangle;

/**
 * Custom Rectangle whose contain method includes the bounds
 *
 * @author Evan Belcher
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("unused") public class DS2Rectangle extends Rectangle {

	private static final long serialVersionUID = 8209060786988503451L;

	public DS2Rectangle(Rectangle r) {
		super(r);
	}

	public DS2Rectangle(int i, int j, int k, int l) {
		super(i, j, k, l);
	}

	/**
	 * Constructor that takes doubles for convenience / cleanliness
	 *
	 * @since 1.0
	 */
	public DS2Rectangle(double d, double e, double f, double g) {
		super((int) d, (int) e, (int) f, (int) g);
	}

	/**
	 * Override inside() to include the edges of the rectangle
	 *
	 * @param X x coordinate
	 * @param Y y coordinate
	 * @return whether the given coordinates fall inside or on the edges of this rectangle
	 * @since 1.0
	 */
	@SuppressWarnings("deprecation") @Override
	public boolean inside(int X, int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		//    overflow || intersect
		return ((w < x || w >= X) && (h < y || h >= Y));
	}

}
