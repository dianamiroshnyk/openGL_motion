package com.example.opengl_motion;

import android.opengl.GLES31;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private FloatBuffer vertexBuffer;
    private final int mProgram;

    private int positionHandle;
    private int colorHandle;
    private int mvpMatrixHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {   // in counterclockwise order:
            0.1f,  0.0f, 0.0f,
            -0.8f, -0.8f, 0.0f,
            0.8f, -0.8f, 0.0f
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES31.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES31.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES31.glCreateProgram();

        // add the vertex shader to program
        GLES31.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES31.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES31.glLinkProgram(mProgram);
    }
    public void draw(float[] mvpMatrix) {
        GLES31.glUseProgram(mProgram);

        positionHandle = GLES31.glGetAttribLocation(mProgram, "vPosition");
        GLES31.glEnableVertexAttribArray(positionHandle);
        GLES31.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES31.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        colorHandle = GLES31.glGetUniformLocation(mProgram, "vColor");
        GLES31.glUniform4fv(colorHandle, 1, color, 0);

        //pass mvp matrix into shader
        mvpMatrixHandle = GLES31.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES31.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, vertexCount);
        GLES31.glDisableVertexAttribArray(positionHandle);
    }

}
