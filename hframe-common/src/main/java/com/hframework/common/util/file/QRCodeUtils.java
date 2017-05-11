package com.hframework.common.util.file;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public final class QRCodeUtils {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }


    private static void writeToFile(BitMatrix matrix, String format, File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }


    private static void writeToStream(BitMatrix matrix, String format, OutputStream stream)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    private static String fileInputStream(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        while ((fileInputStream.read(buf)) != -1) {
            sb.append(new String(buf));
            buf = new byte[1024];//重新生成，避免和上次读取的数据重复
        }
        return sb.toString();
    }

    public static String createQRCode(String path,String fileName, String content) throws Throwable {
        FileUtils.createDir(path);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map map = new HashMap();
        map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, map);
        File file = new File(path, fileName + ".jpg");//以时间戳命名
        QRCodeUtils.writeToFile(bitMatrix, "jpg", file);
        BufferedImage bufferedImage;
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            bufferedImage = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            //System.out.println(encoder.encodeBuffer(bytes).trim());
            return encoder.encodeBuffer(bytes).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}