@/* This is a test script demonstrating the creation of a project with these forge plugins */;
@/* run this by starting a forge console & write 'run forge-plugin-example.fsh' */;
@/* OBS ! For some reason the script hangs when running in the eclipse shell, but works fine in a separate console window. */;

@/* Clear the screen */;
clear;

@/* This means less typing. If a script is automated, or is not meant to be interactive, use this command */;
set ACCEPT_DEFAULTS true;

@/* Create root project */;
new-project --named forge-plugin-test --topLevelPackage se.comp.test;

forge-plugin-prj setup;

forge-plugin-prj add-plugin --name Test --path ~.test --alias qtest;
