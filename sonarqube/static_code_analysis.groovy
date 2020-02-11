/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

def call(){

  // sonarqube api token
  cred_id = config.credential_id ?:
            "sonarqube"
  enforce = config.enforce_quality_gate ?:
            true


/*
env.sonarHome= tool name: 'scanner-2.4', type: 'hudson.plugins.sonar.SonarRunnerInstallation'

withSonarQubeEnv('sonar.installation') { // from SonarQube servers > name
  sh "${sonarHome}/bin/sonar-runner -Dsonar.host.url=${SONAR_HOST_URL}  -Dsonar.login=${SONAR_AUTH_TOKEN}    -Dsonar.projectName=xxx -Dsonar.projectVersion=xxx -Dsonar.projectKey=xxx -Dsonar.sources=."

}
*/

  stage("SonarQube Analysis"){
	node {
      withCredentials([usernamePassword(credentialsId: cred_id, passwordVariable: 'token', usernameVariable: 'user')]) {
        env.sonarHome= tool name: 'scanner-2.4', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
        withSonarQubeEnv("SonarQube"){
		  /*
          //unstash "workspace"
          try{ unstash "test-results" }catch(ex){}
          sh "mkdir -p empty"
		  */
		  env.REPO_NAME = env.REPO_NAME ?: "sonarQube"
          projectKey = "$env.JOB_NAME:$env.BRANCH_NAME".replaceAll("/", "_")
          projectName = "$env.JOB_NAME - $env.BRANCH_NAME"
          def script = """${sonarHome}/bin/sonar-scanner -X -Dsonar.login=${user} -Dsonar.password=${token} -Dsonar.projectKey="$projectKey" -Dsonar.projectName="$projectName" -Dsonar.projectBaseDir=. """
           
		  /*
          if (!fileExists("sonar-project.properties"))
            script += "-Dsonar.sources=\"./src\""
		  */
		  echo "script ${script}"

          sh script
            
        }
        timeout(time: 1, unit: 'HOURS') {
		  script {
		    sleep 10
            def qg = waitForQualityGate()
            if (qg.status != 'OK' && enforce) {
              error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
		  }
        }
      }
    }
  }
}
