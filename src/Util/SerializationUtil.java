package Util;

import Model.Result;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.*;

public class SerializationUtil {
    public static void WriteInfoFlowResultsToFile(Long actualTimeCost, InfoflowResults infoflowResults, String filePath) {
        File resultsOutputFile = new File(filePath);
        try {
            // if file does not exist, then create it.
            if (!resultsOutputFile.exists()) {
                resultsOutputFile.createNewFile();
            }
            Writer resultsWriter = new BufferedWriter(new FileWriter(resultsOutputFile.getAbsolutePath()));
            var performanceData = infoflowResults.getPerformanceData();

            resultsWriter.write(String.format("Vj-result: Overall time in seconds: %s", actualTimeCost));
            resultsWriter.write("\n --------------FlowDroid detail---------------");
            resultsWriter.write(String.format("\n FlowDroid-result: FlowDroid Runtime in seconds: %s", performanceData.getTotalRuntimeSeconds()));
            resultsWriter.write(String.format("\n FlowDroid-result: Call graph build in seconds: %s", performanceData.getCallgraphConstructionSeconds()));
            resultsWriter.write(String.format("\n FlowDroid-result: Path reconstruction in seconds: %s", performanceData.getPathReconstructionSeconds()));
            resultsWriter.write(String.format("\n FlowDroid-result: Taint propagation in seconds: %s", performanceData.getTaintPropagationSeconds()));
            resultsWriter.write(String.format("\n FlowDroid-result: Memory consumption: %s MB", performanceData.getMaxMemoryConsumption()));
            resultsWriter.write(String.format("\n FlowDroid-result: If FlowDroid terminated normally: %s", (infoflowResults.getTerminationState() == 0 ? "Success" : "Fail")));
//            resultsWriter.write("\n --------------Dataflow path---------------");
//            result.getInfoflowResults().printResults(resultsWriter);
            resultsWriter.flush();
            resultsWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void WriteInfoFlowResultsToFile(Result result, String filePath)
    {
        File resultsOutputFile = new File(filePath);
        try {
            // if file does not exist, then create it.
            if (!resultsOutputFile.exists()) {
                resultsOutputFile.createNewFile();
            }
            Writer resultsWriter = new BufferedWriter(new FileWriter(resultsOutputFile.getAbsolutePath()));
            var performanceData = result.getInfoflowResults().getPerformanceData();

            String addedMethodsStat = result.getAddedMethodsCnt() + " - Ratio: " + String.format("%.3f", (float) result.getAddedMethodsCnt().getO1() * 100 /result.getAddedMethodsCnt().getO2()) + "%";
            String modifiedMethodsStat = result.getModifiedMethodsCnt() + " - Ratio: " + String.format("%.3f", (float) result.getModifiedMethodsCnt().getO1() * 100 /result.getModifiedMethodsCnt().getO2()) + "%";
            String deletedMethodsStat = result.getDeletedMethodsCnt() + " - Ratio: " + String.format("%.3f", (float) result.getDeletedMethodsCnt().getO1() * 100 /result.getDeletedMethodsCnt().getO2()) + "%";

            resultsWriter.write(String.format("Evotaint-result: Overall time in seconds: %s", result.getIncrementalTaintAnalysisResult()));
            resultsWriter.write("\n --------------Evotaint detail---------------");
            resultsWriter.write(String.format("\n Evotaint-result: Added methods: %s", addedMethodsStat));
            resultsWriter.write(String.format("\n Evotaint-result: Modified methods: %s", modifiedMethodsStat));
            resultsWriter.write(String.format("\n Evotaint-result: Deleted methods: %s", deletedMethodsStat));
            resultsWriter.write(String.format("\n Evotaint-result: Get function list in seconds: %s", result.getGetFunctionListCost()));
            resultsWriter.write(String.format("\n Evotaint-result: Call graph build in seconds: %s", result.getBuildCallGraphCost()));
            resultsWriter.write(String.format("\n Evotaint-result: Find changed methods in seconds: %s", result.getFindChangedMethodsCost()));
            resultsWriter.write(String.format("\n Evotaint-result: Find impact methods in seconds: %s", result.getImpactAnalysisCost()));
            resultsWriter.write(String.format("\n Evotaint-result: Find sources and sinks in seconds: %s", result.getFindSourcesAndSinksCost()));
            resultsWriter.write(String.format("\n Evotaint-result: FlowDroid run infoflow in seconds: %s", result.getTaintAnalysisCost()));
            resultsWriter.write("\n --------------FlowDroid detail---------------");
            resultsWriter.write(String.format("\n FlowDroid-result: FlowDroid Runtime in seconds: %s", performanceData.getTotalRuntimeSeconds()));
            resultsWriter.write(String.format("\n FlowDroid-result: Call graph build in seconds: %s", result.getBuildCallGraphCost()));
            resultsWriter.write(String.format("\n FlowDroid-result: Path reconstruction in seconds: %s", performanceData.getPathReconstructionSeconds()));
            resultsWriter.write(String.format("\n FlowDroid-result: Taint propagation in seconds: %s", performanceData.getTaintPropagationSeconds()));
            resultsWriter.write(String.format("\n FlowDroid-result: Memory consumption: %s MB", performanceData.getMaxMemoryConsumption()));
            resultsWriter.write(String.format("\n FlowDroid-result: If FlowDroid terminated normally: %s", (result.getInfoflowResults().getTerminationState() == 0 ? "Success" : "Fail")));
//            resultsWriter.write("\n --------------Dataflow path---------------");
//            result.getInfoflowResults().printResults(resultsWriter);


            resultsWriter.flush();
            resultsWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
