package com.zoomphant.agent.trace;


import com.zoomphant.agent.trace.checker.ProcInfo;
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
                    return ProcInfo.builder().args(Arrays.asList(new String[0])).cmd(cmd).id(pid).containerId(containerName == null ? ""
                            : containerName).build();
                }).collect(Collectors.toList());
    }
//
//    /**
//     * This method is using the jdk's imple. but it has a bug
//     * when the command line is too long, it will not SHOW THE FULL command line.
//     *
//     * @return
//     */
//    @Deprecated
//    public static List<ProcInfo> allProcess() {
//        return ProcessHandle.allProcesses().map(p -> {
//            long pid = p.pid();
//            String cmd = p.info().command().orElse("");
//            String containerName = DockerUtils.getContainerName(pid);
//            return ProcInfo.builder().args(Arrays.asList(p.info().arguments().orElse(new String[0]))).cmd(cmd).id(pid).containerId(containerName == null ? "" : containerName).build();
//        }).collect(Collectors.toList());
//    }
//
//    public static void main(String[] args) {
//        ProcessUtils.allProcess().forEach(r -> System.out.println(r));
//    }
}
