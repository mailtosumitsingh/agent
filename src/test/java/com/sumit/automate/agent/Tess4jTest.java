package com.sumit.automate.agent;
import java.awt.image.BufferedImage;
import java.io.File;
import net.sourceforge.tess4j.*;

import javax.imageio.ImageIO;

public class Tess4jTest {
    public static void main(String[] args) throws Exception{

        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("eng");
        tesseract.setOcrEngineMode(1);


        tesseract.setDatapath("c:\\projects\\agent\\data\\");

        BufferedImage image = ImageIO.read(new File("c:\\temp\\Screenshot 2023-02-10 183455.png"));

        String result = tesseract.doOCR(image);
        System.out.println(result);
    }
}
