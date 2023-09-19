import AnalysisService.IncrementalAnalysis;
import AnalysisService.IncrementalAnalysis1;
import Model.Config;

import java.io.IOException;

public class EvotaintAnalysis1 {
    public static void main(String[] args) throws IOException {
        Config.appName = args[2];
        IncrementalAnalysis1.perform(args[0], args[1]);
    }
}
