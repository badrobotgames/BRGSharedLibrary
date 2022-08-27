package org.BRG

import jenkins.plugins.slack.workflow.SlackResponse

class SlackUtils
{
	def context
	def env
	def SlackResponse slackMessage

	SlackUtils(context) 
	{
		this.context = context
		this.env = context.env
	}

	def Initialize()
	{
		slackMessage = context.slackSend(channel: 'invasion-builds', message: "${env.JOB_NAME}_${env.BUILD_ID}: Initializing".toString(), color: 'CCCCCC')
	}

	def PostStatus()
	{
		def updateMessage = "${env.JOB_NAME}_${env.BUILD_ID}: ${context.GetAllStagesStatus()}"
		UpdateSlackMessage(updateMessage.toString(), 'CCCCCC')
	}

	def UpdateMessage(message, color) 
	{
		context.slackSend(channel: slackMessage.channelId, message: "${message}".toString(), color: "${color}".toString(), timestamp: slackMessage.ts)
	}

	def PostToThread(message, color) 
	{
		context.slackSend(channel: slackMessage.threadId, message: "${message}".toString(), color: "${color}".toString(), timestamp: slackMessage.ts)
	}

	def UploadToMessage(filePath) 
	{
		context.slackUploadFile(channel: slackMessage.channelId, filePath: filePath, timestamp: slackMessage.ts)
	}

	def UploadToThread(filePath) 
	{
		context.slackUploadFile(channel: 'invasion-builds' + slackMessage.ts, filePath: filePath)
	}
}
