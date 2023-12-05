package lin.xposed.hook.util.qq;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.ConstructorUtils;
import lin.util.ReflectUtils.MethodTool;
import lin.xposed.hook.util.ToastTool;

public class SendMsgUtils {


    /**
     * 发送一条消息
     *
     * @param contact     发送联系人 通过 getContact方法创建
     * @param elementList 元素列表 通过 {@link CreateElement}创建元素
     */
    public static void sendMsg(Object contact, ArrayList elementList) {
        if (contact == null) {
            ToastTool.show("contact==null");
            return;
        }
        if (elementList == null) {
            ToastTool.show("elementList==null");
            return;
        }
        Class<?> IMsgServiceClass = ClassUtils.getClass("com.tencent.qqnt.msg.api.IMsgService");
        Object msgServer = QQEnvTool.getQRouteApi(IMsgServiceClass);
        MethodTool.find(msgServer.getClass())
                .params(ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.Contact"), ArrayList.class, ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.IOperateCallback"))
                .returnType(void.class)
                .name("sendMsg")
                .call(msgServer, contact, elementList, Proxy.newProxyInstance(ClassUtils.getHostLoader(), new Class[]{ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.IOperateCallback")}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // void onResult(int i2, String str);
                        return null;
                    }
                }));

        /*int random = (int) Math.random();
        Object c = XposedHelpers.callStaticMethod(ClassUtils.getClass("com.tencent.qqnt.msg.g"), "c");
        XposedHelpers.callMethod(c, "sendMsg", new Class[]{
                        long.class,
                        ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.Contact"),
                        ArrayList.class,
                        HashMap.class,
                        ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.IOperateCallback")
                },
                random, contact, elementList, new HashMap<>(),
                //代理此接口
                Proxy.newProxyInstance(ClassUtils.getHostLoader(),
                        new Class[]{ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.IOperateCallback")}, new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                return null;
                            }
                        }));*/
    }

    /**
     * 获取好友聊天对象
     */
    public static Object getFriendContact(String uin) {
        return getContact(1, uin);
    }

    /**
     * 获取群聊聊天对象
     */
    public static Object getGroupContact(String uin) {
        return getContact(2, uin);
    }

    /**
     * @param type    联系人类型 2是群聊 1是好友
     * @param peerUid 正常的QQ号/群号
     */
    public static Object getContact(int type, String peerUid) {
        Class<?> aClass = ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.Contact");
        try {
            return ConstructorUtils.newInstance(aClass, new Class[]{int.class, String.class, String.class}, type, (type != 2 && type != 4 && isNumeric(peerUid)) ? QQEnvTool.getUidFromUin(peerUid) : peerUid, "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getContact(int type, String peerUid, String guildId) {
        Class<?> aClass = ClassUtils.getClass("com.tencent.qqnt.kernel.nativeinterface.Contact");
        try {
            return ConstructorUtils.newInstance(aClass, new Class[]{int.class, String.class, String.class}, type, (type != 2 && type != 4 && isNumeric(peerUid)) ? QQEnvTool.getUidFromUin(peerUid) : peerUid, guildId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getAIOContact(int chatType, String peerUid, String guildId, String nick) {
        try {
            return ConstructorUtils.newInstance(CommonClass.getAIOContactClass(), new Class[]{int.class, String.class, String.class, String.class}, chatType, (chatType != 2 && chatType != 4 && isNumeric(peerUid)) ? QQEnvTool.getUidFromUin(peerUid) : peerUid, guildId, nick);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) return false;
        }
        return true;
    }
}
