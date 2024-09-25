package com.github.paopaoyue.metrics;

import basemod.BaseMod;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.StartGameSubscriber;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.MTSClassLoader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.github.paopaoyue.metrics.api.IMetricsCaller;
import com.github.paopaoyue.metrics.data.CardPickStatCache;
import com.github.paopaoyue.metrics.data.MetricsData;
import com.github.paopaoyue.metrics.proto.MetricsProto;
import com.github.paopaoyue.rpcmod.RpcApi;
import com.github.paopaoyue.rpcmod.RpcApp;
import com.google.protobuf.Api;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import io.github.paopaoyue.mesh.rpc.api.CallOption;
import io.github.paopaoyue.mesh.rpc.util.RespBaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpireInitializer
public class MetricsMod implements StartGameSubscriber, EditStringsSubscriber {

    private static final Logger logger = LogManager.getLogger(MetricsMod.class);

    public static final String MOD_ID = "sts-metrics";

    public static MetricsData metricsData = new MetricsData();

    public static CardPickStatCache cardPickStatCache = new CardPickStatCache();

    public MetricsMod() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new MetricsMod();
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
