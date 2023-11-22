package blackjack.domain.stage

import blackjack.domain.BlackJackGame
import blackjack.domain.PlayerAction
import blackjack.domain.result.DealToPlayerResult

class DealToPlayer : CardDistributor {
    override fun invoke(game: BlackJackGame): DealToPlayerResult {
        game.askHitOrStand().also {
            when (it) {
                PlayerAction.HIT -> {
                    game.dealCardToPlayerInTurn()
                    if (game.isPlayerInTurnScoreOverMax) game.setDistributor(DistributionEnd())
                }

                PlayerAction.STAND -> when (game.isLastTurn) {
                    true -> game.setDistributor(DistributionEnd())
                    false -> game.passTurnToNextPlayer()
                }
            }
        }
        return DealToPlayerResult(game.playerInTurn)
    }
}
