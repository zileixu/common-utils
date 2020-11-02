package com.lmnplace.commonutils.common.serializer;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import static org.apache.commons.io.Charsets.UTF_8;

public class ZkStringSerializer implements ZkSerializer {
    @Override
    public byte[] serialize(Object data) throws ZkMarshallingError {
        return JSON.toJSONString(data).getBytes(UTF_8);
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return new String(bytes,UTF_8);
    }
}
