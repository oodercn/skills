@echo off
setlocal enabledelayedexpansion

REM ========================================
REM Update ooder-spi-core pom.xml
REM Version: 3.0.3
REM ========================================

set TARGET=E:\github\ooder-sdk\skill\_base\ooder-spi-core\pom.xml

echo Updating ooder-spi-core pom.xml...

(
echo ^<?xml version="1.0" encoding="UTF-8"?^>
echo ^<project xmlns="http://maven.apache.org/POM/4.0.0"
echo          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
echo          xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"^>
echo     ^<modelVersion^>4.0.0^</modelVersion^>
echo.
echo     ^<parent^>
echo         ^<groupId^>net.ooder^</groupId^>
echo         ^<artifactId^>ooder-sdk-parent^</artifactId^>
echo         ^<version^>3.0.3^</version^>
echo     ^</parent^>
echo.
echo     ^<artifactId^>ooder-spi-core^</artifactId^>
echo     ^<packaging^>jar^</packaging^>
echo.
echo     ^<name^>Ooder SPI Core^</name^>
echo     ^<description^>OoderOS unified SPI core interfaces - IM/RAG/Workflow/Facade^</description^>
echo     ^<url^>https://github.com/oodercn/apexos^</url^>
echo.
echo     ^<organization^>
echo         ^<name^>ooder^</name^>
echo         ^<url^>https://ooder.cn^</url^>
echo     ^</organization^>
echo.
echo     ^<licenses^>
echo         ^<license^>
echo             ^<name^>MIT License^</name^>
echo             ^<url^>https://www.opensource.org/licenses/mit-license.php^</url^>
echo             ^<distribution^>repo^</distribution^>
echo         ^</license^>
echo     ^</licenses^>
echo.
echo     ^<developers^>
echo         ^<developer^>
echo             ^<name^>IhyTdX^</name^>
echo             ^<email^>18683731@qq.com^</email^>
echo             ^<organization^>ooder^</organization^>
echo             ^<organizationUrl^>https://ooder.cn^</organizationUrl^>
echo             ^<roles^>
echo                 ^<role^>Developer^</role^>
echo             ^</roles^>
echo             ^<timezone^>+8^</timezone^>
echo         ^</developer^>
echo     ^</developers^>
echo.
echo     ^<scm^>
echo         ^<url^>https://github.com/oodercn/apexos^</url^>
echo         ^<connection^>scm:git:https://github.com/oodercn/apexos.git^</connection^>
echo         ^<developerConnection^>scm:git:https://github.com/oodercn/apexos.git^</developerConnection^>
echo         ^<tag^>v3.0.3^</tag^>
echo     ^</scm^>
echo.
echo     ^<properties^>
echo         ^<maven.javadoc.skip^>false^</maven.javadoc.skip^>
echo         ^<maven.source.skip^>false^</maven.source.skip^>
echo         ^<gpg.skip^>false^</gpg.skip^>
echo     ^</properties^>
echo.
echo     ^<dependencies^>
echo         ^<dependency^>
echo             ^<groupId^>org.projectlombok^</groupId^>
echo             ^<artifactId^>lombok^</artifactId^>
echo             ^<version^>1.18.30^</version^>
echo             ^<scope^>provided^</scope^>
echo         ^</dependency^>
echo     ^</dependencies^>
echo.
echo     ^<build^>
echo         ^<plugins^>
echo             ^<plugin^>
echo                 ^<groupId^>org.apache.maven.plugins^</groupId^>
echo                 ^<artifactId^>maven-compiler-plugin^</artifactId^>
echo                 ^<version^>3.13.0^</version^>
echo                 ^<configuration^>
echo                     ^<source^>21^</source^>
echo                     ^<target^>21^</target^>
echo                     ^<encoding^>UTF-8^</encoding^>
echo                     ^<release^>21^</release^>
echo                 ^</configuration^>
echo             ^</plugin^>
echo.
echo             ^<plugin^>
echo                 ^<groupId^>org.apache.maven.plugins^</groupId^>
echo                 ^<artifactId^>maven-source-plugin^</artifactId^>
echo                 ^<version^>3.3.1^</version^>
echo                 ^<configuration^>
echo                     ^<skipSource^>${maven.source.skip}^</skipSource^>
echo                 ^</configuration^>
echo                 ^<executions^>
echo                     ^<execution^>
echo                         ^<id^>attach-sources^</id^>
echo                         ^<goals^>
echo                             ^<goal^>jar-no-fork^</goal^>
echo                         ^</goals^>
echo                     ^</execution^>
echo                 ^</executions^>
echo             ^</plugin^>
echo.
echo             ^<plugin^>
echo                 ^<groupId^>org.apache.maven.plugins^</groupId^>
echo                 ^<artifactId^>maven-javadoc-plugin^</artifactId^>
echo                 ^<version^>3.6.3^</version^>
echo                 ^<configuration^>
echo                     ^<skip^>${maven.javadoc.skip}^</skip^>
echo                     ^<source^>21^</source^>
echo                     ^<additionalJOption^>-Xdoclint:none^</additionalJOption^>
echo                     ^<failOnError^>false^</failOnError^>
echo                     ^<charset^>UTF-8^</charset^>
echo                     ^<encoding^>UTF-8^</encoding^>
echo                     ^<docencoding^>UTF-8^</docencoding^>
echo                 ^</configuration^>
echo                 ^<executions^>
echo                     ^<execution^>
echo                         ^<id^>attach-javadocs^</id^>
echo                         ^<goals^>
echo                             ^<goal^>jar^</goal^>
echo                         ^</goals^>
echo                     ^</execution^>
echo                 ^</executions^>
echo             ^</plugin^>
echo.
echo             ^<plugin^>
echo                 ^<groupId^>org.sonatype.central^</groupId^>
echo                 ^<artifactId^>central-publishing-maven-plugin^</artifactId^>
echo                 ^<version^>0.4.0^</version^>
echo                 ^<extensions^>true^</extensions^>
echo                 ^<configuration^>
echo                     ^<publishingServerId^>central^</publishingServerId^>
echo                     ^<tokenAuth^>true^</tokenAuth^>
echo                 ^</configuration^>
echo             ^</plugin^>
echo.
echo             ^<plugin^>
echo                 ^<groupId^>org.apache.maven.plugins^</groupId^>
echo                 ^<artifactId^>maven-gpg-plugin^</artifactId^>
echo                 ^<version^>3.2.4^</version^>
echo                 ^<configuration^>
echo                     ^<skip^>${gpg.skip}^</skip^>
echo                 ^</configuration^>
echo                 ^<executions^>
echo                     ^<execution^>
echo                         ^<id^>sign-artifacts^</id^>
echo                         ^<phase^>verify^</phase^>
echo                         ^<goals^>
echo                             ^<goal^>sign^</goal^>
echo                         ^</goals^>
echo                     ^</execution^>
echo                 ^</executions^>
echo             ^</plugin^>
echo.
echo             ^<plugin^>
echo                 ^<groupId^>org.apache.maven.plugins^</groupId^>
echo                 ^<artifactId^>maven-surefire-plugin^</artifactId^>
echo                 ^<version^>3.2.5^</version^>
echo                 ^<configuration^>
echo                     ^<skipTests^>true^</skipTests^>
echo                 ^</configuration^>
echo             ^</plugin^>
echo         ^</plugins^>
echo     ^</build^>
echo ^</project^>
) > "%TARGET%"

echo Done: %TARGET%
pause