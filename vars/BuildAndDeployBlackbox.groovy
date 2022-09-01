#!/usr/bin/env groovy

def call() 
{
	script
	{	
		blackboxBuildChannel = env["${JOB_NAME}_BuildChannel"]		
		bat("attrib -r ${blackboxGamePath}Config/Blackbox.ini".toString())
		bat("${blackboxCLI} version add --name ${blackboxVersion} ${blackboxBaseParameters}".toString())								
		bat("${blackboxCLI} version set --name ${blackboxVersion} ${blackboxBaseParameters}".toString())

		bat("${UAT} BuildGraph -Script=BuildPipeline/BuildWindowsClient.xml -Target=\"BuildWindowsClient\" -set:UProjectPath=${env.PROJECT_PATH} -set:BuildNumber=${env.BUILD_NUMBER} -set:Configuration=${params.ClientConfiguration} -set:Iterate=${params.ITERATE_COOK} -set:StreamName=${P4Stream}")
		
		bat("${blackboxCLI} build register --platform-name ${BB_PlatformName} --platform-arch x64 ${blackboxBaseParameters}".toString())
		bat("${blackboxCLI} build upload-binaries --entry-point Invasion.exe --game-archive ${blackboxBuildPath} ${blackboxBaseParameters} --no-log".toString())
	
		bat("${blackboxCLI} build-channel update --stream-id ${blackboxBuildChannel} --as-head ${blackboxBaseParameters}".toString())
		slackUtils.PostToThread("Published client to Blackbox Launcher.".toString())
	}
}