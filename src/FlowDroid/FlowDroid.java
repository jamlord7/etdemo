package FlowDroid;

import Model.Config;
import org.xmlpull.v1.XmlPullParserException;
import soot.G;
import soot.Scene;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.io.IOException;

public class FlowDroid {
    private static FlowDroid flowDroid = null;

    private SetupApplication application = null;

    private FlowDroid() {

    }

    public static FlowDroid v() {
        if (flowDroid == null) {
            flowDroid = new FlowDroid();
        }

        return flowDroid;
    }

    public void initFlowDroid(String apkName) throws IOException {
        if (application == null) {
            G.reset();
            application = new SetupApplication(Config.androidJarPath, Config.apkDirectory + apkName);
            setFlowDroidConfig();
        }
    }

    public void setFlowDroidConfig() throws IOException {
        if (application == null) {
            System.out.println("initialize FlowDroid first");
        } else {
            application.setCallbackFile(Config.androidCallbacksPath);
            application.setTaintWrapper(new EasyTaintWrapper(Config.taintWrapperPath));
            application.getConfig().setInspectSinks(true);
            application.getConfig().setInspectSources(true);
            application.getConfig().setFlowSensitiveAliasing(false);
            application.getConfig().setMergeDexFiles(true);
            application.getConfig().getAccessPathConfiguration().setAccessPathLength(1);
            application.getConfig().setAliasingAlgorithm(InfoflowConfiguration.AliasingAlgorithm.None);
            application.getConfig().getPathConfiguration().setPathReconstructionMode(InfoflowConfiguration.PathReconstructionMode.Precise);
            application.getConfig().setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
        }
    }

    public void buildCallGraph(InfoflowConfiguration.CallgraphAlgorithm algorithm) {
        application.getConfig().setCallgraphAlgorithm(algorithm);
        application.constructCallgraph();
    }

    public CallGraph getCallGraph(InfoflowConfiguration.CallgraphAlgorithm algorithm) {
        buildCallGraph(algorithm);
        return Scene.v().getCallGraph();
    }

    public InfoflowResults runTaintAnalysis(String sourcesAndSinksFilePath) throws XmlPullParserException, IOException {
        //setFlowDroidConfig();
        application.getConfig().setTaintAnalysisEnabled(true);
        application.getConfig().setSootIntegrationMode(InfoflowConfiguration.SootIntegrationMode.UseExistingCallgraph);
        return application.runInfoflow(sourcesAndSinksFilePath);
    }





}