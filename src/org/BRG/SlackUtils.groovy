package org.BRG

import jenkins.plugins.slack.workflow.SlackResponse

class SlackUtils
{
	def env
	def SlackResponse slackMessage

	SlackUtils(env) 
	{
		this.env = env
		slackMessage = slackSend(channel: 'invasion-builds', message: "${env.JOB_NAME}_${env.BUILD_ID}: Initializing".toString(), color: 'CCCCCC')
	}

	def PostStatusToSlack()
	{
		def updateMessage = "${env.JOB_NAME}_${env.BUILD_ID}: ${GetAllStagesStatus()}"
		UpdateSlackMessage(updateMessage.toString(), 'CCCCCC')
		echo updateMessage
	}

	def UpdateSlackMessage(message, color) 
	{
		slackSend(channel: slackResponse.threadId, message: "${message}".toString(), color: "${color}".toString(), timestamp: slackMessage.ts)
	}

	def PostToSlackThread(message, color) 
	{
		slackSend(channel: slackMessage.channelId, message: "${message}".toString(), color: "${color}".toString(), timestamp: slackMessage.ts)
	}

	def UploadToSlackMessage(filePath) 
	{
		slackUploadFile(channel: slackMessage.channelId, filePath: filePath, timestamp: slackMessage.ts)
	}
}
