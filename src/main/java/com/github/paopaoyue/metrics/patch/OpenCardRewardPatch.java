package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickStatData;
import com.github.paopaoyue.metrics.utility.Async;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(
        clz = RewardItem.class,
        method = "claimReward"
)
public class OpenCardRewardPatch {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(RewardItem __instance) {
        Async.run(() -> {
            List<AbstractCard> cardsToFetch = new ArrayList<>();
            for (AbstractCard card : __instance.cards) {
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


    private static class Locator extends SpireInsertLocator {

        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CardRewardScreen.class, "open");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
