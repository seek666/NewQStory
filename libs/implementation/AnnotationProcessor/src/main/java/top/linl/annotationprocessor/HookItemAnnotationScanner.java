package top.linl.annotationprocessor;


import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * 用来自动处理注解
 * 以便不用手动添加需要加载的Hook类
 * 在运行时会自动扫描并加载
 */
@AutoService(Processor.class)
//自动创建\resources\META-INF\services\javax.annotation.processing.Processor写入当前类类名
@SupportedSourceVersion(SourceVersion.RELEASE_17)//版本
@SupportedAnnotationTypes("lin.xposed.hook.annotation.HookItem")//指定只处理哪个注解 如果要处理所有的注解填*
public class HookItemAnnotationScanner extends AbstractProcessor {
    private Map<String, String> AnnotatedList;

    //获取该注解对象的属性值
    public static Object getAnnotationValue(Annotation annotation, String property) {
        Object result = null;
        if (annotation != null) {
            InvocationHandler invo = Proxy.getInvocationHandler(annotation); //获取被代理的对象
            Map map = (Map) getFieldValue(invo, "memberValues");
            if (map != null) {
                result = map.get(property);
            }
        }
        return result;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public static <T> Object getFieldValue(T object, String property) {
        if (object != null && property != null) {
            Class<T> currClass = (Class<T>) object.getClass();

            try {
                Field field = currClass.getDeclaredField(property);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(currClass + " has no property: " + property);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void addAsClassArray(StringBuilder builder) {
        //写文件头
        builder.append("package " + AnnotationClassNameTools.CLASS_PACKAGE + ";\n\n");
        //写必要依赖
        builder.append("import lin.xposed.hook.load.base.BaseHookItem;\n");
        //写import(其实没有import全类名也可以导)
        for (Map.Entry<String, String> entry : AnnotatedList.entrySet()) {
            String entryKey = entry.getKey();
            builder.append("import ").append(entryKey).append(";\n");
        }
        builder.append("public class " + AnnotationClassNameTools.CLASS_NAME + " {\n\n");

        //array
        builder.append("\tpublic static final BaseHookItem[] allHookItemClass = {\n\t\t\t");
        for (Map.Entry<String, String> entry : AnnotatedList.entrySet()) {
            builder.append("new ").append(entry.getKey()).append("(),\n\t\t\t");
//            builder.append(entry.getKey()).append(".class,\n\t\t\t");
        }
        builder.append("};");
        //array end

        //build time
        builder.append("\n\tpublic static final String BUILD_TIME =\"").append(getTime()).append("\";");

        builder.append("\n}\n");

    }


    public static String getTime() {
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd"),
                df3 = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String TimeMsg1 = df1.format(calendar.getTime()),
                TimeMsg3 = df3.format(calendar.getTime());
        if (TimeMsg1.contains("-0")) {
            TimeMsg1 = TimeMsg1.replace("-0", "-");
        }
        return TimeMsg1 + " " + TimeMsg3;
    }

    /**
     * @param annotations 所有注解
     * @param roundEnv    environment for information about the current and prior round
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("[start] Start building annotated Hook project class index");

        StringBuilder builder = new StringBuilder();

        for (TypeElement element : annotations) {
            AnnotatedList = getAnnotatedClassList(element, roundEnv);
        }
        addAsClassArray(builder);

        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile(AnnotationClassNameTools.CLASS_PACKAGE + "." + AnnotationClassNameTools.CLASS_NAME);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
        System.out.println("[End]Index classes have been built for all Hook projects");
        return true;
    }

    private Map<String, String> getAnnotatedClassList(TypeElement elements,RoundEnvironment roundEnv) {
        HashMap<String, String> map = new HashMap<>();
        // 获取所有被该注解 标记过的实例
        Set<? extends Element> typeElements = roundEnv.getElementsAnnotatedWith(elements);

        for (Element element : typeElements) {
            //获取被注解的成员变量
            TypeElement typeElement = (TypeElement) element;
            try {
                Annotation annotation = typeElement.getAnnotation((Class<? extends Annotation>) Class.forName("lin.xposed.hook.annotation.HookItem"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //获取全类名
            String className = typeElement.getQualifiedName().toString();
           /* Annotation itemPathAnnotation = annotatedElement.getAnnotations()[0];
            String itemPath = (String) getAnnotationValue(itemPathAnnotation, "value");
            System.out.println(itemPath);*/
            /*//获取被注解元素的包名
            String packageName = element.getPackageOf(element).getQualifiedName().toString();
            //取到这个注解元素的包
            String packageName = element.getEnclosingElement().toString();
            //获取并拼接被注解的类名
            String className = packageName + "." + element.getSimpleName();*/
            System.out.println("[HookItem]" + className);
            map.put(className, null);
        }
        return map;
    }
}
