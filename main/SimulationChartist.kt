package main

import main.helpers.LogFile
import main.agents.IntradayCandleAgent
import v13.Day
import v13.Logger
import v13.MonothreadedSimulation
import v13.agents.ZIT
import v13.agents.chartist.StrategicAgent
import v13.agents.chartist.policy.MarketOrderPolicy
import v13.agents.chartist.signal.*
import v13.agents.chartist.strategy.SingleSignalStrategy

// Constantes parametrant la modelisation
// BASE_MONEY correspond a la base monetaire de chaque agent
const val BASE_MONEY = 100000L
// VARIATION_MONEY correspond a la variation positive maximale
// venant s'ajouter à BASE_MONEY
const val VARIATION_MONEY = 15000L
// NB_DAYS correspond au nombre de jours de tradings
const val NB_DAYS = 10
// TICKS correspond au nombre de ticks par jour
// TODO: Demander car il me semble tres lourd de faire cette simu
const val TICKS = 30600000
// NB_CHARTIST correspond au nombre unique de chaque type de chartiste
const val NB_CHARTIST = 10
// NB_BEST_LIMIT correspond au nombre unique de chaque type de chartiste achetant à la meilleur limite
const val NB_BEST_LIMIT = 3
// NB_REVERSE correspond au nombre unique de chaque type de chartiste étant contrariant
const val NB_REVERSE = 1
// NB_REVERSE_BL correspond au nombre unique de chaque type de chartiste étant contrariant et meilleur limite
const val NB_REVERSE_BL = 1
// NB_ZIT correspond au nombre d'agent offrant de la liquidité
const val NB_ZIT = 2

fun main(args: Array<String>) {
    val sim = MonothreadedSimulation()

    // Pas de possibilite de set logType (static in non-static method)
    //sim.market.logType = MarketPlace.SHORT // ou SHORT
    // Old hack to get real-time data
    //val mainApp = MainApplication()

    // On utilise un logger qui print en stdout et enregistre les logs dans un fichier
    // Ensuite on utilisera ./generatecsv.py pour générer les différents fichiers csv
    sim.logger = Logger(LogFile(System.out))

    val obName = "lvmh"
    sim.addNewOrderBook(obName)

    for (i in 1..NB_ZIT) {
        sim.addNewAgent(ZIT("paul-${i}", BASE_MONEY + Math.round(Math.random()*VARIATION_MONEY))) // cash, bornes par défaut
    }

    for (i in 1..NB_CHARTIST) {
        // On crée les différents chartistes
        // Chaque chartiste possede une
        var bestLimit = false
        var reverse = false
        if (i >= NB_CHARTIST - NB_BEST_LIMIT) {
            bestLimit = true
            if (i >= NB_CHARTIST - NB_REVERSE_BL) {
                reverse = true
            }
        } else if (i < NB_REVERSE)
            reverse = true
        sim.addNewAgent(StrategicAgent("RSI-${i}", BASE_MONEY + Math.round(Math.random()* VARIATION_MONEY), Rsi(5), SingleSignalStrategy(reverse), MarketOrderPolicy(14500L, 75, bestLimit)))
        sim.addNewAgent(StrategicAgent("MMA--${i}", BASE_MONEY + Math.round(Math.random()*VARIATION_MONEY), MixedMovingAverage(15,50), SingleSignalStrategy(reverse), MarketOrderPolicy(14500L, 75, bestLimit)))
        sim.addNewAgent(StrategicAgent("Mom-${i}", BASE_MONEY + Math.round(Math.random()*VARIATION_MONEY), Momentum(5), SingleSignalStrategy(reverse), MarketOrderPolicy(14500L, 75, bestLimit)))
        sim.addNewAgent(StrategicAgent("MA-${i}",BASE_MONEY +  Math.round(Math.random()*VARIATION_MONEY), MovingAverage(10), SingleSignalStrategy(reverse), MarketOrderPolicy(14500L, 75, bestLimit)))
        sim.addNewAgent(StrategicAgent("VA1-${i}", BASE_MONEY + Math.round(Math.random()*VARIATION_MONEY), Variation(3, 3, 5), SingleSignalStrategy(reverse), MarketOrderPolicy(14500L, 75, bestLimit)))
        sim.addNewAgent(StrategicAgent("VA2-${i}", BASE_MONEY + Math.round(Math.random()*VARIATION_MONEY), Variation(7, 4,11), SingleSignalStrategy(reverse), MarketOrderPolicy(14500L, 75, bestLimit)))
    }

    // On ajoute un IntradayCandleAgent qui va agir avec ses strategies contre tous les autres agents Chartistes
    // On peut ainsi tester seulement les performances de sa strategie (meme maniere que le papier HFT)
    sim.addNewAgent(IntradayCandleAgent("ID_Candle",BASE_MONEY + Math.round(Math.random()*VARIATION_MONEY), 5, policy = MarketOrderPolicy(14500L, 75, false)))

    // Reste du old hack pour avoir les donnees en temps reel
    //    mainApp.runMarket(sim, Day.createEuroNEXT(0, 1000, 0), 4)

    // On choisit 30 600 000 ticks par jour de trading comme dans le papier HFT de mme Oriol
    // TODO: Verifier la justesse de 1 tick = 1 milliseconde
    sim.run(Day.createEuroNEXT(0, TICKS, 0), NB_DAYS)
    sim.market.printState()
    sim.market.close()
}