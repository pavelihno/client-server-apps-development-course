package com.mirea

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import groovy.transform.CompileStatic
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.plugins.metadata.*

import io.rsocket.core.RSocketServer
import io.rsocket.SocketAcceptor
import io.rsocket.transport.netty.server.TcpServerTransport

@CompileStatic
@ComponentScan
class Application extends GrailsAutoConfiguration {
    
    static void main(String[] args) {
        def context = GrailsApp.run(Application, args) 

        RSocketServer.create(SocketAcceptor.with(context.getBean(StockRSocket)))
          .bind(TcpServerTransport.create("localhost", 7000))
          .subscribe()

        println "RSocket Server running at http://localhost:7000"
    }
}
