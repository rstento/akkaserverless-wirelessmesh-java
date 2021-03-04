package wirelessmesh;

import com.google.protobuf.ByteString;

public interface PubsubService {
    /**
     * Publish to pubsub.
     * @param event the event to publish as a ByteString
     */
    void publish(ByteString event);
}
