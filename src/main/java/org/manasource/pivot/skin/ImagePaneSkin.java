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

package org.manasource.pivot.skin;

import java.io.File;
import java.io.IOException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.CardPane;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheet.Mode;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MenuHandler;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.SheetCloseListener;
import org.apache.pivot.wtk.skin.ContainerSkin;
import org.manasource.dyetool.ImageFilter;
import org.manasource.pivot.DyeableImage;
import org.manasource.pivot.ImagePane;
import org.manasource.pivot.ResourceKeyChangeListener;
import org.manasource.util.Dye;
import org.manasource.util.DyeChangeListener;
import org.manasource.util.ImageUtils;

/**
 * Class that backs the main window.
 */
public class ImagePaneSkin extends ContainerSkin implements DyeChangeListener, ResourceKeyChangeListener {

	@BXML
	private CardPane cardPane;

	@BXML
	private PushButton loadButton;

	@BXML
	private ImageView imageView;

	private Resources resources;

	private DyeableImage image;

	private final Action loadImage = new Action( true ) {

		@Override
		public String getDescription() {
			return "Load image";
		}

		@Override
		public void perform( Component source ) {
			FileBrowserSheet fileBrowserSheet = new FileBrowserSheet();
			fileBrowserSheet.setMode( Mode.OPEN );
			fileBrowserSheet.setDisabledFileFilter( new ImageFilter() );
			fileBrowserSheet.setTitle( "Load Image" );

			fileBrowserSheet.open( source.getWindow(), new SheetCloseListener() {

				@Override
				public void sheetClosed( Sheet sheet ) {
					if ( sheet.getResult() ) {
						FileBrowserSheet fileBrowserSheet = (FileBrowserSheet) sheet;
						File selectedFile = fileBrowserSheet.getSelectedFile();

						try {
							ImagePaneSkin.this.image = new DyeableImage( ImageUtils.getImage( selectedFile ) );
							ImagePaneSkin.this.image.setDye( getImagePane().getDye() );
						} catch ( IOException e ) {
							e.printStackTrace();
							ImagePaneSkin.this.image = null;
						}

						ImagePaneSkin.this.imageView.setImage( ImagePaneSkin.this.image );
						ImagePaneSkin.this.cardPane.setSelectedIndex( 1 );
					}
				}
			} );
		}
	};

	private final Action clearImage = new Action( true ) {

		@Override
		public String getDescription() {
			return "Clear image";
		}

		@Override
		public void perform( Component source ) {
			ImagePaneSkin.this.imageView.clear();
			ImagePaneSkin.this.cardPane.setSelectedIndex( 0 );
			ImagePaneSkin.this.cardPane.setSize( ImagePaneSkin.this.cardPane.getPreferredSize() );
		}
	};

	private MenuHandler menuHandler = new MenuHandler.Adapter() {

		@Override
		public boolean configureContextMenu( Component component, Menu menu, int x, int y ) {

			Menu.Section menuSection = new Menu.Section();
			menu.getSections().add( menuSection );

			Menu.Item loadReferenceImage = new Menu.Item( getResource( "loadImage" ) );
			loadReferenceImage.setAction( ImagePaneSkin.this.loadImage );

			menuSection.add( loadReferenceImage );

			if ( ImagePaneSkin.this.cardPane.getSelectedIndex() == 1 ) {

				Menu.Item clearReferenceImage = new Menu.Item( getResource( "clearImage" ) );
				clearReferenceImage.setAction( ImagePaneSkin.this.clearImage );

				menuSection.add( clearReferenceImage );
			}

			return false;
		}
	};

	ImagePane getImagePane() {
		return (ImagePane) getComponent();
	}

	@SuppressWarnings ( "unchecked" )
	Object getResource( String key ) {
		Object obj = this.resources.get( getImagePane().getResourceKey() );
		@SuppressWarnings ( "rawtypes" )
		Map map = (Map) obj;

		return map.get( key );
	}

	@Override
	public void install( Component component ) {
		super.install( component );

		try {
			this.resources = new Resources( ImagePaneSkin.class.getName() );
		} catch ( IOException | SerializationException e ) {
			// TODO
			e.printStackTrace();
		}

		final ImagePane imagePane = (ImagePane) component;
		imagePane.getDyeChangeListeners().add( this );
		imagePane.getResourceKeyChangeListeners().add( this );

		// Load the sheet content
		BXMLSerializer bxmlSerializer = new BXMLSerializer();

		Component content;
		try {
			content = (Component) bxmlSerializer.readObject( ImagePaneSkin.class.getResource( "image_pane.bxml" ), this.resources );
		} catch ( IOException | SerializationException e ) {
			throw new RuntimeException( e );
		}

		imagePane.add( content );

		bxmlSerializer.bind( this, ImagePaneSkin.class );

		this.cardPane.setMenuHandler( this.menuHandler );
		this.loadButton.setAction( this.loadImage );
	}

	@Override
	public void layout() {
		int width = getWidth();
		int height = getHeight();

		if ( this.cardPane != null ) {
			this.cardPane.setLocation( 1, 1 );

			int contentWidth = Math.max( width - 2, 0 );
			int contentHeight = Math.max( height - 2, 0 );
			this.cardPane.setSize( contentWidth, contentHeight );
		}
	}

	@Override
	public void dyeChanged( Object source, Dye newDye ) {
		if ( this.image != null ) {
			this.image.setDye( newDye );
		}
	}

	@Override
	public void resourceKeyChanged( Object source, String oldKey, String neyKey ) {
		if ( source == getComponent() ) {
			this.loadButton.setButtonData( getResource( "loadImage" ) );
		}
	}
}
