package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickStatData;
import com.github.paopaoyue.metrics.utility.Async;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(
        clz = ShopScreen.class,
        method = "open"
)
public class OpenShopPatch {

    @SpirePostfixPatch
    public static void Postfix(ShopScreen __instance) {
        if (MetricsMod.isDisplayDisabled()) return;
        Async.run(() -> {
            List<AbstractCard> cardsToFetch = new ArrayList<>();
            for (AbstractCard card : __instance.coloredCards) {
                CardPickStatData cardPickStatData = MetricsMod.cardPickStatCache.get(card);
                if (cardPickStatData != null) {
                    CardFieldPatch.pickRate.set(card, cardPickStatData);
                } else {
                    cardsToFetch.add(card);
                }
            }
            if (!cardsToFetch.isEmpty()) {
                MetricsMod.cardPickStatCache.fetchFromServer(cardsToFetch);
            }
            for (AbstractCard card : cardsToFetch) {
                CardPickStatData cardPickStatData = MetricsMod.cardPickStatCache.get(card);
                if (cardPickStatData != null) {
                    CardFieldPatch.pickRate.set(card, cardPickStatData);
                }
            }
        });

    }
}
