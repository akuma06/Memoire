package main.helpers

import ui.MainApplication
import ui.MainFrame
import java.io.OutputStream
import java.io.PrintStream
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

class SysLog(out: OutputStream?, private val app: MainApplication) : PrintStream(out) {
    private val points = Points()
    private var currentTick = ""
    private var currentFrame: MainFrame? = null
    private var activeTab = 0
    private var askOrders = Orders(true)
    private var bidOrders = Orders(false)

    override fun println(x: String?) {
        super.println(x)
        if (x != null) {
            val infos = x.split(";")
            if (infos[0] == "Price") {
                askOrders.removeOrder(infos[2].toDouble()/100, infos[3].toInt())
                bidOrders.removeOrder(infos[2].toDouble()/100, infos[3].toInt())
                points.addPoint(currentTick.toDouble(), infos[2].toDouble()/100)
                currentFrame!!.replaceSeries(points.getXPoints(), points.getYPoints())

            } else if (infos[0] == "Tick") {
                if (currentTick != infos[1]) {
                    currentTick = infos[1]
                }
            } else if (infos[0] == "Order") {
                if (currentTick == "" && currentFrame == null) {
                    currentTick = "1"
                    currentFrame = MainFrame()
                    currentFrame!!.addGraph()
                    activeTab = app.addPanel(currentFrame!!.panel, "")
                }
                if (infos[5] == "A") {
                    askOrders.addOrder(infos[6].toDouble()/100, infos[2], infos[1], infos[7].toInt())
                    val askTable = currentFrame!!.askTable
                    SwingUtilities.invokeLater {
                        askTable.model = DefaultTableModel(askOrders.getOrders(), arrayOf("Nom", "Prix", "Quantité"))
                    }
                } else {
                    bidOrders.addOrder(infos[6].toDouble()/100, infos[2], infos[1], infos[7].toInt())
                    val bidTable = currentFrame!!.bidTable
                    SwingUtilities.invokeLater {
                        bidTable.model = DefaultTableModel(bidOrders.getOrders(), arrayOf("Nom", "Prix", "Quantité"))
                    }
                }
            } else if (infos[0] == "Day") {
                app.setTabTitle(activeTab, "Jour ${infos[1]}")
                currentTick = ""
                currentFrame = null
            }
        }
    }
}