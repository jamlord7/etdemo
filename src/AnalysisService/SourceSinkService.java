package AnalysisService;

import Model.SourceSink;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceSinkService {
    private static String parseMethodSignatureFromDefinition(String originalMethodSignature)
    {
        Pattern pattern = Pattern.compile("(<.*?>).*");
        Matcher matcher = pattern.matcher(originalMethodSignature);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return originalMethodSignature;
    }

    public static Set<SourceSink> buildSourcesSinksFromFile(String sourceSinkFileLocation) {
        Set<SourceSink> sourceSinkDefinitions = new HashSet<SourceSink>();

        try (BufferedReader br = new BufferedReader(new FileReader(sourceSinkFileLocation))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty() && !line.isBlank() && !line.startsWith("%")) {

                    SourceSink newSourceSink = new SourceSink();
                    newSourceSink.setSourceSinkDefinition(line);

                    if (line.contains("_SINK_")) {
                        newSourceSink.setSink(true);
                        newSourceSink.setSource(false);
                    }

                    if (line.contains("_SOURCE_")) {
                        newSourceSink.setSource(true);
                        newSourceSink.setSink(false);
                    }

                    if (line.contains("_BOTH_")) {
                        newSourceSink.setSource(true);
                        newSourceSink.setSink(true);
                    }

                    newSourceSink.setMethodSignature(parseMethodSignatureFromDefinition(line));

                    sourceSinkDefinitions.add(newSourceSink);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sourceSinkDefinitions;
    }

    public static Set<SourceSink> returnSourceSinkUsagesFromMethodInvocations(Set<SourceSink> sourceSinkDefinitions, Set<String> methodInvocations, boolean findSource) {
        Set<SourceSink> sourceSinkUsages = new HashSet<>();

        for (SourceSink sourceSinkDefinition : sourceSinkDefinitions) {
            for (String methodInvocation : methodInvocations) {
                if (methodInvocation.equals(sourceSinkDefinition.getMethodSignature())) {
                    if ((findSource && sourceSinkDefinition.isSource()) || (!findSource && sourceSinkDefinition.isSink())) {
                        sourceSinkUsages.add(sourceSinkDefinition);
                    }
                }
            }
        }

        return sourceSinkUsages;
    }

    public static void createSourceSinkFile(Set<SourceSink> sourceSinkUsages, String outputFileLocation) {
        try (PrintStream fileStream = new PrintStream(outputFileLocation)) {
            for (SourceSink sourceSink : sourceSinkUsages) {
                fileStream.println(sourceSink.getSourceSinkDefinition());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
