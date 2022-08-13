#!/usr/bin/env bash

#JAVA_HOME="/opt/jdk1.8.0_121"

if [ -e "PID" ]
 then
 PID=`cat PID`
fi

if [ -n "$PID" ]
 then
   echo bot already running
   exit
fi

LOGDIR="logs"

nohup $JAVA_HOME/bin/java -Xmx100m -Dlogback.configurationFile=logback.xml -Djava.awt.headless=true -jar maze4dbot-${project.version}.jar production.properties 1>$LOGDIR/out 2>$LOGDIR/err & echo $!>PID;

