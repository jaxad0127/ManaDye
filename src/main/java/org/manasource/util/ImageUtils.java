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

package org.manasource.util;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Utilities for images.
 */
public class ImageUtils {

	/**
	 * Loads an image from an InputStream. Will always return a ARGB image.
	 * 
	 * @param is the InputStream to load from
	 * @return the image in the stream (converted to ARGB) or <code>null</code>
	 *         if it cannot be loaded
	 */
	public static BufferedImage getImage( InputStream is ) throws IOException {
		BufferedImage in = ImageIO.read( is );

		// Pass the null on
		if ( in == null ) {
			return null;
		}

		int w = in.getWidth(), h = in.getHeight();

		BufferedImage ret = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );

		int x, y;

		if ( in.getType() == BufferedImage.TYPE_CUSTOM && in.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_GRAY ) {
			// This is to fix a bug in Sun's apis, see
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5051418

			Raster raster = in.getData();
			int numBands = raster.getNumBands();

			// Check for alpha
			if ( numBands == 1 ) {
				int[] sample = new int[1];
				Color temp;

				for ( y = 0; y < h; y++ ) {
					for ( x = 0; x < w; x++ ) {
						sample = raster.getPixel( x, y, sample );
						temp = new Color( sample[0], sample[0], sample[0], 255 );
						ret.setRGB( x, y, temp.getRGB() );
					}
				}
			} else if ( numBands == 2 ) {
				int[] sample = new int[2];
				Color temp;

				for ( y = 0; y < h; y++ ) {
					for ( x = 0; x < w; x++ ) {
						sample = raster.getPixel( x, y, sample );
						temp = new Color( sample[0], sample[0], sample[0], sample[1] );
						ret.setRGB( x, y, temp.getRGB() );
					}
				}
			}
		} else {
			// Do a simple copy of the image
			ret = copy( in );
		}

		return ret;
	}

	/**
	 * Loads an image from a file. This is a convience method for
	 * getImage(InputStream).
	 * 
	 * @param file the file to load from
	 * @return the image in the file (converted to ARGB) or <code>null</code> if
	 *         it cannot be loaded
	 */
	public static BufferedImage getImage( File file ) throws IOException {
		return getImage( new FileInputStream( file ) );
	}

	/**
	 * Resizes the given image. A bicubic algorithm is used to ensure quality.
	 * 
	 * @param img the image to resize
	 * @param sX the horizontal scale factor to use
	 * @param sY the vertical scale factor to use
	 * @return a scaled instance of the given image
	 */
	public static BufferedImage resize( BufferedImage img, double sX, double sY ) {
		AffineTransform scale = AffineTransform.getScaleInstance( sX, sY );
		AffineTransformOp op = new AffineTransformOp( scale, AffineTransformOp.TYPE_BICUBIC );
		return op.filter( img, null );
	}

	/**
	 * Resizes the given image. Convience method for
	 * resize(BufferedImage,double,double).
	 * 
	 * @param img the image to resize
	 * @param scale the scale factor to use
	 * @return a scaled instance of the given image
	 */
	public static BufferedImage resize( BufferedImage img, double scale ) {
		return resize( img, scale, scale );
	}

	/**
	 * Copies the given image.
	 * 
	 * @param img the image to copy
	 * @return a copy of the given image
	 */
	public static BufferedImage copy( BufferedImage img ) {
		AffineTransformOp op = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BICUBIC );
		return op.filter( img, null );
	}
}
