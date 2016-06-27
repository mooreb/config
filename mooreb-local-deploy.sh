#!/bin/bash -x

# This is for mooreb's local tomcat deployment. YMMV

mvn clean package
if [[ $? -ne 0 ]]; then
    exit 1
fi

catalina.sh stop

rm -rf ${CATALINA_HOME}/webapps/config ${CATALINA_HOME}/webapps/config.war
rm -rf ${CATALINA_HOME}/webapps/ROOT ${CATALINA_HOME}/webapps/ROOT.war
rm -rf ${CATALINA_HOME}/webapps/exampleconfigclient ${CATALINA_HOME}/webapps/exampleconfigclient.war

W1=example-client/target/example-config-client-*.war 
W2=war/target/config-service-*.war

cp ${W1} ${CATALINA_HOME}/webapps/exampleconfigclient.war
cp ${W2} ${CATALINA_HOME}/webapps/config.war

catalina.sh run
