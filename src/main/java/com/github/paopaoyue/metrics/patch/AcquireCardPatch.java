package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickData;
import com.github.paopaoyue.metrics.utility.Reflect;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

@SpirePatch(
        clz = CardRewardScreen.class,
        method = "acquireCard"
)
public class AcquireCardPatch {

    @SpirePrefixPatch
    public static void Prefix(CardRewardScreen __instance, AbstractCard hoveredCard) {
        CardFieldPatch.pickRate.set(hoveredCard, null);
    }

    @SpirePostfixPatch
    public static void Postfix(CardRewardScreen __instance, AbstractCard hoveredCard) {
        Boolean codex = Reflect.getPrivate(CardRewardScreen.class, __instance, "codex", Boolean.class);
        Boolean discovery = Reflect.getPrivate(CardRewardScreen.class, __instance, "discovery", Boolean.class);
        Boolean chooseOne = Reflect.getPrivate(CardRewardScreen.class, __instance, "chooseOne", Boolean.class);
        Boolean draft = Reflect.getPrivate(CardRewardScreen.class, __instance, "draft", Boolean.class);
        System.out.println("Codex: " + codex + " Discovery: " + discovery + " ChooseOne: " + chooseOne + " Draft: " + draft);
        if (!codex && !discovery && !chooseOne && !draft) {
            CardPickData cardPickData = new CardPickData();
            cardPickData.addPickedCard(hoveredCard);
            for (AbstractCard card : __instance.rewardGroup) {
                if (card != hoveredCard) {
                    cardPickData.addNotPickedCard(card);
                }
            }
            MetricsMod.metricsData.addCardPickData(cardPickData);
        } else {
            System.out.println("Codex: " + codex + " Discovery: " + discovery + " ChooseOne: " + chooseOne + " Draft: " + draft);
        }
    }
}
