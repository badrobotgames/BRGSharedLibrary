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
		slackMessage = context.slackSend(channel: 'invasion-builds', message: "${env.JOB_NAME}_${env.BUILD_ID}: Initializing".toString(), color: '777777')
		PostParameters()
	}

	def PostStatus()
	{
		def updateMessage = "${env.JOB_NAME}_${env.BUILD_ID}: ${context.GetAllStagesStatus()}"
		UpdateMessage(updateMessage.toString(), 'CCCCCC')
	}

	def PostParameters()
	{
		String paramsMessage = 'PARAMETERS'
		context.params.each { param ->
			def paramName = param.key.replaceAll("[\r\n]+", "")
			def paramValue = param.value
			if(paramValue.getClass() == String)
			{
				paramValue = paramValue.replaceAll("[\r\n]+", "")
			}
			paramsMessage = "${paramsMessage},  ${paramName}(${paramValue})".toString()
		}
		PostToThread(paramsMessage, color: '777777')
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
		context.slackUploadFile(channel: 'invasion-builds:' + slackMessage.ts, filePath: filePath)
	}
}
