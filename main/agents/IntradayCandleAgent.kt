package main.agents

import main.helpers.PricesList
import main.policy.RealMarketOrderPolicy
import main.signals.*
import main.strategy.AtLeastStrategy
import v13.*
import v13.agents.Agent
import v13.agents.chartist.signal.Signal
import v13.agents.chartist.policy.BestBidAsk
import v13.agents.chartist.policy.MarketOrderPolicy
import v13.agents.chartist.policy.OrderPolicy
import v13.agents.chartist.strategy.Strategy

data class OrderHold(val direction: Signal.Direction, var expire: Int, var quantity: Int, val id: Int)

class IntradayCandleAgent(name: String, cash: Long, private val interval: Int = 11, private  val strategy: Strategy = AtLeastStrategy(), private val policy: OrderPolicy = RealMarketOrderPolicy(), private val signals: ArrayList<Signal> = ArrayList(), private val hold: Int?) : Agent(name, cash) {
    // Puisque ATOM ne semble pas garder et/ou rendre accessible les jours
    // on les store ici (singleton pour moins de RAM et eviter Garbage Collector Error)
    private val prices = PricesList
    private val holding = arrayListOf<OrderHold>()
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
        val asset = this.market.orderBooks[p0] ?: return null

        // At the end of the day, we close all positions
        if (p1.currentPeriod().isContinuous && p1.currentTick() == p1.currentPeriod().totalTicks()) { // We are in the last Tick of the day
            holding.forEach {
                this.market.send(this,  RealMarketOrderPolicy(it.quantity).build(it.direction.reverse(), asset, this, myId.toInt()))
            }
            holding.clear()
        }

        if (p1.currentTick()%interval > 0 || p1.currentTick() == 0 || !p1.currentPeriod().isContinuous) {
            if (p1.currentTick() == 1)
                prices.clear()
            return null
        }


        prices.add(PriceCandle().ReadDay(asset, interval, p1.currentTick()))
        // this.market.log.info("DEBUG: Seems to at least run!!!!")

        val directions = ArrayList<Signal.Direction>()

        for (s in signals) {
            directions.add(s.update(asset, p1))
        }
        val direction = strategy.aggregate(directions, this)
        val current: Order?
        if (direction == Signal.Direction.HOLD) {
            if (holding.size > 0 && holding.first().expire == p1.currentTick()) { // We check that we have no position expired
                // There is then we make a counter offer
                    current = if (holding.first().quantity > 0) RealMarketOrderPolicy(holding.first().quantity).build(holding.first().direction.reverse(), asset, this, myId.toInt()) else null
                holding.remove(holding.first())
            } else current = null
        } else {
            current = policy.build(direction, asset, this, myId.toInt())
            current!!.sender = this
            if (hold != null) {
                holding.add(OrderHold(direction, p1.currentTick() + hold*interval, 0, myId.toInt()))
                holding.sortBy { it.expire }
                if (holding.first().expire == p1.currentTick()) holding.first().expire += interval // We can't send 2 Orders
            }
        }

        return current
    }

    override fun touchedOrExecutedOrder(e: Event?, o: Order?, p: PriceRecord?) {
        super.touchedOrExecutedOrder(e, o, p)
        if (e == Event.EXECUTED || e == Event.UPDATED) {
            val order = this.market.orderBooks[o!!.obName]!!.ask.find { it.extId == o.extId && it.sender == o.sender } ?: this.market.orderBooks[o!!.obName]!!.bid.find { it.extId == o.extId && it.sender == o.sender }
            if (order != null) {
                val h = holding.find { it.id == order.extId.toInt() }
                if (h != null)
                    h.quantity = order.quantity
            }
        }
    }
}