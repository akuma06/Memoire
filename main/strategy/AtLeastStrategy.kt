package main.strategy

import v13.agents.Agent
import v13.agents.chartist.signal.Signal
import v13.agents.chartist.strategy.Strategy

class AtLeastStrategy(): Strategy {
    override fun aggregate(p0: MutableList<Signal.Direction>?, p1: Agent?): Signal.Direction {
        if (p0 === null) {
            return Signal.Direction.HOLD
        }
        var buyOrSell = 0
        for (d in p0) {
            if (d == Signal.Direction.BUY) run { ++buyOrSell } else if (d == Signal.Direction.SELL) run { --buyOrSell }
        }
        return if (buyOrSell > 0) {
            Signal.Direction.BUY
        } else if (buyOrSell < 0){
            Signal.Direction.SELL
        } else {
            Signal.Direction.HOLD
        }
    }

}