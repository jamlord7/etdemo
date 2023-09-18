package Model;

import jas.Pair;
import soot.jimple.infoflow.results.InfoflowResults;

public class Result {
    private long incrementalTaintAnalysisResult;
    private InfoflowResults infoflowResults;
    private long getFunctionListCost;
    private long findChangedMethodsCost;
    private long impactAnalysisCost;
    private long findSourcesAndSinksCost;
    private long taintAnalysisCost;
    private Pair<Integer, Integer> addedMethodsCnt;
    private Pair<Integer, Integer> modifiedMethodsCnt;
    private Pair<Integer, Integer> deletedMethodsCnt;

    public Pair<Integer, Integer> getAddedMethodsCnt() {
        return addedMethodsCnt;
    }

    public void setAddedMethodsCnt(Pair<Integer, Integer> addedMethodsCnt) {
        this.addedMethodsCnt = addedMethodsCnt;
    }

    public Pair<Integer, Integer> getModifiedMethodsCnt() {
        return modifiedMethodsCnt;
    }

    public void setModifiedMethodsCnt(Pair<Integer, Integer> modifiedMethodsCnt) {
        this.modifiedMethodsCnt = modifiedMethodsCnt;
    }

    public Pair<Integer, Integer> getDeletedMethodsCnt() {
        return deletedMethodsCnt;
    }

    public void setDeletedMethodsCnt(Pair<Integer, Integer> deletedMethodsCnt) {
        this.deletedMethodsCnt = deletedMethodsCnt;
    }

    public long getIncrementalTaintAnalysisResult() {
        return incrementalTaintAnalysisResult;
    }

    public void setIncrementalTaintAnalysisResult(long incrementalTaintAnalysisResult) {
        this.incrementalTaintAnalysisResult = incrementalTaintAnalysisResult;
    }

    public InfoflowResults getInfoflowResults() {
        return infoflowResults;
    }

    public void setInfoflowResults(InfoflowResults infoflowResults) {
        this.infoflowResults = infoflowResults;
    }

    public long getGetFunctionListCost() {
        return getFunctionListCost;
    }

    public void setGetFunctionListCost(long getFunctionListCost) {
        this.getFunctionListCost = getFunctionListCost;
    }

    public long getFindChangedMethodsCost() {
        return findChangedMethodsCost;
    }

    public void setFindChangedMethodsCost(long findChangedMethodsCost) {
        this.findChangedMethodsCost = findChangedMethodsCost;
    }

    public long getImpactAnalysisCost() {
        return impactAnalysisCost;
    }

    public void setImpactAnalysisCost(long impactAnalysisCost) {
        this.impactAnalysisCost = impactAnalysisCost;
    }

    public long getFindSourcesAndSinksCost() {
        return findSourcesAndSinksCost;
    }

    public void setFindSourcesAndSinksCost(long findSourcesAndSinksCost) {
        this.findSourcesAndSinksCost = findSourcesAndSinksCost;
    }

    public long getTaintAnalysisCost() {
        return taintAnalysisCost;
    }

    public void setTaintAnalysisCost(long taintAnalysisCost) {
        this.taintAnalysisCost = taintAnalysisCost;
    }
}
