#!/usr/bin/env groovy
import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphpreviousVisitor
import io.jenkins.blueocean.rest.impl.pipeline.FlowNodeWrapper

def call() {
	def message = "${env.JOB_NAME}_${env.BUILD_ID} Status:"

	// Get all pipeline nodes that represent stages
	def visitor = new PipelineNodeGraphpreviousVisitor(currentBuild.rawBuild)
	def previousVisitor = new PipelineNodeGraphpreviousVisitor(currentBuild.getPreviousBuild().rawBuild)
	def stages = visitor.getPipelineNodes()//.findAll{stage -> stage.type == FlowNodeWrapper.NodeType.STAGE }
	def previousStages = previousVisitor.getPipelineNodes()//.findAll{stage -> stage.type == FlowNodeWrapper.NodeType.STAGE }
    
    stages.each{ stage -> 
		message = "${message}\n   - ${stage.displayName} : ${stage.status.state}" 
	}   

    //previousStages.each{ previousStage -> 
	//	def state = visitor.
	//	previousStage.status.state
	//	if(state)
	//	message = "${message}\n   - ${stage.displayName} : ${state}" 
	//}

	return message
}