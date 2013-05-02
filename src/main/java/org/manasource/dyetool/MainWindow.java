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

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;
import org.manasource.pivot.ImagePane;
import org.manasource.util.Dye;

/**
 * Class that backs the main window.
 */
public class MainWindow extends Window implements Bindable {

	@BXML
	ImagePane dyePane;

	@BXML
	ImagePane referencePane;

	@BXML
	Form form;

	@BXML
	TextInput dyeStringInput;

	@BXML
	Button applyDyeButton;

	private final Action applyDye = new Action( true ) {

		@Override
		public String getDescription() {
			return "Apply dye";
		}

		@Override
		public void perform( Component source ) {
			String dyeString = MainWindow.this.dyeStringInput.getText();
			MainWindow.this.dyePane.setDye( new Dye( dyeString ) );
		}
	};

	@Override
	public void initialize( Map< String, Object > namespace, URL location, Resources resources ) {
		this.applyDyeButton.setAction( this.applyDye );
		this.form.setMinimumHeight( this.form.getPreferredHeight() );
	}
}
