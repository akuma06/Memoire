package test.agents

import main.agents.IntradayCandleAgent
import main.helpers.PricesList
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import main.signals.*
import main.strategy.AtLeastStrategy
import test.signals.EveningStarSignalTest
import test.signals.ThreeInsideUpSignalTest
import v13.Day
import v13.OrderBook
import v13.Period
import v13.agents.ZIT
import v13.agents.chartist.signal.Signal
import v13.agents.chartist.strategy.Strategy

@RunWith(Arquillian::class)
class IntradayCandleAgentTest {
    val signals = ArrayList<Signal>()
    init {
        signals.add(ThreeWhiteSoldiersSignal())
        signals.add(ThreeBlackCrowsSignal())
        signals.add(ThreeInsideUpSignal())
        signals.add(ThreeInsideDownSignal())
        signals.add(ThreeOutsideUpSignal())
        signals.add(ThreeOutsideDownSignal())
        signals.add(MorningStarSignal())
        signals.add(EveningStarSignal())
    }

    @Test
    fun decide() {
        EveningStarSignalTest().buildPrices()
        assertEquals("Should SELL", Signal.Direction.SELL, runSignals())
        PricesList.clear()
        ThreeInsideUpSignalTest().buildPrices()
        assertEquals("Should BUY", Signal.Direction.BUY, runSignals())
        PricesList.clear()
    }

    fun runSignals(): Signal.Direction {
        val directions = ArrayList<Signal.Direction>()
        for (s in signals) {
            directions.add(s.update(OrderBook(""), Day(arrayOf(Period(1,0)))))
        }
        val strategy: Strategy = AtLeastStrategy()
        return strategy.aggregate(directions, ZIT(""))
    }

    companion object {
        @Deployment
        fun createDeployment(): JavaArchive {
            return ShrinkWrap.create(JavaArchive::class.java)
                    .addClass(IntradayCandleAgent::class.java)
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
        }
    }
}
