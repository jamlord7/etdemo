import FlowDroid.FlowDroid;
import Model.Config;
import Util.SerializationUtil;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.IOException;

public class VjAnalysis {
    public static void main(String[] args) throws IOException, XmlPullParserException {
        String vjApkName = args[0];
        Config.appName = args[1];
        String resultDirectoryPath = "./out/" + Config.appName + "/";
        FlowDroid.v().initFlowDroid(vjApkName);
        FlowDroid.v().buildCallGraph(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        InfoflowResults versionedTaintResults = FlowDroid.v().runTaintAnalysis(Config.sourcesAndSinksPath);
        SerializationUtil.WriteInfoFlowResultsToFile(versionedTaintResults, resultDirectoryPath + Config.appName + "-vj-result.txt");
    }
}
