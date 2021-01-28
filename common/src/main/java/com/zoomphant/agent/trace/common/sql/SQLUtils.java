package com.zoomphant.agent.trace.common.sql;

public class SQLUtils {
    public static DbInfo extractDbInfoFromUrl(String jdbcUrl) {
        // parse the jdbc host from the jdbc url
        // an example jdbc:mysql://${MYSQL_HOST:localhost}:3306/information_schema
        // jdbc:mysql://localhost:3306/sonoo

        String host = jdbcUrl;
        if (jdbcUrl.startsWith("jdbc:mysql://")) {
            // for now only supports mysql
            // jdbc:mysql://[host][,failoverhost...]
            //[:port]/[database]
            //[?propertyName1][=propertyValue1]
            //[&propertyName2][=propertyValue2]
            String sub = jdbcUrl.substring("jdbc:mysql://".length());
            int colonIndex = sub.indexOf(":");
            if (colonIndex > 0) {
                host = sub.substring(0, colonIndex);
            }
            else {
                host = sub.substring(0, sub.indexOf("/"));
            }
        }
        return DbInfo.builder().host(host).build();
    }

    public static void main(String[] args) {
        String[] urls = new String[]{"jdbc:mysql://localhost:3306/sonoo", "jdbc:mysql://localhost/sonoo"};
        for (String s : urls) {
            System.out.println(extractDbInfoFromUrl(s));
        }
    }
}
