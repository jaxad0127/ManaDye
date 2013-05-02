/*
 *  Copyright (C) 2007-2009  The Mana World Development Team
 *  Copyright (C) 2009-2012  The Mana Developers
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

package org.manasource.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * The Dye class is used to re-color images. For a description of the dyeing
 * system, please refer to the <a
 * href="http://wiki.themanaworld.org/index.php/Image_dyeing">Image dyeing
 * article</a> on The Mana World wiki.
 */
public class Dye implements Cloneable {

	/**
	 * The available dye channels.
	 */
	public static enum Channel {
		R( Color.RED ), G( Color.GREEN ), Y( Color.YELLOW ), B( Color.BLUE ), M( Color.MAGENTA ), C( Color.CYAN ), W( Color.WHITE ), NONE( null );

		private Color color;

		private Channel( Color color ) {
			this.color = color;
		}

		/**
		 * Returns the color associated with this channel.
		 * 
		 * @return the channel's color
		 */
		public Color getColor() {
			return this.color;
		}

		/**
		 * Returns the channel with the specified name.
		 * 
		 * @param channel the desired channel's name
		 * @return the channel with the given name or NONE if it doesn't exist
		 */
		public static Channel cast( String channel ) {
			try {
				return valueOf( Channel.class, channel );
			} catch ( IllegalArgumentException iae ) {
				return NONE;
			}
		}

		/**
		 * Returns the channel with the specified name.
		 * 
		 * @see cast(java.lang.String)
		 */
		public static Channel cast( char channel ) {
			return cast( String.format( "%c", channel ) );
		}

		/**
		 * Returns the channel and intensity of the given color.
		 * 
		 * @param channel the values for the desired color
		 * @return the color's channel as the first element (or NONE if it can't
		 *         be recolored) and it's intensity as the second
		 */
		public static Object[] getChannel( Color in ) {
			int r = in.getRed(), g = in.getGreen(), b = in.getBlue();
			int cmax = Math.max( r, Math.max( g, b ) );
			if ( cmax == 0 ) {
				// Black
				return new Object[] { NONE, 0 };
			}

			int cmin = Math.min( r, Math.min( g, b ) );
			int intensity = r + g + b;

			if ( cmin != cmax && ( cmin != 0 || ( intensity != cmax && intensity != 2 * cmax ) ) ) {
				// not pure
				return new Object[] { NONE, cmax };
			}

			int i = ( r != 0 ? 1 : 0 ) | ( ( g != 0 ? 1 : 0 ) << 1 ) | ( ( b != 0 ? 1 : 0 ) << 2 );

			return new Object[] { Channel.values()[i - 1], cmax };
		}
	}

	/**
	 * An individual color palette.
	 */
	public static class Palette implements Comparable< Palette > {

		private static List< Color > getList( String data ) {
			if ( data.length() == 0 ) {
				return new Vector< Color >( 0 );
			}

			if ( data.charAt( 0 ) != '#' ) {
				return new Vector< Color >( 0 );
			}

			String[] colorStrings = data.split( "," );
			Vector< Color > colors = new Vector< Color >();

			for ( String color : colorStrings ) {
				try {
					// Color.decode expects a pound sign in front of hex code
					if ( color.charAt( 0 ) != '#' ) {
						color = "#" + color;
					}
					Color c = Color.decode( color );
					colors.add( c );
				} catch ( NumberFormatException nfe ) {
					// Nothing to do here
				}
			}

			return colors;
		}

		private final Color[] colors;

		/**
		 * Builds a palette from an array of colors.
		 * 
		 * @param colors an array of colors to use for this palette
		 */
		public Palette( Color[] colors ) {
			if ( colors == null ) {
				throw new NullPointerException( "colors cannot be null" );
			}

			this.colors = colors;
		}

		/**
		 * Builds a palette from a list of colors.
		 * 
		 * @param colors a list of colors to use for this palette
		 */
		public Palette( List< Color > colors ) {
			this( colors.toArray( new Color[0] ) );
		}

		/**
		 * Builds a palette from string data.
		 * 
		 * @param data the palette data
		 */
		public Palette( String data ) {
			this( getList( data ) );
		}

