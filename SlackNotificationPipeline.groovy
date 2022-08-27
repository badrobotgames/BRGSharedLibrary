@Library('BRGSharedLibrary')_

pipeline
{
	agent any

	parameters
	{
		booleanParam name: 'SKIP', defaultValue: false, description: 'Test a skip'
		booleanParam name: 'FAIL', defaultValue: false, description: 'Test a failure'
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
					slackUtils = new org.BRG.SlackUtils(this)
					slackUtils.Initialize()
					slackUtils.PostStatus()
					sleep 10
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
					slackUtils.PostStatus()
					sleep 11
				}
			}
		}	
		
		stage('Step 2')
		{
			steps
			{			
				script
				{
					echo 'Step 2'
					slackUtils.PostStatus()
					sleep 12
				}
			}
		}
		
		stage('Step 3')
		{
			when
			{
				expression
				{
					params.SKIP != true
				}
			}
			steps
			{			
				script
				{
					echo 'Step 3'
					slackUtils.PostStatus()
					sleep 13
				}
			}
		}
		
		stage('Step 4')
		{
			steps
			{			
				script
				{
					if(params.FAIL)
					{
						error("This pipeline stops here!")
					}
					sleep 14
				}
			}
		}
	}
	
	post
	{
		always
		{			
			script
			{
				slackUtils.PostStatus()
			}
		}
		failure
		{
			script
			{
				sh("cp \"${env.JENKINS_HOME}/jobs/${env.JOB_NAME}/builds/${env.BUILD_NUMBER}/log\" console-log.txt".toString())
				slackUtils.UploadToThread('console-log.txt')
			}
		}
	}
}

//def SlackLog(message) 
//{
//	return SlackMessage(message, '32a852')
//}
//def SlackWarn(message) 
//{
//	return SlackMessage(message, 'e4be3a')
//}
//def SlackError(message) 
//{
//	return SlackMessage(message, 'CC1111')
//}

