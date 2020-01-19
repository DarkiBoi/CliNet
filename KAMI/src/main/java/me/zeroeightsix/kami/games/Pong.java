package me.zeroeightsix.kami.games;


import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class Pong {

    private static Thread threed;

    public static void createDisplay() {

        ContextAttribs attribs = new ContextAttribs(3,2);
        attribs.withForwardCompatible(true);
        attribs.withProfileCore(true);

        threed = new Thread(() -> {
            try {
                Display.setDisplayMode(new DisplayMode(400, 400));
                Display.create(new PixelFormat(), attribs);
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
            GL11.glViewport(0, 0, 400, 400);
        });

        threed.start();





    }

    public static void updateDisplay() {

        Display.sync(60);
        Display.update();

    }

    public static void closeDisplay() {

        Display.destroy();

    }



}
