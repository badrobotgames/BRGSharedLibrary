#!/usr/bin/env groovy

def call() 
{
	script
	{	
		UBT = 'Invasion/Engine/Build/BatchFiles/Build.bat'
		UAT = 'Invasion/Engine/Build/BatchFiles/RunUAT.bat'

		P4Stream=env["${JOB_NAME}_Stream"]
		blackboxCLI = "${env.WORKSPACE}/Invasion/BuildPipeline/Blackbox/BlackBoxCLI.exe"
		blackboxGamePath = "${env.WORKSPACE}/Invasion/Invasion/"
		blackboxEnginePath = "${env.WORKSPACE}/Invasion/"
		blackboxBuildPath = "${env.WORKSPACE}/Invasion/Invasion/Saved/StagedBuilds/WindowsClient/"
		blackboxBaseParameters = "--namespace ${params.BB_Namespace} --apikey ${params.BB_APIKey} --game-engine ${blackboxEnginePath} --game-project ${blackboxGamePath}"
		blackboxEditorVersion = "Editor_${env.BUILD_ID}_${env.P4_CHANGELIST}"
		blackboxVersion = "${P4Stream}_${env.BUILD_ID}_${env.P4_CHANGELIST}"
		
		blackboxBuildChannel = env["${JOB_NAME}_BuildChannel"]		
		bat("attrib -r ${env.blackboxGamePath}Config/Blackbox.ini".toString())
		bat("${env.blackboxCLI} version add --name ${env.blackboxVersion} ${env.blackboxBaseParameters}".toString())								
		bat("${env.blackboxCLI} version set --name ${env.blackboxVersion} ${env.blackboxBaseParameters}".toString())

		bat("${env.UAT} BuildGraph -Script=BuildPipeline/BuildWindowsClient.xml -Target=\"BuildWindowsClient\" -set:UProjectPath=${env.PROJECT_PATH} -set:BuildNumber=${env.BUILD_NUMBER} -set:Configuration=${params.ClientConfiguration} -set:Iterate=${params.ITERATE_COOK} -set:StreamName=${P4Stream}")
		
		bat("${env.blackboxCLI} build register --platform-name ${BB_PlatformName} --platform-arch x64 ${env.blackboxBaseParameters}".toString())
		bat("${env.blackboxCLI} build upload-binaries --entry-point Invasion.exe --game-archive ${env.blackboxBuildPath} ${env.blackboxBaseParameters} --no-log".toString())
	
		bat("${env.blackboxCLI} build-channel update --stream-id ${env.blackboxBuildChannel} --as-head ${env.blackboxBaseParameters}".toString())
		slackUtils.PostToThread("Published client to Blackbox Launcher.".toString())
	}
}