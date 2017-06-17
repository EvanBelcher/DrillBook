/*
		Drill Sweet 2 is a marching band drill creation software.
		Copyright (C) 2017  Evan Belcher

		This program is free software: you can redistribute it and/or modify
		it under the terms of the GNU General Public License as published by
		the Free Software Foundation, either version 3 of the License, or
		(at your option) any later version.

		This program is distributed in the hope that it will be useful,
		but WITHOUT ANY WARRANTY; without even the implied warranty of
		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
		GNU General Public License for more details.

		You should have received a copy of the GNU General Public License
		along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.evanbelcher.DrillBook.display.data;

import java.awt.*;

/**
 * Custom Rectangle whose contain method includes the bounds
 *
 * @author Evan Belcher
 */
@SuppressWarnings("unused") public class DBRectangle extends Rectangle {

	private static final long serialVersionUID = 8209060786988503451L;

	public DBRectangle(Rectangle r) {
		super(r);
	}

	/**
	 * If the dimensions are negative, change the bounds so that everything works
	 *
	 * @param i x
	 * @param j y
	 * @param k width
	 * @param l height
	 */
	public DBRectangle(int i, int j, int k, int l) {
		super(i, j, k, l);
		int x = i, y = j, w = k, h = l;
		if (k < 0) {
			x = i + k;
			w = -k;
		}
		if (l < 0) {
			y = j + l;
			h = -l;
		}
		setBounds(x, y, w, h);
	}

	/**
	 * Constructor that takes doubles for convenience / cleanliness
	 */
	public DBRectangle(double d, double e, double f, double g) {
		new DBRectangle((int) d, (int) e, (int) f, (int) g);
	}

	/**
	 * Override inside() to include the edges of the rectangle
	 *
	 * @param X x coordinate
	 * @param Y y coordinate
	 * @return whether the given coordinates fall inside or on the edges of this rectangle
	 */
	@SuppressWarnings("deprecation") @Override public boolean inside(int X, int Y) {
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
