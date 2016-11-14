
package com.evanbelcher.DrillSweet2.data;

import java.awt.Point;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom ConcurrentHashMap that treats Points differently in the get method
 * Pretty sure this is unnecessary but don't feel like removing it and testing
 * 
 * @author Evan Belcher
 * @version 1.0
 * @since 1.0
 */
public class DS2ConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
	
	private static final long serialVersionUID = -4762144215147408857L;

	public DS2ConcurrentHashMap() {
		super();
	}
	
	public DS2ConcurrentHashMap(DS2ConcurrentHashMap<K, V> m) {
		super(m);
	}
	
	/**
	 * Gets the value of the key. If the key is a point, get the value from the equivalent point in
	 * the keyset.
	 * 
	 * @param key
	 * @return value
	 * @since 1.0
	 */
	@Override
	public V get(Object key) {
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
