#!/usr/bin/env groovy

import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor

def call() {
    def visitor = new PipelineNodeGraphVisitor(currentBuild.rawBuild)
    echo "${visitor}"
    def stages = visitor.pipelineNodes.findAll{ it ->
        it.type == FlowNodeWrapper.NodeType.STAGE
    }
    
    return stages
}