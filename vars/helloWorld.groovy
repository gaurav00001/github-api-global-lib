#!groovy

// imports
import hudson.plugins.git.*
import hudson.plugins.git.extensions.*
import hudson.plugins.git.extensions.impl.*
import jenkins.model.Jenkins

// parameters
def jobParameters = [
  name:          'MyJob',
  description:   'Build of my STG environment : https://stg.mycompany.com',
  repository:    'git@github.com:my-company/my-repo.git',
  branch:        'master',
  credentialId:  'jenkins-key'
]

// define repo configuration
def branchConfig                =   [new BranchSpec(jobParameters.branch)]
def userConfig                  =   [new UserRemoteConfig(jobParameters.repository, null, null, jobParameters.credentialId)]
def cleanBeforeCheckOutConfig   =   new CleanBeforeCheckout()
def sparseCheckoutPathConfig    =   new SparseCheckoutPaths([new SparseCheckoutPath("Jenkinsfile")])
def cloneConfig                 =   new CloneOption(true, true, null, 3)
def extensionsConfig            =   [cleanBeforeCheckOutConfig,sparseCheckoutPathConfig,cloneConfig]
def scm                         =   new GitSCM(userConfig, branchConfig, false, [], null, null, extensionsConfig)

// define SCM flow
def flowDefinition = new org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition(scm, "Jenkinsfile")

// set lightweight checkout
flowDefinition.setLightweight(true)

// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// create the job
def job = new org.jenkinsci.plugins.workflow.job.WorkflowJob(jenkins,jobParameters.name)

// define job type
job.definition = flowDefinition

// set job description
job.setDescription(jobParameters.description)

// save to disk
jenkins.save()
jenkins.reload()
