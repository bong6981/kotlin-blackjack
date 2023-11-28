package blackjack.domain.distirbution

import blackjack.mock.table
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class DealInitialCardsTest : DescribeSpec({
    describe("deal") {
        context("첫 카드 배분 스테이지를 진행시키면") {
            val table = table()
            val dealInitialCards = DealInitialCards(table)
            dealInitialCards.deal()

            it("플레이어마다 2장의 카드 수령") {
                table.players.value.forEach { player ->
                    player.hand.cards.size shouldBe 2
                }
            }

            it("딜러도 2장의 카드 수령") {
                table.dealer.hand.cards.size shouldBe 2
            }

            it("다음 카드 배분은 플레이어에게 카드 배분") {
                dealInitialCards.nextDistributor.shouldBeTypeOf<DealToPlayer>()
            }
        }
    }
})
