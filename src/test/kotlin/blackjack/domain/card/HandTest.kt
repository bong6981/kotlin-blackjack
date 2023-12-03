package blackjack.domain.card

import blackjack.mock.card
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class HandTest : DescribeSpec({
    describe("add") {
        context("카드가 없었을 때") {
            val hand = Hand()
            val card = Card(Suit.HEART, Rank.FOUR)

            hand.add(card)

            it("주어진 카드 추가") {
                hand.cards shouldBe listOf(card)
            }
        }

        context("다른 카드가 있던 경우") {
            val oldCard = Card(Suit.DIAMOND, Rank.ACE)
            val hand = Hand(mutableListOf(oldCard))
            val newCard = Card(Suit.HEART, Rank.FOUR)

            hand.add(newCard)

            it("주어진 카드 추가") {
                hand.cards shouldBe listOf(oldCard, newCard)
            }
        }
    }

    describe("rank") {
        val cards =
            mutableListOf(Card(Suit.SPADE, Rank.FIVE), Card(Suit.DIAMOND, Rank.THREE), Card(Suit.CLUB, Rank.FIVE))
        context("카드($cards)") {
            val hand = Hand(cards)

            val expect = listOf(Rank.FIVE, Rank.THREE, Rank.FIVE)
            it("rank 반환 ($expect)") {
                hand.ranks shouldBe expect
            }
        }
    }

    describe("isBlackJackCardSize") {
        context("블랙잭 조건인 카드가 2장이라면") {
            val hand = Hand(mutableListOf(card(), card()))
            it("true 반환") {
                hand.isBlackJackCardSize shouldBe true
            }
        }

        context("블랙잭 조건인 카드가 2장이 아니라면") {
            val hand = Hand(mutableListOf(card(), card(), card()))
            it("false 반환") {
                hand.isBlackJackCardSize shouldBe false
            }
        }
    }
})