		/**
		 * Converts the given intensity to it's corresponding color value.
		 * 
		 * @param intensity the requested intensity
		 * @param alpha the alpha for the new color
		 * @return the color that corresponds to the given intensity or
		 *         <code>null</code> if there are no colors to use
		 */
		public Color getColor( int intensity, int alpha ) {
			if ( intensity == 0 ) {
				return new Color( 0, 0, 0, alpha );
			}
			if ( this.colors.length == 0 ) {
				return null;
			}

			int j = intensity * this.colors.length;
			int i = j / 255;
			int t = j % 255;

			j = t != 0 ? i : i - 1;

			// Get the next highest color in the palette
			int r2 = this.colors[j].getRed(), g2 = this.colors[j].getGreen(), b2 = this.colors[j].getBlue();

			if ( t == 0 ) {
				return new Color( r2, g2, b2, alpha ); // Exact color
			}

			// Get the previous color in the palette.
			// First color is implicitly black.
			int r1 = 0, g1 = 0, b1 = 0;
			if ( i > 0 ) {
				r1 = this.colors[i - 1].getRed();
				g1 = this.colors[i - 1].getGreen();
				b1 = this.colors[i - 1].getBlue();
			}

			// Perform a linear interpolation
			int r = ( ( 255 - t ) * r1 + t * r2 ) / 255, g = ( ( 255 - t ) * g1 + t * g2 ) / 255, b = ( ( 255 - t ) * b1 + t * b2 ) / 255;

			return new Color( r, g, b, alpha );
		}

		/**
		 * Converts the given intensity to it's corresponding color value. The
		 * new color will have an alpha of 255.
		 * 
		 * @param intensity the requested intensity
		 * @return the color that corresponds to the given intensity or
		 *         <code>null</code> if there are no colors to use
		 */
		public Color getColor( int intensity ) {
			return getColor( intensity, 255 );
		}

		/**
		 * Returns the internal list of colors used by this palette.
		 * 
		 * @return the array of colors used b this palette
		 */
		public Color[] getColors() {
			return this.colors;
		}

		@Override
		public boolean equals( Object obj ) {
			if ( ! ( obj instanceof Palette ) ) {
				return false;
			}

			if ( this == obj ) {
				return true;
			}

			Palette o = (Palette) obj;

			if ( this.colors.length != o.colors.length ) {
				return false;
			}

			return compareTo( o ) == 0;
		}

