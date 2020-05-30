#!/usr/bin/env sh

##############################################################################
##
##  com.systex.sysgateii.ratesvr start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
GATEWAY_NAME="ratesvr"
MAINCLASS="com.systex.sysgateii.server.RateServer"
GATEWAY_PIDFILE="RATESVRPIDfile"
if [ -z "$GATEWAY_KILL_MAXSECONDS" ]; then
   GATEWAY_KILL_MAXSECONDS=10
fi

PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/.." >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="com.systex.sysgateii.ratesvr"
APP_BASE_NAME=`basename "$0"`


# Add default JVM options here. You can also use JAVA_OPTS and COM_SYSTEX_SYSGATEII_RATESVR_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/lib/com.systex.sysgateii.ratesvr.jar:$APP_HOME/lib/commons-beanutils-1.9.3.jar:$APP_HOME/lib/commons-beanutils-1.9.3-javadoc.jar:$APP_HOME/lib/commons-beanutils-1.9.3-sources.jar:$APP_HOME/lib/commons-configuration2-2.5.jar:$APP_HOME/lib/commons-lang3-3.9.jar:$APP_HOME/lib/commons-logging-1.2.jar:$APP_HOME/lib/commons-text-1.6.jar:$APP_HOME/lib/groovy-2.5.7.jar:$APP_HOME/lib/hamcrest-core-1.3.jar:$APP_HOME/lib/junit-4.12.jar:$APP_HOME/lib/logback-classic-1.2.3.jar:$APP_HOME/lib/logback-core-1.2.3.jar:$APP_HOME/lib/logback-core-1.2.3-sources.jar:$APP_HOME/lib/slf4j-api-1.7.26.jar:$APP_HOME/lib/slf4j-api-1.7.26-sources.jar:$APP_HOME/lib/netty-all-4.1.33.Final.jar:$APP_HOME/lib/commons-io-2.6.jar:$APP_HOME/lib/commons-math3-3.6.1.jar:$APP_HOME/lib/guava-27.0.1-jre.jar:$APP_HOME/lib/failureaccess-1.0.1.jar:$APP_HOME/lib/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:$APP_HOME/lib/jsr305-3.0.2.jar:$APP_HOME/lib/checker-qual-2.5.2.jar:$APP_HOME/lib/error_prone_annotations-2.2.0.jar:$APP_HOME/lib/j2objc-annotations-1.1.jar:$APP_HOME/lib/animal-sniffer-annotations-1.17.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$nonstop" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# For Darwin, add options to specify how the application appears in the dock
if $darwin; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`
    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d 2>/dev/null`
    SEP=""
    for dir in $ROOTDIRSRAW ; do
        ROOTDIRS="$ROOTDIRS$SEP$dir"
        SEP="|"
    done
    OURCYGPATTERN="(^($ROOTDIRS))"
    # Add a user-defined pattern to the cygpath arguments
    if [ "$GRADLE_CYGPATTERN" != "" ] ; then
        OURCYGPATTERN="$OURCYGPATTERN|($GRADLE_CYGPATTERN)"
    fi
    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    i=0
    for arg in "$@" ; do
        CHECK=`echo "$arg"|egrep -c "$OURCYGPATTERN" -`
        CHECK2=`echo "$arg"|egrep -c "^-"`                                 ### Determine if an option

        if [ $CHECK -ne 0 ] && [ $CHECK2 -eq 0 ] ; then                    ### Added a condition
            eval `echo args$i`=`cygpath --path --ignore --mixed "$arg"`
        else
            eval `echo args$i`="\"$arg\""
        fi
        i=$((i+1))
    done
    case $i in
        (0) set -- ;;
        (1) set -- "$args0" ;;
        (2) set -- "$args0" "$args1" ;;
        (3) set -- "$args0" "$args1" "$args2" ;;
        (4) set -- "$args0" "$args1" "$args2" "$args3" ;;
        (5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
        (6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
        (7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
        (8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
        (9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
    esac
fi

# Escape application args
save () {
    for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
    echo " "
}

APP_ARGS=$(save "$@")

# Collect all arguments for the java command, following the shell quoting and substitution rules
eval set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $COM_SYSTEX_SYSGATEII_RATESVR_OPTS -classpath "\"$CLASSPATH\"" com.systex.sysgateii.server.RateServer "$APP_ARGS"

# by default we should be in the correct project dir, but when run from Finder on Mac, the cwd is wrong
if [ "$(uname)" = "Darwin" ] && [ "$HOME" = "$PWD" ]; then
  cd "$(dirname "$0")"
fi

#exec "$JAVACMD" "$@"
#nohup  "$JAVACMD" "$@" </dev/null >ratesvr.log 2>&1 &


invokeJar(){
   PIDFILE="$1"
   RET=1
   # Execute gateway java binary
   if [ -n "$PIDFILE" ] && [ "$PIDFILE" != "stop" ];then
     #echo "start ---->" "$JAVACMD" "$WHOLEARG"
     #exec "$JAVACMD" "$WHOLEARG"
     #nohup  "$JAVACMD" "$WHOLEARG" </dev/null >ratesvr.log 2>&1 &
     echo $! >$GATEWAY_PIDFILE
     echo $GATEWAY_NAME start
     RET="$?"
   elif [ -f $GATEWAY_PIDFILE ];then
     PID=`cat $GATEWAY_PIDFILE`
     echo try to stop previous Gateway $GATEWAY_NAME process $PID if exist
     RET=`ps -p $PID|grep java`
     if [ -n "$RET" ];then
       kill -15 $PID
       echo stop previous Gateway $GATEWAY_NAME process $PID
       RET=$PID
     else
       echo $GATEWAY_NAME process $PID not exist no need to stop
     fi
   fi
   return "$RET"
}

checkRunning(){
    if [ -f "$GATEWAY_PIDFILE" ]; then
       if  [ -z "`cat $GATEWAY_PIDFILE`" ];then
         echo "ERROR: $GATEWAY_NAME Pidfile '$GATEWAY_PIDFILE' exists but contains no pid"
         return 2
       fi
       PID=`cat $GATEWAY_PIDFILE`
       RET=`ps -p $PID|grep java`
       if [ -n "$RET" ];then
         return 0;
       else
         return 1;
       fi
    else
         return 1;
    fi
}

invoke_status(){
    if ( checkRunning );then
         PID=`cat $GATEWAY_PIDFILE`
         echo "$GATEWAY_NAME $MAINCLASS is running (pid '$PID')"
         exit 0
    fi
    echo "$GATEWAY_NAME $MAINCLASS not running"
    exit 1
}

invoke_start(){
    if ( checkRunning );then
      PID=`cat $GATEWAY_PIDFILE`
      echo "INFO: $GATEWAY_NAME $MAINCLASS Process with pid '$PID' is already running"
      exit 0
    fi
    invokeJar $GATEWAY_PIDFILE
    exit "$?"
}

invoke_stop(){
    RET="1"
    if ( checkRunning );then
       invokeJar "stop"
       RET="$?"
       PID=`cat $GATEWAY_PIDFILE`
       echo "INFO: Waiting at least $GATEWAY_KILL_MAXSECONDS seconds for regular process termination of pid '$PID' : "
       FOUND="0"
       i=1
       while [ $i != $GATEWAY_KILL_MAXSECONDS ]; do
         if [ ! checkStopRunning ];then
           echo " FINISHED"
           FOUND="1"
          break
         fi

         if ( checkRunning );then
            sleep 1
            printf  "."
         else
            echo " FINISHED"
            FOUND="1"
            break
         fi
         i=`expr $i + 1`
       done
       if [ "$FOUND" -ne "1" ];then
         echo
         echo "INFO: Regular shutdown not successful,  sending SIGKILL to process with pid '$PID'"
         kill -KILL $PID
         RET="1"
       fi
    elif [ -f "$" ];then
       echo "ERROR: No or outdated process id in '$GATEWAY_PIDFILE'"
       echo
       echo "INFO: Removing $GATEWAY_PIDFILE"
    else
       echo "$GATEWAY_PIDFILE not running"
       exit 0
    fi
    rm -f $GATEWAY_PIDFILE >/dev/null 2>&1
    rm -f $DB_PIDFILE >/dev/null 2>&1
    exit $RET
}

show_help() {
  echo "Usage: $0 " >&2
  cat << EOF
   start           - start instance
   restart         - stop running instance (if there is one), start new instance
   status          - status check if process is running
   stop            - stop running instance
EOF
  exit 1
}

# ------------------------------------------------------------------------
# MAIN

# show help

if [ "$#" -lt 4 ]; then
  show_help
elif [ "$4" != "start" ] && [ "$4" != "restart" ] && [ "$4" != "status" ] && [ "$4" != "stop" ]; then
  show_help
else
  case "$4" in
    status)
      invoke_status
        ;;
      restart)
        if ( checkRunning );then
          "$0" stop
        fi
        "$0" status
        "$0" start
        "$0" status
        ;;
      start)
        if ( checkRunning );then
          PID=`cat $GATEWAY_PIDFILE`
          echo "INFO: $GATEWAY_NAME $MAINCLASS Process with pid '$PID' is already running"
          exit 0
        fi
        #exec "$JAVACMD" "$@"
        nohup  "$JAVACMD" "$@" </dev/null >ratesvr.log 2>&1 &
        echo $! >$GATEWAY_PIDFILE
        echo $GATEWAY_NAME start
        ;;
      stop)
        invoke_stop
        ;;
      *)
        show_help
        ;;
    esac
fi

