package wirelessmesh;

import com.google.protobuf.ByteString;

public class NoopPubsubClient implements PubsubClient {

    public void publish(String projectId, String topicName, ByteString eventByteString) {
        // Nada
    }
}
