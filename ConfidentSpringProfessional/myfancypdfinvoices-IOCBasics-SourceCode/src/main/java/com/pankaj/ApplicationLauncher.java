package com.pankaj;

import com.pankaj.web.MyFancyPdfInvoicesServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

public class ApplicationLauncher {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();
        Context ctx = tomcat.addContext("",null);
        Wrapper servlet = tomcat.addServlet(ctx,"myFirstServlet",new MyFancyPdfInvoicesServlet());
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/*");
        tomcat.start();
    }
}
