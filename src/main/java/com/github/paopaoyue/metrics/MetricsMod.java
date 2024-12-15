package com.github.paopaoyue.metrics;

import basemod.*;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostDeathSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.StartGameSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.github.paopaoyue.metrics.data.MetricsData;
import com.github.paopaoyue.metrics.utility.ModLabeledDropdown;
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
public class MetricsMod implements PostDeathSubscriber, EditStringsSubscriber, PostInitializeSubscriber {

    public static final Logger logger = LogManager.getLogger(MetricsMod.class);

    public static final String MOD_ID = "sts-metrics-local";

    public static MetricsData metricsData = new MetricsData();
    public static Exporter exporter = new Exporter();

    private static SpireConfig config = null;

    public enum ConfigField {
        DISPLAY_DISABLED("DisplayDisabled");

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
        exporter.initialize();
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
        settingsPanel.addUIElement(new ModLabeledToggleButton("Disable display",
                400f, 600f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                isDisplayDisabled(), settingsPanel, (label) -> {
        }, (button) -> {
            config.setBool(ConfigField.DISPLAY_DISABLED.id, button.enabled);
        }));
            settingsPanel.addUIElement(new ModLabeledDropdown("Export Character Pick Rate", null, 400f, 500f, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, exporter.getCharacterList(), label -> {}, dropdownMenu -> {
        }, (i, name) -> {
            exporter.setSelectedCharacter(name);
        }));
        settingsPanel.addUIElement(new ModLabeledButton("Confirm Export", 900f, 470f, Settings.CREAM_COLOR, Settings.GREEN_TEXT_COLOR, FontHelper.charDescFont, settingsPanel, button -> {
            exporter.export();
        }));
        ModLabel exportResult = new ModLabel("", 400f, 300f, FontHelper.tipBodyFont, settingsPanel, (me) -> {});
        exporter.setExportResult(exportResult);
        settingsPanel.addUIElement(exportResult);
        BaseMod.registerModBadge(badgeTexture, info.Name, Strings.join(Arrays.asList(info.Authors), ','), info.Description, settingsPanel);

        logger.info("Metrics Loading Runs.....");
        metricsData.LoadRunData();
        logger.info("Metrics Loading Runs.....Done");
    }

    @Override
    public void receivePostDeath() {
        metricsData.reset();
        MetricsMod.metricsData.LoadRunData();
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