		@Override
		public int compareTo( Palette o ) {
			int a = 0, b = 0;

			while ( a < this.colors.length && b < o.colors.length ) {
				int cmp = this.colors[a].getRGB() - o.colors[b].getRGB();
				if ( cmp != 0 ) {
					return cmp;
				}
				a++;
				b++;
			}

			if ( a == this.colors.length && b == o.colors.length ) {
				return 0;
			} else if ( a < this.colors.length ) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	private final EnumMap< Channel, Palette > palettes = new EnumMap< Channel, Palette >( Channel.class );

	/**
	 * Builds a dye from the given palette information.
	 * 
	 * @param desc the palettes to use
	 */
	public Dye( EnumMap< Channel, Palette > palettes ) {
		if ( palettes == null ) {
			throw new NullPointerException( "palettes cannot be null" );
		}

		this.palettes.putAll( palettes );
	}

	/**
	 * Builds a dye from the given palette description.
	 * 
	 * @param desc the description of the palettes
	 */
	public Dye( String desc ) {
		this( parsePalettes( desc ) );
	}

	/**
	 * Dye an individual color.
	 * 
	 * @param in the color to dye
	 * @return the color dyed (or untouched if it cannot be dyed)
	 */
	public Color update( Color in ) {
		Object[] arr = Channel.getChannel( in );
		Channel channel = (Channel) arr[0];
		int intensity = (Integer) arr[1];

		Palette p = this.palettes.get( channel );

		if ( p == null ) {
			return in;
		}

		Color ret = p.getColor( intensity, in.getAlpha() );

		if ( ret == null ) {
			ret = in;
		}

		return ret;
	}

	/**
	 * Dye an entire image. A new image is returned.
	 * 
	 * @param img the image to dye
	 * @return a new image that is the dyed version of <code>img</code>
	 */
	public BufferedImage recolor( BufferedImage img ) {
		if ( img == null ) {
			return null;
		}
		int w = img.getWidth();
		int h = img.getHeight();

		BufferedImage ret = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );

		int x, y;
		Color color;

		for ( y = 0; y < h; y++ ) {
			for ( x = 0; x < w; x++ ) {
				color = new Color( img.getRGB( x, y ), true );
				color = update( color );
				ret.setRGB( x, y, color.getRGB() );
			}
		}

		return ret;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( ! ( obj instanceof Dye ) ) {
			return false;
		}

		if ( this == obj ) {
			return true;
		}

		Dye o = (Dye) obj;

		for ( Channel c : Channel.values() ) {
			Palette a = this.palettes.get( c );
			Palette b = o.palettes.get( c );

			if ( a == b ) {
				continue;
			} else if ( a == null ) {
				return false;
			} else if ( b == null ) {
				return false;
			} else if ( !a.equals( b ) ) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Parse the given string for palette information.
	 * 
	 * @param data the string to parse
	 * @param palettes an existing map from channels to palettes that will be
	 *            added to
	 * @param channels a list of channels to use for palettes without specified
	 *            channels
	 */
	public static void parsePalettes( String data, EnumMap< Channel, Palette > palettes, LinkedList< Channel > channels ) {
		if ( data.length() == 0 ) {
			return;
		}
		if ( palettes == null ) {
			throw new NullPointerException( "palettes cannot be null" );
		}
		if ( channels == null ) {
			channels = new LinkedList< Channel >();
		}

		String[] paletteArray = data.split( ";" );
		Channel channel;
		int n;

		for ( String palette : paletteArray ) {
			n = palette.indexOf( ":" );

			if ( n == -1 ) {
				channel = channels.poll();
				if ( channel == null ) {
					throw new IllegalArgumentException( "Bad palette data: " + data );
				}
				data = palette;
			} else {
				channel = Channel.cast( palette.substring( 0, n ) );
				data = palette.substring( n + 1 );
			}

			if ( data.length() > 0 ) {
				palettes.put( channel, new Palette( data ) );
			}
		}
	}

	/**
	 * Parse the given string for palette information.
	 * 
	 * @param data the string to parse
	 * @param palettes an existing map from channels to palettes that be added
	 *            to
	 */
	public static void parsePalettes( String data, EnumMap< Channel, Palette > palettes ) {
		parsePalettes( data, palettes, new LinkedList< Channel >() );
	}

	/**
	 * Parse the given string for palette information.
	 * 
	 * @param data the string to parse
	 * @return a map from channels to palettes
	 */
	public static EnumMap< Channel, Palette > parsePalettes( String data ) {
		EnumMap< Channel, Palette > palettes = new EnumMap< Channel, Palette >( Channel.class );
		parsePalettes( data, palettes );
		return palettes;
	}

	/**
	 * Resolves an image name and palette information. The name is searched for
	 * palette information, which is stripped and combined with the given
	 * palette information. Null inputs are converted to empty strings.
	 * 
	 * @param name the image name
	 * @param palettes the palette information
	 * @return an array with the image name first and a Dye second
	 */
	public static Object[] resolveDyes( String name, String paletteString ) {
		if ( name == null ) {
			name = "";
		}
		if ( paletteString == null ) {
			paletteString = "";
		}

		int n = name.indexOf( "|" );

		EnumMap< Channel, Palette > palettes = new EnumMap< Channel, Palette >( Channel.class );
		LinkedList< Channel > channels = new LinkedList< Channel >();

		if ( n > -1 ) {
			String[] paletteArray = name.substring( n + 1 ).split( ";" );
			name = name.substring( 0, n );

			Channel channel;
			String data;

			for ( String palette : paletteArray ) {
				n = palette.indexOf( ":" );

				if ( n == -1 ) {
					channel = Channel.cast( palette );
					data = null;
				} else {
					channel = Channel.cast( palette.substring( 0, n ) );
					data = palette.substring( n + 1 );
				}

				if ( data == null ) {
					channels.add( channel );
				} else {
					palettes.put( channel, new Palette( data ) );
				}
			}

			paletteArray = paletteString.split( ";" );
		}

		parsePalettes( paletteString, palettes, channels );

		/*
		 * for (Channel c : Channel.values()) { if (data == null) continue;
		 * palettes.put(c, new Palette(dyes.get(c))); }
		 */

		return new Object[] { name, new Dye( palettes ) };
	}
}
