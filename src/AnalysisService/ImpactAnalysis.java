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
                if (!SootMethodUtil.twoMethodsEqual0(methodInVi, matchedMethodInVj)) {
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

    public static Map<String, Set<String>> findChangedMethods2(Set<SootMethod> methodsInVi, Set<SootMethod> methodsInVj) {
        // fixme: here could be sped up by intersection with method signature
        Map<String, Set<String>> changedMethods = new HashMap<>();
        Set<String> addedMethods = new HashSet<>();
        Set<String> modifiedMethods = new HashSet<>();
        Set<String> deletedMethods = new HashSet<>();
        // find deleted and modified
        SootMethod matchedMethodInVj = null;
        for (SootMethod methodInVi : methodsInVi) {
            matchedMethodInVj = SootMethodUtil.getMethodBySignature(methodInVi.getSignature(), methodsInVj);

            if (matchedMethodInVj == null) {
                deletedMethods.add(methodInVi.getSignature());
            } else {
                if (!SootMethodUtil.twoMethodsEqual0(methodInVi, matchedMethodInVj)) {
                    modifiedMethods.add(matchedMethodInVj.getSignature());
                }
            }

        }
        // find added methods
        for (SootMethod methodInVj : methodsInVj) {
            if (SootMethodUtil.getMethodBySignature(methodInVj.getSignature(), methodsInVi) == null) {
                addedMethods.add(methodInVj.getSignature());
            }
        }

        changedMethods.put("A", addedMethods);
        changedMethods.put("M", modifiedMethods);
        changedMethods.put("D", deletedMethods);
        return changedMethods;
    }

    public static Map<String, Set<String>> findChangedMethodsSignature(Set<String> methodsInViSig, Set<String> methodsInVjSig, Set<SootMethod> methodsInVi) {
        Map<String, Set<String>> changedMethodsSig = new HashMap<>();
        Set<String> addedMethodsSig = new HashSet<>(methodsInVjSig);
        Set<String> deletedMethodsSig = new HashSet<>(methodsInViSig);
        Set<String> modifiedMethodsSig = new HashSet<>();
        // Vj - Vi to get added methods
        addedMethodsSig.removeAll(methodsInViSig);
        // Vi - Vj tp get deleted methods
        deletedMethodsSig.removeAll(methodsInVjSig);
        // Vi intersects Vj to find modified methods
        Set<String> unvariedMethodsSig = new HashSet<>(methodsInVjSig);
        unvariedMethodsSig.retainAll(methodsInViSig);

        for (SootMethod methodInVi: methodsInVi) {
            if (unvariedMethodsSig.contains(methodInVi.getSignature())) {
                SootMethod methodInVj = Scene.v().getMethod(methodInVi.getSignature());
                if (!SootMethodUtil.twoMethodsEqual(methodInVi, methodInVj)) {
                    modifiedMethodsSig.add(methodInVi.getSignature());
                }
            }
        }

        changedMethodsSig.put("A", addedMethodsSig);
        changedMethodsSig.put("M", modifiedMethodsSig);
        changedMethodsSig.put("D", deletedMethodsSig);
        return changedMethodsSig;
    }


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

    public static Set<String> findAncestorsSigInCallGraph(Set<String> methods, CallGraph callGraph) {
        Set<String> ancestorsOfMethods = new HashSet<>(methods);
        Queue<String> queue = new LinkedList<>(methods);

        while (!queue.isEmpty()) {
            String currentMethod = queue.poll();
            Iterator<Edge> edges = callGraph.edgesInto(Scene.v().getMethod(currentMethod));


            while (edges.hasNext()) {
                String predecessorMethod = edges.next().src().method().getSignature();

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

    public static Set<String> findDescendantsSigInCallGraph(Set<String> methods, CallGraph callGraph) {
        Set<String> descendantsOfMethods = new HashSet<>(methods);
        Queue<String> queue = new LinkedList<>(methods);

        while (!queue.isEmpty()) {
            String currentMethod = queue.poll();
            Iterator<Edge> edges = callGraph.edgesOutOf(Scene.v().getMethod(currentMethod));

            while (edges.hasNext()) {
                SootMethod successorMethod = edges.next().tgt();

                // If this descendant has not been seen before, we add it to the set and the queue to explore its descendants
                if (descendantsOfMethods.add(successorMethod.getSignature())) {
                    queue.add(successorMethod.getSignature());
                }
            }
        }

        return descendantsOfMethods;
    }

}
