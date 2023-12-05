package lin.xposed.hook.util.qq;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.robv.android.xposed.XposedHelpers;
import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.ConstructorUtils;
import lin.util.ReflectUtils.FieIdUtils;
import lin.xposed.common.utils.FileUtils;
import lin.xposed.common.utils.HttpUtils;


public class CreateElement {
    public static Object createTextElement(String text) {
        Object o = QQEnvTool.getQRouteApi(ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createTextElement", new Class[]{String.class}, text);
    }

    public static Object createPicElement(String url) {
        String path = getPicPath(url);
        Object o = QQEnvTool.getQRouteApi(ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createPicElement", new Class[]{String.class, boolean.class, int.class},
                path, true, 0);
    }

    public static Object createAtTextElement(String text, String uin, int atType) {//0不艾特1全体2个人
        Object o = QQEnvTool.getQRouteApi(ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createAtTextElement", new Class[]{String.class, String.class, int.class},
                text, uin, atType);
    }

    public static Object createReplyElement(long msgId) {
        Object o = QQEnvTool.getQRouteApi(ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createReplyElement", new Class[]{long.class},
                msgId);
    }

    public static Object createFileElement(String path) {
        Object o = QQEnvTool.getQRouteApi(ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createFileElement", new Class[]{String.class},
                path);
    }

    /*public static Object createPttElement(String url) {
        String path = getPttPath(url);
        Object o = QQEnvTool.getQRouteApi(ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        ArrayList<Byte> myList = new ArrayList<>(Arrays.asList(new Byte[]{28, 26, 43, 29, 31, 61, 34, 49, 51, 56, 52, 74, 41, 62, 66, 46, 25, 57, 51, 70, 33, 45, 39, 27, 68, 58, 46, 59, 59, 63}));
        return XposedHelpers.callMethod(o, "createPttElement", new Class[]{String.class, int.class, ArrayList.class},
                path, (int) MUtils.getAudioFileVoiceTime(path), myList);

    }*/

    public static Object createVideoElement(String path) {
        Object o = QQEnvTool.getQRouteApi(ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
        return XposedHelpers.callMethod(o, "createVideoElement", new Class[]{String.class},
                path);
    }


    public static Object createJsonGrayTipElement(String text, String url) {
        JSONObject jsonObject = new JSONObject();
        boolean empty = !(url.contains("http://") || url.contains("https://"));
        try {
            jsonObject.put("align", "center");
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("col", 3);
            jsonObject1.put("jp", url);
            jsonObject1.put("txt", text);
            jsonObject1.put("type", empty ? "nor" : "url");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject1);
            jsonObject.put("items", jsonArray);
            Object jsonGrayElement = ConstructorUtils.newInstance(ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.JsonGrayElement"),
                    new Class[]{
                            long.class,
                            String.class,
                            String.class,
                            boolean.class,
                            ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.XmlToJsonParam")
                    }, empty ? 1014 : 1015, jsonObject.toString(), "", false, null);

            Object grayTipElement = ConstructorUtils.newInstance(ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.GrayTipElement"));
            FieIdUtils.setField(grayTipElement, "subElementType", null, 17);
            FieIdUtils.setField(grayTipElement, "jsonGrayTipElement", null, jsonGrayElement);

            Object msgElement = ConstructorUtils.newInstance(ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.MsgElement"));
            XposedHelpers.callMethod(msgElement, "setElementType", new Class[]{int.class}, 8);
            XposedHelpers.callMethod(msgElement, "setGrayTipElement", new Class[]{ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.GrayTipElement")}, grayTipElement);
            return msgElement;
        } catch (Exception e) {
            Log.d("报错:createJsonGrayTipElement", String.valueOf(e));
            return null;
        }
    }

    public static Object createArkElement(String card) {
        Class<?> card_data = ClassUtils.getClass("com.tencent.qqnt.msg.data.b");
        try {
            Object card_data_object = card_data.newInstance();
            boolean o1 = (boolean) XposedHelpers.callMethod(card_data_object, "o", new Class[]{String.class}, card);
            if (!o1) {
                Log.d("报错", "卡片格式有问题:" + card);
                return null;
            }
            Object o = QQEnvTool.getQRouteApi(ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgUtilApi"));
            return XposedHelpers.callMethod(o, "createArkElement", new Class[]{card_data},
                    card_data_object);
        } catch (IllegalAccessException | InstantiationException e) {
            //throw new RuntimeException(e);
            return null;
        }
    }

    public static String getPttPath(String url) {
        String copyTo = Environment.getExternalStorageDirectory() + "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/" + QQEnvTool.getCurrentUin() + "/ptt/";
        if (url.toLowerCase().startsWith("http:") || url.toLowerCase().startsWith("https:")) {
            String mRandomPathName = (String.valueOf(Math.random())).substring(2);
            HttpUtils.fileDownload(url, copyTo + mRandomPathName + ".aac");
            copyTo += mRandomPathName + ".aac";
            //MUtils.copy(url,copyTo+new File(url).getName(),4096);
        } else {
            copyTo += new File(url).getName();
            try {
                FileUtils.copyFile(url, copyTo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return copyTo;
    }

    public static String getPicPath(String Path) {
        String mPath = Path.toLowerCase();
        if (mPath.startsWith("http:") || mPath.startsWith("https:")) {
            String mRandomPathName = (String.valueOf(Math.random())).substring(2);
            String mRandomPath = "/storage/emulated/0/TL_ing/Cache/";
            HttpUtils.fileDownload(Path, mRandomPath + mRandomPathName);
            return mRandomPath + mRandomPathName;
        } else {
            return Path;
        }
    }
}
