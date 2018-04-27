package main.agents

import v13.*
import v13.agents.Agent

/*
 * COMMENT CREER UN AGENT ?
 *
 * Dans sa version la plus basique, un agent hérite de la classe Agent et
 * surcharge la méthode decide(obName,day) qui est appelée automatiquement par
 * le marché à chaque fois que cet agent a la possibilité de s'exprimer sur cet
 * obName. La structure Day permet à l'agent d'avoir des informations sur le
 * temps quis'écoule durant l'expérience.
 *
 * L'agent ci dessous est un agent qui n'enverra qu'un seul ordre à un moment de
 * la journée passé en paramètre dans son constructeur. Cet agent, très trivial
 * n'a évidemment pour but que de montrer comment écrire son propre agent. Il
 * possède néanmoins un paramètre spécifique, raisonne sur le temps et ne parle
 * pas tout le temps.
 */
internal class MonAgent(name: String, cash: Long, private val declenche: Int) : Agent(name, cash) {

    override fun decide(obName: String, day: Day): Order? {
        return if (day.currentPeriod().isContinuous && day.currentPeriod().currentTick() == declenche) {

            LimitOrder(obName, "" + myId, LimitOrder.ASK, 10, 10000)
        } else
            null
        /*
         * Bien sur, l'agent a le droit de ne rien faire !
         */
    }

    override fun touchedOrExecutedOrder(e: Event?, o: Order?, p: PriceRecord?) {
        /*
         * L'agent est notifié quand l'un de ses ordres a été touché ou exécuté
         * A lui de voir ce qu'il fait de cette information.
         * Cette méthode n'est pas obligatoire.
         *
         * if (e == Event.EXECUTED && o.extId.equals(...)
         * {
         *
         * }
         *
         */
    }
}
/*
 * Il suffit pour utiliser cet agent d'ajouter dans l'expérience précédente la
 * ligne sim.addNewAgent(new MonAgent("pierre",0,100); L'agent se déclenchera au
 * 100è tour de parole. On vérifiera cela aisément dans la trace d'exécution
 * générée.
 */
