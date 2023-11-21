package lin.xposed.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtils {
    /*
    *
    *
    * public class ImageTypeUtils {
 常用文件的文件头如下：(以前六位为准)
JPEG (jpg)，文件头：FFD8FF
PNG (png)，文件头：89504E 47
GIF (gif)，文件头：474946 38
TIFF (tif)，文件头：49492A 00
Windows Bitmap (bmp)，文件头：424D
CAD (dwg)，文件头：41433130
Adobe Photoshop (psd)
* */
    public static String getImageType(String imgUrl) {
        if (imgUrl.startsWith("http")) {
            BufferedInputStream bis = null;
            HttpURLConnection urlconnection = null;
            String prefix;
            try {
                URL url = new URL(imgUrl);
                urlconnection = (HttpURLConnection) url.openConnection();
                urlconnection.connect();
                bis = new BufferedInputStream(urlconnection.getInputStream());
                String imageTypes = HttpURLConnection.guessContentTypeFromStream(bis);
                if (imageTypes.contains("/")) {
                    prefix = imageTypes.substring(imageTypes.lastIndexOf("/"));
                    prefix = prefix.replaceFirst("/", ".");
                    return prefix;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (urlconnection != null) {
                        urlconnection.disconnect();
                    }
                } catch (IOException ignored) {

                }
            }
            return ".jpg";
        } else {
            BufferedInputStream in;
            try {
                in = new BufferedInputStream(new FileInputStream(imgUrl));//输入容器
                byte[] bytePrefix = new byte[6];
                in.read(bytePrefix);
                in.close();
                return getImageType(bytePrefix);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getImageType(byte[] hexPrefix) {
        //以前六位为准即可
        String hex = bytesToHex(hexPrefix);
        if (hex.startsWith("FFD8FF")) return ".jpg";
        if (hex.startsWith("89504E")) return ".png";
        if (hex.startsWith("474946")) return ".gif";
        if (hex.startsWith("49492A")) return ".tif";
        if (hex.startsWith("424D")) return ".bmp";
        if (hex.startsWith("414331")) return ".dwg";
        else return "非记录在内的图像格式";
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
    /**
     * 图片缩放比例
     */
    private static final float BITMAP_SCALE = 0.4f;
    /**
     * 最大模糊度(在0.0到25.0之间)
     */
    private static final float BLUR_RADIUS = 15f;

    /**
     * 模糊图片的具体方法
     *
     * @param context   上下文对象
     * @param image     需要模糊的图片
     * @return          模糊处理后的图片
     */
    public static Bitmap blur(Context context, Bitmap image) {
        // 计算图片缩小后的长宽
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        // 将缩小后的图片做为预渲染的图片。
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        // 创建一张渲染后的输出图片。
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(BLUR_RADIUS);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }


}
