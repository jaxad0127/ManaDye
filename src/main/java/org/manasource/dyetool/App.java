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

package org.manasource.dyetool;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.Window;
import org.manasource.pivot.ImagePane;
import org.manasource.pivot.skin.ImagePaneSkin;

/**
 * Main class.
 */
public class App implements Application {

	private Window window = null;

	@Override
	public void startup( Display display, Map< String, String > properties ) throws Exception {

		Theme.getTheme().set( ImagePane.class, ImagePaneSkin.class );

		BXMLSerializer bxmlSerializer = new BXMLSerializer();
		this.window = (Window) bxmlSerializer.readObject( MainWindow.class, "mainWindow.bxml" );
		this.window.open( display );
	}

	@Override
	public boolean shutdown( boolean arg0 ) throws Exception {
		if ( this.window != null ) {
			this.window.close();
		}

		return false;
	}

	@Override
	public void suspend() throws Exception {
		// no-op
	}

	@Override
	public void resume() throws Exception {
		// no-op
	}

	public static void main( String[] args ) {
		DesktopApplicationContext.main( App.class, args );
	}
}
