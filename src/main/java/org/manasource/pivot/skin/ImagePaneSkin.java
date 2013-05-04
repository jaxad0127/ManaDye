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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.io.FileList;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.CardPane;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.DragSource;
import org.apache.pivot.wtk.DropAction;
import org.apache.pivot.wtk.DropTarget;
import org.apache.pivot.wtk.FileBrowserSheet;
import org.apache.pivot.wtk.FileBrowserSheet.Mode;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.LocalManifest;
import org.apache.pivot.wtk.Manifest;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MenuHandler;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.SheetCloseListener;
import org.apache.pivot.wtk.Visual;
import org.apache.pivot.wtk.media.Image;
import org.apache.pivot.wtk.media.Picture;
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
			fileBrowserSheet.getStyles().put( "hideDisabledFiles", true );

			fileBrowserSheet.open( source.getWindow(), new SheetCloseListener() {

				@Override
				public void sheetClosed( Sheet sheet ) {
					if ( sheet.getResult() ) {
						FileBrowserSheet fileBrowserSheet = (FileBrowserSheet) sheet;
						File selectedFile = fileBrowserSheet.getSelectedFile();

						setFile( selectedFile );
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

	private final MenuHandler menuHandler = new MenuHandler.Adapter() {

		@Override
		public boolean configureContextMenu( Component component, Menu menu, int x, int y ) {

			Menu.Section menuSection = new Menu.Section();
			menu.getSections().add( menuSection );

			Menu.Item loadReferenceImage = new Menu.Item( getResource( "loadImage" ) );
			loadReferenceImage.setAction( ImagePaneSkin.this.loadImage );

			menuSection.add( loadReferenceImage );

			Menu.Item clearReferenceImage = new Menu.Item( getResource( "clearImage" ) );
			clearReferenceImage.setAction( ImagePaneSkin.this.clearImage );
			ImagePaneSkin.this.clearImage.setEnabled( hasImage() );

			menuSection.add( clearReferenceImage );

			return false;
		}
	};

	private final DragSource dragSource = new DragSource() {

		@Override
		public boolean beginDrag( Component component, int x, int y ) {
			return hasImage();
		}

		@Override
		public void endDrag( Component component, DropAction dropAction ) {
			// no-op
		}

		@Override
		public boolean isNative() {
			return true;
		}

		@Override
		public LocalManifest getContent() {
			LocalManifest content = new LocalManifest();
			Image img = ImagePaneSkin.this.imageView.getImage();
			if ( img instanceof Picture ) {
				content.putImage( img );
			} else if ( img instanceof DyeableImage ) {
				content.putImage( new Picture( ( (DyeableImage) img ).getCache() ) );
			}

			return content;
		}

		@Override
		public Visual getRepresentation() {
			// Not used for native drags
			return null;
		}

		@Override
		public Point getOffset() {
			// Not used for native drags
			return null;
		}

		@Override
		public int getSupportedDropActions() {
			return DropAction.COPY.getMask();
		}
	};

	private final DropTarget dropTarget = new DropTarget() {

		private DropAction isSupported( Manifest dragContent ) {
			if ( dragContent.containsFileList() || dragContent.containsImage() ) {
				return DropAction.COPY;
			}

			return null;
		}

		@Override
		public DropAction userDropActionChange( Component component, Manifest dragContent, int supportedDropActions, int x, int y, DropAction userDropAction ) {
			return isSupported( dragContent );
		}

		@Override
		public DropAction drop( Component component, Manifest dragContent, int supportedDropActions, int x, int y, DropAction userDropAction ) {
			DropAction dropAction = null;

			try {
				if ( dragContent.containsFileList() ) {
					FileList list = dragContent.getFileList();

					if ( list.getLength() == 0 ) {
						return null;
					} else {
						setFile( list.get( 0 ) );
					}

					dropAction = DropAction.COPY;
				} else if ( dragContent.containsImage() ) {
					setImage( ImageUtils.copy( dragContent.getImage() ) );

					ImagePaneSkin.this.imageView.setImage( dragContent.getImage() );
					dropAction = DropAction.COPY;
				}

				return dropAction;
			} catch ( IOException e ) {
				// TODO
				e.printStackTrace();

				return null;
			}
		}

		@Override
		public DropAction dragMove( Component component, Manifest dragContent, int supportedDropActions, int x, int y, DropAction userDropAction ) {
			return isSupported( dragContent );
		}

		@Override
		public void dragExit( Component component ) {
			// no-op
		}

		@Override
		public DropAction dragEnter( Component component, Manifest dragContent, int supportedDropActions, DropAction userDropAction ) {
			return isSupported( dragContent );
		}
	};

	void setFile( File file ) {
		try {
			setImage( ImageUtils.getImage( file ) );
		} catch ( IOException e ) {
			// TODO
			e.printStackTrace();
		}

	}

	void setImage( BufferedImage image ) {
		this.image = new DyeableImage( image );
		this.image.setDye( getImagePane().getDye() );

		this.imageView.setImage( this.image );
		this.cardPane.setSelectedIndex( 1 );
	}

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

	boolean hasImage() {
		return this.cardPane.getSelectedIndex() == 1;
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

		this.cardPane.setDragSource( this.dragSource );
		this.cardPane.setDropTarget( this.dropTarget );
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
	public void resourceKeyChanged( Object source, String oldKey, String newKey ) {
		if ( source == getComponent() ) {
			this.loadButton.setButtonData( getResource( "loadImage" ) );
		}
	}
}
