#!/usr/bin/env groovy
import com.vidal.jenkins.*

pipeline {
   agent {
      docker {
         image 'ci/mvn-chrome:2.0.0'
         args "--volume /data/repository:/tmp/mvn-repo --name=lz-string4java-${BRANCH_NAME}-${env.BUILD_ID} -v /var/run/docker.sock:/var/run/docker.sock --group-add docker-slave"
      }
   }
   options {
      disableConcurrentBuilds()
      buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
   }
   stages {
      stage('Prepare') {
         when {
            expression { Branches.isPullRequest(BRANCH_NAME) }
         }
         steps {
            cancelPreviousBuild()
         }
      }

      stage('Build and Tests') {
         steps {
            mvn 'clean verify'
            junit '**/target/surefire-reports/TEST-*.xml'
         }
      }

      stage('Share (Nexus)') {
         when {
            anyOf {
               branch 'master'
               branch 'sprint-*'
            }
            expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
         }
         steps {
            mvn 'deploy -DskipTests'
         }
      }
   }
}