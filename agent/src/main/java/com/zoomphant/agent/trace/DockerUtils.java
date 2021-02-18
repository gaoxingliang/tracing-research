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
         *
         *
         * 12:pids:/kubepods/besteffort/pode8b3fd01-bdfb-4a8b-9992-d34d064a68a2
         * /7af11114ce560eca928c37b8f3e49f94d7c4f940beef5403ff48685d5354a16f
         * 11:freezer:/kubepods/besteffort/pode8b3fd01-bdfb-4a8b-9992-d34d064a68a2
         * /7af11114ce560eca928c37b8f3e49f94d7c4f940beef5403ff48685d5354a16f
         *
         *
         */
        try {
            Optional<String> l = Files.readAllLines(new File(String.format("/proc/%d/cgroup", pid)).toPath()).stream()
                    .filter(r -> grep(r) != null)
                    .findFirst();
            return l.orElse(null);
        } catch (Exception e) {
            return null;
        }
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
        System.out.println(DockerUtils.grep("12:pids:/kubepods/besteffort/pode8b3fd01-bdfb-4a8b-9992-d34d064a68a2/7af11114ce560eca928c37b8f3e49f94d7c4f940beef5403ff48685d5354a16f"));
    }
}
