package AnalysisService;

import Util.SootMethodUtil;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.*;

public class ImpactAnalysis {
    public static Map<String, Set<SootMethod>> findChangedMethods(Set<SootMethod> methodsInVi, Set<SootMethod> methodsInVj) {
        // fixme: here could be sped up by intersection with method signature
        Map<String, Set<SootMethod>> changedMethods = new HashMap<>();
        Set<SootMethod> addedMethods = new HashSet<>();
        Set<SootMethod> modifiedMethods = new HashSet<>();
        Set<SootMethod> deletedMethods = new HashSet<>();
        // find deleted and modified
        SootMethod matchedMethodInVj = null;
        for (SootMethod methodInVi : methodsInVi) {
            matchedMethodInVj = SootMethodUtil.getMethodBySignature(methodInVi.getSignature(), methodsInVj);

            if (matchedMethodInVj == null) {
                deletedMethods.add(methodInVi);
            } else {
                if (!SootMethodUtil.twoMethodsEqual(methodInVi, matchedMethodInVj)) {
                    modifiedMethods.add(matchedMethodInVj);
                }
            }

        }
        // find added methods
        for (SootMethod methodInVj : methodsInVj) {
            if (SootMethodUtil.getMethodBySignature(methodInVj.getSignature(), methodsInVi) == null) {
                addedMethods.add(methodInVj);
            }
        }

        changedMethods.put("A", addedMethods);
        changedMethods.put("M", modifiedMethods);
        changedMethods.put("D", deletedMethods);
        return changedMethods;
    }

//    public static Map<String, Set<String>> findChangedMethodsSignature(Set<String> methodsInViSig, Set<String> methodsInVjSig, Set<SootMethod> methodsInVi) {
        // fixme: here could be sped up by intersection with method signature
//        Map<String, Set<String>> changedMethods = new HashMap<>();
//        Set<String> addedMethods = new HashSet<>(methodsInVjSig);
//        Set<String> deletedMethods = new HashSet<>(methodsInViSig);
//        // Vj - Vi to get added methods
//        addedMethods.removeAll(methodsInViSig);
//        // Vi - Vj tp get deleted methods
//        deletedMethods.removeAll(methodsInVjSig);
//        // Vi intersects Vj to find modified methods
//        Set<String> unvariedMethods = new HashSet<>(methodsInViSig);
//        unvariedMethods.retainAll(methodsInVjSig);
//        SootMethod matchedMethodInVj = null;
//        for (String methodInVi : unvariedMethods) {
//            matchedMethodInVj = SootMethodUtil.getMethodBySignature(methodInVi, methodsInVi);
//
//            if (matchedMethodInVj == null) {
//                deletedMethods.add(methodInVi);
//            } else {
//                if (!SootMethodUtil.twoMethodsEqual(methodInVi, matchedMethodInVj)) {
//                    modifiedMethods.add(matchedMethodInVj);
//                }
//            }
//
//        }
//
//
//        changedMethods.put("A", addedMethods);
//        changedMethods.put("M", modifiedMethods);
//        changedMethods.put("D", deletedMethods);
//        return changedMethods;
//    }


    public static Set<SootMethod> findAncestorsInCallGraph(Set<SootMethod> methods, CallGraph callGraph) {
        Set<SootMethod> ancestorsOfMethods = new HashSet<>(methods);
        Queue<SootMethod> queue = new LinkedList<>(methods);

        while (!queue.isEmpty()) {
            SootMethod currentMethod = queue.poll();
            Iterator<Edge> edges = callGraph.edgesInto(Scene.v().getMethod(currentMethod.getSignature()));


            while (edges.hasNext()) {
                SootMethod predecessorMethod = edges.next().src();

                // If this ancestor has not been seen before, we add it to the set and the queue to explore its ancestors
                if (ancestorsOfMethods.add(predecessorMethod)) {
                    queue.add(predecessorMethod);
                }
            }
        }

        return ancestorsOfMethods;
    }

    public static Set<SootMethod> findDescendantsInCallGraph(Set<SootMethod> methods, CallGraph callGraph) {
        Set<SootMethod> descendantsOfMethods = new HashSet<>(methods);
        Queue<SootMethod> queue = new LinkedList<>(methods);

        while (!queue.isEmpty()) {
            SootMethod currentMethod = queue.poll();
            Iterator<Edge> edges = callGraph.edgesOutOf(Scene.v().getMethod(currentMethod.getSignature()));

            while (edges.hasNext()) {
                SootMethod successorMethod = edges.next().tgt();

                // If this descendant has not been seen before, we add it to the set and the queue to explore its descendants
                if (descendantsOfMethods.add(successorMethod)) {
                    queue.add(successorMethod);
                }
            }
        }

        return descendantsOfMethods;
    }

}
