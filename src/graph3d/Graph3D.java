package graph3d;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.glu.GLU;

public class Graph3D {

    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(640, 480));
            Display.create();
        } catch (LWJGLException e) {
            System.out.println(e);
        }
        Display.setTitle("Graph3D");
        
        Graph3D graph = new Graph3D();
        graph.init();
        Camera.init();
        
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            graph.render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
    
    int VBOVertexHandle;
    int VBOColorHandle;
    
    float q11 = 1;
    float q12 = .5f;
    float q21 = .5f;
    float q22 = 1;
    float c1 = 0;
    float c2 = 0;
    
    float xMax = 20;
    float xMin = -20;
    float yMax = 20;
    float yMin = -20;
    
    float res = 1/10f;
    float heightScale = 1f;
    int start = (int) Math.floor(Math.min(xMin, yMin));
    int end = (int) Math.ceil(Math.max(xMax, yMax));
    
    final int VERTICES = 4*4*(start-end)*(start-end) + 16*2;
    
    FloatBuffer positionBuffer;
    FloatBuffer colorBuffer;
    
    private float func(float x, float y) {
        return (q11*x*x + q22*y*y + (q12+q21)*x*y + c1*x + c2*y);
        //return (3*x*x + 7*y*y + 10*x*y + x + 2*y)/10f;
    }
    
    private void loadData() {
        for (int x=start;x<end;x++) {
            for (int y=start;y<end;y++) {
                float sx = x*res;
                float sy = y*res;
                positionBuffer.put(sx);
                positionBuffer.put(func(sx, sy)*heightScale);
                positionBuffer.put(sy);
                
                positionBuffer.put(sx+res);
                positionBuffer.put(func(sx+res, sy)*heightScale);
                positionBuffer.put(sy);
                
                positionBuffer.put(sx+res);
                positionBuffer.put(func(sx+res, sy+res)*heightScale);
                positionBuffer.put(sy+res);
                
                positionBuffer.put(sx);
                positionBuffer.put(func(sx, sy+res)*heightScale);
                positionBuffer.put(sy+res);
                
                colorBuffer.put((x-(float)start)/(float)(end-start));
                colorBuffer.put((y-(float)start)/(float)(end-start));
                colorBuffer.put(1);
                colorBuffer.put(1);
                
                colorBuffer.put((x-(float)start)/(float)(end-start));
                colorBuffer.put((y-(float)start)/(float)(end-start));
                colorBuffer.put(1);
                colorBuffer.put(1);
                
                colorBuffer.put((x-(float)start)/(float)(end-start));
                colorBuffer.put((y-(float)start)/(float)(end-start));
                colorBuffer.put(1);
                colorBuffer.put(1);
                
                colorBuffer.put((x-(float)start)/(float)(end-start));
                colorBuffer.put((y-(float)start)/(float)(end-start));
                colorBuffer.put(1);
                colorBuffer.put(1);
            }
        }
        
        float h = .75f;
        
        positionBuffer.put(xMax);
        positionBuffer.put(h);
        positionBuffer.put(yMax);
        
        positionBuffer.put(xMin);
        positionBuffer.put(h);
        positionBuffer.put(yMax);
        
        positionBuffer.put(xMin);
        positionBuffer.put(h);
        positionBuffer.put(yMin);
        
        positionBuffer.put(xMax);
        positionBuffer.put(h);
        positionBuffer.put(yMin);

        for (int i=0;i<4;i++) {
            colorBuffer.put(0);
            colorBuffer.put(1);
            colorBuffer.put(0);
            colorBuffer.put(0.5f);
        }
        
        // y = 1 - x
        
        positionBuffer.put(10);
        positionBuffer.put(-5);
        positionBuffer.put(-9);
        
        positionBuffer.put(10);
        positionBuffer.put(10);
        positionBuffer.put(-9);
        
        positionBuffer.put(-10);
        positionBuffer.put(10);
        positionBuffer.put(11);
        
        positionBuffer.put(-10);
        positionBuffer.put(-5);
        positionBuffer.put(11);
        
        for (int i=0;i<4;i++) {
            colorBuffer.put(1);
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0.5f);
        }
    }
    
    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
        
        Camera.update();
        
        positionBuffer.rewind();
        colorBuffer.rewind();
        
        loadData();
        
        positionBuffer.flip();
        colorBuffer.flip();
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,VBOVertexHandle);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,positionBuffer,GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,VBOColorHandle);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,colorBuffer,GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);
        
        GL11.glPushMatrix();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOVertexHandle);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOColorHandle);
        GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0L);
        GL11.glDrawArrays(GL11.GL_QUADS, 0, VERTICES/4);
        GL11.glPopMatrix();
    }
    
    private void init() {
        positionBuffer = BufferUtils.createFloatBuffer(VERTICES);
        colorBuffer = BufferUtils.createFloatBuffer(VERTICES);
        
        GL11.glShadeModel(GL11.GL_SMOOTH); // Smoother textures.
        GL11.glClearDepth(1.0); // Buffer depth, allows objects to draw over things behind them.
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Depth testing (see above).
        GL11.glDepthFunc(GL11.GL_LEQUAL); // Type of depth testing.
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        
        GL11.glMatrixMode(GL11.GL_PROJECTION); // Sets matrix mode to displaying pixels.
        GL11.glLoadIdentity(); // Loads the above matrix mode.
        
        // Sets default perspective location.                       Render Distances: Min   Max
        GLU.gluPerspective(45.0f,(float)Display.getWidth()/(float)Display.getHeight(),0.1f,300.0f);
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Sets the matrix to displaying objects.
        //GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT,GL11.GL_NICEST); // Something unimportant for quality.
        
        VBOVertexHandle = GL15.glGenBuffers();
        VBOColorHandle = GL15.glGenBuffers();
    }
}