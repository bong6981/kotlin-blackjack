package blackjack.domain.stage

import blackjack.domain.BlackJackGame
import blackjack.domain.result.InitialDistributionResult

class InitialDistribution: Stage {
    var isProgressDone = false
        private set

    override fun dealCards(game: BlackJackGame) {
        game.dealCardsToAllPlayers(INITIAL_DISTRIBUTION_COUNT)
        game.dealCardToDealer(INITIAL_DISTRIBUTION_COUNT)
        isProgressDone = true
    }

    override fun emitResult(game: BlackJackGame) {
        game.emitResult(InitialDistributionResult(game.players))
    }

    override fun nextStage(game: BlackJackGame): Stage = when (isProgressDone) {
        true -> InGameStage()
        false -> this
    }

    companion object {
        private const val INITIAL_DISTRIBUTION_COUNT = 2
    }
}