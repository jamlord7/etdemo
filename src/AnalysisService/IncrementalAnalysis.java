package AnalysisService;

import FlowDroid.FlowDroid;
import Model.Config;
import Model.Result;
import Model.SourceSink;
import Util.SerializationUtil;
import Util.SootMethodUtil;
import jas.Pair;
import soot.SootMethod;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IncrementalAnalysis {
    public static void perform(String viApkName, String vjApkName) throws IOException {
        try {
            String resultDirectoryPath = "./out/" + Config.appName + "/";
            // 1. get all the methods in vi and vj
            LocalDateTime getFunctionListStart = LocalDateTime.now();

            Set<SootMethod> methodsInVi = new HashSet<>();
            Set<SootMethod> methodsInVj = new HashSet<>();
            SootMethodUtil.getFunctionLists(null, methodsInVi, viApkName);
            SootMethodUtil.getFunctionLists(null, methodsInVj, vjApkName);

            LocalDateTime getFunctionListEnd = LocalDateTime.now();

            // 2. find changed methods
            LocalDateTime findChangedMethodsStart = LocalDateTime.now();

            Map<String, Set<SootMethod>> changedMethods = ImpactAnalysis.findChangedMethods(methodsInVi, methodsInVj);
            Set<SootMethod> addedMethods = changedMethods.get("A");
            Set<SootMethod> modifiedMethods = changedMethods.get("M");
            Set<SootMethod> deletedMethods = changedMethods.get("D");

            LocalDateTime findChangedMethodsEnd = LocalDateTime.now();

            // 3. Find impacted methods of M,A on vj, and successors and predecessors of them
            LocalDateTime impactAnalysisStart = LocalDateTime.now();

            FlowDroid.v().initFlowDroid(vjApkName);
            CallGraph callGraphOfVj = FlowDroid.v().getCallGraph(InfoflowConfiguration.CallgraphAlgorithm.SPARK);

            // - for convenience and efficiency, combine mod and add methods
            modifiedMethods.addAll(addedMethods);

            Set<SootMethod> impactedMethods = ImpactAnalysis.findDescendantsInCallGraph(modifiedMethods, callGraphOfVj);

            Set<SootMethod> predecessorsOfImpactedMethods = ImpactAnalysis.findAncestorsInCallGraph(impactedMethods, callGraphOfVj);
            Set<SootMethod> successorsOfImpactedMethods = ImpactAnalysis.findDescendantsInCallGraph(impactedMethods, callGraphOfVj);

            LocalDateTime impactAnalysisEnd = LocalDateTime.now();

            //4. Find sources and sinks from ancestors and descendants of impacted methods
            LocalDateTime findSourcesAndSinksStart = LocalDateTime.now();

            Set<String> callSitesAtPredecessors = SootMethodUtil.returnMethodSignaturesCalledInAMethod(predecessorsOfImpactedMethods.iterator());
            Set<String> callSitesAtSuccessors = SootMethodUtil.returnMethodSignaturesCalledInAMethod(successorsOfImpactedMethods.iterator());

            Set<SourceSink> sourceSinkDefinitions = SourceSinkService.buildSourcesSinksFromFile(Config.sourcesAndSinksPath);
            Set<SourceSink> sourcesInImPredecessors = SourceSinkService.returnSourceSinkUsagesFromMethodInvocations(sourceSinkDefinitions, callSitesAtPredecessors, true);
            Set<SourceSink> sinksInImSuccessors = SourceSinkService.returnSourceSinkUsagesFromMethodInvocations(sourceSinkDefinitions, callSitesAtSuccessors, false);

            sourcesInImPredecessors.addAll(sinksInImSuccessors);

            String impactedSourcesAndSinksPath = resultDirectoryPath + Config.appName +"-impacted-sources-and-sinks.txt";
            SourceSinkService.createSourceSinkFile(sourcesInImPredecessors, impactedSourcesAndSinksPath);

            LocalDateTime findSourcesAndSinksEnd = LocalDateTime.now();

            //4. run data flow on new sources and sinks
            LocalDateTime taintAnalysisStart = LocalDateTime.now();
            InfoflowResults infoflowResults = FlowDroid.v().runTaintAnalysis(impactedSourcesAndSinksPath);
            LocalDateTime taintAnalysisEnd = LocalDateTime.now();

            //5. write and count statistics
            Result result = new Result();
            result.setInfoflowResults(infoflowResults);
            result.setGetFunctionListCost(ChronoUnit.SECONDS.between(getFunctionListStart, getFunctionListEnd));
            result.setFindChangedMethodsCost(ChronoUnit.SECONDS.between(findChangedMethodsStart, findChangedMethodsEnd));
            result.setImpactAnalysisCost(ChronoUnit.SECONDS.between(impactAnalysisStart, impactAnalysisEnd));
            result.setFindSourcesAndSinksCost(ChronoUnit.SECONDS.between(findSourcesAndSinksStart, findSourcesAndSinksEnd));
            result.setTaintAnalysisCost(ChronoUnit.SECONDS.between(taintAnalysisStart, taintAnalysisEnd));
            result.setIncrementalTaintAnalysisResult(ChronoUnit.SECONDS.between(getFunctionListStart, taintAnalysisEnd));
            result.setAddedMethodsCnt(new Pair<>(addedMethods.size(), methodsInVj.size()));
            result.setModifiedMethodsCnt(new Pair<>(modifiedMethods.size(), methodsInVi.size()));
            result.setDeletedMethodsCnt(new Pair<>(deletedMethods.size(), methodsInVi.size()));

            SerializationUtil.WriteInfoFlowResultsToFile(result, resultDirectoryPath + Config.appName + "-evotaint-result.txt");
            System.out.println("Evotaint done on" + Config.appName);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
