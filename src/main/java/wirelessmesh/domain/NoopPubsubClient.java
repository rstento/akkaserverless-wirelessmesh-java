package wirelessmesh.domain;

import com.google.protobuf.ByteString;

public class NoopPubsubClient implements PubsubClient {

    public void publish(String topic, ByteString eventByteString) {
        // Nada
    }
}
