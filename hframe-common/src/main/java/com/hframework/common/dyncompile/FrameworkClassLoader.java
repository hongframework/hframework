package com.hframework.common.dyncompile;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangquanhong on 2017/3/14.
 */
public class FrameworkClassLoader extends ClassLoader {
    private  final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private  final FileManager manager = new FileManager(compiler);  //自定义FileManager

    public FrameworkClassLoader() {
        //共享FrameworkClassLoader的ClassLoader
        super(FrameworkClassLoader.getSystemClassLoader());
    }
    FrameworkClassLoader(ClassLoader cl) {
        super(cl);
    }

    public Class<?> compileClass(String name, String code) throws MalformedURLException, ClassNotFoundException {

        List<Source> list = new ArrayList<Source>();
        list.add(new Source(name, JavaFileObject.Kind.SOURCE, code)); //输入
        StringWriter stringWriter = new StringWriter();

//        System.out.println(System.getProperty("user.dir"));
        DiagnosticCollector diagnosticCollector = new DiagnosticCollector();
        compiler.getTask(stringWriter, manager,diagnosticCollector , new ArrayList<String>(){{
//            add("-classpath");
//            add("//D:/my_workspace/hframe-trunk/hframe-beans/target/classes");
//            add("-d");
//            add("D:/my_workspace/hframe-trunk/hframe-beans/target/classes/com/hframework/common/dyncompile");
        }}, null, list).call();
        Output mc = manager.getOutput(name);    //输出
        List diagnostics = diagnosticCollector.getDiagnostics();
        if(diagnostics != null && diagnostics.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Object diagnostic : diagnostics) {
                sb.append(diagnostic);
//                System.out.println(diagnostic);
            }
            throw new RuntimeException(sb.toString());
        }
        if (mc != null) {
            byte[] array = mc.toByteArray();  //转换成byte[]
            return defineClass(name, array, 0, array.length);  //需要继承ClassLoader
        }
        return null;
    }

    public Class<?> compileClass(List<String> names, List<String> code) throws MalformedURLException, ClassNotFoundException {

        List<Source> list = new ArrayList<Source>();
        for (String name : names) {
            list.add(new Source(name, JavaFileObject.Kind.SOURCE, code.get(names.indexOf(name)))); //输入
        }

        StringWriter stringWriter = new StringWriter();
        compiler.getTask(stringWriter, manager, null, new ArrayList<String>(){{
//            add("-d");
//            add("D:/my_workspace/hframe-trunk/hframe-beans/target/classes");
//            add("-classpath");
//            add("D:/my_workspace/hframe-trunk/hframe-beans/target/classes");
        }}, null, list).call();
        Output mc = manager.getOutput(names.get(names.size() - 1));    //输出
        if (mc != null) {
            byte[] array = mc.toByteArray();  //转换成byte[]
            return defineClass(names.get(names.size() - 1), array, 0, array.length);  //需要继承ClassLoader
        }
        return null;
    }

//    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
//        String strjava = "public class TestJava{public static void main(String[] args){System.out.println(\"nihao\");}}";
//        Class<?> testJava = new FrameworkClassLoader().compileClass("TestJava", strjava);
//        System.out.println(testJava.getName());
//    }

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String strjava = "package com.hframework.common.dyncompile;\n" +
                "public class TestSpringContextExecutor extends AbstractSpringContextExecutor implements SpringContextExecutor {\n" +
                "    public Integer execute() {\n" +
                "        return Integer.valueOf(dao.query(\"1\",\"1\",\"1\"));\n" +
                "    }\n" +
                "}\n";
        Class<?> testJava = new FrameworkClassLoader().compileClass("com.hframework.common.dyncompile." + "TestSpringContextExecutor", strjava);


        System.out.println(((SpringContextExecutor)testJava.newInstance()).execute());
    }

//    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
//        Class<?> testJava = new FrameworkClassLoader().compileClass(new ArrayList<String>(){{
//            add("SpringContextExecutor");
//            add("AbstractSpringContextExecutor");
//            add("TestSpringContextExecutor");
//        }}, new ArrayList<String>(){{
//            add("package com.hframework.common.dyncompile.demo;\n" +
//                    "public interface SpringContextExecutor {\n" +
//                    "\n" +
//                    "\n" +
//                    "    public Integer execute();\n" +
//                    "}\n");
//            add("package com.hframework.common.dyncompile.demo;\n" +
//                    "public abstract class AbstractSpringContextExecutor {\n" +
//                    "    protected GenericDAO dao = new GenericDAO();\n" +
//                    "\n" +
//                    "    public static class GenericDAO{\n" +
//                    "        public String query(String tableName, String keyColumn, String returnColumn) {\n" +
//                    "            return \"123456\";\n" +
//                    "        }\n" +
//                    "    }\n" +
//                    "}\n");
//            add("package com.hframework.common.dyncompile.demo;\n" +
//                    "public class TestSpringContextExecutor extends AbstractSpringContextExecutor implements SpringContextExecutor {\n" +
//                    "    public Integer execute() {\n" +
//                    "        return Integer.valueOf(dao.query(\"1\",\"1\",\"1\"));\n" +
//                    "    }\n" +
//                    "}\n");
//        }});
//
//
//        System.out.println(((SpringContextExecutor)testJava.newInstance()).execute());
//    }
}
