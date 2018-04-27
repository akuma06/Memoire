package test.signals

import main.helpers.PricesList
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

import main.agents.PriceCandle
import main.signals.ThreeInsideUpSignal
import v13.Day
import v13.OrderBook
import v13.Period
import v13.agents.chartist.signal.Signal

@RunWith(Arquillian::class)
class ThreeInsideUpSignalTest {

    @Test
    fun update() {
        buildPrices()
        val mm = ThreeInsideUpSignal()
        val signal = mm.update(OrderBook(""), Day(arrayOf(Period(1,0))))
        Assert.assertEquals("Should be BUYING", Signal.Direction.BUY, signal)
    }

    fun buildPrices() {
        // Down trend
        PricesList.add(PriceCandle(last = 100))
        PricesList.add(PriceCandle(last = 90))
        PricesList.add(PriceCandle(last = 80))
        PricesList.add(PriceCandle(last = 70))
        PricesList.add(PriceCandle(last = 60))
        PricesList.add(PriceCandle(last = 50))
        PricesList.add(PriceCandle(last = 40))
        PricesList.add(PriceCandle(last = 35))
        PricesList.add(PriceCandle(last = 33, first = 34))
        //
        PricesList.add(PriceCandle(first = 35, last = 30))
        PricesList.add(PriceCandle(first = 33, last = 34))
        PricesList.add(PriceCandle(first = 33, last = 36))
        // T
        PricesList.add(PriceCandle(first=38, last = 40))
    }

    companion object {
        @Deployment
        fun createDeployment(): JavaArchive {
            return ShrinkWrap.create(JavaArchive::class.java)
                    .addClass(ThreeInsideUpSignal::class.java)
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
        }
    }
}
