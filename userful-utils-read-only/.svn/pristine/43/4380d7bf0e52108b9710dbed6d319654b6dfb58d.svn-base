package com.utils.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import sun.awt.image.ToolkitImage;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;

/**
 * @author Alex Tsin (chm125#gmail.com)
 * @copyright LFT
 * @version 1.0
 * @date 2009-08-02
 */
public class ImageUtil {

/*	public static void main(String[] args) throws Exception {
	    System.out.println("start!");
		String src = "d:/20090501_01.jpg";
		drawRect(src, 89, 215, 280, 18);
		System.out.println("end!");
	}
*/	
	private ImageUtil(){
	}

	/**
	 * convert bmp, png, gif, tif to single jpg
	 * @param src(absolute path of the source image file)
	 * @return absolute file path of jpg
	 */
	public static String toJPG(String src) {
		if (!available(src))
			return null;
		if (isJpg(src))
			return src;
		String dest = src.substring(0, src.lastIndexOf(".")) + ".jpg";
		return process(src, dest);
	}

	/**
	 * convert jpg, png, gif, tif to single bmp
	 * @param src (absolute path of the source image file)
	 * @return absolute file path of bmp
	 */
	public static String toBMP(String src) {
		if (!available(src))
			return null;
		if (isBmp(src))
			return src;
		String dest = src.substring(0, src.lastIndexOf(".")) + ".bmp";
		return process(src, dest);
	}

	/**
	 * convert jpg, bmp, gif, tif to single png
	 * @param src (absolute path of the source image file)
	 * @return absolute file path of png
	 */
	public static String toPNG(String src) {
		if (!available(src))
			return null;
		if (isPng(src))
			return src;
		String dest = src.substring(0, src.lastIndexOf(".")) + ".png";
		return process(src, dest);
	}

