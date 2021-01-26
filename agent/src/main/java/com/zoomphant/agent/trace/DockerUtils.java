package com.zoomphant.agent.trace;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://stackoverflow.com/questions/24406743/coreos-get-docker-container-name-by-pid
public class DockerUtils {

    // cat /proc/<process-pid>/cgroup
    public static String getContainerName(long pid) {
        /**
         * 12:freezer:/kubepods.slice/kubepods-besteffort.slice/kubepods-besteffort-podf9f89fd9_f49f_4cbe_b04b_cae5c9043de2
         * .slice/docker-6f80e1af790e2290283e473b56cc6e5d9d113d135e39416822ec4af6fd5fd72d.scope
         * 11:hugetlb:/kubepods.slice/kubepods-besteffort.slice/kubepods-besteffort-podf9f89fd9_f49f_4cbe_b04b_cae5c9043de2
         * .slice/docker-6f80e1af790e2290283e473b56cc6e5d9d113d135e39416822ec4af6fd5fd72d.scope
         * 10:blkio:/kubepods.slice/kubepods-besteffort.slice/kubepods-besteffort-podf9f89fd9_f49f_4cbe_b04b_cae5c9043de2
         * 0::/kubepods.slice/kubepods-besteffort.slice/kubepods-besteffort-podf9f89fd9_f49f_4cbe_b04b_cae5c9043de2
         * .slice/docker-6f80e1af790e2290283e473b56cc6e5d9d113d135e39416822ec4af6fd5fd72d.scope
         */
        try {
            Optional<String> l = Files.readAllLines(new File(String.format("/proc/%d/cgroup", pid)).toPath()).stream().filter(r -> r.contains("docker-"))
                    .findFirst();
            if (l.isPresent()) {
                String line = l.get();
                return grep(line);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static final String grep(String line) {
        Pattern p = Pattern.compile(".*([a-f0-9]{64}).*");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(DockerUtils.grep(".slice/docker-6f80e1af790e2290283e473b56cc6e5d9d113d135e39416822ec4af6fd5fd72d.scope"));
    }
}
