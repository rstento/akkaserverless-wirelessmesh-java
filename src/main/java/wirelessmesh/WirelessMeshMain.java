package wirelessmesh;

import service.Wirelessmeshservice;
import wirelessmesh.domain.*;
import io.cloudstate.javasupport.CloudState;

import domain.*;

/**
 * This is the entry point into this user function.
 */
public class WirelessMeshMain {

    public static void main(String... args) {
        new CloudState()
                .registerEventSourcedEntity(
                        CustomerLocation.class,
                        Wirelessmeshservice.getDescriptor().findServiceByName("WirelessMeshService"),
                        Devicedomain.getDescriptor())
                .start();
    }
}
