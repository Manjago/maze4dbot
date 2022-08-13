#!/usr/bin/env bash
if [ -e "PID" ]
 then
  PID=`cat PID`
fi


if [ -n "$PID" ]
 then
   kill -s SIGKILL $PID
   rm PID
   echo bot stopped
 else
   echo no PID file, bot not stoped
fi
