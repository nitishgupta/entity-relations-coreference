#mvn clean
#mvn dependency:copy-dependencies
mvn compile

DEFAULT_PACKAGE="edu.illinois.cs.cogcomp"
PACKAGE="main"
MAINCLASS="Main"
CP="./:./target/classes/:./target/dependency/*:./config/:target/dependency/*"
java -Xmx8g -cp $CP $DEFAULT_PACKAGE.$PACKAGE.$MAINCLASS