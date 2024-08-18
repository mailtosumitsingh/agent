package com.sumit.automate.agent;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class TessService {
    Tesseract tesseract ;
    @PostConstruct
    public void init(){
         tesseract = new Tesseract();
        tesseract.setLanguage("eng");
        tesseract.setOcrEngineMode(1);
        tesseract.setDatapath("c:\\projects\\agent\\data\\");

    }
    public String toText( BufferedImage image) throws Exception{

        String result = tesseract.doOCR(image);
        System.out.println(result);
        return result;
    }
}
