package com.utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.iver.cit.gvsig.drivers.dwg.DwgMemoryDriver;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class PDFUtil {

    public static final String CHINESE_FONT = "STSong-Light";
    public static final String CHINESE_ENCODING = "UniGB-UCS2-H";
    public static final String EXPIRE_STAMP = "已过�?;
    public static final String DEFAULT_PROJECTION = "EPSG:23030";

    private static BaseFont baseFont;

    static {
        try {
            baseFont = BaseFont.createFont(CHINESE_FONT,
                                           CHINESE_ENCODING,
                                           BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    public static void expire(InputStream src, OutputStream dest) throws IOException,
        DocumentException {
        stamp(src, dest, EXPIRE_STAMP);
    }

    public static void stamp(InputStream src, OutputStream dest, String text) throws IOException,
        DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, dest);
        for (int i = 1; i <= reader.getNumberOfPages(); ++i) {
            ColumnText.showTextAligned(stamper.getOverContent(i),
                                       Element.ALIGN_LEFT,
                                       new Phrase(text, new Font(baseFont,
                                                                 40,
                                                                 Font.NORMAL,
                                                                 BaseColor.RED)),
                                       30,
                                       10,
                                       45);
        }
        stamper.close();
    }

    public static boolean dwg2pdf(InputStream src, OutputStream dest) throws IOException,
        DocumentException {
        File tmp = File.createTempFile(String.valueOf(System.nanoTime()),
                                       ".dwg");
        OutputStream out = new FileOutputStream(tmp);

        byte[] buf = new byte[4096];
        int read;
        while ((read = src.read(buf)) != -1) {
            out.write(buf, 0, read);
        }
        src.close();
        out.flush();
        out.close();

        int width = (int) PageSize.A4.getWidth() - 20;
        int height = (int) PageSize.A4.getHeight() - 20;

        FLayer layer = LayerFactory.createLayer("layer1",
                                                new DwgMemoryDriver(),
                                                tmp,
                                                CRSFactory.getCRS(DEFAULT_PROJECTION));
        ViewPort vp = new ViewPort(CRSFactory.getCRS(DEFAULT_PROJECTION));
        vp.setImageSize(new Dimension(width, height));
        MapContext map = new MapContext(vp);
        map.getLayers().addLayer(layer);

        BufferedImage img = new BufferedImage(width,
                                              height,
                                              BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);
        try {
            map.draw(img, g2d, map.getScaleView());
        } catch (ReadDriverException e) {
            throw new AssertionError(e.getMessage());
        }

        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        PdfWriter.getInstance(document, dest);
        document.open();
        document.add(Image.getInstance(img, null));
        document.close();

        tmp.delete();
        return true;
    }

    public static void main(String[] args) {
        File in = new File("C:/temp/8th floor power.dwg");
        File out = new File("C:/temp/8th floor power.pdf");
        try {
            InputStream is = new FileInputStream(in);
            OutputStream os = new FileOutputStream(out);
            dwg2pdf(is, os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
