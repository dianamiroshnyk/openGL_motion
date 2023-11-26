package com.example.opengl_motion;

import android.opengl.GLES31;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

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
    private ShortBuffer drawListBuffer;
    private final int mProgram;

    private int positionHandle;
    private int colorHandle;
    private int mvpMatrixHandle;

    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private static final int COORDS_PER_VERTEX = 3;
    private static float squareCoords[] = {
            -0.5f,  0.9f, 0.0f,  // верхний левый угол
            -0.5f, 0.1f, 0.0f,  // нижний левый угол
            0.5f,  0.1f, 0.0f,  // нижний правый угол
            0.5f,  0.9f, 0.0f   // верхний правый угол
    };
    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Square() {
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES31.glCreateProgram();
        GLES31.glAttachShader(mProgram, vertexShader);
        GLES31.glAttachShader(mProgram, fragmentShader);
        GLES31.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES31.glUseProgram(mProgram);
        positionHandle = GLES31.glGetAttribLocation(mProgram, "vPosition");
        GLES31.glEnableVertexAttribArray(positionHandle);

        GLES31.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX,
                GLES31.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // Set color
        colorHandle = GLES31.glGetUniformLocation(mProgram, "vColor");
        GLES31.glUniform4fv(colorHandle, 1, color, 0);

        //pass mvp matrix into shader
        mvpMatrixHandle = GLES31.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES31.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES31.glDrawArrays(GLES31.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES31.glDisableVertexAttribArray(positionHandle);
    }
}
