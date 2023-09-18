package Model;


public class SourceSink {
    private String sourceSinkDefinition;
    private String methodSignature;
    private Boolean isSource;
    private Boolean isSink;



    public String getSourceSinkDefinition() {
        return sourceSinkDefinition;
    }

    public void setSourceSinkDefinition(String sourceSinkDefinition) {
        this.sourceSinkDefinition = sourceSinkDefinition;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public Boolean isSource() {
        return isSource;
    }

    public void setSource(Boolean source) {
        isSource = source;
    }

    public Boolean isSink() {
        return isSink;
    }

    public void setSink(Boolean sink) {
        isSink = sink;
    }
}