	/**
	 * convert jpg, bmp, png, tif to single gif
	 * @param src (absolute path of the source image file)
	 * @return absolute file path of gif
	 */
	public static String toGIF(String src) {
		if (!available(src))
			return null;
		if (isGif(src))
			return src;
		if (!isJpg(src))
			src = toJPG(src);
		String dest = src.substring(0, src.lastIndexOf(".")) + ".gif";
		try {
			AnimatedGifEncoder e = new AnimatedGifEncoder();
			e.start(new FileOutputStream(dest));
			e.setDelay(0);
			e.addFrame(ImageIO.read(new File(src)));
			e.finish();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return dest;
	}
	
	/**
	 * convert jpg, bmp, png, gif to single tif
	 * @param src (absolute path of the source image file)
	 * @return absolute file path of tif
	 */
	public static String toTIF(String src) {
		if (!available(src))
			return null;
		if (isTif(src))
			return src;
		String dest = src.substring(0, src.lastIndexOf(".")) + ".tif";
		OutputStream os = null;
		try {
			os = new FileOutputStream(dest);
			RenderedOp ro = JAI.create("fileload", src);
			ImageEncoder ie = ImageCodec.createImageEncoder("TIFF", os,
					new TIFFEncodeParam());
			ie.encode(ro);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dest;
	}
	
	/**
	 * convert tif to jpg(s)
	 * @param src (absolute path of tif file)
	 * @return absolute file path of jpg(s)
	 */
	public static List<String> tifToJpg(String src) {
		return splitTiff(src, "JPEG", ".jpg", new JPEGEncodeParam());
	}
	
	/**
	 * convert tif to bmp(s)
	 * @param src (absolute path of tif file)
	 * @return absolute file path of bmp(s)
	 */
	public static List<String> tifToBmp(String src) {
		return splitTiff(src, "BMP", ".bmp", new BMPEncodeParam());
	}

	/**
	 * @param src (absolute path of the source image file)
	 * @param width
	 * @param height
	 * @return absolute file path of the thumb-nail which is of png format
	 */
	public static String getThumbnail(String src, int width, int height) {
		if (!available(src))
			return null;
		Image source = getImage(src);
		double scale = getOriginScale(source);
		if(width <= 0) width = 100;
		if(height <= 0) height = 100;
		if (width > height) {
			height = (int) (width * scale);
		} else {
			width = (int) (height * scale);
		}
		return makeThumbnail(src, source, width, height);
	}
	
	/**
	 * 
	 * @param documentTitle
	 * @param in
	 * @param out
	 * @param width
	 * @param height
	 * @return
	 */
    public static String getThumbnailForImage(String documentTitle,InputStream in, OutputStream out,int width, int height) {
        if (!available(documentTitle))
            return null;
        Image source = getImageForInOut(documentTitle,in);
        double scale = getOriginScale(source);
        if(width <= 0) width = 100;
        if(height <= 0) height = 100;
        if (width > height) {
            height = (int) (width * scale);
        } else {
            width = (int) (height * scale);
        }

        String dest = documentTitle.substring(0, documentTitle.lastIndexOf(".")) + "_tn.png";
        Image target = source.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        try {
            //Jimi.putImage(target, dest);
            Jimi.putImage("image/png", target, out);
        } catch (JimiException e) {
            e.printStackTrace();
        }
        return dest;

    }

    
	/**
	 * @param src (absolute path of the source image file)
	 * @param length
	 * @return absolute file path of the thumb-nail which is of png format
	 */
	public static String getThumbnail(String src, int length) {
		if (!available(src))
			return null;
		Image source = getImage(src);
		double scale = getOriginScale(source);
		int width = 100, height = 100;
		if(scale >= 1){
			width = length;
			height = (int)(width / scale);
		}else{
			height = length;
			width = (int)(height * scale);
		}
		if(width <= 0) width = 100;
		if(height <= 0) height = 100;
		return makeThumbnail(src, source, width, height);
	}
	
	/**
	 * @param src
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static String drawRect(String src, int x, int y, int width,
			int height) {
		if (!available(src))
			return null;
		if (isTif(src))
			return drawRectOnTif(src, x, y, width, height);
		String dest = src.substring(0, src.lastIndexOf(".")) + "_rc.png";
		try {
			Image source = ImageIO.read(new File(src));
			Graphics g = source.getGraphics();
			Color c = g.getColor();
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(x, y, width, height);
			g.setColor(c);
			Jimi.putImage(source, dest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	/**
	 * 
	 * @param src
	 * @param in
	 * @param out
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 * @throws Exception
	 */
    public static String drawRectForImage(String documentTitle,
                                          InputStream in,
                                          OutputStream out,
                                          int x,
                                          int y,
                                          int width,
                                          int height) throws Exception {
        if (!available(documentTitle))
            return null;
        if (isTif(documentTitle))
            return drawRectOnTif(documentTitle, x, y, width, height);
        String dest = documentTitle.substring(0, documentTitle.lastIndexOf(".")) + "_rc.png";
        Image source = ImageIO.read(in);
        Graphics g = source.getGraphics();
        Color c = g.getColor();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);
        g.setColor(c);
        // Jimi.putImage(source, dest);
        Jimi.putImage("image/jpg", source, out);
        return dest;
    }
	
	/**
     * @param src
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
	private static String drawRectOnTif(String src, int x, int y, int width, int height){
		String dest = src.substring(0, src.lastIndexOf(".")) + "_rc.tif";
		
		return null;
	}
	
	/**
	 * determine whether the source image is of .jpg format or not, 
	 * just based on the suffix of the file name of the source image, 
	 * regardless of the actual encode format.
	 * @param src
	 * @return
	 */
	public static boolean isJpg(String src) {
		src = src.toLowerCase().trim();
		if (src.endsWith(".jpg") || src.endsWith(".jpe")
				|| src.endsWith(".jpeg"))
			return true;
		return false;
	}

	/**
	 * determine whether the source image is of .tif format or not, 
	 * just based on the suffix of the file name of the source image, 
	 * regardless of the actual encode format.
	 * @param src
	 * @return
	 */
	public static boolean isTif(String src) {
		src = src.toLowerCase().trim();
		if (src.endsWith(".tif") || src.endsWith(".tiff"))
			return true;
		return false;
	}

	/**
	 * determine whether the source image is of .bmp format or not, 
	 * just based on the suffix of the file name of the source image, 
	 * regardless of the actual encode format.
	 * @param src
	 * @return
	 */
	public static boolean isBmp(String src) {
		src = src.toLowerCase().trim();
		if (src.endsWith(".bmp"))
			return true;
		return false;
	}

	/**
	 * determine whether the source image is of .png format or not, 
	 * just based on the suffix of the file name of the source image, 
	 * regardless of the actual encode format.
	 * @param src
	 * @return
	 */
	public static boolean isPng(String src) {
		src = src.toLowerCase().trim();
		if (src.endsWith(".png"))
			return true;
		return false;
	}

	/**
	 * determine whether the source image is of .gif format or not, 
	 * just based on the suffix of the file name of the source image, 
	 * regardless of the actual encode format.
	 * @param src
	 * @return
	 */
	public static boolean isGif(String src) {
		src = src.toLowerCase().trim();
		if (src.endsWith(".gif"))
			return true;
		return false;
	}
	
	private static List<String> splitTiff(String src, String format,
			String suffix, ImageEncodeParam param) {
		if (!isTif(src))
			return null;
		List<String> list = new ArrayList<String>();
		String dest = src.substring(0, src.lastIndexOf(".")) + suffix;
		try {
			ImageDecoder decoder = ImageCodec.createImageDecoder("TIFF",
					new File(src), new TIFFDecodeParam());
			int num = decoder.getNumPages();
			if (num == 1) {
				list.add(process(dest, format, decoder.decodeAsRenderedImage(),
						param));
			} else {
				for (int i = 0; i < num; i++) {
					dest = src.substring(0, src.lastIndexOf(".")) + "-" + i
							+ suffix;
					list.add(process(dest, format, decoder
							.decodeAsRenderedImage(i), param));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	
	private static String process(final String dest, String format,
			RenderedImage img, ImageEncodeParam param) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(dest);
			ImageEncoder encoder = ImageCodec.createImageEncoder(format, os,
					param);
			encoder.encode(img);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return dest;
	}

	private static String process(final String src, final String dest) {
		try {
			ImageProducer ip = Jimi.getImageProducer(src);
			Jimi.putImage(ip, dest);
		} catch (JimiException je) {
			throw new RuntimeException(je);
		}
		return dest;
	}
	
	private static boolean available(String src) {
		if (isJpg(src) || isBmp(src) || isPng(src) || isTif(src) || isGif(src))
			return true;
		return false;
	}
	
	private static Image getImage(String src) {
		Image img = null;
		if(isTif(src))
			return ((ToolkitImage)Jimi.getImage(src)).getBufferedImage();
		else
			try{
				img = ImageIO.read(new File(src));
			}catch(IOException e){
				e.printStackTrace();
			}
		return img;
	}
	
    private static Image getImageForInOut(String documentTitle,InputStream in) {
        Image img = null;
        if(isTif(documentTitle))
            return ((ToolkitImage)Jimi.getImage(in)).getBufferedImage();
        else
            try{
                img = ImageIO.read(in);
            }catch(IOException e){
                e.printStackTrace();
            }
        return img;
    }
	
	
	private static double getOriginScale(Image source){
		int srcWidth = source.getWidth(null);
		int srcHeight = source.getHeight(null);
		return (double)srcWidth / srcHeight;
	}
	
	private static String makeThumbnail(String src, Image source, int width, int height) {
		String dest = src.substring(0, src.lastIndexOf(".")) + "_tn.png";
		Image target = source.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		try {
			Jimi.putImage(target, dest);
		} catch (JimiException e) {
			e.printStackTrace();
		}
		return dest;
	}

}
