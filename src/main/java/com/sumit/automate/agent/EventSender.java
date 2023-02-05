package com.sumit.automate.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sikuli.script.Image;
import org.sikuli.script.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Component
public class EventSender {
    ZMQ.Socket socket;
    ZContext context;

    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    Robot robot;
    public EventSender() {
        context = new ZContext();
        socket = context.createSocket(SocketType.REP);
        socket.bind("tcp://127.0.0.1:5555");
        receiver();
    }

    public void receiver() {
        Runnable receiver = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] reply = socket.recv(0);
                // Print the message
                System.out.println("Received: [" + new String(reply, ZMQ.CHARSET) + "]");
                String response = "";
                try {
                    BaseAutomationEvent baseAutomationEvent = mapper.readValue(reply,BaseAutomationEvent.class);
                    Point p = handle(baseAutomationEvent);
                    response = mapper.writeValueAsString(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        };
        new Thread(receiver).start();
    }
    public  Point handle(BaseAutomationEvent event){
        Point p  = null;
        int x = event.getData().get("x")==null?0:(Integer)event.getData().get("x");
        int y = event.getData().get("y")==null?0:(Integer)event.getData().get("y");
        int w = event.getData().get("w")==null?0:(Integer)event.getData().get("w");
        int h = event.getData().get("h")==null?0:(Integer)event.getData().get("h");

        int amt = event.getData().get("amt")==null?0:(Integer)event.getData().get("amt");

        String img = (String)event.getData().get("img");
        String text = (String) event.getData().get("text");
        Location loc = null;
        switch (event.getMsg()){
            case "test":
                System.out.println("got a test message");
                p = new Point();
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "movemouse":
                System.out.println("got a mouse move message");
                robot.moveMouse(x,y);
                p = new Point();
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "click":
                System.out.println("got a mouse click message");
                robot.click(x,y);
                p = new Point();
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "movewheel":
                System.out.println("got a move wheel message");
                robot.moveMouseWheel(amt);
                p = new Point();
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "sendtext":
                System.out.println("got a mouse sendtext message");
                robot.sendText(x,y,text);
                p = new Point();
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "type":
                System.out.println("got a type message");
                robot.type(text);
                p = new Point();
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "findtext":
                System.out.println("got a mouse findtext message");
                loc = robot.findFirstText(text);
                p = new Point();
                if(loc!=null) {
                    p.setX(loc.x);
                    p.setY(loc.y);
                }
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "gettext":
                System.out.println("got a mouse gettext message");
                String textVal = robot.toText(x,y,w,h);
                p.getData().put("text",textVal);
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "findimage":
                System.out.println("got a mouse findimage message");
                byte[]data = Base64.getDecoder().decode(img);
                ByteArrayInputStream baos = new ByteArrayInputStream(data);
                try {
                    BufferedImage image = ImageIO.read(baos);
                    Image image1 = new Image(image);
                    loc = robot.findFirstImage(image1);
                    p = new Point();
                    if(loc!=null) {
                        p.setX(loc.x);
                        p.setY(loc.y);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "screenshot":
                System.out.println("got a screenshot message");
                p = new Point();
                p.getData().put("img",robot.screenShot());
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "getmousecolor":
                System.out.println("got a getmousecolor message");
                p = new Point();
                int[] color = robot.getMouseColor(x,y);
                p.getData().put("rgb_r",color[0]);
                p.getData().put("rgb_g",color[1]);
                p.getData().put("rgb_b",color[2]);
                p.getData().put("reply",System.currentTimeMillis());
                break;
            case "getmouse":
                System.out.println("got a getmousecolor message");
                p = new Point();
                int[] location = robot.getMouseLoc();
                p.setX(location[0]);
                p.setY(location[1]);
                p.getData().put("reply",System.currentTimeMillis());
                break;

        }
        return p;
    }
}
