package com.github.paopaoyue.metrics.data;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;

public class CardPickData {

    List<AbstractCard> pickedCards = new ArrayList<>();
    List<AbstractCard> notPickedCards = new ArrayList<>();
    List<Integer> pickedCardsNumInDeck = new ArrayList<>();
    List<Integer> notPickedCardsNumInDeck = new ArrayList<>();

    public void addPickedCard(AbstractCard card) {
        int num = AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.cardID.equals(card.cardID)).mapToInt(c -> 1).sum();
        pickedCardsNumInDeck.add(num);
        pickedCards.add(card);
    }

    public void addNotPickedCard(AbstractCard card) {
        int num = AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.cardID.equals(card.cardID)).mapToInt(c -> 1).sum();
        notPickedCardsNumInDeck.add(num);
        notPickedCards.add(card);
    }
}
