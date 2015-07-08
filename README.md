# AndroidUtils
splash screen, animation, more content --- soon

'''
utils


#how to migrate maven to gradle files ? 
here's example:

#maven:

< dependency >
  < groupId > org.scribe < / groupId >
  < artifactId > scribe< / artifactId >
  < version>1.3.6< / version > 
< / dependency >

#gradle:

build.gradle(project)

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.2.3'
   }
}

build.gradle(module)


allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}dependicies{
  compile group: 'org.scribe',name: 'scribe', version: '1.3.6'
}
