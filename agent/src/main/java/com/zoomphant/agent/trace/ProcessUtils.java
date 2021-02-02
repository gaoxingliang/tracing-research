package com.zoomphant.agent.trace;


import com.zoomphant.agent.trace.checker.ProcInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessUtils {

    public static List<ProcInfo> allProcess() {
        return ProcessHandle.allProcesses().map(p -> {
            long pid = p.pid();
            String cmd = p.info().command().orElse("");
            String containerName = DockerUtils.getContainerName(pid);
            return ProcInfo.builder().args(Arrays.asList(p.info().arguments().orElse(new String[0]))).cmd(cmd).id(pid).containerId(containerName == null ? "" : containerName).build();
        }).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        ProcessUtils.allProcess().forEach(r -> System.out.println(r));
    }
}
