package main.policy

import v13.LimitOrder
import v13.MarketOrder
import v13.Order
import v13.OrderBook
import v13.agents.Agent
import v13.agents.chartist.policy.OrderPolicy
import v13.agents.chartist.signal.Signal
import java.util.*

class RealMarketOrderPolicy(val quantity: Int = 75) : OrderPolicy {

    init {
        if (quantity < 0) {
            throw RuntimeException("pb with the parameters of " + this)
        }
    }

    override fun build(direction: Signal.Direction, ob: OrderBook, a: Agent, orderId: Int): Order? {
        if (direction == Signal.Direction.HOLD) {
            return null
        } else {
            return MarketOrder(ob.obName, orderId.toString(), direction.toChar(), this.quantity)
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
