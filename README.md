# CodeSparks Demo Plugin

This is a demo implementation of a CodeSparks plugin for the IntelliJ IDEA that can be used as a template for further
plugins. It makes use of the [PMD Java API](https://pmd.sourceforge.io/pmd-6.29.0/pmd_userdocs_tools_java_api.html) to
calculate the cyclomatic complexity of Java classes and methods in the current project. Using
the [CodeSparks framework](https://github.com/segroup-uni-trier/codesparks-core), the source code is augmented with
interactive glyph-based visualizations that display the values of the cyclomatic complexity and an interpretation of
them.

## Build

Prerequisites:

1. Apache Ant (>=1.10)
2. JDK 11
3. IntelliJ IDEA Community Edition 2022.2.4 (or comparable)
4. Specify the path to the IntelliJ IDEA installation directory in the `idea.properties` file.

To create the actual CodeSparks plugin run `ant` (default target=`zip`) in the terminal.

## Install

Download the ZIP file from the release section or build it yourself.

The ZIP file can then be installed in IntelliJ IDEA:

1. File &rarr; Settings &rarr; Plugins
2. Install Plugin from Disk...
3. Choose Plugin File, i.e. the ZIP file created with ant or downloaded from the release section.
4. OK &rarr; Apply &rarr; OK

Note, the actions of this CodeSparks plugin are added to the toolbar which might be hidden dependening on your
preferences. To show the toolbar:

1. View &rarr; Appearance &rarr; Toolbar

## Tested under

Operating systems:

1. Windows 10 22H2 64-bit, Build 19045.2364
2. Linux Mint 21 Vanessa 64-bit, Kernel 5.15.0-56-generic x86_64

IntelliJ IDEA versions:

1. 2022.2.4 Community Edition (Build #IC-222.4459.24, built on November 22, 2022)

## License

The CodeSparks Demo Plugin is Open Source software released under the
[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0). Note, some parts of this software, such as the 
compile and runtime dependencies, may have different licenses (see NOTICE.txt).
