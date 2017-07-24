package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.input.GLTextureOutputRenderer;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.nio.FloatBuffer;

/**
 * resize camera's texture to a 2D texture
 * if the ratio is different, then the content will be trimmed
 */
public class VideoResizeFilter extends BasicFilter{

    private OffScreenTexture _FilterTarget;
    public static final String LogTag = "VideoResizer";
    private static final int videoMaxWidth = 1080;
    private static final int videoMaxHeight = 1920;

    public static class ShaderProgram {
        public int program;
        public HashMap<String, Integer> attribLocations;
        public HashMap<String, Integer> uniformLocations;

        public ShaderProgram(int pg) {
            program = pg;
            attribLocations = new HashMap<>();
            uniformLocations = new HashMap<>();
        }

        public void SetActive() {
            if (program > 0) {
                GLES20.glUseProgram(program);
            }
        }

        public void Release() {
            if (program > 0) {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
    }

    public static class OffScreenTexture {
        public int texture;
        public int framebuffer;
        public int width;
        public int height;

        public static boolean BindTextureToFrameBuffer(int texid, int framebuffer) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texid);
            // make the frame buffer become the current rendering target
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, texid, 0);

            int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
            if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                Log.e(LogTag, "binding texture to frame buffer failed: " + status);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                return false;
            }
            return true;
        }

        public boolean MakeAsRenderingTarget() {
            if (BindTextureToFrameBuffer(texture, framebuffer)) {
                GLES20.glViewport(0, 0, width, height);
                return true;
            }
            return false;
        }

        public void ReleaseAsRenderingTarget() {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        public void Release() {
            ReleaseFrameBuffer(framebuffer);
            ReleaseTexture(texture);
            framebuffer = 0;
            texture = 0;
        }
    }

    public VideoResizeFilter (Point targetSize) {
        _targetSize = new Point(targetSize.x, targetSize.y);
        Matrix.setIdentityM(_identityMatrix, 0);
    }

    public static int LoadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == GLES20.GL_FALSE) {
            Log.e(LogTag, "Could not compile shader " + shaderType + ":");
            Log.e(LogTag, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        return shader;
    }

    public static int CreateProgram(String vertexSource, String fragmentSource) {
        int vertexShader = LoadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = LoadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            GLES20.glDeleteShader(vertexShader);
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program == 0) {
            Log.e(LogTag, "Could not create program");
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(pixelShader);
            return 0;
        }

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, pixelShader);

        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(LogTag, "Could not link program: ");
            Log.e(LogTag, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }

        return program;
    }

    /**
     * this is a simplest shader to render a texture 2D to target frame buffer
     */
    public static ShaderProgram CreateTextureRenderShader(boolean hasTexMatrix, boolean externalTexture) {
        String vertexShader =
                (hasTexMatrix ? "uniform mat4 uTexMatrix;" : "") +
                        "attribute vec4 aPosition;" +
                        "attribute vec4 aTextureCoord;" +
                        "varying vec2 vTextureCoord;" +
                        "void main() {" +
                        "    gl_Position = aPosition;" +
                        (hasTexMatrix ? "vTextureCoord = (uTexMatrix * aTextureCoord).xy;" : "vTextureCoord = aTextureCoord.xy;") +
                        "}";

        String fragmentShader =
                (externalTexture ? "#extension GL_OES_EGL_image_external : require\n" : "") +
                        "precision mediump float;" +
                        "varying vec2 vTextureCoord;" +
                        (externalTexture ? "uniform samplerExternalOES input_tex;" : "uniform sampler2D input_tex;") +
                        "void main() {" +
                        "    gl_FragColor = texture2D(input_tex, vTextureCoord.st);" +
                        "}";

        int program = CreateProgram(vertexShader, fragmentShader);
        if (program <= 0) {
            return null;
        }

        ShaderProgram shader = new ShaderProgram(program);
        shader.attribLocations.put("vpos", GLES20.glGetAttribLocation(program, "aPosition"));
        shader.attribLocations.put("vtex", GLES20.glGetAttribLocation(program, "aTextureCoord"));
        if (hasTexMatrix) {
            shader.uniformLocations.put("texmat", GLES20.glGetUniformLocation(program, "uTexMatrix"));
        }
        if (!externalTexture) {
            // external texture don't need to be bound before using it
            shader.uniformLocations.put("utex0", GLES20.glGetUniformLocation(program, "input_tex"));
        }
        return shader;
    }

    public static int CreateTexture(int textureTarget, int minFilter, int magFilter, int wrapS, int wrapT) {
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        GLES20.glBindTexture(textureTarget, textureHandle[0]);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter); //线性插值
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, wrapS);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, wrapT);
        return textureHandle[0];
    }

    public static void ReleaseTexture(int textureId) {
        int[] textureHandle = new int[1];
        textureHandle[0] = textureId;
        GLES20.glDeleteTextures(1, textureHandle, 0);
    }

    public static int CreateFrameBuffer() {
        int[] pbHandle = new int[1];
        GLES20.glGenFramebuffers(1, pbHandle, 0);
        return pbHandle[0];
    }

    public static void ReleaseFrameBuffer(int fb) {
        int[] handles = new int[1];
        handles[0] = fb;
        GLES20.glDeleteFramebuffers (1, handles, 0);
    }

    public static int CreateTexture2DWithImageData(int width, int height, int fmt, byte[] data) {
        int texid = CreateTexture(GLES20.GL_TEXTURE_2D,
                GLES20.GL_LINEAR, GLES20.GL_LINEAR,
                GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
        if (texid > 0) {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, fmt, width, height, 0, fmt, GLES20.GL_UNSIGNED_BYTE,
                    data == null ? null : ByteBuffer.wrap(data));
            int error = GLES20.glGetError();
            if (error != GLES20.GL_NO_ERROR) {
                ReleaseTexture(texid);
                texid = 0;
            }
        }
        return texid;
    }
    /**
     * OffScreenTexture used for drawing on a framebuffer with a texture
     */
    public static OffScreenTexture CreateOffScreenTexture(int width, int height, int fmt) {
        int framebuffer = CreateFrameBuffer();
        int texture = CreateTexture2DWithImageData(width, height, fmt, null);
        if (texture == 0) {
            ReleaseFrameBuffer(framebuffer);
            return null;
        }

        OffScreenTexture offscreen = new OffScreenTexture();
        offscreen.texture = texture;
        offscreen.framebuffer = framebuffer;
        offscreen.width = width;
        offscreen.height = height;
        return offscreen;
    }

    public void InitResources() {
        if (_shader == null) {
            _shader = CreateTextureRenderShader(true, true);
        }
    }

    public void ReleaseResources() {
        if (_shader != null) {
            _shader.Release();
            _shader = null;
        }
    }

    public void SetTargetSize(int x, int y)
    {
        _targetSize.x = x;
        _targetSize.y = y;
    }

    public void SetFilterTarget(OffScreenTexture offScreenTex)
    {
        _FilterTarget = offScreenTex;
    }

    @Override
    public synchronized void newTextureReady(int texture, GLTextureOutputRenderer source, boolean newData) {
        int textureid[] = new int[1];
        drawFrame(texture, source.getWidth(), source.getHeight(), null, textureid, _targetSize.x, _targetSize.y);
        texture_id = textureid[0];
        encWidth = _targetSize.x;
        encHeight = _targetSize.y;
    }

    /*
        SurfaceTexture surface = _textureController.GetSurfaceTexture();
        float[] matrix = new float[16];
        surface.getTransformMatrix(matrix);
    */
    public void drawFrame(int srcTextureid, int srcWidth, int srcHeight, float[] texMatrix, int[] destTextureid, int destWidth, int destHeight) {
        if (_shader == null) {
            return;
        }

        if (_texFloatBuffer == null || srcWidth != _inputSize.x || srcHeight != _inputSize.y ||
                _targetSize.x != _FilterTarget.width || _targetSize.y != _FilterTarget.height) {
            // reset the texture coords
            // camera's output is usually rotated with the target orientation
            // we use camera's output matrix to rotate to the target texture
            _inputSize.x = srcWidth;
            _inputSize.y = srcHeight;
            CreateTextureCoords(srcWidth, srcHeight);
        }

        if(_FilterTarget == null) {
            _FilterTarget = CreateOffScreenTexture(videoMaxWidth, videoMaxHeight, GLES20.GL_RGBA);
        }
        _FilterTarget.MakeAsRenderingTarget();

        // render
        _shader.SetActive();

        int vposidx = _shader.attribLocations.get("vpos");
        int vtexidx = _shader.attribLocations.get("vtex");
        int texmat = _shader.uniformLocations.get("texmat");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, srcTextureid);

        if (texMatrix == null) {
            GLES20.glUniformMatrix4fv(texmat, 1, false, _identityMatrix, 0);
        }
        else {
            GLES20.glUniformMatrix4fv(texmat, 1, false, texMatrix, 0);
        }

        DrawRectangle(vposidx, vtexidx, _fullRectPosFloatBuffer, _texFloatBuffer);

        _FilterTarget.ReleaseAsRenderingTarget();

        destTextureid[0] = _FilterTarget.texture;
    }

    /**
     * A helper function for drawing one rectangle
     */
    public static void DrawRectangle(int verIdx, int texIdx, FloatBuffer vertexCoords, FloatBuffer texCoords) {
        GLES20.glEnableVertexAttribArray(verIdx);
        GLES20.glVertexAttribPointer(verIdx, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexCoords);

        GLES20.glEnableVertexAttribArray(texIdx);
        GLES20.glVertexAttribPointer(texIdx, 2, GLES20.GL_FLOAT, false, 2 * 4, texCoords);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(verIdx);
        GLES20.glDisableVertexAttribArray(texIdx);
    }
    private void CreateTextureCoords(int inputWidth, int inputHeight) {
        // being carefully, the target is the rotated result
        double ratioDelta = Math.abs((float)inputWidth / (float)inputHeight) -
                Math.abs((float)_targetSize.y / (float)_targetSize.x);
        ratioDelta = Math.abs(ratioDelta);
        if (ratioDelta <= 0.1f) {
            // just scale to the target
            _texFloatBuffer = CreateFloatBuffer(FullRectTexCoords);
        }
        else {
            // trim the input picture on both sides to fit into the target size
            float expectedHeight = (float)_targetSize.x / (float) _targetSize.y * inputWidth;
            float trimPercent = (1.0f - expectedHeight / (float)inputHeight) / 2;
            float texCoords[] = {
                trimPercent, 0.0f,          // 0 bottom left
                1.0f - trimPercent, 0.0f,   // 1 bottom right
                trimPercent, 1.0f,          // 2 top left
                1.0f - trimPercent, 1.0f    // 3 top right
            };

            _texFloatBuffer = CreateFloatBuffer(texCoords);
        }
    }

    public static FloatBuffer CreateFloatBuffer(float[] fdata) {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        ByteBuffer bb = ByteBuffer.allocateDirect(fdata.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(fdata);
        fb.position(0);
        return fb;
    }

    private ShaderProgram _shader;
    private Point _targetSize;
    private Point _inputSize = new Point(0, 0);
    private FloatBuffer _texFloatBuffer;
    private float[] _identityMatrix = new float[16];
    /**
     * base vertex and texture coord for drawing a rect on rendering target
     */
    public static final float FullRectVertexCoords[] = {
            -1.0f, -1.0f,   // 0 bottom left
            1.0f, -1.0f,   // 1 bottom right
            -1.0f,  1.0f,   // 2 top left
            1.0f,  1.0f,   // 3 top right
    };

    public static final float FullRectTexCoords[] = {
            0.0f, 0.0f,     // 0 bottom left
            1.0f, 0.0f,     // 1 bottom right
            0.0f, 1.0f,     // 2 top left
            1.0f, 1.0f      // 3 top right
    };

    private static final FloatBuffer _fullRectPosFloatBuffer =
            CreateFloatBuffer(FullRectVertexCoords);
}
