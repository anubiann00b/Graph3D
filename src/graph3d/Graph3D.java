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
    final int VERTICES = 72;
    
    FloatBuffer positionBuffer;
    FloatBuffer colorBuffer;
    
    private void loadData() {
        positionBuffer.put(new float[] {
            5, 5, 0, 0, 5, 0, 0, 5, 5, 5, 5, 5,
            5, 0, 5, 0, 0, 5, 0, 0, 0, 5, 0, 0,
            5, 5, 5, 0, 5, 5, 0, 0, 5, 5, 0, 5,
            5, 0, 0, 0, 0, 0, 0, 5, 0, 5, 5, 0,
            0, 5, 5, 0, 5, 0, 0, 0, 0, 0, 0, 5,
            5, 5, 0, 5, 5, 5, 5, 0, 5, 5, 0, 0
        });
        colorBuffer.put(new float[] {
            1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1,
            1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1,
            1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1,
            1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1,
            1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1,
            1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1
        });
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
        GL11.glColorPointer(3, GL11.GL_FLOAT, 0, 0L);
        GL11.glDrawArrays(GL11.GL_QUADS, 0, VERTICES/3);
        GL11.glPopMatrix();
    }
    
    private void init() {
        positionBuffer = BufferUtils.createFloatBuffer(VERTICES);
        colorBuffer = BufferUtils.createFloatBuffer(VERTICES);
        
        GL11.glShadeModel(GL11.GL_SMOOTH); // Smoother textures.
        GL11.glClearDepth(1.0); // Buffer depth, allows objects to draw over things behind them.
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Depth testing (see above).
        GL11.glDepthFunc(GL11.GL_LEQUAL); // Type of depth testing.
        
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