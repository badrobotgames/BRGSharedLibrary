@Library('BRGSharedLibrary')_

pipeline
{
	agent any

	parameters
	{
		booleanParam name: 'SKIP', defaultValue: false, description: 'Test a skip'
		booleanParam name: 'FAIL', defaultValue: false, description: 'Test a failure'

		
		text name: 'PERFORCE_SHELVES', defaultValue: '', description: 'To make a build against a given shelved Changelist, list each CL number on a new line.'

		choice name: 'EditorConfiguration', choices: ['Development', 'Debug', 'DebugGame', 'Shipping', 'Test']
		booleanParam name: 'BUILD_EDITOR', defaultValue: true, description: 'Probably always want this set?'
		booleanParam name: 'ALLOW_DEPLOY_EDITOR', defaultValue: true, description: 'Deploy editor to UGS after building it if there are code changes (requires BUILD_EDITOR)'
		booleanParam name: 'FORCE_DEPLOY_EDITOR', defaultValue: false, description: 'Deploy editor whether there were code changes or not (requires BUILD_EDITOR)'

		choice name: 'ClientConfiguration', choices: ['Test', 'Debug', 'Development', 'DebugGame', 'Shipping']
		booleanParam name: 'BUILD_CLIENT', defaultValue: true, description: 'Build a standalone client'
		booleanParam name: 'DEPLOY_BLACKBOX', defaultValue: true, description: 'Deploy client to Blackbox after building it'
		booleanParam name: 'DEPLOY_BLACKBOX_SMARTBUILD', defaultValue: false, description: 'Deploy client to Blackbox SmartBuild after building it'
		booleanParam name: 'DEPLOY_GDRIVE', defaultValue: false, description: 'Deploy client to GDrive after building it'

		choice name: 'ServerConfiguration', choices: ['Test', 'Debug', 'Development', 'DebugGame', 'Shipping']
		booleanParam name: 'BUILD_SERVER', defaultValue: true, description: 'Build a standalone server'
		booleanParam name: 'DEPLOY_SERVER', defaultValue: true, description: 'Deploy server to AWS after building it'

		string name: 'BB_APIKey', defaultValue: '7cc6c97153cc40308d6e05547bf3d211jsK97ssx2bWEiS9btE3HwoYqTn86aAin', description: 'Blackbox Build API Key'
		string name: 'BB_Namespace', defaultValue: 'badrobotgames', description: 'Blackbox Build Namespace'
		choice name: 'BB_PlatformName', choices: ['windows', 'linux'], description: 'Blackbox Build Platform'

		booleanParam name: 'ITERATE_COOK', defaultValue: true, description: 'Add -iterate to cook command line for faster builds'
		booleanParam name: 'CLEAN_BUILD', defaultValue: false, description: 'Force a perforce resync (SLOW)'
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
					slackUtils = new org.BRG.SlackUtils(this, false)
					slackUtils.Initialize()
					slackUtils.PostStatus()

					changes = 
					[
						[
							'change': 323,
							'user': 'kara',
							'desc': 'this is a change'
						],
						[
							'change': 3323,
							'user': 'jenkins',
							'desc': 'this ifdsafsas a change'
						],
						[
							'change': 3423,
							'user': 'kara',
							'desc': 'this is a chafdsafnge'
						],
						[
							'change': 2323,
							'user': 'blarg',
							'desc': 'this is afsdafs change'
						]
					]
					slackUtils.PostChanges(100, changes)

					slackUtils.PostParameters()
					sleep 1
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
					sleep 1
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
					sleep 2
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
					sleep 3
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
					sleep 4
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