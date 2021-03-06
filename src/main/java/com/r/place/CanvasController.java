package com.r.place;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;

@Controller
@RequestMapping(path="/canvas")
public class CanvasController {
    @Autowired
    private CanvasRepository canvasRepository;

    @GetMapping(path="/add")
    public @ResponseBody
    String greeting(@RequestParam(name="width", required=false, defaultValue="2") String width,
                    @RequestParam(name="height", required=false, defaultValue="2") String height, Model model) {
        Canvas n = new Canvas(Integer.parseInt(width), Integer.parseInt(height));
        canvasRepository.save(n);
        return "New canvas added";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Canvas> getAllCanvases() {
        // This returns a JSON or XML with the users
        return canvasRepository.findAll();
    }

    @GetMapping(path="/debug")
    public @ResponseBody Canvas getLastCanvas() {
        // This returns a JSON or XML with the users
        Iterable<Canvas> canvases =  canvasRepository.findAll();
        final Iterator<Canvas> itr = canvases.iterator();
        Canvas lastElement = itr.next();

        while(itr.hasNext()) {
            lastElement=itr.next();
        }

        return lastElement;
    }

    @GetMapping(path="/other")
    public @ResponseBody Canvas test() {

        return getLastCanvas();
    }

    @GetMapping(path="/pixel")
    public @ResponseBody String pixel(@RequestParam(name="x") String x,
                                      @RequestParam(name="y") String y,
                                      @RequestParam(name="R") String R,
                                      @RequestParam(name="G") String G,
                                      @RequestParam(name="B") String B) {
        Canvas c = this.getLastCanvas();
        int xx = Integer.parseInt(x);
        int yy = Integer.parseInt(y);
        byte rr = (byte)(Integer.parseInt(R) & 0xff);
        byte bb = (byte)(Integer.parseInt(B) & 0xff);
        byte gg = (byte)(Integer.parseInt(G) & 0xff);
        if (xx >= c.getWidth()) {
            return "x wrong";
        }
        if (yy >= c.getHeight()) {
            return "y wrong";
        }

        c.setPixel(xx, yy, rr, bb, gg);
        canvasRepository.save(c);
        return "ok";
    }

    @GetMapping(path="/show")
    public @ResponseBody String show() {
        Canvas c = this.getLastCanvas();
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < c.getHeight(); y++) {
            for (int x = 0; x < c.getWidth(); x++) {
               sb.append(String.format("(%d), ", c.getPixel(x, y)));
            }
            sb.append("<br>");
        }

        return sb.toString();
    }


    @GetMapping(path = "/get", produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String sayHello()
    {
        Canvas c = this.getLastCanvas();
        return String.format("{\"width\": %d, \"height\": %d,  \"data\": \"%s\"}", c.getWidth(), c.getHeight(), new String(Base64.encodeBase64(c.getData())));
    }
}