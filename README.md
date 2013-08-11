forge-prj-plugin
================

A JBoss Forge plugin to create Forge plugin projects

This JBoss Forge plugin is used if you want to create a JBoss Forge plugin.
It simplifies starting up to create new Forge plugins.

Usage of the plugin commands
----------------------------

It has 2 commands:
* setup
* add-plugin

### Command: setup
The **setup** command does the following:
* adds all needed dependencies to the pom.xml
* Adds a beans.xml file
* Adds a forge.xml file

### Command: add-plugin
The **add-plugin** command does the following:
* adds a Facet  class
* adds a Plugin class using the created facet
* Adds a example resources that are used by the generated plugin/facet
* Adds a Completer & Type class to show the completer concept

#### Parameter: name
The wanted name of the Plugin/Facet, these classes will be prefixed by this name. [Required]

#### Parameter: alias
The alias the plugin should have, this the command you will use in the forge console when using the plugin. [Required]

#### Parameter: path
Where the plugin should be created (under java/src), if this isn't suplpied the default package name will be used.

You can use ~ to refer to the default project package name (ex: ~.plugins)

### Test script

To test to create a Forge plugin project run the **forge-plugin-example.fsh** file in a forge shell after installing this plugin.

Otherwise this script can be used as an example on how to use the plugin.


