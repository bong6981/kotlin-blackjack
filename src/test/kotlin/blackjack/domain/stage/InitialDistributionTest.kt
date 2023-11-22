package blackjack.domain.stage

import blackjack.domain.BlackJackGame
import blackjack.mock.InputProcessorMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class InitialDistributionTest : DescribeSpec({
    val game = BlackJackGame(InputProcessorMock())
    describe("dealCards") {
        context("첫 카드 배분 스테이지를 진행시키면") {
            val dealCards = DealInitialCards()
            dealCards(game)

            it("플레이어마다 2장의 카드 수령") {
                game.players.allPlayers.forEach { player ->
                    player.hand.cards.size shouldBe 2
                }
            }

            it("딜러도 2장의 카드 수령") {
                game.dealer.hand.cards.size shouldBe 2
            }

            it("다음 카드 배분은 플레이어에게 카드 배분") {
                game.dealCards.shouldBeTypeOf<DealToPlayer>()
            }
        }
    }
})
