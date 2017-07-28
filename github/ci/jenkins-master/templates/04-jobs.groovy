job('kubevirt-functional-tests') {
    throttleConcurrentBuilds {
        maxTotal(0)
        maxPerNode(1)
    }
    parameters {
        stringParam('sha1', '', 'commit to build')
    }
    scm {
        git {
            remote {
                github('{{ githubRepo }}')
                refspec('+refs/pull/*:refs/remotes/origin/pr/*')
            }
            branch('${sha1}')
            extensions {
                relativeTargetDirectory('go/src/kubevirt.io/kubevirt')
            }
        }
    }
    triggers {
        githubPullRequest {
            admins(['rmohr', 'fabiand', 'stu-gott', 'admiyo', 'davidvossel', 'vladikr', 'berrange'])
            cron('H/2 * * * *')
            triggerPhrase('OK to test')
            allowMembersOfWhitelistedOrgsAsAdmin()
            extensions {
                commitStatus {
                    context('kubevirt-functional-tests/jenkins/pr')
                    triggeredStatus('kubevirt-functional-tests/jenkins/pr')
                    startedStatus('kubevirt-functional-tests/jenkins/pr')
                    statusUrl('https://github.com/kubevirt/kubevirt')
                    completedStatus('SUCCESS', 'All is well')
                    completedStatus('FAILURE', 'Something went wrong. Investigate!')
                    completedStatus('PENDING', 'still in progress...')
                    completedStatus('ERROR', 'Something went really wrong. Investigate!')
                }
            }
        }
    }
    steps {
        shell('cd go/src/kubevirt.io/kubevirt && bash automation/test.sh')
    }
}
