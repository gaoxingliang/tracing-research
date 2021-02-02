package com.zoomphant.agent.trace.checker;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProcInfo {
    long id;
    String cmd;
    String containerId;
    List<String> args;
}
