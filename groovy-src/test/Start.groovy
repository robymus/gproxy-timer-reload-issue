package test

import com.innowhere.relproxy.gproxy.GProxy
import com.innowhere.relproxy.gproxy.GProxyConfig
import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine

class Start extends TimerTask {

    ITicker ticker
   
    /**
     * Starts up with RelProxy dynamic reloading
     */
    public void startWithRelProxy(GroovyScriptEngine groovyEngine) {
        GProxyGroovyScriptEngine gproxyGroovyEngine = {
            String scriptName -> return (java.lang.Class) groovyEngine.loadScriptByName(scriptName)
        } as GProxyGroovyScriptEngine

        GProxyConfig gpConfig = GProxy.createGProxyConfig()
        gpConfig.setEnabled(true).setGProxyGroovyScriptEngine(gproxyGroovyEngine)
        GProxy.init(gpConfig)

        // create GProxy proxy
        ticker = GProxy.create(new Ticker(), ITicker.class)

        // create a timer
        Timer timer = new Timer()
        timer.scheduleAtFixedRate(this, 0, 1000)

	// overwrite Ticker.groovy
	overwriteTicker(1)

	// and wait for timer to activate
	Thread.sleep(1500)
    }

    @Override
    void run() {
        ticker.tick()
    }


    /**
     * Write a new Ticker.groovy
     */
    void overwriteTicker(val) {
        new File('groovy-src/test/Ticker.groovy').write(
"""package test
class Ticker implements ITicker {
 void tick() {
  myMethod(${val}, null)    
 }
 void myMethod(int x, String job) {
 }
}""")
    }

    
}
