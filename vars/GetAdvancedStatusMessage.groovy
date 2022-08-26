#!/usr/bin/env groovy
import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor
import io.jenkins.blueocean.rest.impl.pipeline.FlowNodeWrapper
import io.jenkins.blueocean.rest.model.BlueRun.BlueRunState

def call() {
	def message = "${env.JOB_NAME}_${env.BUILD_ID} Status:"

	// Get all pipeline nodes that represent stages
	def visitor = new PipelineNodeGraphVisitor(currentBuild.rawBuild)
	def previousVisitor = new PipelineNodeGraphVisitor(currentBuild.getPreviousBuild().rawBuild)
	def stages = visitor.getPipelineNodes()
	def previousStages = previousVisitor.getPipelineNodes()
    
	previousStages.each{ previousStage -> 
		def currStage = stages.find{ stage -> stage && stage.displayName == previousStage.displayName }
		def status = BlueRunState.QUEUED
		if(currStage)
		{
			status = currStage.status.state
		}
		message = "${message}\n   - ${previousStage.displayName} : ${status}" 
	}

	return message
}