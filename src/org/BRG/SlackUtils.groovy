package org.BRG

import jenkins.plugins.slack.workflow.SlackResponse

class SlackUtils
{
	def context
	def env
	def SlackResponse slackMessage
	def allowSlackSend //Used for debugging without clogging up invasion-builds
	def lastSuccessCL = "UNSET"
	def changes

	SlackUtils(context, allowSlackSend = true) 
	{
		this.context = context
		this.env = context.env
		this.allowSlackSend = allowSlackSend
	}

	def Initialize()
	{
		if(allowSlackSend)
		{
			def blocks = 
			[
				[
					"type": "header",
					"text": 
					[
						"type": "plain_text",
						"text": "${env.JOB_NAME}_${env.BUILD_ID}: Initializing",
						"emoji": true
					]
				]
			]
			slackMessage = context.slackSend(channel: 'invasion-builds', blocks: blocks)
		}
		else
		{
			context.echo "${env.JOB_NAME}_${env.BUILD_ID}: Initializing".toString()
		}
	}

	def UpdateMessage(message, color) 
	{
		if(allowSlackSend)
		{
			context.slackSend(channel: slackMessage.channelId, message: "${message}".toString(), timestamp: slackMessage.ts, color: color)
		}
		else
		{
			context.echo "UpdateMessage(${message}) color(${color})".toString()
		}
	}

	def PostProcessBlocks(blocks)
	{
		def sectionCharacterLimit = 3000
		for(def block : blocks)
		{
			def sectionCharacterCount = 0
			if(block["type"] == "section")
			{
				for(def item : block)
				{
					sectionCharacterCount += item.toString().length()
					if(sectionCharacterCount >= sectionCharacterLimit)
					{
						blocks.remove(item)
					}
				}
			}
		}
		return blocks
	}

	def UpdateMessageBlocks(blocks) 
	{
		blocks = PostProcessBlocks(blocks)
		if(allowSlackSend)
		{
			context.slackSend(channel: slackMessage.channelId, blocks: blocks, timestamp: slackMessage.ts)
		}
		else
		{
			context.echo "UpdateMessageBlocks(${blocks}))".toString()
		}
	}

	def PostToThread(message) 
	{
		if(allowSlackSend)
		{
			context.slackSend(channel: slackMessage.threadId, message: "${message}".toString())
		}
		else
		{
			context.echo "PostToThread(${message})".toString()
		}
	}

	def PostBlockToThread(blocks) 
	{
		blocks = PostProcessBlocks(blocks)
		if(allowSlackSend)
		{
			context.slackSend(channel: slackMessage.threadId, blocks: blocks)
		}
		else
		{
			context.echo "PostBlockToThread(${blocks})".toString()
		}
	}

	def UploadToMessage(filePath) 
	{
		if(allowSlackSend)
		{
			context.slackUploadFile(channel: slackMessage.channelId, filePath: filePath, timestamp: slackMessage.ts)
		}
		else
		{
			context.echo "UploadToMessage(${filePath})".toString()
		}
	}

	def UploadToThread(filePath, message = '') 
	{
		if(allowSlackSend)
		{
			context.slackUploadFile(channel: 'invasion-builds:' + slackMessage.ts, filePath: filePath, initialComment:  message)
		}
		else
		{
			context.echo "UploadToThread(${filePath}) message(${message})".toString()
		}
	}

	def SetChanges(lastSuccessCL, changes)
	{
		this.lastSuccessCL = lastSuccessCL
		this.changes = changes
		PostStatus()
	}

	def PostStatus()
	{
		def specificCause = context.currentBuild.getBuildCauses()[0].shortDescription.toString().replace('[', '').replace(']', '')

		def changesText = "*Building changes since ${lastSuccessCL}*"
		if(changes && changes.size() > 0)
		{
			for(def item : changes)
			{
				String changlist = item.get('change').toString()
				String author = item.get('user').toString()
				String description = item.get('desc').toString().replaceAll("[\r\n]+", "")
				changesText = "${changesText}\nCL-${changlist} by ${author}: ${description}"
			}
		}

		def blocks = 
		[
			[
				"type": "header",
				"text": 
				[
					"type": "plain_text",
					"text": "${env.JOB_NAME}_${env.BUILD_ID}: ${specificCause}",
					"emoji": true
				]
			],
			[
				"type": "divider"
			],
			[
				"type": "section",
				"text": 
				[
					"type": "mrkdwn",
					"text": changesText
				]
			],
			[
				"type": "divider"
			]
		]

		blocks.addAll(context.GetAllStagesStatusBlocks())

		UpdateMessageBlocks(blocks)
	}

	def PostFinalStatus()
	{
		def specificCause = context.currentBuild.getBuildCauses()[0].shortDescription.toString().replace('[', '').replace(']', '')

		def changesText = "*Building changes since ${lastSuccessCL}*"
		if(changes && changes.size() > 0)
		{
			for(def item : changes)
			{
				String changlist = item.get('change').toString()
				String author = item.get('user').toString()
				String description = item.get('desc').toString().replaceAll("[\r\n]+", "")
				changesText = "${changesText}\nCL-${changlist} by ${author}: ${description}"
			}
		}

		def statusIcon = 'white_circle'
		switch(context.currentBuild.currentResult)
		{
			case 'SUCCESS':
				statusIcon = 'large_green_circle'
				break
			case 'UNSTABLE':
				statusIcon = 'yellow_cicle'
				break;
			case 'FAILURE':
				statusIcon = 'red_circle'
				break
			case 'ABORTED':
				statusIcon = 'white_circle'
				break
		}

		def blocks = 
		[
			[
				"type": "header",
				"text": 
				[
					"type": "plain_text",
					"text": "${env.JOB_NAME}_${env.BUILD_ID}: ${specificCause}",
					"emoji": true
				]
			],
			[
				"type": "divider"
			],
			[
				"type": "section",
				"text": 
				[
					"type": "mrkdwn",
					"text": changesText
				]
			],
			[
				"type": "divider"
			],
			[
				"type": "section",
				"text": 
				[
					"type": "mrkdwn",
					"text": ":${statusIcon}: Build finished with result ${context.currentBuild.currentResult}"
				]
			]
		]

		UpdateMessageBlocks(blocks)
	}

	def PostParameters()
	{
		def blocks = 
		[
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
			def paramValue = "${param.value}"
			paramValue = paramValue.replaceAll("[\r\n]+", ",")
			
			if(paramValue == null || paramValue.isEmpty())
			{
				paramValue = 'NULL'
			}
			
			if(paramName != null && !paramName.isEmpty())
			{
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
								"text": paramValue
							]
						]
					]
				)
			}
		}
		
		PostBlockToThread(blocks)
	}

	def PostShelfList(shelfList)
	{
		def shelfFields = []
		for(def shelf : shelfList)
		{
			def shelfItem = context.p4.run('describe', '-S', shelf)[0] //Describe returns an array of dictionaries. One for each changelist. But since were only passing on than we only need sub 0
			String author = shelfItem.get('user').toString()
			String description = shelfItem.get('desc').toString().replaceAll("[\r\n]+", "")
			changesFields.add(
				[
					"type": "mrkdwn",
					"text": "CL${shelf} by ${author} - ${description}"
				]
			)
		}
		
		def blocks = [
			[
				"type": "header",
				"text": 
				[
					"type": "plain_text",
					"text": "Unshelving revisions from the following shelved changelists:",
					"emoji": true
				]
			],
			[
				"type": "section",
				"fields": changesFields		
			]
		]

		PostBlockToThread(blocks)
	}
}
