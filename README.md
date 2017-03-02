#### Synopsis
This is an Maven project implementing an ImageJ plugin for Cell analysis. This project is carried out by two [ISIMA](http://www.isima.fr) students for the laboratory of INRA Clermont-Ferrand.

#### Download 
To use the plugin download this [jar file](../blob/master/docs/uv-characterization-plugin.jar), and copy it intp images plugins/ directory.

#### Installation 

You can even collaborate with developers using a
different IDE :

* In [Eclipse](http://eclipse.org), for example, it is as simple as
  _File&gt;Import...&gt;Existing Maven Project_.

* In [NetBeans](http://netbeans.org), it is even simpler:
  _File&gt;Open Project_.

* The same works in [IntelliJ](http://jetbrains.net).

* If [jEdit](http://jedit.org) is your preferred IDE, you will need the
  [Maven Plugin](http://plugins.jedit.org/plugins/?MavenPlugin).
  

To copy the artifact into the correct place, you can call
`mvn -Dimagej.app.directory=/path/to/ImageJ.app/`.
This will not only copy your artifact, but also all the dependencies. Restart
your ImageJ or call *Help>Refresh Menus* to see your plugin in the menus.


##### Eclipse: To ensure that Maven copies the plugin to your ImageJ folder

1. Go to _Run Configurations..._
2. Choose _Maven Build_
3. Add the following parameter:
    - name: `imagej.app.directory`
    - value: `/path/to/ImageJ.app/`

This ensures that the final `.jar` file will also be copied to your ImageJ
plugins folder everytime you run the Maven Build
