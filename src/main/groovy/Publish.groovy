import groovy.xml.XmlSlurper
import org.apache.commons.io.FileUtils

class Publish {
    /**
     * Publish local maven repository to gitee
     * for example:
     * Publish org/grails/grails-datastore-gorm-mongodb ../maven
     * @param args
     */
    static void main(String[] args) {
        if (!args || args.size() < 2){
            println("Usage:")
            println("Publish [local maven repository directory] [maven repo]")
            println("Example:")
            println "Publish org/grails/grails-datastore-gorm-mongodb ../maven"
            return
        }
        String m2LibDir = args[0]
        println "Local maven repository directory:${m2LibDir}"

        def localMavenRepo = new File(args[1])
        println "localMavenRepo:${localMavenRepo.getAbsolutePath()}"

        def rootRepository = getRootRepository()
        println "maven root repository:${rootRepository}"
        gitPull(localMavenRepo)

        copyM2Lib(m2LibDir, rootRepository, localMavenRepo)
        gitCommit(localMavenRepo, m2LibDir)
        gitPush(localMavenRepo)
    }


    /**
     * Get the root repository, e.g.:/Users/jienyu/.m2/repository
     * @return
     */
    static private File getRootRepository(){
        def m2Dir = new File(System.getProperty("user.home"), ".m2")
        def settingFile = new File(m2Dir, "settings.xml")
        if (settingFile){
            def data = new XmlSlurper().parse(settingFile)
            return new File(data.localRepository.toString())
        }
        return new File(m2Dir, "repository")
    }

    static private gitPull(File workingDir){
        runCommand("git pull", workingDir)
    }

    static private runCommand(String cmd, File workingDir){
        def proc = cmd.execute([], workingDir)
        def sout = new StringBuilder()
        def serr = new StringBuilder()
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()
        if (sout){
            println sout
        }
        if (serr){
            println "Error on ${cmd}:" + serr
        }
    }

    /**
     *
     * Copy local maven repository from rootRepository to projectDir
     * @param depencyName e.g.: org/grails/grails-datastore-gorm-mongodb
     * @param rootRepository e.g.: /Users/jienyu/.m2/repository
     * @param projectDir e.g.: /Users/jienyu/Documents/project/maven
     */
    static private copyM2Lib(String m2LibDir, File rootRepository, File projectDir){
        def srcFile = new File(rootRepository, m2LibDir)
        def dstFile = new File(projectDir, m2LibDir)
        FileUtils.copyDirectory(srcFile, dstFile)
    }

    static private gitCommit(File projectDir, String dependencyName){
        runCommand("git add .", projectDir)
        runCommand("git commit -m \"${dependencyName}\"", projectDir)
    }

    static private gitPush(File projectDir){
        runCommand("git push", projectDir)
    }
}
