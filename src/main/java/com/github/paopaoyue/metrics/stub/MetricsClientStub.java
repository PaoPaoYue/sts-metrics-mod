package com.github.paopaoyue.metrics.stub;

import io.github.paopaoyue.mesh.rpc.api.CallOption;
import io.github.paopaoyue.mesh.rpc.RpcAutoConfiguration;
import io.github.paopaoyue.mesh.rpc.proto.Protocol;
import io.github.paopaoyue.mesh.rpc.stub.IClientStub;
import io.github.paopaoyue.mesh.rpc.stub.ServiceClientStub;
import io.github.paopaoyue.mesh.rpc.util.Context;
import io.github.paopaoyue.mesh.rpc.util.Flag;
import io.github.paopaoyue.mesh.rpc.util.RespBaseUtil;
import io.github.paopaoyue.mesh.rpc.util.TraceInfoUtil;
import com.github.paopaoyue.metrics.proto.MetricsProto;
import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessage;

@ServiceClientStub(serviceName = "metrics")
public class MetricsClientStub implements IClientStub {

    private static final String SERVICE_NAME = "metrics";

    public <RESP extends GeneratedMessage, REQ extends GeneratedMessage> RESP process(Class<RESP> respClass, REQ request, CallOption option) {
        String handlerName;
        switch (request.getClass().getSimpleName()) {
            case "MGetCardPickStatRequest":
                handlerName = "mGetCardPickStat";
                break;
            case "MCreateCardPickRequest":
                handlerName = "mCreateCardPick";
                break;
            default:
                throw new IllegalArgumentException("Invalid request type: " + request.getClass().getSimpleName());
        }

        try {
            return respClass.cast(RpcAutoConfiguration.getRpcClient().getSender()
                    .send(SERVICE_NAME, handlerName, Any.pack(request), false, option).getBody().unpack(respClass));
        } catch (Exception e) {
            switch (handlerName) {
                case "mGetCardPickStat":
                    return respClass.cast(MetricsProto.MGetCardPickStatResponse.newBuilder().setBase(RespBaseUtil.ErrorRespBase(e)).build());
                case "mCreateCardPick":
                    return respClass.cast(MetricsProto.MCreateCardPickResponse.newBuilder().setBase(RespBaseUtil.ErrorRespBase(e)).build());
                default:
                    throw new IllegalArgumentException("Invalid request type: " + request.getClass().getSimpleName());
            }
        }
    }
}
