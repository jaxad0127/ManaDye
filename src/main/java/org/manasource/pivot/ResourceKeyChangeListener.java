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

import org.apache.pivot.util.ListenerList;

public interface ResourceKeyChangeListener {

	public class ResourceKeyChangeListenerList extends ListenerList< ResourceKeyChangeListener > implements ResourceKeyChangeListener {

		@Override
		public void resourceKeyChanged( Object source, String oldKey, String neyKey ) {
			for ( ResourceKeyChangeListener listener : this ) {
				listener.resourceKeyChanged( source, oldKey, neyKey );
			}
		}

	}

	public void resourceKeyChanged( Object source, String oldKey, String neyKey );

}
