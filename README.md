# AndroidUtils
splash screen, animation, more content --- soon

'''
utils


#how to migrate maven to gradle files ? 
here's example:

maven:Id>org.scribe</groupId>
  <artifactId>scribe</artifactId>
  <version>1.3.6</version> // please use always the latest version
</dependency>

gradle:
build.gradle(project)

build.gradle (module)
'''buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.2.3'
   }
}''''

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}dependicies{
  compile group: 'org.scribe',name: 'scribe', version: '1.3.6'
}
