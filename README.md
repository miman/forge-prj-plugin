forge-prj-plugin
================

A JBoss Forge plugin to create Forge plugin projects

This JBoss Forge plugin is used if you want to create a JBoss Forge plugin.
It simplifies starting up to create new Forge plugins.

It has 2 commands:
* setup
* add-plugin

The **setup** command does the following:
* adds all needed dependencies to the pom.xml
* Adds a beans.xml file
* Adds a forge.xml file

The **add-plugin** command does the following:
* adds a Facet  class
* adds a Plugin class using the created facet
* Adds a example resources that are used by the generated plugin/facet
* Adds a Completer & Type class to show the completer concept


To test to create a Forge plugin project run the **forge-plugin-example.fsh** file in a forge shell after installing this plugin.


OBS ! - Needed Maven project dependency
---------------------------------------
This project has a dependency to the **miman-forge-plugin-util** project.
So to build this project you must download (and build) version 1.0.0 of this project first
[Found here](https://github.com/mikaelth/miman-forge-plugin-util)
