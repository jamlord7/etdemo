import Model.Config;
import FlowDroid.FlowDroid;
import Util.SootMethodUtil;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.InfoflowConfiguration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws IOException, XmlPullParserException {
       Test test = new Test();
       //test.testExistingCallGraph("Todo_1.1.4_Apkpure.apk");
       test.testMethodsGet();
    }

    public void testExistingCallGraph(String apkname) throws XmlPullParserException, IOException {
        //SetupApplication app = new SetupApplication(Config.androidJarPath, Config.apkDirectory+"Todo_1.1.4_Apkpure.apk");
        FlowDroid.v().initFlowDroid(Config.apkDirectory + apkname);
        FlowDroid.v().buildCallGraph(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        var infoflow = FlowDroid.v().runTaintAnalysis(Config.sourcesAndSinksPath);

    }

    //public

    public void testMethodsGet(){
        Set<String> methods = new HashSet<>();
        int methodsCnt = SootMethodUtil.getFunctionLists(methods, null, "SendSMS-1.1.apk");
        System.out.println(methods.size());
    }
}
