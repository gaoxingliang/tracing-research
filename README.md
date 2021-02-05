# tracing-research

# for each trace object named - xxx-trace
run the `gralde build` to generate the jar and will auto copy into folder releaselibs.<br>
For now there are two main types of tracers:
- jmx supported. We scraped all metrics and reported to remote client as prometheus metrics.
- only bytebuddy agents.  based on tracer and reported to remote client (as zipkin data).

# for the agent module
run the `gradle build` and now files are under agentlibs. This is used to starts:
- process list
- auto instrument possible java processes.