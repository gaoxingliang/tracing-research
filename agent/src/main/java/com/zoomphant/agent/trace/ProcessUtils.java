package com.zoomphant.agent.trace;


import com.zoomphant.agent.trace.checker.ProcInfo;
import com.zoomphant.agent.trace.common.minimal.utils.StringUtils;
import com.zoomphant.agent.trace.common.minimal.TraceLog;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessUtils {

    public static List<ProcInfo> allProcess2() {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        return os.getProcesses(0, null)
                .stream().map(p -> {
                    long pid = p.getProcessID();
                    String cmd = p.getCommandLine();
                    String containerName = DockerUtils.getContainerName(pid);
                    TraceLog.debug(String.format("Found process %d cmd=%s, container=%s", pid,
                            StringUtils.abbr(cmd, 100), containerName));
                    ProcInfo procInfo = new ProcInfo();
                    procInfo.setArgs(Arrays.asList(new String[0]));
                    procInfo.setCmd(cmd);
                    procInfo.setContainerId(containerName == null ? "" : containerName);
                    procInfo.setId(pid);
                    return procInfo;
                }).collect(Collectors.toList());
    }
}
