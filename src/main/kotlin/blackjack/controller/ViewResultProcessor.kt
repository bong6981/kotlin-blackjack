package blackjack.controller

import blackjack.domain.result.GameResult
import blackjack.domain.result.DealToPlayerResult
import blackjack.domain.result.DealInitialCardResult
import blackjack.view.dto.FinalPlayerStateDto
import blackjack.view.dto.PlayerDto
import blackjack.view.output.OutputView

object ViewResultProcessor {
    fun drawInitialDistribution(result: DealInitialCardResult) {
        val model = result.players.allPlayers.map { PlayerDto(it.name.value, it.hand.cards) }
        OutputView.initialDistributionResult(model)
    }

    fun drawPlayerState(result: DealToPlayerResult) {
        val model = result.player.let { PlayerDto(it.name.value, it.hand.cards) }
        OutputView.playerCurrentState(model)
    }

    fun drawGameResult(result: GameResult) {
        val model =
            result.players.allPlayers.map { FinalPlayerStateDto(it.name.value, it.hand.cards, it.score.value) }
        OutputView.playerFinalStates(model)
    }
}
