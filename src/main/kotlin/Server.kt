package com.github.panarik

import com.github.panarik.endpoints.buildQuiz.GetQuizHandler
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

val log = LoggerFactory.getLogger(Server::class.java)

object Server {

    fun start() {
        log.info("ENVIRONMENT: Project dir=${System.getProperty("user.dir")}")
        log.info("ENVIRONMENT: System variables=${System.getenv().entries.joinToString()}")
        val server = HttpServer.create(InetSocketAddress(ServerProperties.port), 0)
        log.info("Server is started on port ${ServerProperties.port}.")
        server.createContext("/quiz", GetQuizHandler())
        server.start()
    }


}