package main.agents

import main.helpers.PricesList
import main.signals.*
import v13.Day
import v13.Order
import v13.agents.Agent
import v13.agents.chartist.signal.Signal
import v13.agents.chartist.policy.BestBidAsk
import v13.agents.chartist.policy.OrderPolicy
import v13.agents.chartist.strategy.Majority
import v13.agents.chartist.strategy.Strategy

class CandleAgent(name: String, cash: Long, private  val strategy: Strategy = Majority(), private val policy: OrderPolicy = BestBidAsk()) : Agent(name, cash) {
    // Puisque ATOM ne semble pas garder et/ou rendre accessible les jours
    // on les store ici
    private val prices = PricesList
    private var currentDay = 0
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

    override fun decide(p0: String, p1: Day): Order? {
        if (p1.currentTick() > 1 || !p1.currentPeriod().isContinuous)
            return null

        val asset = this.market.orderBooks.get(p0)
        if (asset == null)
            return null
        prices.add(PriceCandle(asset.lowestPriceOfDay, asset.highestPriceOfDay, asset.lastPriceOfDay, asset.firstPriceOfDay))

        ++currentDay
        this.market.log.info("DEBUG: Seems to at least run!!!!")

        val directions = ArrayList<Signal.Direction>()

        for (s in signals) {
            directions.add(s.update(asset, p1))
        }
        val direction = strategy.aggregate(directions, this)
        val current: Order?
        if (direction == Signal.Direction.HOLD) {
            current = null
        } else {
            current = policy.build(direction, asset, this, myId.toInt())
            current!!.sender = this
        }

        return current
    }
}