Embeddable-Badges-Plugin
==============================
[![Build Status](https://travis-ci.org/SxMShaDoW/embeddable-badges-plugin.svg?branch=master)](https://travis-ci.org/SxMShaDoW/embeddable-badges-plugin) [![codecov.io](https://codecov.io/github/SxMShaDoW/embeddable-badges-plugin/coverage.svg?branch=master)](https://codecov.io/github/SxMShaDoW/embeddable-badges-plugin?branch=master) [![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/SxMShaDoW/embeddable-badges-plugin)

Inspired and based off of the the [Embeddable-Build-Status-Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Embeddable+Build+Status+Plugin), this project cleans up some of the URL types and adds in additional fun badges!

# Versions

See our [CHANGELOG](CHANGELOG.md) for a history of available versions and changes.

# Manual Installation

This plugin is not yet available for installation via Jenkins Plugin Manager (Manage Jenkins -> Manage Plugins), and requires manual installation.  The following steps are how to install it manually.

1. Sync the latest code line, or from one of our git tags.
2. Run `mvn clean package` locally, this generates a `target/embeddable-badges.hpi` file that you can manually install.
3. On the `Advanced` tab of the Jenkins plugin manager (`http://<your jenkins host>/pluginManager/advanced`), browse to the generated `target/embeddable-badges.hpi` file under the `Upload Plugin` section.
4. Click Upload.
5. Restart Jenkins.
6. Profit.

# Badge Types

### Build Status
Reports success or failure of the latest build.

### Code Coverage
Reports a coverage percentage for the following technologies:

1. Clover: Line coverage percentage
2. Cobertura: Element coverage percentage
3. Jacoco: Instruction coverage percentage

### Unit Tests
Number of JUnit or TestNG tests executed successfully.

### Description
With the [build description setter](https://wiki.jenkins-ci.org/display/JENKINS/Description+Setter+Plugin) plugin, folks often create regex's to post the version of the module that was created as a build description.  This badge will display whatever that description is.

# License

[MIT](LICENSE)
