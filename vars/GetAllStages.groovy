#!/usr/bin/env groovy

def call() {
    def visitor = new io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor(currentBuild.rawBuild)
    echo "${visitor}"
    def stages = visitor.pipelineNodes.findAll{ it ->
        it.type == FlowNodeWrapper.NodeType.STAGE
    }
    
    return stages
}