package test.signals

import main.helpers.PricesList
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.junit.Assert
import org.junit.runner.RunWith

import org.junit.Test
import main.agents.PriceCandle
import main.signals.ThreeInsideDownSignal
import v13.Day
import v13.OrderBook
import v13.Period
import v13.agents.chartist.signal.Signal

@RunWith(Arquillian::class)
class ThreeInsideDownSignalTest {
    @Test
    fun update() {
        buildPrices()
        val mm = ThreeInsideDownSignal()
        val signal = mm.update(OrderBook(""), Day(arrayOf(Period(1,0))))
        Assert.assertEquals("Should be SELLING", Signal.Direction.SELL, signal)
    }

    fun buildPrices() {
        // Up trend
        PricesList.add(PriceCandle(last = 33))
        PricesList.add(PriceCandle(last = 35))
        PricesList.add(PriceCandle(last = 39))
        PricesList.add(PriceCandle(last = 42))
        PricesList.add(PriceCandle(last = 45))
        PricesList.add(PriceCandle(last = 47))
        PricesList.add(PriceCandle(last = 49))
        PricesList.add(PriceCandle(last = 51))
        PricesList.add(PriceCandle(last = 55, first = 50))
        // Pattern
        PricesList.add(PriceCandle(first=53, last = 60))
        PricesList.add(PriceCandle(first=58, last = 56))
        PricesList.add(PriceCandle(first=57, last = 50))
        // T
        PricesList.add(PriceCandle(first=45, last = 40))
    }
    companion object {
        @Deployment
        fun createDeployment(): JavaArchive {
            return ShrinkWrap.create(JavaArchive::class.java)
                    .addClass(ThreeInsideDownSignal::class.java)
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
        }
    }
}
