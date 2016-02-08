/*******************************************************************************
 * This file is part of logisim-evolution.
 *
 *   logisim-evolution is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   logisim-evolution is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Original code by Carl Burch (http://www.cburch.com), 2011.
 *   Subsequent modifications by :
 *     + Haute École Spécialisée Bernoise
 *       http://www.bfh.ch
 *     + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *       http://hepia.hesge.ch/
 *     + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *       http://www.heig-vd.ch/
 *   The project is currently maintained by :
 *     + REDS Institute - HEIG-VD
 *       Yverdon-les-Bains, Switzerland
 *       http://reds.heig-vd.ch
 *******************************************************************************/

package com.cburch.logisim.circuit.appear;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cburch.draw.model.CanvasObject;
import com.cburch.draw.shapes.DrawAttr;
import com.cburch.draw.shapes.Rectangle;
import com.cburch.draw.shapes.Text;
import com.cburch.draw.util.EditableLabel;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.std.wiring.Pin;

class DefaultEvolutionAppearance {

	public static List<CanvasObject> build(Collection<Instance> pins) {
		Map<Direction, List<Instance>> edge;
		edge = new HashMap<Direction, List<Instance>>();
		edge.put(Direction.EAST, new ArrayList<Instance>());
		edge.put(Direction.WEST, new ArrayList<Instance>());
		int MaxLeftLabelLength = 0;
		int MaxRightLabelLength = 0;
		int TextHeight = 0;
		int TextAscent = 0;
		for (Instance pin : pins) {
//			Direction pinFacing = pin.getAttributeValue(StdAttr.FACING);
//			Direction pinEdge = pinFacing.reverse();
			Direction pinEdge;
			Text label = new Text(0,0,pin.getAttributeValue(StdAttr.LABEL));
			int LabelWidth = label.getLabel().getWidth();
			if (TextHeight==0) {
				TextHeight = label.getLabel().getHeight();
				TextAscent = label.getLabel().getAscent();
			}
			if (pin.getAttributeValue(Pin.ATTR_TYPE)) {
				pinEdge=Direction.EAST;
				if (LabelWidth>MaxRightLabelLength)
					MaxRightLabelLength = LabelWidth;
			}
			else {
				pinEdge=Direction.WEST;
				if (LabelWidth>MaxLeftLabelLength)
					MaxLeftLabelLength = LabelWidth;
			}
			List<Instance> e = edge.get(pinEdge);
			e.add(pin);
		}
		for (Map.Entry<Direction, List<Instance>> entry : edge.entrySet()) {
			DefaultAppearance.sortPinList(entry.getValue(), entry.getKey());
		}

		int numEast = edge.get(Direction.EAST).size();
		int numWest = edge.get(Direction.WEST).size();
		int maxHorz = Math.max(numEast, numWest);

		int dy = ((TextHeight+(TextHeight>>2)+5)/10)*10;

		int offsEast = computeOffset(numEast, numWest, dy);
		int offsWest = computeOffset(numWest, numEast, dy);

		int width = ((MaxLeftLabelLength+MaxRightLabelLength+35)/10)*10;
		int height = Math.max(dy+10, maxHorz*dy);

		// compute position of anchor relative to top left corner of box
		int ax;
		int ay;
		if (numEast > 0) { // anchor is on east side
			ax = width;
			ay = offsEast;
		} else if (numWest > 0) { // anchor is on west side
			ax = 0;
			ay = offsWest;
		} else { // anchor is top left corner
			ax = 0;
			ay = 0;
		}

		// place rectangle so anchor is on the grid
		int rx = OFFS + (9 - (ax + 9) % 10);
		int ry = OFFS + (9 - (ay + 9) % 10);

		Rectangle rect = new Rectangle(rx, ry, width, height);
		rect.setValue(DrawAttr.STROKE_WIDTH, Integer.valueOf(2));
		List<CanvasObject> ret = new ArrayList<CanvasObject>();
		ret.add(rect);
		placePins(ret, edge.get(Direction.WEST), rx, ry + offsWest, 0, dy,true,
				TextAscent/3);
		placePins(ret, edge.get(Direction.EAST), rx + width, ry + offsEast, 0,
				dy,false,TextAscent/3);
		ret.add(new AppearanceAnchor(Location.create(rx + ax, ry + ay)));
		return ret;
	}

	private static int computeOffset(int numFacing, int numOpposite, int dy) {
		int maxThis = Math.max(numFacing, numOpposite);
		int maxOffs;
		switch (maxThis) {
		case 0:
		case 1:
			maxOffs = (dy+10)/2;
			break;
		default:
			maxOffs = dy/2;
		}
		return maxOffs + dy * ((maxThis - numFacing) / 2);
	}

	private static void placePins(List<CanvasObject> dest, List<Instance> pins,
			int x, int y, int dx, int dy, boolean LeftSide, int ldy) {
		int halign;
		Color color = Color.DARK_GRAY;
		int ldx;
		for (Instance pin : pins) {
			dest.add(new AppearancePort(Location.create(x, y), pin));
			if (LeftSide) {
				ldx=5;
				halign=EditableLabel.LEFT;
			} else {
				ldx=-5;
				halign=EditableLabel.RIGHT;
			}
			if (pin.getAttributeSet().containsAttribute(StdAttr.LABEL)) {
				Text label = new Text(x+ldx,y+ldy,pin.getAttributeValue(StdAttr.LABEL));
				label.getLabel().setHorizontalAlignment(halign);
				label.getLabel().setColor(color);
				dest.add(label);
			}
			x += dx;
			y += dy;
		}
	}

	private static final int OFFS = 50;

	private DefaultEvolutionAppearance() {
	}
}