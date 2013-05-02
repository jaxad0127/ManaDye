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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pivot.wtk.Container;
import org.manasource.pivot.ResourceKeyChangeListener.ResourceKeyChangeListenerList;
import org.manasource.util.Dye;

/**
 * Class that backs the main window.
 */
public class ImagePane extends Container {

	protected final DyeChangeListenerList dyeChangeListeners = new DyeChangeListenerList();

	protected final ResourceKeyChangeListenerList resourceKeyChangeListeners = new ResourceKeyChangeListenerList();

	private String resourceKey;

	private Dye dye;

	public ImagePane() {
		super();

		installSkin( ImagePane.class );
	}

	/**
	 * @return the dyeChangeListeners
	 */
	public DyeChangeListenerList getDyeChangeListeners() {
		return this.dyeChangeListeners;
	}

	/**
	 * @return the resourceKeyChangeListeners
	 */
	public ResourceKeyChangeListenerList getResourceKeyChangeListeners() {
		return this.resourceKeyChangeListeners;
	}

	/**
	 * @return the resourceKey
	 */
	public String getResourceKey() {
		return this.resourceKey;
	}

	/**
	 * @param resourceKey the resourceKey to set
	 */
	public void setResourceKey( String resourceKey ) {
		if ( StringUtils.equals( this.resourceKey, resourceKey ) ) {
			return;
		}

		String oldKey = this.resourceKey;
		this.resourceKey = resourceKey;
		getResourceKeyChangeListeners().resourceKeyChanged( this, oldKey, resourceKey );
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
		getDyeChangeListeners().dyeChanged( this, dye );
	}

}
