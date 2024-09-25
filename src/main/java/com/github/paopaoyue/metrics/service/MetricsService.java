package com.github.paopaoyue.metrics.service;

import io.github.paopaoyue.mesh.rpc.service.RpcService;
import io.github.paopaoyue.mesh.rpc.util.Context;
import com.github.paopaoyue.metrics.proto.MetricsProto;

@RpcService(serviceName = "metrics")
public class MetricsService implements IMetricsService {

    @Override
    public MetricsProto.MGetCardPickStatResponse mGetCardPickStat(MetricsProto.MGetCardPickStatRequest request) {
        return MetricsProto.MGetCardPickStatResponse.newBuilder().build();
    }
    @Override
    public MetricsProto.MCreateCardPickResponse mCreateCardPick(MetricsProto.MCreateCardPickRequest request) {
        return MetricsProto.MCreateCardPickResponse.newBuilder().build();
    }
}
