package com.sumit.automate.agent;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.sikuli.script.Image;
import org.sikuli.script.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api")
public class AutomationController {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    TessService tessService;

    @Autowired
    Robot robot;

    @PostMapping("/test")
    public Point test( @RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point p  = null;
          System.out.println("got a test message");
        p = new com.sumit.automate.agent.Point();
        p.getData().put("reply",System.currentTimeMillis());
        return p;


    }

    @PostMapping("/mousemove")
    public Point mouseMove(@RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point p  = null;
        int x = event.getData().get("x")==null?0:(Integer)event.getData().get("x");
        int y = event.getData().get("y")==null?0:(Integer)event.getData().get("y");
           System.out.println("got a mouse move message");
        robot.moveMouse(x,y);
        p = new Point();
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    @PostMapping("/click")
    public Point click( @RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point p  = null;
        int x = event.getData().get("x")==null?0:(Integer)event.getData().get("x");
        int y = event.getData().get("y")==null?0:(Integer)event.getData().get("y");
        System.out.println("got a mouse click message");
        robot.click(x,y);
        p = new Point();
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    @PostMapping("/movewheel")
    public Point moveWheel( @RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point p  = null;

        int amt = event.getData().get("amt")==null?0:(Integer)event.getData().get("amt");

        System.out.println("got a move wheel message");
        robot.moveMouseWheel(amt);
        p = new Point();
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    @PostMapping("/sendtext")
    public Point sendText( @RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point p  = null;
        int x = event.getData().get("x")==null?0:(Integer)event.getData().get("x");
        int y = event.getData().get("y")==null?0:(Integer)event.getData().get("y");
         String text = (String) event.getData().get("text");
        System.out.println("got a mouse sendtext message");
        robot.sendText(x,y,text);
        p = new Point();
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    @PostMapping("/type")
    public Point type( @RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point p  = null;
        String text = (String) event.getData().get("text");
        System.out.println("got a type message");
        robot.type(text);
        p = new Point();
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    @PostMapping("/findtext")
    public Point findText(@RequestBody  BaseAutomationEvent event) {
        com.sumit.automate.agent.Point p  = null;
       String text = (String) event.getData().get("text");
        Location loc = null;
        System.out.println("got a mouse findtext message");
        loc = robot.findFirstText(text);
        p = new Point();
        if(loc!=null) {
            p.setX(loc.x);
            p.setY(loc.y);
        }
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    //to bne made better
    @PostMapping("/gettext")
    public Point getText( @RequestBody BaseAutomationEvent event) throws Exception{
        com.sumit.automate.agent.Point p  = new Point();
        int x = event.getData().get("x")==null?0:(Integer)event.getData().get("x");
        int y = event.getData().get("y")==null?0:(Integer)event.getData().get("y");
        int w = event.getData().get("w")==null?0:(Integer)event.getData().get("w");
        int h = event.getData().get("h")==null?0:(Integer)event.getData().get("h");

        System.out.println("got a mouse gettext message");
        BufferedImage buffimage = robot.getBufferedImage(x, y, w, h);
        String textVal = tessService.toText(buffimage);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(buffimage, "png", baos);
            byte[] bytes = baos.toByteArray();
            String data = Base64.getEncoder().encodeToString(bytes);
            p.getData().put("img",data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        p.getData().put("text",textVal);
        p.getData().put("reply",System.currentTimeMillis());
        return p;

    }

    @PostMapping("/findimage")
    public Point findImage(@RequestBody  BaseAutomationEvent event) {
        com.sumit.automate.agent.Point p  = new Point();

        String img = (String)event.getData().get("img");
        Location loc = null;
        System.out.println("got a mouse findimage message");
        byte[]data = Base64.getDecoder().decode(img);
        ByteArrayInputStream baos = new ByteArrayInputStream(data);
        try {
            BufferedImage image = ImageIO.read(baos);
            Image image1 = new Image(image);
            loc = robot.findFirstImage(image1);
            if(loc!=null) {
                p.setX(loc.x);
                p.setY(loc.y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    @PostMapping("/screenshot")
    public Point screenshot( @RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point  p = new Point();

        System.out.println("got a screenshot message");

        p.getData().put("img",robot.screenShot());

        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    @PostMapping("/getmousecolor")
    public Point getMouseColor( @RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point  p = new Point();
        int x = event.getData().get("x")==null?0:(Integer)event.getData().get("x");
        int y = event.getData().get("y")==null?0:(Integer)event.getData().get("y");
        Location loc = null;
        System.out.println("got a getmousecolor message");

        int[] color = robot.getMouseColor(x,y);
        p.getData().put("rgb_r",color[0]);
        p.getData().put("rgb_g",color[1]);
        p.getData().put("rgb_b",color[2]);
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }

    @PostMapping("/getmouse")
    public Point getMouse( @RequestBody BaseAutomationEvent event) {
        com.sumit.automate.agent.Point  p = new Point();
        System.out.println("got a getmousecolor message");
        int[] location = robot.getMouseLoc();
        p.setX(location[0]);
        p.setY(location[1]);
        p.getData().put("reply",System.currentTimeMillis());
        return p;
    }
}