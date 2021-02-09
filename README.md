[TOC]

# tracing-research
Project structure:
- spy (used in bootstrap. no need to change anymore.)
- common-minimal (used in bootstrap. which will be load into the remote target jvm bootstrap classloader)
- bootstrap (loading another classloader)
- common (additional stuff shared between agents)  
- dummy (a test loader class. ignore it)
- agent (exported to the zp collector.)

and different kinds of agents:
- jmx-base (provided base class for jvm style agents which want to scrape jmx metrics)
- sql-trace (scrape the jdbc stuff when a jvm using jdbc)
- kafka-trace (scrape the Kafka broker processes based on jmx-base)
- kafka-java (scrape when a jvm using kafka library)
- http-java (scrape when a jvm using http)

# for each trace agents
run the `gralde build` to generate the jar and will auto copy into folder releaselibs.<br>
For now there are two main types of tracers:
- jmx supported. We scraped all metrics and reported to remote client as prometheus metrics.
- only bytebuddy agents.  based on tracer and reported to remote client (as zipkin data).
if u are using the bytebuddy's advice, you have to know:
  - in your advice, **DO NOT IMPORT** anything except packages in the [minimal packages](common-minimal)
  - a complex example with dynamic value is [ProduceAdvice](kafka-java/src/main/java/com/zoomphant/agent/trace/kafkajava/ProduceAdvice.java)  


## kafka trace
Now it scraped the full jmx beans of a kafka process. and reported to central agent. <br> 
and it provides the unique `kafka_brokerid` and `kafka_clusterid` as the label in prometheus metrics.

## sql trace
Now it visits the java processes using jdbc connections. Which will generate trace data. <br>
the source is the containerId. and the target is: sql@jdbcurl.

## kafka-java trace
Track the java processes which using the kafka related library.

# for the agent module
run the `gradle build` and now files are under agentlibs. This is used to starts:
- process list
- auto instrument possible java processes.


# reference
1. bytebuddy
