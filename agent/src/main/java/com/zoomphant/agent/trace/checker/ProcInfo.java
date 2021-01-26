package com.zoomphant.agent.trace.checker;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProcInfo {
    long id;
    String cmd;
    String containerId;
}
