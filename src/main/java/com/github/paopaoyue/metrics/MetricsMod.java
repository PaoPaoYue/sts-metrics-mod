package com.github.paopaoyue.metrics;

import basemod.*;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.StartGameSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.github.paopaoyue.metrics.data.CardPickStatCache;
import com.github.paopaoyue.metrics.data.MetricsData;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.Arrays;

@SpireInitializer
public class MetricsMod implements StartGameSubscriber, EditStringsSubscriber, PostInitializeSubscriber {

    public static final Logger logger = LogManager.getLogger(MetricsMod.class);

    public static final String MOD_ID = "sts-metrics";

    public static MetricsData metricsData = new MetricsData();

    public static CardPickStatCache cardPickStatCache = new CardPickStatCache();

    private static SpireConfig config = null;

    public enum ConfigField {
        DISPLAY_DISABLED("VoiceDisabled");

        final String id;

        ConfigField(String val) {
            this.id = val;
        }
    }

    public static boolean isDisplayDisabled() {
        return config.getBool(ConfigField.DISPLAY_DISABLED.id);
    }

    public MetricsMod() {
        BaseMod.subscribe(this);
        try {
            config = new SpireConfig(MOD_ID, "Common");
        } catch (IOException e) {
            logger.error("Failed to load config", e);
        }
    }

    public static void initialize() {
        new MetricsMod();
    }

    public static void sideload()
    {
        new MetricsMod();

        // disable display when silent loaded
        config.setBool(ConfigField.DISPLAY_DISABLED.id, true);
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = ImageMaster.loadImage("image/icon/metrics_mod_badge.png");
        Gson gson = new Gson();
        ModInfo info = Arrays.stream(Loader.MODINFOS).filter(modInfo -> modInfo.ID.endsWith(MOD_ID)).findFirst().orElse(null);
        if (info == null) {
            logger.info("ModInfo not found for " + MOD_ID);
            return;
        }
        ModPanel settingsPanel = new ModPanel();
        settingsPanel.addUIElement(new ModLabel("STS Metrics", 400.0f, 700.0f, settingsPanel, (me) -> {
        }));
        settingsPanel.addUIElement(new ModLabeledToggleButton("Silent Mode (Disable display and upload data only)",
                350f, 650f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                isDisplayDisabled(), settingsPanel, (label) -> {
        }, (button) -> {
            config.setBool(ConfigField.DISPLAY_DISABLED.id, button.enabled);
            try {
                config.save();
            } catch (IOException e) {
                logger.error("Config save failed:", e);
            }
        }));
        BaseMod.registerModBadge(badgeTexture, info.Name, Strings.join(Arrays.asList(info.Authors), ','), info.Description, settingsPanel);
    }

    @Override
    public void receiveStartGame() {
        metricsData.reset();
    }

    @Override
    public void receiveEditStrings() {
        String language;
        switch (Settings.language) {
            case ZHS:
                language = "zhs";
                break;
            default:
                language = "eng";
                break;
        }
        BaseMod.loadCustomStringsFile(UIStrings.class, "localization/" + language + "/metrics_ui.json");
    }
}
