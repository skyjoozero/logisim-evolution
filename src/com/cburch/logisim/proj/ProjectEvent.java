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

package com.cburch.logisim.proj;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.tools.Tool;

public class ProjectEvent {
  public final static int ACTION_SET_FILE = 0; // change file
  public final static int ACTION_SET_CURRENT = 1; // change current
  public final static int ACTION_SET_TOOL = 2; // change tool
  public final static int ACTION_SELECTION = 3; // selection alterd
  public final static int ACTION_SET_STATE = 4; // circuit state changed
  public static final int ACTION_START = 5; // action about to start
  public static final int ACTION_COMPLETE = 6; // action has completed
  public static final int ACTION_MERGE = 7; // one action has been appended to another
  public static final int UNDO_START = 8; // undo about to start
  public static final int UNDO_COMPLETE = 9; // undo has completed
  public static final int REPAINT_REQUEST = 10; // canvas should be repainted
  public static final int REDO_START = 11;
  public static final int REDO_COMPLETE = 12;
  public final static int ACTION_ADD_STATE = 13; // new root circuit state created
  public final static int ACTION_DELETE_STATE = 14; // old root circuit state removed
  public final static int ACTION_CLEAR_STATES = 15; // clear all old root circuit states

  private int action;
  private Project proj;
  private Object old_data;
  private Object data;

  ProjectEvent(int action, Project proj) {
    this.action = action;
    this.proj = proj;
    this.data = null;
  }

  ProjectEvent(int action, Project proj, Object data) {
    this.action = action;
    this.proj = proj;
    this.data = data;
  }

  ProjectEvent(int action, Project proj, Object old, Object data) {
    this.action = action;
    this.proj = proj;
    this.old_data = old;
    this.data = data;
  }

  // access methods
  public int getAction() {
    return action;
  }

  public Circuit getCircuit() {
    return proj.getCurrentCircuit();
  }

  public Object getData() {
    return data;
  }

  // convenience methods
  public LogisimFile getLogisimFile() {
    return proj.getLogisimFile();
  }

  public Object getOldData() {
    return old_data;
  }

  public Project getProject() {
    return proj;
  }

  public Tool getTool() {
    return proj.getTool();
  }

  @Override
  public String toString() {
    switch(action) {
    case ACTION_SET_FILE: return "ACTION_SET_FILE";
    case ACTION_SET_CURRENT: return "ACTION_SET_CURRENT";
    case ACTION_SET_TOOL: return "ACTION_SET_TOOL";
    case ACTION_SELECTION: return "ACTION_SELECTION";
    case ACTION_SET_STATE: return "ACTION_SET_STATE";
    case ACTION_START: return "ACTION_START";
    case ACTION_COMPLETE: return "ACTION_COMPLETE";
    case ACTION_MERGE: return "ACTION_MERGE";
    case UNDO_START: return "UNDO_START";
    case UNDO_COMPLETE: return "UNDO_COMPLETE";
    case REPAINT_REQUEST: return "REPAINT_REQUEST";
    case REDO_START: return "REDO_START";
    case REDO_COMPLETE: return "REDO_COMPLETE";
    case ACTION_ADD_STATE: return "ACTION_ADD_STATE";
    case ACTION_DELETE_STATE: return "ACTION_DELETE_STATE";
    case ACTION_CLEAR_STATES: return "ACTION_CLEAR_STATES";
    default: return "ProjectAction<?>";
    }
  }

}
