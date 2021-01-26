package wirelessmesh;

import com.google.protobuf.ByteString;

// Interface to publishing to google pubsub, etc.
public interface PubsubClient {

    void publish(String projectId, String topicName, ByteString eventByteString);
}
