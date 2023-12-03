package blackjack.domain.betting

import blackjack.domain.Dealer
import blackjack.domain.batting.BetBoard
import blackjack.domain.batting.PlayerBet
import blackjack.domain.card.Rank
import blackjack.domain.player.DealerPlayer
import blackjack.domain.player.Player
import blackjack.domain.player.PlayerName
import blackjack.domain.result.distribution.DealEndResult
import blackjack.domain.result.game.Profit
import blackjack.mock.amount
import blackjack.mock.card
import blackjack.mock.hand
import blackjack.mock.player
import blackjack.mock.players
import blackjack.mock.profit
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class BetBoardTest : DescribeSpec({
    describe("BettingBoard") {
        context("플레이어 이름으로 베팅 금액 등록 (kim: 3_000원, lee: 4_000원)") {
            val players = players(player("kim"), player("lee"))
            val kimAmount = amount(3_000)
            val leeAmount = amount(4_000)
            val betAmount = { player: Player ->
                if (player.name.value == "kim") kimAmount
                else leeAmount
            }

            val betBoard: BetBoard = BetBoard.of(players, betAmount)

            it("플레이어 kim 의 베팅 금액은 3_000") {
                val name = PlayerName("kim")
                val playerBet = betBoard.playerBet(name)

                playerBet.playerName shouldBe name
                (playerBet as? PlayerBet.Placed)?.betAmount shouldBe kimAmount
            }

            it("플레이어 lee 의 베팅 금액은 4_000") {
                val name = PlayerName("lee")
                val playerBet = betBoard.playerBet(name)

                playerBet.playerName shouldBe name
                (playerBet as? PlayerBet.Placed)?.betAmount shouldBe leeAmount
            }
        }
    }

    describe("playerProfit") {
        context("베팅이 완료된 플레이어 (베팅: 1000, 받은돈: 0)") {
            val name = PlayerName("kim")
            val betAmount = amount(1000)
            val payoutAmount = amount(0)
            val finishedBet = PlayerBet.Finished(name, betAmount, payoutAmount)
            val betBord = BetBoard(
                mutableMapOf(
                    name to finishedBet,
                    PlayerName("lee") to PlayerBet.Finished(PlayerName("lee"), amount(5_000), amount(5_000))
                )
            )

            val result = betBord.playerProfit(name)

            it("베팅 수익 조회(-1000)") {
                result.value shouldBe BigDecimal(-1000)
            }
        }

        context("베팅이 완료되지 않은 플레이어") {
            val name = PlayerName("kim")
            val betAmount = amount(1000)
            val placedBet = PlayerBet.Placed(name, betAmount)
            val betBord = BetBoard(
                mutableMapOf(
                    name to placedBet,
                    PlayerName("lee") to PlayerBet.Placed(PlayerName("lee"), amount(5_000))
                )
            )

            it("수익 조회 실패") {
                shouldThrowExactly<IllegalStateException> {
                    betBord.playerProfit(name)
                }
            }
        }

        describe("dealerProfit") {
            context("플레이어1의 수익: 2000, 플레이어2의 수익: -1000") {
                val betBoard = BetBoard(
                    mutableMapOf(
                        PlayerName("kim") to PlayerBet.Finished(PlayerName("kim"), amount(2_000), amount(4_000)),
                        PlayerName("lee") to PlayerBet.Finished(PlayerName("lee"), amount(1_000), amount(0)),
                    )
                )

                betBoard.playerProfit(PlayerName("kim")) shouldBe profit(2_000)
                betBoard.playerProfit(PlayerName("lee")) shouldBe profit(-1000)

                it("딜러의 수익 = -(두 플레이어 수익의 합) (-1000)") {
                    betBoard.dealerProfit() shouldBe profit(-1000)
                }
            }
            context("모든 플레이어 베팅이 끝나지 않았다면") {
                val betBord = BetBoard(
                    mutableMapOf(
                        PlayerName("kim") to PlayerBet.Placed(PlayerName("kim"), amount(5_000)),
                        PlayerName("lee") to PlayerBet.Placed(PlayerName("lee"), amount(5_000))
                    )
                )
                it("수익 조회 실패") {
                    shouldThrowExactly<IllegalStateException> {
                        betBord.dealerProfit()
                    }
                }
            }
        }

        describe("closeBetting") {
            context("딜러를 블랙잭으로 이긴 플레이어") {
                val betBord = BetBoard(
                    mutableMapOf(
                        PlayerName("kim") to PlayerBet.Placed(PlayerName("kim"), amount(5_000)),
                        PlayerName("lee") to PlayerBet.Placed(PlayerName("lee"), amount(5_000))
                    )
                )

                val player = player("kim", hand = hand(card(Rank.ACE), card(Rank.TEN)))
                val dealer = Dealer(player = DealerPlayer(hand(card(Rank.TEN), card(Rank.TEN))))

                it("베팅 종료시 수익이 1.5배") {
                    betBord.closeBetting(DealEndResult(players(player, player("lee")), dealer))

                    val expect = (5_000 * 1.5).toBigDecimal()
                    betBord.playerProfit(PlayerName("kim")) shouldBe Profit(expect)
                }
            }

            context("딜러를 일반 숫자로 이긴 플레이어") {
                val betBord = BetBoard(
                    mutableMapOf(
                        PlayerName("kim") to PlayerBet.Placed(PlayerName("kim"), amount(5_000)),
                        PlayerName("lee") to PlayerBet.Placed(PlayerName("lee"), amount(5_000))
                    )
                )

                val player = player("kim", hand = hand(card(Rank.TEN), card(Rank.TEN)))
                val dealer = Dealer(player = DealerPlayer(hand(card(Rank.TEN), card(Rank.TEN), card(Rank.TEN))))

                it("베팅 종료시 수익이 1배") {
                    betBord.closeBetting(DealEndResult(players(player, player("lee")), dealer))

                    val expect = (5_000 * 1).toBigDecimal()
                    betBord.playerProfit(PlayerName("kim")) shouldBe Profit(expect)
                }
            }

            context("딜러와 둘다 블랙잭으로 무승부인 플레이어") {
                val betBord = BetBoard(
                    mutableMapOf(
                        PlayerName("kim") to PlayerBet.Placed(PlayerName("kim"), amount(5_000)),
                        PlayerName("lee") to PlayerBet.Placed(PlayerName("lee"), amount(5_000))
                    )
                )

                val player = player("kim", hand = hand(card(Rank.TEN), card(Rank.ACE)))
                val dealer = Dealer(player = DealerPlayer(hand(card(Rank.TEN), card(Rank.ACE))))

                it("베팅 종료시 수익은 0") {
                    betBord.closeBetting(DealEndResult(players(player, player("lee")), dealer))

                    betBord.playerProfit(PlayerName("kim")) shouldBe Profit(BigDecimal(0))
                }
            }

            context("딜러와 둘다 일반 점수로 무승부인 플레이어") {
                val betBord = BetBoard(
                    mutableMapOf(
                        PlayerName("kim") to PlayerBet.Placed(PlayerName("kim"), amount(5_000)),
                        PlayerName("lee") to PlayerBet.Placed(PlayerName("lee"), amount(5_000))
                    )
                )

                val player = player("kim", hand = hand(card(Rank.TWO), card(Rank.ACE)))
                val dealer = Dealer(player = DealerPlayer(hand(card(Rank.TWO), card(Rank.ACE))))

                it("베팅 종료시 수익은 0") {
                    betBord.closeBetting(DealEndResult(players(player, player("lee")), dealer))

                    betBord.playerProfit(PlayerName("kim")) shouldBe Profit(BigDecimal(0))
                }
            }

            context("버스트인 플레이어") {
                val betBord = BetBoard(
                    mutableMapOf(
                        PlayerName("kim") to PlayerBet.Placed(PlayerName("kim"), amount(5_000)),
                        PlayerName("lee") to PlayerBet.Placed(PlayerName("lee"), amount(5_000))
                    )
                )

                val player = player("kim", hand = hand(card(Rank.TEN), card(Rank.TEN), card(Rank.TEN)))
                val dealer = Dealer(player = DealerPlayer(hand(card(Rank.TWO), card(Rank.ACE))))

                it("베팅 종료시 수익은 -베팅금액") {
                    betBord.closeBetting(DealEndResult(players(player, player("lee")), dealer))

                    betBord.playerProfit(PlayerName("kim")) shouldBe Profit(BigDecimal(-5000))
                }
            }

            context("버스트인 플레이어와 딜러도 버스트") {
                val betBord = BetBoard(
                    mutableMapOf(
                        PlayerName("kim") to PlayerBet.Placed(PlayerName("kim"), amount(5_000)),
                        PlayerName("lee") to PlayerBet.Placed(PlayerName("lee"), amount(5_000))
                    )
                )

                val player = player("kim", hand = hand(card(Rank.TEN), card(Rank.TEN), card(Rank.TEN)))
                val dealer = Dealer(player = DealerPlayer(hand(card(Rank.TEN), card(Rank.TEN), card(Rank.TEN))))

                it("베팅 종료시 수익은 -베팅금액") {
                    betBord.closeBetting(DealEndResult(players(player, player("lee")), dealer))

                    betBord.playerProfit(PlayerName("kim")) shouldBe Profit(BigDecimal(-5000))
                }
            }
        }
    }
})
