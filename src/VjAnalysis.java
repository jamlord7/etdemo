import FlowDroid.FlowDroid;
import Model.Config;
import Util.SerializationUtil;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class VjAnalysis {
    public static void main(String[] args) throws IOException, XmlPullParserException {
        LocalDateTime vjAnalysisStart = LocalDateTime.now();
        //
        String vjApkName = args[0];
        Config.appName = args[1];
        String resultDirectoryPath = "./out/" + Config.appName + "/";
        FlowDroid.v().initFlowDroid(vjApkName);
        FlowDroid.v().buildCallGraph(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        InfoflowResults versionedTaintResults = FlowDroid.v().runTaintAnalysis(Config.sourcesAndSinksPath);
        //
        LocalDateTime vjAnalysisEnd = LocalDateTime.now();

        SerializationUtil.WriteInfoFlowResultsToFile(ChronoUnit.SECONDS.between(vjAnalysisStart, vjAnalysisEnd), versionedTaintResults, resultDirectoryPath + Config.appName + "-vj-result.txt");

    }
}
