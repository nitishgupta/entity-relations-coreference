set -e

mvn -q compile exec:java -Dexec.args="-Xmx8g" -Dexec.mainClass=edu.illinois.cs.cogcomp.erc.main.Main -Dexec.args"$*"

