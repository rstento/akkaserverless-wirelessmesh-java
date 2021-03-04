package wirelessmesh;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GooglePubsubService implements PubsubService {

    private final String TOPIC_NAME = "wirelessmesh";

    private String projectName = System.getenv().get("GOOGLE_PROJECT_ID");

    private boolean initialized = false;

    private Publisher publisher = null;

    private void initialize() {
        try {
            TopicName topic = TopicName.of(System.getenv("GOOGLE_PROJECT_ID"), TOPIC_NAME);
            publisher = Publisher.newBuilder(topic).build();
            initialized = true;
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Unable to initialize google pubsub-" + ex.getMessage());
        }
    }

    /**
     * Publish to google pubsub.
     * @param event the event to publish as a ByteString
     */
    public void publish(ByteString event) {
        if (projectName != null) {

            if (!initialized) {
                initialize();
            }

            if (initialized) {
                try {
                    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(event).build();
                    ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
                    String messageId = messageIdFuture.get();
                    Logger.getAnonymousLogger().log(Level.FINEST, "Published message with id-" + messageId);
                } catch (Exception ex) {
                    Logger.getAnonymousLogger().log(Level.WARNING, "Unable to publish to google pubsub-" + ex.getMessage());
                } finally {
                    if (publisher != null) {
                        // When finished with the publisher, shutdown to free up resources.
                        publisher.shutdown();

                        try {
                            publisher.awaitTermination(1, TimeUnit.MINUTES);
                        } catch (Exception ex) {
                            Logger.getAnonymousLogger().log(Level.WARNING, "Unable shutdown google pubsub-" + ex.getMessage());
                        }
                    }
                }
            }
        }
    }
}
