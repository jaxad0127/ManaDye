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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.pivot.wtk.media.Image;
import org.apache.pivot.wtk.media.ImageListener;
import org.manasource.util.Dye;
import org.manasource.util.ImageUtils;

/**
 * A Pivot {@link Image} that can take a {@link Dye}.
 */
public class DyeableImage extends Image {

	protected DyeChangeListenerList dyeChangeListeners = new DyeChangeListenerList();

	private Dye dye;

	private final BufferedImage source;

	private BufferedImage cache;

	private boolean needsRedye = true;

	/**
	 * @param source the source image
	 */
	public DyeableImage( BufferedImage source ) {
		super();

		this.source = source;
	}

	/**
	 * @param dye the {@link Dye}
	 * @param source the source image
	 */
	public DyeableImage( Dye dye, BufferedImage source ) {
		this( source );

		setDye( dye );
	}

	/**
	 * @return the dye
	 */
	public Dye getDye() {
		return this.dye;
	}

	/**
	 * @param dye the dye to set
	 */
	public void setDye( Dye dye ) {
		if ( ObjectUtils.equals( this.dye, dye ) ) {
			return;
		}

		this.dye = dye;
		this.needsRedye = true;
		getDyeChangeListeners().dyeChanged( this, dye );
		for ( ImageListener listener : getImageListeners() ) {
			listener.regionUpdated( this, 0, 0, getWidth(), getHeight() );
		}
	}

	/**
	 * @return the source
	 */
	public BufferedImage getSource() {
		return this.source;
	}

	/**
	 * @return the cache
	 */
	public BufferedImage getCache() {
		if ( this.needsRedye ) {
			if ( this.dye == null ) {
				this.cache = ImageUtils.copy( this.source );
			} else {
				this.cache = this.dye.recolor( this.source );
			}
		}
		return this.cache;
	}

	/**
	 * @see org.apache.pivot.wtk.Visual#getWidth()
	 */
	@Override
	public int getWidth() {
		BufferedImage cache = getCache();
		return cache == null ? 0 : cache.getWidth();
	}

	/**
	 * @see org.apache.pivot.wtk.Visual#getHeight()
	 */
	@Override
	public int getHeight() {
		BufferedImage cache = getCache();
		return cache == null ? 0 : cache.getHeight();
	}

	/**
	 * @see org.apache.pivot.wtk.Visual#paint(java.awt.Graphics2D)
	 */
	@Override
	public void paint( Graphics2D graphics ) {
		graphics.drawImage( getCache(), 0, 0, null );
	}

	/**
	 * @return the dyeChangeListeners
	 */
	public DyeChangeListenerList getDyeChangeListeners() {
		return this.dyeChangeListeners;
	}

}
