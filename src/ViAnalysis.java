import Model.Config;
import FlowDroid.FlowDroid;
import Util.SerializationUtil;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.IOException;

public class ViAnalysis {
    public static void main(String[] args) throws IOException, XmlPullParserException {
        String viApkName = args[0];
        Config.appName = args[1];
        String resultDirectoryPath = "./out/" + Config.appName + "/";
        FlowDroid.v().initFlowDroid(viApkName);
        FlowDroid.v().buildCallGraph(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        InfoflowResults originalTaintResults = FlowDroid.v().runTaintAnalysis(Config.sourcesAndSinksPath);
        SerializationUtil.WriteInfoFlowResultsToFile(originalTaintResults, resultDirectoryPath + Config.appName + "-vi-result.txt");
    }
}
