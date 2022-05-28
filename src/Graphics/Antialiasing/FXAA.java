package Graphics.Antialiasing;
//мыло
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FXAA {
    private static final String inPath = "E:\\Программы\\Idea Projects\\Videos\\src\\Graphics\\Images\\shark.jpg";
    private static final String outPath = "E:\\Программы\\Idea Projects\\Videos\\src\\Graphics\\Images\\fxaa.jpg";
    private static final String format = "jpg";
    private static boolean rewrite = true;

    public static void main(String[] args) {
        if (!outPath.endsWith(format)) {
            System.out.println("Incorrect format");
            return;
        }

        try {
            BufferedImage image = ImageIO.read(new File(inPath));
            System.out.println("Starting antialiasing...");
            fxaa(image);
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

    public static void fxaa(BufferedImage img) {
        for (int i = 1; i < img.getWidth(); i++) {
            for (int j = 1; j < img.getHeight(); j++) {
                int r1, r2, r3, r4;
                r1 = img.getRGB(i - 1, j - 1);
                r2 = img.getRGB(i - 1, j);
                r3 = img.getRGB(i, j - 1);
                r4 = img.getRGB(i, j);
                int blue1 = r1 & 0xff;
                int green1 = (r1 & 0xff00) >> 8;
                int red1 = (r1 & 0xff0000) >> 16;
                int blue2 = r2 & 0xff;
                int green2 = (r2 & 0xff00) >> 8;
                int red2 = (r2 & 0xff0000) >> 16;
                int blue3 = r3 & 0xff;
                int green3 = (r3 & 0xff00) >> 8;
                int red3 = (r3 & 0xff0000) >> 16;
                int blue4 = r4 & 0xff;
                int green4 = (r4 & 0xff00) >> 8;
                int red4 = (r4 & 0xff0000) >> 16;

                int medRed = (red1 + red2 + red3 + red4) / 4;
                int medGreen = (green1 + green2 + green3 + green4) / 4;
                int medBlue = (blue1 + blue2 + blue3 + blue4) / 4;
                img.setRGB(i, j, new Color(medRed, medGreen, medBlue).getRGB());
            }
        }
    }

}
