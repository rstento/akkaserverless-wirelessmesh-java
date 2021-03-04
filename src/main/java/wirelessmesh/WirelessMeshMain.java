package wirelessmesh;

import io.cloudstate.javasupport.CloudState;

import wirelessmesh.domain.CustomerLocationEntity;
import wirelessmeshservice.Wirelessmeshservice;
import wirelessmeshdomain.*;

/**
 * This is the entry point into this user function.
 */
public class WirelessMeshMain {

    public static void main(String... args) {
        new CloudState()
                .registerEventSourcedEntity(
                        CustomerLocationEntity.class,
                        Wirelessmeshservice.getDescriptor().findServiceByName("WirelessMeshService"),
                        Wirelessmeshdomain.getDescriptor())
                .start();
    }
}
