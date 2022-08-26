#!/usr/bin/env groovy
import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor
import io.jenkins.blueocean.rest.impl.pipeline.FlowNodeWrapper

def call() {
	def message = "${env.JOB_NAME}_${env.BUILD_ID} Status:"

	// Get all pipeline nodes that represent stages
	def visitor = new PipelineNodeGraphVisitor(currentBuild.rawBuild)
	def previousVisitor = new PipelineNodeGraphVisitor(currentBuild.getPreviousBuild().rawBuild)
	def stages = visitor.getPipelineNodes()
	def previousStages = previousVisitor.getPipelineNodes()
    
	previousStages.each{ previousStage -> 
		def stage = stages.find(currStage -> currStage.displayName == previousStage.displayName)
		def status = BlueRun.BlueRunState.QUEUED
		if(stage)
		{
			status = stage.status.state
		}
		message = "${message}\n   - ${stage.displayName} : ${status}" 
	}

	return message
}