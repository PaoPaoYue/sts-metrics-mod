package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.github.paopaoyue.metrics.data.CardPickStatData;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(
        clz= AbstractCard.class,
        method=SpirePatch.CLASS
)
public class CardFieldPatch {
    public static SpireField<CardPickStatData> pickRate = new SpireField<>(() -> null);
}
