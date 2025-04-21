package com.example.jetonsserver
import fi.iki.elonen.NanoHTTPD

class HttpServer(port: Int) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession?): Response {
        val responseJson = """{ "status": "ok", "message": "Healthcheck passed!" }"""
        return newFixedLengthResponse(Response.Status.OK, "application/json", responseJson)
    }
}
