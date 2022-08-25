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
					def allStages = GetAllStages()

				}
			}
		}	
		
		stage('Step 1')
		{
			steps
			{			
				script
				{
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
	if(slackMessage)
	{
		slackMessage = slackSend(channel: 'invasion-builds', message: "${env.JOB_NAME}_${env.BUILD_ID}: ${message}".toString(), color: "${color}".toString())
	}
}

def UpdateSlackStatus()
{
	def updateMessage = "${env.JOB_NAME}_${env.BUILD_ID} Status:"
	allStages.each { stage ->
		updateMesage = "${updateMessage}/n   - ${stage.displayName} : ${stage.status}"
	}

	echo updateMessage
}