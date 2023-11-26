package com.example.opengl_motion;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] rotationMatrix = new float[16];

    private Triangle mTriangle;
    private Square mSquare;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mTriangle = new Triangle();
        mSquare = new Square();
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        float[] scratch = new float[16];
        //set camera position
        Matrix.translateM(viewMatrix, 0, -1.5f, 1.5f, -5.0f);
        float[] mvpMatrix = new float[16];
        //multiply projection and view matrix
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        //create a rotation transformation for square
        long timeS = SystemClock.uptimeMillis() % 4000L;
        float angleS = 0.090f * ((int) timeS);
        Matrix.setRotateM(rotationMatrix, 0, angleS, 0, 0, -1.0f);
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

        mSquare.draw(scratch);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Create a rotation transformation for the triangle
        long timeT = SystemClock.uptimeMillis() % 4000L;
        float angleT = 0.090f * ((int) timeT);
        Matrix.setRotateM(rotationMatrix, 0, angleT, 0, 0, -1.0f);
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
        mTriangle.draw(scratch);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES31.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    public static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES31.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES31.GL_FRAGMENT_SHADER)
        int shader = GLES31.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES31.glShaderSource(shader, shaderCode);
        GLES31.glCompileShader(shader);

        return shader;
    }
}