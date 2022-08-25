#!/usr/bin/env groovy
// import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor
// import io.jenkins.blueocean.rest.impl.pipeline.FlowNodeWrapper

def call() {
    echo 'Getting all stages'

    // Get all pipeline nodes that represent stages
    def visitor = new io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor( currentBuild.rawBuild )
    echo "${visitor}"

    def stages = visitor.pipelineNodes.findAll{stage -> stage.type == FlowNodeWrapper.NodeType.STAGE }
    echo "${stages}"
    return stages
}