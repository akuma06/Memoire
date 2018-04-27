package main.helpers

import java.io.OutputStream
import java.io.PrintStream
import java.io.PrintWriter

class LogFile(out: OutputStream?, output: String = "out.atom") : PrintStream(out) {
    val file = PrintWriter(output)

    override fun println(x: String?) {
        super.println(x)
        if (!x!!.contains("DEBUG:"))
            file.println(x)
    }

    override fun close() {
        super.close()
        file.close()
    }
}