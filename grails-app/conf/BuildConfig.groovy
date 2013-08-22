//grails.project.class.dir = "target/classes"
//grails.project.test.class.dir = "target/test-classes"
//grails.project.test.reports.dir = "target/test-reports"
grails.project.plugins.dir = "../local-plugins/risk-analytics-pc-master"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve true // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
        grailsHome()
        grailsCentral()
        mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
    }

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:2.2.1"
        runtime ":joda-time:0.5"
        runtime ":maven-publisher:0.7.5"
        runtime ":quartz:0.4.2"
        runtime ":spring-security-core:1.2.7.3"
        runtime ":tomcat:2.2.1"

        test ":code-coverage:1.2.4"
        compile ":excel-import:1.0.0"

        if (appName == "risk-analytics-pc") {
            runtime "org.pillarone:risk-analytics-core:1.8-a2"
            runtime("org.pillarone:risk-analytics-commons:1.8-a1") { transitive = false }
        }
    }

    dependencies {
        test 'hsqldb:hsqldb:1.8.0.10'
    }
}

//grails.plugin.location.'risk-analytics-core' = "../risk-analytics-core"
//grails.plugin.location.'risk-analytics-commons' = "../risk-analytics-commons"

grails.project.dependency.distribution = {
    String password = ""
    String user = ""
    String scpUrl = ""
    try {
        Properties properties = new Properties()
        properties.load(new File("${userHome}/deployInfo.properties").newInputStream())

        user = properties.get("user")
        password = properties.get("password")
        scpUrl = properties.get("url")
    } catch (Throwable t) {
    }
    remoteRepository(id: "pillarone", url: scpUrl) {
        authentication username: user, password: password
    }
}

coverage {
    exclusions = [
        'models/**',
        '**/*Test*',
        '**/com/energizedwork/grails/plugins/jodatime/**',
        '**/grails/util/**',
        '**/org/codehaus/**',
        '**/org/grails/**',
        '**GrailsPlugin**',
        '**TagLib**'
    ]

}