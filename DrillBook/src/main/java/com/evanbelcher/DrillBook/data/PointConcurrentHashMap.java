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

package com.evanbelcher.DrillBook.data;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom ConcurrentHashMap that treats Points differently in the get method
 * Pretty sure this is unnecessary but don't feel like removing it and testing
 *
 * @author Evan Belcher
 */
public class PointConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = -4762144215147408857L;

	public PointConcurrentHashMap() {
		super();
	}

	public PointConcurrentHashMap(PointConcurrentHashMap<K, V> m) {
		super(m);
	}

	/**
	 * Gets the value of the key. If the key is a point, get the value from the equivalent point in
	 * the keyset.
	 *
	 * @param key
	 * @return value
	 */
	@Override public V get(Object key) {
		if (key instanceof Point) {
			Point point = (Point) key;
			for (Object o : keySet()) {
				Point p = (Point) o;
				if (point.x == p.x && point.y == p.y) {
					return super.get(o);
				}
			}
			return null;
		} else {
			return super.get(key);
		}
	}
}
