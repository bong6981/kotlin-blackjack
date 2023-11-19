package blackjack.domain.player

import blackjack.domain.card.Card
import blackjack.domain.card.Hand
import blackjack.domain.card.HandScore
import blackjack.domain.card.Rank
import blackjack.domain.card.Suit
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PlayerTest : DescribeSpec({
    describe("플레이어 생성") {
        context("플레이어 이름이 주어지면") {
            val name = PlayerName("홍길동")
            it("플레이어 생성") {
                val result = Player(name)

                result.name shouldBe name
            }
        }
    }

    describe("카드 배분") {
        context("플레이어게 카드를 주면") {
            val player = Player(PlayerName("홍길동"))
            val card = Card(Suit.CLUB, Rank.ACE)

            player.addCard(card)

            it("플레이가 소유한 카드에 카드가 추가") {
                player.hand shouldBe Hand(mutableListOf(card))
            }
        }

        context("플레이어가 다른 카드를 갖고 있던 경우") {
            val oldCard = Card(Suit.DIAMOND, Rank.EIGHT)
            val player = Player(PlayerName("홍길동"), Hand(mutableListOf(oldCard)))
            val newCard = Card(Suit.CLUB, Rank.ACE)

            player.addCard(newCard)

            it("플레이가 소유한 카드에 카드가 추가") {
                player.hand shouldBe Hand(mutableListOf(oldCard, newCard))
            }
        }
    }

    describe("최대 점수 21 넘은 여부") {
        context("최대 점수 21을 넘었을 때") {
            val player = Player(
                PlayerName("kim"), Hand(
                    mutableListOf(
                        Card(Suit.DIAMOND, Rank.THREE), Card(Suit.HEART, Rank.TEN), Card(Suit.HEART, Rank.TEN)
                    )
                )
            )
            it("플레이어 점수 반환") {
                player.isOverMaxScore shouldBe true
            }
        }

        context("최대 점수 21을 넘지 않았을 때") {
            val player = Player(
                PlayerName("kim"), Hand(
                    mutableListOf(
                        Card(Suit.DIAMOND, Rank.ACE), Card(Suit.HEART, Rank.ACE), Card(Suit.HEART, Rank.ACE)
                    )
                )
            )
            it("플레이어 점수 반환") {
                player.isOverMaxScore shouldBe false
            }
        }
    }

    describe("점수 계산") {
        val player = Player(
            PlayerName("kim"), Hand(
                mutableListOf(
                    Card(Suit.DIAMOND, Rank.ACE), Card(Suit.HEART, Rank.TEN)
                )
            )
        )
        context("플레이어의 점수 조회") {
            val result = player.score
            it("플레이어 점수 반환") {
                result shouldBe HandScore(21)
            }
        }
    }
})
