package com.github.paopaoyue.metrics_local.patch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.github.paopaoyue.metrics_local.MetricsMod;
import com.github.paopaoyue.metrics_local.data.CardPickStatData;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

@SpirePatch(
        clz = AbstractCard.class,
        method = "renderEnergy"
)
public class CardRenderPatch {

    @SpirePostfixPatch
    public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
        if (MetricsMod.isDisplayDisabled()) return;
        if (Loader.isModLoaded("sts-metrics")) return;
        CardPickStatData data = CardFieldPatch.pickRate.get(__instance);
        if (data != null) {
            FontHelper.renderRotatedText(sb,
                    FontHelper.cardEnergyFont_L,
                    data.pickRate,
                    __instance.current_x,
                    __instance.current_y,
                    +132.0F * __instance.drawScale * Settings.scale,
                    -192.0F * __instance.drawScale * Settings.scale,
                    __instance.angle,
                    false,
                    Color.WHITE.cpy());
        }
    }
}
