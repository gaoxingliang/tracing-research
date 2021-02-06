# tracing-research

# for each trace object named - xxx-trace
run the `gralde build` to generate the jar and will auto copy into folder releaselibs.<br>
For now there are two main types of tracers:
- jmx supported. We scraped all metrics and reported to remote client as prometheus metrics.
- only bytebuddy agents.  based on tracer and reported to remote client (as zipkin data).

## kafka trace
Now it scraped the full jmx beans. and reported to central agent. <br> 
and it provides the unique `kafka_brokerid` and `kafka_clusterid` as the label in prometheus metrics.

## sql trace
Now it visits the java processes using jdbc connections. Which will generate trace data. <br>
the source is the containerId. and the target is: sql@jdbcurl.

# for the agent module
run the `gradle build` and now files are under agentlibs. This is used to starts:
- process list
- auto instrument possible java processes.