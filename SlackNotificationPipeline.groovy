@Library('BRGSharedLibrary')_

pipeline
{
	agent any

	stages
	{
		stage('Initialize')
		{
			steps
			{			
				script
				{
					slackMessage = null
					hasSlackMessage = false
					echo "Initialize"
					UpdateSlackStatus()
				}
			}
		}	
		
		stage('Step 1')
		{
			steps
			{			
				script
				{
					echo 'Step 1'
					UpdateSlackStatus()
				}
			}
		}	
		
		stage('Step 2')
		{
			steps
			{			
				script
				{
					echo 'Step 1'
					UpdateSlackStatus()
				}
			}
		}
	}
}

def SlackLog(message) 
{
	return SlackMessage(message, '32a852')
}
def SlackWarn(message) 
{
	return SlackMessage(message, 'e4be3a')
}
def SlackError(message) 
{
	return SlackMessage(message, 'CC1111')
}
def SlackMessage(message, color) 
{
	if(hasSlackMessage)
	{
		slackSend(channel: slackMessage.channelId, message: "${env.JOB_NAME}_${env.BUILD_ID}: ${message}".toString(), color: "${color}".toString())
	}
	else
	{
		echo 'blarg'
		slackMessage = slackSend(channel: 'invasion-builds', message: "${env.JOB_NAME}_${env.BUILD_ID}: ${message}".toString(), color: "${color}".toString())
		hasSlackMessage = true
	}
}

def UpdateSlackStatus()
{
	def updateMessage = GetAllStagesStatus()
	SlackLog(updateMessage.toString())
	echo updateMessage
}