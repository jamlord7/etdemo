import AnalysisService.IncrementalAnalysis;
import Model.Apk;
import Model.Config;

import java.io.IOException;

public class EvotaintAnalysis {
    public static void main(String[] args) throws IOException {
        Config.appName = args[2];
        IncrementalAnalysis.perform(args[0], args[1]);
    }
}
