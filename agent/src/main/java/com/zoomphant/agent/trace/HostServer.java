package com.zoomphant.agent.trace;

import com.alibaba.fastjson.JSONObject;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.annotation.ConsumesJson;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.RequestObject;
import com.zoomphant.agent.trace.common.TraceLog;

import java.util.concurrent.CompletableFuture;

public class HostServer {

    public static final int DEFAULT_PORT = 19234;

    public static void start(int port) {
        ServerBuilder sb = Server.builder();
        sb.http(port);
        sb.annotatedService(new Object() {
            @Post("/data")
            @ConsumesJson
            public HttpResponse data(@RequestObject JSONObject json) {
                TraceLog.info("Receivced " + json);
                return HttpResponse.of(HttpStatus.OK);
            }
        });
        Server server = sb.build();
        CompletableFuture<Void> future = server.start();
        future.join();
    }


    public static void main(String[] args) {
        HostServer.start(9234);
    }
}
