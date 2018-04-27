package main.signals

import main.helpers.PricesList
import v13.agents.chartist.signal.Signal

abstract class ChartSignal() : Signal {

    enum class Direction {
        UP, DOWN, NA
    }

    fun getTrend(): Direction {
        // On a besoin selon le papier de calculer le MM de 3 jours sur 6 jours auparavent des 3 jours de chart
        if (prices.size < 11)
            return Direction.NA
        var upSomme = 0
        var downSomme = 0
        val t = prices.size - 3
        for (i in 0..5) {
            if (compute3DMovingAverage(t-i) < compute3DMovingAverage(t-i-1))
                --downSomme
            else
                ++upSomme
        }

        // On additionne ou on soustrait 7 fois à 0
        // On autorise qu'une seule variation dans les MA
        if (upSomme >= 5)
            return Direction.UP
        else if (downSomme <= -5)
            return Direction.DOWN
        else
            return Direction.NA
    }

    private fun compute3DMovingAverage(t: Int): Long {
        return (prices[t-2].last + prices[t-1].last + prices[t].last)/3
    }

    companion object {
        val prices = PricesList
    }
}