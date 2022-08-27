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
	}

	def PostStatus()
	{
		def updateMessage = "${env.JOB_NAME}_${env.BUILD_ID}: ${context.GetAllStagesStatus()}"
		UpdateMessage(updateMessage.toString(), 'CCCCCC')
	}

	def UpdateMessage(message, color) 
	{
		context.slackSend(channel: slackMessage.channelId, message: "${message}".toString(), color: "${color}".toString(), timestamp: slackMessage.ts)
	}

	def PostToThread(message, color) 
	{
		context.slackSend(channel: slackMessage.threadId, message: "${message}".toString(), color: "${color}".toString())
	}

	def PostBlockToThread(blocks) 
	{
		context.slackSend(channel: slackMessage.threadId, blocks: blocks)
	}

	def UploadToMessage(filePath) 
	{
		context.slackUploadFile(channel: slackMessage.channelId, filePath: filePath, timestamp: slackMessage.ts)
	}

	def UploadToThread(filePath) 
	{
		context.slackUploadFile(channel: 'invasion-builds:' + slackMessage.ts, filePath: filePath)
	}

	def PostChanges(lastSuccessCL, changes)
	{
		def specificCause = context.currentBuild.getBuildCauses()[0].shortDescription.toString().replace('[', '').replace(']', '')

		def blocks = [
			[
				"type": "header",
				"text": [
					"type": "plain_text",
					"text": "${specificCause}",
					"emoji": true
				]
			],
			[
				"type": "section",
				"text": 
				[
					[
						"type": "mrkdwn",
						"text": "Building changes since ${lastSuccessCL}"
					],
				]
			]
		]

		if(changes.size() > 0)
		{
			def changesFields = []
			for(def item : changes)
			{
				String changlist = item.get('change').toString()
				String author = item.get('user').toString()
				String description = item.get('desc').toString().replaceAll("[\r\n]+", "")
				changesFields.add(
					[
						"type": "mrkdwn",
						"text": "CL${changlist} by ${author} - ${description}"
					]
				)
			}

			blocks.add(
				[
					"type": "section",
					"fields": changesFields		
				]
			)
		}

		PostBlockToThread(blocks)
	}

	def PostParameters()
	{
		def blocks = [
			[
				"type": "header",
				"text": [
					"type": "plain_text",
					"text": "Parameters",
					"emoji": true
				]
			]
		]
		
		context.params.each { param ->
			def paramName = param.key.replaceAll("[\r\n]+", "")
			def paramValue = param.value
			if(paramValue.getClass() == String)
			{
				paramValue = paramValue.replaceAll("[\r\n]+", ",")
			}
			blocks.add(
				[
					"type": "context",
					"elements":
					[
						[
							"type": "mrkdwn",
							"text": paramName
						],
						[
							"type": "mrkdwn",
							"text": "${paramValue}"
						]
					]
				]
			)
		}
		
		PostBlockToThread(blocks)
	}
}
