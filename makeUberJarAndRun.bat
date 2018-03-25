set MYWAR=build\libs\filemanager.war
rem set JAR=c:\payara41\payara-micro-4.1.2.181.jar
set JAR=c:\payara5\payara-micro-5.181.jar
set APP=app.jar

rem java -jar %JAR% --deploy %MYWAR%

echo Build uber jar

java -jar %JAR% --deploy %MYWAR% --outputuberjar %APP%

echo Run app.jar

java -jar %APP%


