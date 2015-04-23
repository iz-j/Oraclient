Database gui tool via JavaFX & Web
=====

### How to develop at local?

* Get Gradle and Java8.
* `gradle eclipse` at root directory.
* Import to Eclipse

### How to run at local?

* Launch `iz.dbui.DbUiMain`.
* Or launch `iz.dbui.DbUiServerMain` and access to `localhost:8888`.


### How to build?

* `set JAVA_HOME={Java8}`
* `gradle build`, then jar will be created in `/dist`.
* If you want to wrap jar into exe, use ** /dist/launch4j/launch4j.exe ** .
