import AnalysisService.IncrementalAnalysis;
import AnalysisService.IncrementalAnalysis1;
import AnalysisService.IncrementalAnalysis2;
import Model.Config;

import java.io.IOException;

public class EvotaintAnalysis {
    public static void main(String[] args) throws IOException {
        Config.appName = args[2];
        IncrementalAnalysis.perform(args[0], args[1]);
    }
}
