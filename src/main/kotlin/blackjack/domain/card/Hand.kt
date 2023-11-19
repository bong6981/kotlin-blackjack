package blackjack.domain.card

data class Hand(
    private val _cards: MutableList<Card> = mutableListOf()
) {

    val cards: List<Card>
        get() = _cards.toList()

    val ranks: List<Rank>
        get() = _cards.map { it.rank }

    val score: HandScore
        get() = HandScore.from(this)

    fun add(card: Card) {
        _cards.add(card)
    }
}
