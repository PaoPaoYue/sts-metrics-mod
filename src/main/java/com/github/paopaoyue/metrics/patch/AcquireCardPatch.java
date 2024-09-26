package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickData;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

@SpirePatch(
        clz = CardRewardScreen.class,
        method = "acquireCard"
)
public class AcquireCardPatch {

    @SpirePrefixPatch
    public static void Prefix(CardRewardScreen _instance, AbstractCard hoveredCard) {
        CardFieldPatch.pickRate.set(hoveredCard, null);
    }

    @SpirePostfixPatch
    public static void Postfix(CardRewardScreen _instance, AbstractCard hoveredCard) {
        CardPickData cardPickData = new CardPickData();
        cardPickData.addPickedCard(hoveredCard);
        for (AbstractCard card : _instance.rewardGroup) {
            if (card != hoveredCard) {
                cardPickData.addNotPickedCard(card);
            }
        }
        MetricsMod.metricsData.addCardPickData(cardPickData);
    }
}
