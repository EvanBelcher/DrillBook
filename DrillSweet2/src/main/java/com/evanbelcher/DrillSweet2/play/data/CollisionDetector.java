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

package com.evanbelcher.DrillSweet2.play.data;

import com.evanbelcher.DrillSweet2.display.DS2DesktopPane;

import java.awt.*;
import java.awt.geom.*;

/**
 * Detects if two points collide or intersect
 * <a href=http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect>Stackoverflow post detailing how this is done.</a>
 *
 * @author Evan Belcher
 */
@SuppressWarnings("unused") public class CollisionDetector {

	/**
	 * Returns if the two MovingPoints intersect
	 *
	 * @param mp1
	 * @param mp2
	 */
	public static boolean findIntersection(MovingPoint mp1, MovingPoint mp2) {
		return findIntersection(mp1.getStart(), mp1.getEnd(), mp2.getStart(), mp2.getEnd());
	}

	/**
	 * Returns if the line segments denoted by points (p1,p2) and (q1,q2) intersect
	 *
	 * @param p1
	 * @param p2
	 * @param q1
	 * @param q2
	 */
	public static boolean findIntersection(Point p1, Point p2, Point q1, Point q2) {
		return findIntersection(p1.x, p1.y, p2.x, p2.y, q1.x, q1.y, q2.x, q2.y);
	}

	/**
	 * Returns if the two MovingPoints collide
	 *
	 * @param mp1
	 * @param mp2
	 */
	public static boolean findCollision(MovingPoint mp1, MovingPoint mp2) {
		return findCollision(mp1.getStart(), mp1.getEnd(), mp2.getStart(), mp2.getEnd());
	}

	/**
	 * Returns if the two line segments denoted by points (p1,p2) and (q1,q2) collide
	 *
	 * @param p1
	 * @param p2
	 * @param q1
	 * @param q2
	 */
	public static boolean findCollision(Point p1, Point p2, Point q1, Point q2) {
		return findCollision(p1.x, p1.y, p2.x, p2.y, q1.x, q1.y, q2.x, q2.y);
	}

	/**
	 * @param p1x
	 * @param p1y
	 * @param p2x
	 * @param p2y
	 * @param q1x
	 * @param q1y
	 * @param q2x
	 * @param q2y
	 */
	private static boolean findIntersection(double p1x, double p1y, double p2x, double p2y, double q1x, double q1y, double q2x, double q2y) {
		Point2D.Double p = new Point2D.Double(p1x, p1y);
		Point2D.Double q = new Point2D.Double(q1x, q1y);

		Line2D.Double r = new Line2D.Double(p1x, p1y, p2x, p2y);
		Line2D.Double s = new Line2D.Double(q1x, q1y, q2x, q2y);

		if (p.getX() > q.getX()) {
			q = new Point2D.Double(p1x, p1y);
			p = new Point2D.Double(q1x, q1y);
			s = new Line2D.Double(p1x, p1y, p2x, p2y);
			r = new Line2D.Double(q1x, q1y, q2x, q2y);
		} else if (p.getX() == q.getX() && p.getY() > q.getY()) {
			q = new Point2D.Double(p1x, p1y);
			p = new Point2D.Double(q1x, q1y);
			s = new Line2D.Double(p1x, p1y, p2x, p2y);
			r = new Line2D.Double(q1x, q1y, q2x, q2y);
		}

		double RcrossS = cross(r, s);
		double QminPcrossR = cross(minus(q, p), r);
		double QminPcrossS = cross(minus(q, p), s);

		if (RcrossS == 0 && QminPcrossR == 0) {
			double t0 = QminPcrossR / dot(r, r);
			double t1 = t0 + dot(s, r) / dot(r, r);
			return t1 < t0 || 0 <= t1 && t0 <= 1 && !(length(r) == length(s)) && 0 <= (s.getX1() - r.getX1()) / (length(r) - length(s)) && (s.getX1() - r.getX1()) / (length(r) - length(s)) <= 1;
		} else if (RcrossS == 0) {
			return false;
		} else {
			double t = QminPcrossS / RcrossS;
			double u = QminPcrossR / RcrossS;
			if (0 <= t && t <= 1 && 0 <= u && u <= 1)
				if (t == u)
					return true;
		}
		return false;
	}

