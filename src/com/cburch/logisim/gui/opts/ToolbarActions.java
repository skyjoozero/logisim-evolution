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

package com.cburch.logisim.gui.opts;
import static com.cburch.logisim.gui.opts.Strings.S;

import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Tool;

public class ToolbarActions {
  private static class AddSeparator extends Action {
    ToolbarData toolbar;
    int pos;

    AddSeparator(ToolbarData toolbar, int pos) {
      this.toolbar = toolbar;
      this.pos = pos;
    }

    @Override
    public void doIt(Project proj) {
      toolbar.addSeparator(pos);
    }

    @Override
    public String getName() {
      return S.get("toolbarInsertSepAction");
    }

    @Override
    public void undo(Project proj) {
      toolbar.remove(pos);
    }
  }

  private static class AddToolAction extends Action {
    ToolbarData toolbar;
    Tool tool;
    int requestedPos, insertedPos;

    AddToolAction(ToolbarData toolbar, Tool tool, int pos) {
      this.toolbar = toolbar;
      this.tool = tool;
      this.requestedPos = pos;
    }

    @Override
    public void doIt(Project proj) {
      int n = toolbar.getContents().size();
      if (requestedPos < 0 || requestedPos > n)
        insertedPos = n;
      else
        insertedPos = requestedPos;
      toolbar.addTool(insertedPos, tool);
    }

    @Override
    public String getName() {
      return S.get("toolbarAddAction");
    }

    @Override
    public void undo(Project proj) {
      toolbar.remove(insertedPos);
    }
  }

  private static class MoveTool extends Action {
    ToolbarData toolbar;
    int oldpos;
    int dest;

    MoveTool(ToolbarData toolbar, int oldpos, int dest) {
      this.toolbar = toolbar;
      this.oldpos = oldpos;
      this.dest = dest;
    }

    @Override
    public Action append(Action other) {
      if (other instanceof MoveTool) {
        MoveTool o = (MoveTool) other;
        if (this.toolbar == o.toolbar && this.dest == o.oldpos) {
          // TODO if (this.oldpos == o.dest) return null;
          return new MoveTool(toolbar, this.oldpos, o.dest);
        }
      }
      return super.append(other);
    }

    @Override
    public void doIt(Project proj) {
      toolbar.move(oldpos, dest);
    }

    @Override
    public String getName() {
      return S.get("toolbarMoveAction");
    }

    @Override
    public boolean shouldAppendTo(Action other) {
      if (other instanceof MoveTool) {
        MoveTool o = (MoveTool) other;
        return this.toolbar == o.toolbar && o.dest == this.oldpos;
      } else {
        return false;
      }
    }

    @Override
    public void undo(Project proj) {
      toolbar.move(dest, oldpos);
    }
  }

  private static class RemoveSeparator extends Action {
    ToolbarData toolbar;
    int pos;

    RemoveSeparator(ToolbarData toolbar, int pos) {
      this.toolbar = toolbar;
      this.pos = pos;
    }

    @Override
    public void doIt(Project proj) {
      toolbar.remove(pos);
    }

    @Override
    public String getName() {
      return S.get("toolbarRemoveSepAction");
    }

    @Override
    public void undo(Project proj) {
      toolbar.addSeparator(pos);
    }
  }

  private static class RemoveTool extends Action {
    ToolbarData toolbar;
    Object removed;
    int which;

    RemoveTool(ToolbarData toolbar, int which) {
      this.toolbar = toolbar;
      this.which = which;
    }

    @Override
    public void doIt(Project proj) {
      removed = toolbar.remove(which);
    }

    @Override
    public String getName() {
      return S.get("toolbarRemoveAction");
    }

    @Override
    public void undo(Project proj) {
      if (removed instanceof Tool) {
        toolbar.addTool(which, (Tool) removed);
      } else if (removed == null) {
        toolbar.addSeparator(which);
      }
    }
  }

  public static Action addSeparator(ToolbarData toolbar, int pos) {
    return new AddSeparator(toolbar, pos);
  }

  public static Action addTool(ToolbarData toolbar, Tool tool) {
    return new AddToolAction(toolbar, tool, -1);
  }

  public static Action addTool(ToolbarData toolbar, Tool tool, int pos) {
    return new AddToolAction(toolbar, tool, pos);
  }

  public static Action moveTool(ToolbarData toolbar, int src, int dest) {
    return new MoveTool(toolbar, src, dest);
  }

  public static Action removeSeparator(ToolbarData toolbar, int pos) {
    return new RemoveSeparator(toolbar, pos);
  }

  public static Action removeTool(ToolbarData toolbar, int pos) {
    return new RemoveTool(toolbar, pos);
  }

  private ToolbarActions() {
  }

}
