import Model.Config;
import FlowDroid.FlowDroid;
import Util.SerializationUtil;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ViAnalysis {
    public static void main(String[] args) throws IOException, XmlPullParserException {
        LocalDateTime viAnalysisStart = LocalDateTime.now();
        //
        String viApkName = args[0];
        Config.appName = args[1];
        String resultDirectoryPath = "./out/" + Config.appName + "/";
        FlowDroid.v().initFlowDroid(viApkName);
        FlowDroid.v().buildCallGraph(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        InfoflowResults originalTaintResults = FlowDroid.v().runTaintAnalysis(Config.sourcesAndSinksPath);
        //
        LocalDateTime viAnalysisEnd = LocalDateTime.now();
        SerializationUtil.WriteInfoFlowResultsToFile(ChronoUnit.SECONDS.between(viAnalysisStart, viAnalysisEnd), originalTaintResults, resultDirectoryPath + Config.appName + "-vi-result.txt");

    }
}
