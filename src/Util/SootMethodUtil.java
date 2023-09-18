package Util;

import Model.Config;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.options.Options;

import java.util.*;

public class SootMethodUtil {
    private static void setupSoot(String apkName) {
        G.reset();
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_force_android_jar(Config.androidJarPath);
        Options.v().set_process_dir(Collections.singletonList(Config.apkDirectory + apkName));
        Options.v().set_process_multiple_dex(true);
        List<String> excludeList = new ArrayList<>();
        excludeList.add("java.lang.*");
        excludeList.add("java.util.*");
        excludeList.add("com.google.*");
        excludeList.add("androidx.*");
        excludeList.add("com.google.android.*");
        excludeList.add("kotlin.*");
        excludeList.add("kotlinx.*");
        excludeList.add("org.intellij.*");
        excludeList.add("android.support.*");
        excludeList.add("org.jetbrains.*");
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_exclude(excludeList);
        Scene.v().loadNecessaryClasses();
    }

    /***
     * get methods list of an Apk
     * @param mels
     * @param allm
     * @param apkName
     * @return
     */
    public static int getFunctionLists(Set<String> mels, Set<SootMethod> allm, String apkName) {
        setupSoot(apkName);
        /* traverse all classes */
        Iterator<SootClass> clsIt = Scene.v().getApplicationClasses().iterator();
        int cnt = 0;
        while (clsIt.hasNext()) {
            SootClass sClass = clsIt.next();
            if ( sClass.isPhantom() ) {
                // skip phantom classes
                continue;
            }
            if ( !sClass.isApplicationClass() ) {
                // skip library classes
                continue;
            }


            /* traverse all methods of the class */
            Iterator<SootMethod> meIt = sClass.getMethods().iterator();
            while (meIt.hasNext()) {
                SootMethod sMethod = meIt.next();
                if ( !sMethod.isConcrete() ) {
                    // skip abstract methods and phantom methods, and native methods as well
                    continue;
                }
                if ( sMethod.toString().indexOf(": java.lang.Class class$") != -1 ) {
                    // don't handle reflections now either
                    continue;
                }

                // cannot instrument method event for a method without active body
                try {
                    Body body = sMethod.retrieveActiveBody();
                    // Now 'body' contains the active body
                } catch (Exception e) {
                    continue;
                }

//                if ( !sMethod.hasActiveBody() ) {
//                    continue;
//                }

                if (mels != null) {
                    mels.add(sMethod.getSignature());
                }
                if (allm != null) {
                    allm.add(sMethod);
                }
                cnt ++;
            }
        }

        return cnt;
    }

    /***
     * get SootMethod in a collection by signature
     * @param signature
     * @param methods
     * @return
     */
    public static SootMethod getMethodBySignature(String signature, Set<SootMethod> methods) {
        SootMethod matchedMethod = null;
        for (SootMethod method : methods) {
            if (method.getSignature().equals(signature)) {
                matchedMethod = method;
                break;
            }
        }
        return matchedMethod;
    }

    //fixme: correct and sound?
    public static boolean twoMethodsEqual(SootMethod s1, SootMethod s2) {
        var b1 = s1.retrieveActiveBody();
        var b2 = s2.retrieveActiveBody();
        var unitsInS1 = b1.getUnits();
        var unitsInS2 = b2.getUnits();
        if (unitsInS1.size() != unitsInS2.size()) {
            return false;
        }
        var itS1 = unitsInS1.iterator();
        var itS2 = unitsInS2.iterator();
        while (itS1.hasNext() && itS2.hasNext()) {
            var unitS1 = itS1.next();
            var unitS2 = itS2.next();
            if (!unitS1.toString().equals(unitS2.toString())) {
                return false;
            }
        }
        return true;
    }

    public static Set<String> returnMethodSignaturesCalledInAMethod(Iterator<SootMethod> callgraphSourceMethods) {
        var MethodSignaturesCalledInAMethod = new HashSet<String>();

        while (callgraphSourceMethods.hasNext()) {
            var nextMethod = callgraphSourceMethods.next();

            if (nextMethod.method().isConcrete()) {
                Body body = nextMethod.method().getActiveBody();

                for (Unit unit : body.getUnits()) {
                    Stmt stmt = (Stmt) unit;
                    if (stmt.containsInvokeExpr()) {
                        InvokeExpr invokeExpr = stmt.getInvokeExpr();
                        MethodSignaturesCalledInAMethod.add(invokeExpr.getMethod().getSignature());
                    }
                }
            }
        }

        return MethodSignaturesCalledInAMethod;
    }
}
