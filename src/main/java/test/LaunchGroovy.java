package test;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LaunchGroovy {

    public static final String STARTUPCLASSNAME = "test.Start";
    public static final String STARTUPMETHOD = "startWithRelProxy";

    @SuppressWarnings("unchecked")
    public void launch(String[] urls) {
        try {
            // create initial Ticker.groovy
            Files.write(Paths.get("groovy-src/test/Ticker.groovy"), 
            ("package test\n"+
            "class Ticker implements ITicker {\n"+
            " void tick() {\n"+
            "  myMethod(0, null)\n"+
            " }\n"+
            " void myMethod(int x, String job) {\n"+
            " }\n"+
            "}").getBytes(StandardCharsets.UTF_8));            
            // create script engine
            GroovyScriptEngine groovyEngine = new GroovyScriptEngine(
                    urls,
                    LaunchGroovy.class.getClassLoader()
            );

            // invoke start class and pass the script engine
            Class<GroovyObject> clazz = groovyEngine.loadScriptByName(
                    STARTUPCLASSNAME.replaceAll("\\.", "/").concat(".groovy")
            );
            GroovyObject startup = clazz.newInstance();
            startup.invokeMethod(STARTUPMETHOD, groovyEngine);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: LaunchGroovy <script location> [extra locations]");
            System.exit(1);
        }

        new LaunchGroovy().launch(args);
    }
    
}
