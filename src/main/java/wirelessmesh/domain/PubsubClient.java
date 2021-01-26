package wirelessmesh.domain;

import com.google.protobuf.ByteString;

// Interface to publishing to google pubsub, etc.
public interface PubsubClient {

    void publish(String topic, ByteString eventByteString);
}
