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

	def PostStatusToSlack()
	{
		def updateMessage = "${env.JOB_NAME}_${env.BUILD_ID}: ${context.GetAllStagesStatus()}"
		UpdateSlackMessage(updateMessage.toString(), 'CCCCCC')
		echo updateMessage
	}

	def UpdateSlackMessage(message, color) 
	{
		context.slackSend(channel: slackMessage.channelId, message: "${message}".toString(), color: "${color}".toString(), timestamp: slackMessage.ts)
	}

	def PostToSlackThread(message, color) 
	{
		context.slackSend(channel: slackMessage.threadId, message: "${message}".toString(), color: "${color}".toString(), timestamp: slackMessage.ts)
	}

	def UploadToSlackMessage(filePath) 
	{
		context.slackUploadFile(channel: slackMessage.channelId, filePath: filePath, timestamp: slackMessage.ts)
	}
}
