set -e

#mvn clean
#mvn dependency:copy-dependencies

mvn -q compile exec:java -Dexec.arguments="-Xmx8g" -Dexec.mainClass=edu.illinois.cs.cogcomp.erc.sl.ner.MainClass -Dexec.args="$*"

