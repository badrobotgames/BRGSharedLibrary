#!/usr/bin/env groovy
import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor
import io.jenkins.blueocean.rest.impl.pipeline.FlowNodeWrapper

def call() {
	def message = "${env.JOB_NAME}_${env.BUILD_ID} Status:"

	// Get all pipeline nodes that represent stages
	def visitor = new PipelineNodeGraphVisitor(currentBuild.getPreviousBuild().rawBuild)
	def stages = visitor.pipelineNodes.findAll{stage -> stage.type == FlowNodeWrapper.NodeType.STAGE }
    
    stages.each{ stage -> message = "${message}\n   - ${stage.displayName} : ${stage.status}" }
	return message
}