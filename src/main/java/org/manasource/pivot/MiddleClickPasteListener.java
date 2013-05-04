/*
 *  Copyright (C) 2013  Jared Adams
 *
 *  This file is part of ManaDye.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.manasource.pivot;

import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.Mouse;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextInput;

/**
 * Mouse listener that pastes into {@link TextInput}s and {@link TextArea}s on
 * middle-click.
 */
public class MiddleClickPasteListener extends ComponentMouseButtonListener.Adapter {

	@Override
	public boolean mouseClick( Component component, Mouse.Button button, int x, int y, int count ) {
		if ( button == Mouse.Button.MIDDLE ) {
			if ( component instanceof TextInput ) {
				( (TextInput) component ).paste();
			} else if ( component instanceof TextArea ) {
				( (TextArea) component ).paste();
				return true;
			}
		}

		return false;
	}
}
