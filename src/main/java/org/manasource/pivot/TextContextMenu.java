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

import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Clipboard;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MenuHandler;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextInput;

/**
 * Creates context menus with cut, copy, paste, and undo entries.
 */
public class TextContextMenu extends MenuHandler.Adapter {

	private final Action cutAction = new Action( true ) {

		@Override
		public String getDescription() {
			return "Cut";
		}

		@Override
		public void perform( Component source ) {
			if ( TextContextMenu.this.target instanceof TextInput ) {
				( (TextInput) TextContextMenu.this.target ).cut();
			} else if ( TextContextMenu.this.target instanceof TextArea ) {
				( (TextArea) TextContextMenu.this.target ).cut();
			}
		}
	};

	private final Action copyAction = new Action( true ) {

		@Override
		public String getDescription() {
			return "Cut";
		}

		@Override
		public void perform( Component source ) {
			if ( TextContextMenu.this.target instanceof TextInput ) {
				( (TextInput) TextContextMenu.this.target ).copy();
			} else if ( TextContextMenu.this.target instanceof TextArea ) {
				( (TextArea) TextContextMenu.this.target ).copy();
			}
		}
	};

	private final Action pasteAction = new Action( true ) {

		@Override
		public String getDescription() {
			return "Cut";
		}

		@Override
		public void perform( Component source ) {
			if ( TextContextMenu.this.target instanceof TextInput ) {
				( (TextInput) TextContextMenu.this.target ).paste();
			} else if ( TextContextMenu.this.target instanceof TextArea ) {
				( (TextArea) TextContextMenu.this.target ).paste();
			}
		}
	};

	private final Action undoAction = new Action( true ) {

		@Override
		public String getDescription() {
			return "Undo";
		}

		@Override
		public void perform( Component source ) {
			if ( TextContextMenu.this.target instanceof TextInput ) {
				( (TextInput) TextContextMenu.this.target ).undo();
			} else if ( TextContextMenu.this.target instanceof TextArea ) {
				( (TextArea) TextContextMenu.this.target ).undo();
			}
		}
	};

	private Component target;

	@Override
	public boolean configureContextMenu( Component component, Menu menu, int x, int y ) {
		boolean hasSelection = false;
		if ( component instanceof TextInput ) {
			TextInput input = (TextInput) component;
			hasSelection = input.getSelectionLength() > 0;
		} else if ( component instanceof TextArea ) {
			TextArea input = (TextArea) component;
			hasSelection = input.getSelectionLength() > 0;
		} else {
			return false;
		}

		this.target = component;

		this.cutAction.setEnabled( hasSelection );
		this.copyAction.setEnabled( hasSelection );
		this.pasteAction.setEnabled( Clipboard.getContent().containsText() );

		Menu.Section section = new Menu.Section();
		menu.getSections().add( section );

		Menu.Item cutItem = new Menu.Item( "Cut" );
		cutItem.setAction( this.cutAction );
		section.add( cutItem );

		Menu.Item copyItem = new Menu.Item( "Copy" );
		copyItem.setAction( this.copyAction );
		section.add( copyItem );

		Menu.Item pasteItem = new Menu.Item( "Paste" );
		pasteItem.setAction( this.pasteAction );
		section.add( pasteItem );

		Menu.Item undoItem = new Menu.Item( "Undo" );
		undoItem.setAction( this.undoAction );
		section.add( undoItem );

		return false;
	}
}
