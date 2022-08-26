@Library('BRGSharedLibrary')_

pipeline
{
	agent any
	 
	environment 
	{
		slackMessage=null
    }

	stages
	{
		stage('Initialize')
		{
			steps
			{			
				script
				{
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
	if(env.slackMessage == null)
	{
		echo 'blarg'
		env.slackMessage = slackSend(channel: 'invasion-builds', message: "${env.JOB_NAME}_${env.BUILD_ID}: ${message}".toString(), color: "${color}".toString())
	}
	else
	{
		env.slackMessage = slackSend(channel: slackMessage.channelId, filePath: 'console-log.txt')
	}
}

def UpdateSlackStatus()
{
	def updateMessage = GetAllStagesStatus()
	SlackLog(updateMessage.toString())
	echo updateMessage
}