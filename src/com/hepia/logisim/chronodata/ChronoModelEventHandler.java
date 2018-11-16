/**
 * This file is part of Logisim-evolution.
 *
 * Logisim-evolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Logisim-evolution is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * Subsequent modifications by:
 *   + Haute École Spécialisée Bernoise
 *     http://www.bfh.ch
 *   + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *     http://hepia.hesge.ch/
 *   + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *     http://www.heig-vd.ch/
 *   + REDS Institute - HEIG-VD, Yverdon-les-Bains, Switzerland
 *     http://reds.heig-vd.ch
 * This version of the project is currently maintained by:
 *   + Kevin Walsh (kwalsh@holycross.edu, http://mathcs.holycross.edu/~kwalsh)
 */
package com.hepia.logisim.chronodata;

import java.util.ArrayList;
import java.util.Arrays;

import com.cburch.logisim.circuit.SubcircuitFactory;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.Selection;
import com.cburch.logisim.proj.Project;
import com.hepia.logisim.chronogui.ChronoPanel;

public class ChronoModelEventHandler implements Model.Listener {

	private ChronoPanel chronoPanel;
	private ChronoData chronoData;
	// contains the signals order, as they are stored in the Model.Listener
	private String[] signalNamesKeepOrder;

	private String lastSysclk = "0";
	private int sysclkPos = -1;

	public ChronoModelEventHandler(ChronoPanel chronoPanel, Model model,
			Project prj) throws NoSysclkException {
		this.chronoPanel = chronoPanel;

		this.chronoData = chronoPanel.getChronoData();
		model.addModelListener(this);

		Selection sel = model.getSelection();
		int columns = sel.size();
		signalNamesKeepOrder = new String[columns];
		for (int i = 0; i < columns; i++) {
			String name = "";
			Component[] path = sel.get(i).getPath();
			for (int k = 0; k < path.length; k++) {
				SubcircuitFactory circFact = (SubcircuitFactory) path[k]
						.getFactory();
				name += circFact.getDisplayName() + "/";
			}
			name += sel.get(i).toShortString();
			signalNamesKeepOrder[i] = name;

			String value = sel.get(i).fetchValue(model.getCircuitState())
					.toString();
			// is the entry a bus?
			if (value.length() > 1)
				chronoData.put(name, new SignalDataBus(name,
						new ArrayList<String>()));
			else
				chronoData.put(name, new SignalData(name,
						new ArrayList<String>()));
			// add initial data
			chronoData.appendValueToSignal(name, value);
			chronoData.appendValueToSignal(name, value);

			// save sysclk position in signalNamesKeepOrder
			if (name.equals("sysclk"))
				sysclkPos = i;
		}
		if (sysclkPos == -1)
			throw new NoSysclkException("No sysclk signal found");
		// store signal order
		chronoData.setSignalOrder(new ArrayList<String>(Arrays
				.asList(signalNamesKeepOrder)));
	}

	@Override
	public void entryAdded(Model.Event event, Value[] values) {
		if (chronoPanel.isRealTimeMode() && (sysclkPos >= 0)) {
			try {
				// update gui only on sysclk change
				if (!values[sysclkPos].toString().equals(lastSysclk)) {
					lastSysclk = values[sysclkPos].toString();
					int pos = 0;

					for (Value v : values) {
						chronoData.appendValueToSignal(
								signalNamesKeepOrder[pos++], v.toString());
					}
					chronoPanel.getChronoData().updateRealTimeExpandedBus();
					chronoPanel.repaintAll(false);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void filePropertyChanged(Model.Event event) {
	}

	@Override
	public void selectionChanged(Model.Event event) {
	}
}