	/**
	 * @param p1x
	 * @param p1y
	 * @param p2x
	 * @param p2y
	 * @param q1x
	 * @param q1y
	 * @param q2x
	 * @param q2y
	 */
	private static boolean findCollision(double p1x, double p1y, double p2x, double p2y, double q1x, double q1y, double q2x, double q2y) {
		final double delta = 1;
		final double scale = (100 / DS2DesktopPane.getField().getWidth());

		Point2D.Double p = new Point2D.Double(p1x, p1y);
		Point2D.Double q = new Point2D.Double(q1x, q1y);

		Line2D.Double r = new Line2D.Double(p1x, p1y, p2x, p2y);
		Line2D.Double s = new Line2D.Double(q1x, q1y, q2x, q2y);

		if (p.distance(q) * scale < delta || r.getP2().distance(s.getP2()) * scale < delta)
			return true;

		if (p.getX() > q.getX()) {
			q = new Point2D.Double(p1x, p1y);
			p = new Point2D.Double(q1x, q1y);
			s = new Line2D.Double(p1x, p1y, p2x, p2y);
			r = new Line2D.Double(q1x, q1y, q2x, q2y);
		} else if (p.getX() == q.getX() && p.getY() > q.getY()) {
			q = new Point2D.Double(p1x, p1y);
			p = new Point2D.Double(q1x, q1y);
			s = new Line2D.Double(p1x, p1y, p2x, p2y);
			r = new Line2D.Double(q1x, q1y, q2x, q2y);
		}

		double RcrossS = cross(r, s);
		double QminPcrossR = cross(minus(q, p), r);
		double QminPcrossS = cross(minus(q, p), s);

		if (RcrossS == 0 && QminPcrossR == 0) {
			double t0 = QminPcrossR / dot(r, r);
			double t1 = t0 + dot(s, r) / dot(r, r);
			if (t1 < t0)
				return true;
			else {
				Point2D.Double pAtt0 = new Point2D.Double(p.getX() + (r.getX2() - r.getX1()) * t0, p.getY() + (r.getY2() - r.getY1()) * t0);
				Point2D.Double pAtt1 = new Point2D.Double(p.getX() + (r.getX2() - r.getX1()) * t1, p.getY() + (r.getY2() - r.getY1()) * t1);
				Point2D.Double qAtt0 = new Point2D.Double(q.getX() + (s.getX2() - s.getX1()) * t0, q.getY() + (s.getY2() - s.getY1()) * t0);
				Point2D.Double qAtt1 = new Point2D.Double(q.getX() + (s.getX2() - s.getX1()) * t1, q.getY() + (s.getY2() - s.getY1()) * t1);
				double distance = Math.min(pAtt0.distance(qAtt0), pAtt1.distance(qAtt1));
				distance *= scale;
				if (distance < delta)
					return true;
			}
			//return t1 < t0 || 0 <= t1 && t0 <= 1 && !(length(r) == length(s)) && 0 <= (s.getX1() - r.getX1()) / (length(r) - length(s)) && (s.getX1() - r.getX1()) / (length(r) - length(s)) <= 1;
		} else if (RcrossS == 0) {
			return false;
		} else {
			double t = QminPcrossS / RcrossS;
			double u = QminPcrossR / RcrossS;

			if (0 <= t && t <= 1 && 0 <= u && u <= 1) {
				Point2D.Double pAtt = new Point2D.Double(p.getX() + (r.getX2() - r.getX1()) * t, p.getY() + (r.getY2() - r.getY1()) * t);
				Point2D.Double pAtu = new Point2D.Double(p.getX() + (r.getX2() - r.getX1()) * u, p.getY() + (r.getY2() - r.getY1()) * u);
				Point2D.Double qAtt = new Point2D.Double(q.getX() + (s.getX2() - s.getX1()) * t, q.getY() + (s.getY2() - s.getY1()) * t);
				Point2D.Double qAtu = new Point2D.Double(q.getX() + (s.getX2() - s.getX1()) * u, q.getY() + (s.getY2() - s.getY1()) * u);
				double distance = Math.min(pAtt.distance(qAtt), pAtu.distance(qAtu));
				distance *= scale;

				if (distance < delta)
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns the vector from the subtraction of point p from point q
	 *
	 * @param q
	 * @param p
	 */
	private static Line2D.Double minus(Point2D.Double p, Point2D.Double q) {
		return new Line2D.Double(q, p);
	}

	/**
	 * Returns the value of the cross product from the 2D vectors v and w
	 *
	 * @param v
	 * @param w
	 */
	private static double cross(Line2D.Double v, Line2D.Double w) {
		return (v.getX2() - v.getX1()) * (w.getY2() - w.getY1()) - (v.getY2() - v.getY1()) * (w.getX2() - w.getX1());
	}

	/**
	 * Returns the value of the dot product of the 2D vectors v and w
	 *
	 * @param v
	 * @param w
	 */
	private static double dot(Line2D.Double v, Line2D.Double w) {
		return (v.getX2() - v.getX1()) * (w.getX2() - w.getX1()) + (v.getY2() - v.getY1()) * (w.getY2() - w.getY1());
	}

	/**
	 * Returns the length of a given line/vector
	 *
	 * @param l
	 */
	private static double length(Line2D.Double l) {
		return l.getP1().distance(l.getP2());
	}

}
