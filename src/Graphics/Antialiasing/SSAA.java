package Graphics.Antialiasing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SSAA {
    private static final int SSAASCALE = 16;

    private static final String inPath = "E:\\Программы\\Idea Projects\\Videos\\src\\Graphics\\Images\\shark.jpg";
    private static final String outPath = "E:\\Программы\\Idea Projects\\Videos\\src\\Graphics\\Images\\ssaa.png";
    private static final String format = "png";
    private static boolean rewrite = true;

    public static void main(String[] args) {
        if (!outPath.endsWith(format)) {
            System.out.println("Incorrect format");
            return;
        }

        try {
            BufferedImage image = ImageIO.read(new File(inPath));
            System.out.println("Starting antialiasing...");
            ssaa(image);
            System.out.println("Finished antialiasing...");
            File out = new File(outPath);
            if (out.createNewFile()) {
                System.out.println("Saving...");
                ImageIO.write(image, format, out);
                System.out.println("Saved successfully");
            } else {
                System.out.println("Failed to create an out file");
                if (rewrite) {
                    System.out.println("Rewriting");
                    ImageIO.write(image, format, out);
                    System.out.println("Rewriting successful");
                }
            }
            System.out.println("Finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ssaa(BufferedImage img) {
        Image resizedimg = img.getScaledInstance(img.getWidth() * SSAASCALE, img.getHeight() * SSAASCALE, Image.SCALE_DEFAULT);
        BufferedImage resized = toBufferedImage(resizedimg);
        for (int i = 1; i < img.getWidth(); i++) {
            for (int j = 1; j < img.getHeight(); j++) {
                int r1, r2, r3, r4;
                r1 = resized.getRGB(i * SSAASCALE - 1, j * SSAASCALE - 1);
                r2 = resized.getRGB(i * SSAASCALE - 1, j * SSAASCALE);
                r3 = resized.getRGB(i * SSAASCALE, j * SSAASCALE - 1);
                r4 = resized.getRGB(i * SSAASCALE, j * SSAASCALE);
                short blue1 = (short) (r1 & 0xff);
                short green1 = (short) ((r1 & 0xff00) >> 8);
                short red1 = (short) ((r1 & 0xff0000) >> 16);
                short blue2 = (short) (r2 & 0xff);
                short green2 = (short) ((r2 & 0xff00) >> 8);
                short red2 = (short) ((r2 & 0xff0000) >> 16);
                short blue3 = (short) (r3 & 0xff);
                short green3 = (short) ((r3 & 0xff00) >> 8);
                short red3 = (short) ((r3 & 0xff0000) >> 16);
                short blue4 = (short) (r4 & 0xff);
                short green4 = (short) ((r4 & 0xff00) >> 8);
                short red4 = (short) ((r4 & 0xff0000) >> 16);

                int medRed = ((red1 + red2 + red3 + red4) / 4);
                int medGreen = ((green1 + green2 + green3 + green4) / 4);
                int medBlue = ((blue1 + blue2 + blue3 + blue4) / 4);
                img.setRGB(i, j, new Color(medRed, medGreen, medBlue).getRGB());
            }
        }
    }

    public static BufferedImage toBufferedImage(Image img) {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
}
