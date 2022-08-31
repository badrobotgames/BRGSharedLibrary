#!/usr/bin/env groovy
import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor
import io.jenkins.blueocean.rest.impl.pipeline.FlowNodeWrapper
import io.jenkins.blueocean.rest.model.BlueRun.BlueRunState

def call() 
{
	// Get all pipeline nodes that represent stages
	def visitor = new PipelineNodeGraphVisitor(currentBuild.rawBuild)
	def previousBuild = currentBuild.previousBuild
	for(int i = 0; i < 5; ++i) // Get a recent pipeline with the most steps in its list
	{
		if(previousBuild && previousBuild.previousBuild)
		{
			def previousVisitorA = new PipelineNodeGraphVisitor(previousBuild.rawBuild)
			def previousVisitorB = new PipelineNodeGraphVisitor(previousBuild.previousBuild.rawBuild)
			if(previousVisitorB.pipelineNodes.size() > previousVisitorA.pipelineNodes.size())
			{
				previousBuild = previousBuild.previousBuild
			}
			else
			{
				break
			}
		}
	}

	def previousVisitor = new PipelineNodeGraphVisitor(previousBuild.rawBuild)
	def stages = visitor.pipelineNodes
	def previousStages = previousVisitor.pipelineNodes
    
	def blocks = 
	[
		[
			"type": "section",
			"text": 
			[
				"type": "mrkdwn",
				"text": "*STATUS*"
			]
		]
	]
	
	previousStages.each{ previousStage -> 
		def currStage = stages.find{ stage -> stage && stage.displayName == previousStage.displayName }
		def status = BlueRunState.QUEUED
		
		if(currStage)
		{
			status = currStage.status.state
		}
		else
		{
			currStage = previousStage
		}

		def stageIcon = 'white_circle'
		def durationMessage = ''
		switch(status)
		{
			case BlueRunState.QUEUED:
				durationMessage = "\n     Previous runtime : ${new Date(previousStage.timing.getTotalDurationMillis()).format("mm:ss")}"
				stageIcon = 'white_circle'
				break
			case BlueRunState.RUNNING:
				durationMessage = "\n     Previous runtime : ${new Date(previousStage.timing.getTotalDurationMillis()).format("mm:ss")}"
				stageIcon = 'rainbow-orb'
				break

			case BlueRunState.FINISHED:
				durationMessage = "\n     Ran for : ${new Date(currStage.timing.getTotalDurationMillis()).format("mm:ss")}"
				stageIcon = 'large_green_circle'
				
			default: 
				break 
		}
		blocks.add(
			[
				"type": "section",
				"text": 
				[
					"type": "mrkdwn",
					"text": ":${stageIcon}: ${previousStage.displayName} : ${status}${durationMessage}"
				]
			]
		)
	}

	return blocks
}