package stni.smr;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 */
public class TgaIO {
    public static BufferedImage read(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        byte[] header = new byte[18];
        byte[] data = readHeaderAndData(file, header);

        int w = readWidth(header);
        int h = readHeight(header);

        boolean alpha = readAlpha(w, h, data, header);
        BufferedImage dst = readPixels(w, h, data, alpha);

        if ((header[17] & 0x10) != 0) {
            flipHorizontal(dst);
        }

        if ((header[17] & 0x20) != 0) {
            flipVertical(dst);
        }

        return dst;
    }

    private static byte[] readHeaderAndData(File file, byte[] header) throws IOException {
        int len = (int) file.length() - header.length;
        if (len < 0) {
            throw new IllegalStateException("file not big enough to contain header: " + file.getAbsolutePath());
        }
        byte[] data = new byte[len];

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.read(header);
        raf.read(data);
        raf.close();

        if ((header[0] | header[1]) != 0) {
            throw new IllegalStateException(file.getAbsolutePath());
        }
        if (header[2] != 2) {
            throw new IllegalStateException(file.getAbsolutePath());
        }
        return data;
    }

    private static int readHeight(byte[] header) {
        return (header[14] & 0xFF) | ((header[15] & 0xFF) << 8);
    }

    private static int readWidth(byte[] header) {
        return (header[12] & 0xFF) | ((header[13] & 0xFF) << 8);
    }

    private static boolean readAlpha(int w, int h, byte[] data, byte[] header) {
        boolean alpha;
        if ((w * h) * 3 == data.length) {
            alpha = false;
        } else if ((w * h) * 4 == data.length) {
            alpha = true;
        } else {
            throw new IllegalStateException("cannot read alpha");
        }
        if (!alpha && (header[16] != 24)) {
            throw new IllegalStateException("cannot read alpha");
        }
        if (alpha && (header[16] != 32)) {
            throw new IllegalStateException("cannot read alpha");
        }
        if ((header[17] & 15) != (alpha ? 8 : 0)) {
            throw new IllegalStateException("cannot read alpha");
        }
        return alpha;
    }

    private static BufferedImage readPixels(int w, int h, byte[] data, boolean alpha) {
        BufferedImage dst = new BufferedImage(w, h, alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();
        if (pixels.length != w * h || data.length != pixels.length * (alpha ? 4 : 3)) {
            throw new IllegalStateException("Pixel count does not match");
        }

        if (alpha) {
            for (int i = 0, p = (pixels.length - 1) * 4; i < pixels.length; i++, p -= 4) {
                pixels[i] |= ((data[p + 0]) & 0xFF) << 0;
                pixels[i] |= ((data[p + 1]) & 0xFF) << 8;
                pixels[i] |= ((data[p + 2]) & 0xFF) << 16;
                pixels[i] |= ((data[p + 3]) & 0xFF) << 24;
            }
        } else {
            for (int i = 0, p = (pixels.length - 1) * 3; i < pixels.length; i++, p -= 3) {
                pixels[i] |= ((data[p + 0]) & 0xFF) << 0;
                pixels[i] |= ((data[p + 1]) & 0xFF) << 8;
                pixels[i] |= ((data[p + 2]) & 0xFF) << 16;
            }
        }
        return dst;
    }

    private static void flipVertical(BufferedImage image) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int x = 0; x < image.getWidth(); x++) {
            int h2 = image.getHeight() / 2;
            for (int y = 0; y < h2; y++) {
                int a = (y * image.getWidth()) + x;
                int b = ((image.getHeight() - 1 - y) * image.getWidth()) + x;
                int t = pixels[a];
                pixels[a] = pixels[b];
                pixels[b] = t;
            }
        }
    }

    private static void flipHorizontal(BufferedImage image) {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < image.getHeight(); y++) {
            int w2 = image.getWidth() / 2;
            for (int x = 0; x < w2; x++) {
                int a = (y * image.getWidth()) + x;
                int b = (y * image.getWidth()) + (image.getWidth() - 1 - x);
                int t = pixels[a];
                pixels[a] = pixels[b];
                pixels[b] = t;
            }
        }
    }

    public static void write(BufferedImage src, File file) throws IOException {
        byte[] data = getData(src);
        byte[] header = getHeader(src);
        writeFile(file, data, header);
    }

    private static byte[] getData(BufferedImage src) {
        flipHorizontal(src);
        DataBuffer buffer = src.getRaster().getDataBuffer();
        if (buffer instanceof DataBufferByte) {
            return getDataFromByteBuffer(src, (DataBufferByte) buffer);
        }
        if (buffer instanceof DataBufferInt) {
            return getDataFromIntBuffer(src, (DataBufferInt) buffer);
        }

        throw new UnsupportedOperationException();
    }

    private static byte[] getDataFromIntBuffer(BufferedImage src, DataBufferInt buf) {
        int[] pixels = buf.getData();
        if (pixels.length != src.getWidth() * src.getHeight()) {
            throw new IllegalStateException();
        }

        byte[] data = new byte[pixels.length * (src.getColorModel().hasAlpha() ? 4 : 3)];

        if (src.getColorModel().hasAlpha()) {
            for (int i = 0, p = pixels.length - 1; i < data.length; i += 4, p--) {
                data[i + 0] = (byte) ((pixels[p] >> 0) & 0xFF);
                data[i + 1] = (byte) ((pixels[p] >> 8) & 0xFF);
                data[i + 2] = (byte) ((pixels[p] >> 16) & 0xFF);
                data[i + 3] = (byte) ((pixels[p] >> 24) & 0xFF);
            }
        } else {
            for (int i = 0, p = pixels.length - 1; i < data.length; i += 3, p--) {
                data[i + 0] = (byte) ((pixels[p] >> 0) & 0xFF);
                data[i + 1] = (byte) ((pixels[p] >> 8) & 0xFF);
                data[i + 2] = (byte) ((pixels[p] >> 16) & 0xFF);
            }
        }
        return data;
    }

    private static byte[] getDataFromByteBuffer(BufferedImage src, DataBufferByte buf) {
        byte[] pixels = buf.getData();
        if (pixels.length != src.getWidth() * src.getHeight() * (src.getColorModel().hasAlpha() ? 4 : 3)) {
            throw new IllegalStateException();
        }

        byte[] data = new byte[pixels.length];

        for (int i = 0, p = pixels.length - 1; i < data.length; i++, p--) {
            data[i] = pixels[p];
        }
        return data;
    }

    private static void writeFile(File file, byte[] data, byte[] header) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.write(header);
        raf.write(data);
        raf.setLength(raf.getFilePointer()); // trim
        raf.close();
    }

    private static byte[] getHeader(BufferedImage src) {
        byte[] header = new byte[18];
        boolean alpha = src.getColorModel().hasAlpha();

        header[2] = 2; // uncompressed, true-color image
        header[12] = (byte) ((src.getWidth() >> 0) & 0xFF);
        header[13] = (byte) ((src.getWidth() >> 8) & 0xFF);
        header[14] = (byte) ((src.getHeight() >> 0) & 0xFF);
        header[15] = (byte) ((src.getHeight() >> 8) & 0xFF);
        header[16] = (byte) (alpha ? 32 : 24); // bits per pixel
        header[17] = (byte) ((alpha ? 8 : 0) | (1 << 4));
        return header;
    }
}
