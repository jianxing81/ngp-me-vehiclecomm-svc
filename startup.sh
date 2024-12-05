#!/bin/sh

# Dynamically configure the heap size based on available container memory
limit_in_bytes=$(cat /sys/fs/cgroup/memory/memory.limit_in_bytes)
if [ "$limit_in_bytes" -ne 9223372036854771712 ] && [ "$limit_in_bytes" -ne 9223372036854775807 ]; then
  limit_in_megabytes=$(expr $limit_in_bytes / 1048576)
  xms_size=$(expr $limit_in_megabytes \* 5 / 10)  # 50% of memory for Xms
  xmx_size=$(expr $limit_in_megabytes \* 9 / 10)  # 90% of memory for Xmx
  export JAVA_OPTS="-Xmx${xmx_size}m -Xms${xms_size}m ${JAVA_OPTS}"
fi

JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -XX:+UseStringDeduplication"
echo "JAVA_OPTS=${JAVA_OPTS}"
java -javaagent:./dd-java-agent.jar $JAVA_OPTS -jar service.jar
