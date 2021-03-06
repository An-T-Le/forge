/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.match.input;

import forge.GuiBase;
import forge.interfaces.IButton;

/**
 * Manages match UI OK/Cancel button enabling and focus
 */
public class ButtonUtil {
    public static void update(boolean okEnabled, boolean cancelEnabled, boolean focusOk) {
        update("OK", "Cancel", okEnabled, cancelEnabled, focusOk);
    }
    public static void update(String okLabel, String cancelLabel, boolean okEnabled, boolean cancelEnabled, boolean focusOk) {
        IButton btnOk = GuiBase.getInterface().getBtnOK();
        IButton btnCancel = GuiBase.getInterface().getBtnCancel();

        btnOk.setText(okLabel);
        btnCancel.setText(cancelLabel);
        btnOk.setEnabled(okEnabled);
        btnCancel.setEnabled(cancelEnabled);
        if (okEnabled && focusOk) {
            GuiBase.getInterface().focusButton(btnOk);
        }
        else if (cancelEnabled) {
            GuiBase.getInterface().focusButton(btnCancel);
        }
    }
}
