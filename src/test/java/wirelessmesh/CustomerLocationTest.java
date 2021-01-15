package wirelessmesh;

import io.cloudstate.javasupport.eventsourced.CommandContext;
import org.testng.Assert;
import org.testng.annotations.*;
import org.mockito.*;
import wirelessmesh.domain.CustomerLocation;
import domain.Domain.*;
import service.Wirelessmeshservice.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CustomerLocationTest {

    String customerLocationId = "customerId1";
    String accessToken = "accessToken";
    String room = "person-cave";

    @Test
    public void addCustomerLocationTest() {
        createAndAddCustomerLocation();
    }

    private void createAndActivateDevice(CustomerLocation customerLocation, String deviceId) {
        CommandContext context = Mockito.mock(CommandContext.class);
        DeviceActivated activated = DeviceActivated.newBuilder()
                .setCustomerLocationId(customerLocationId)
                .setDeviceId(deviceId)
                .build();

        customerLocation.activateDevice(ActivateDeviceCommand.newBuilder()
                .setCustomerLocationId(customerLocationId)
                .setDeviceId(deviceId)
                .build(), context);

        customerLocation.deviceActivated(activated); // Simulate event callback to drive state change.

        Mockito.verify(context).emit(activated);
    }

    @Test
    public void activateDevicesTest() {
        CustomerLocation customerLocation = createAndAddCustomerLocation();
        createAndActivateDevice(customerLocation, "deviceId1");
        createAndActivateDevice(customerLocation, "deviceId2");
        createAndActivateDevice(customerLocation, "deviceId3");

        // Test get device.
        GetDevicesCommand command = GetDevicesCommand.newBuilder().setCustomerLocationId(customerLocationId).build();
        Devices devices = customerLocation.getDevices(command, null);
        Assert.assertEquals(devices.getDeviceList().size(), 3);
        Assert.assertEquals(devices.getDevice(0).getDeviceId(), "deviceId1");
        Assert.assertEquals(devices.getDevice(1).getDeviceId(), "deviceId2");
        Assert.assertEquals(devices.getDevice(2).getDeviceId(), "deviceId3");
    }

    @Test
    public void removeCustomerLocationTest() {
        CommandContext context = Mockito.mock(CommandContext.class);
        CustomerLocation customerLocation = createAndAddCustomerLocation();
        CustomerLocationRemoved removed = CustomerLocationRemoved.newBuilder()
                .setCustomerLocationId(customerLocationId)
                .build();

        customerLocation.removeCustomerLocation(RemoveCustomerLocationCommand.newBuilder()
            .setCustomerLocationId(customerLocationId).build(), context);

        Mockito.verify(context).emit(removed);
        customerLocation.customerLocationRemoved(removed); // Simulate event callback to drive state change.
        Mockito.reset(context);
        customerLocation.getDevices(GetDevicesCommand.newBuilder().setCustomerLocationId(customerLocationId).build(), context);
        Mockito.verify(context).fail("customerLocation does not exist.");
    }

    @Test
    public void assignRoomTest() {
        CommandContext context = Mockito.mock(CommandContext.class);
        CustomerLocation customerLocation = createAndAddCustomerLocation();
        createAndActivateDevice(customerLocation, "deviceId1");
        createAndActivateDevice(customerLocation, "deviceId2");
        createAndActivateDevice(customerLocation, "deviceId3");

        RoomAssigned assigned = RoomAssigned.newBuilder()
                .setDeviceId("deviceId2")
                .setCustomerLocationId(customerLocationId)
                .setRoom(room)
                .build();

        customerLocation.assignRoom(AssignRoomCommand.newBuilder()
                .setDeviceId("deviceId2")
                .setRoom(room)
                .build()
                , context);

        Mockito.verify(context).emit(assigned);
        customerLocation.roomAssigned(assigned); // Simulate event callback to drive state change.
        GetDevicesCommand command = GetDevicesCommand.newBuilder()
                .setCustomerLocationId(customerLocationId)
                .build();

        List<Device> expected = new ArrayList<Device>();
        expected.add(defaultDevice("deviceId1"));
        expected.add(defaultDevice("deviceId2").toBuilder().setRoom(room).build());
        expected.add(defaultDevice("deviceId3"));
        Devices devices = customerLocation.getDevices(command, context);
        List<Device> sorted = devices.getDeviceList().stream().sorted(Comparator.comparing(Device::getDeviceId)).collect(toList());
        Assert.assertEquals(sorted, expected);
    }

    @Test
    public void toggleNightlightTest() throws IOException {
        CommandContext context = Mockito.mock(CommandContext.class);
        CustomerLocation customerLocation = createAndAddCustomerLocation();
        createAndActivateDevice(customerLocation, "deviceId1");
        createAndActivateDevice(customerLocation, "deviceId2");
        createAndActivateDevice(customerLocation, "deviceId3");

        NightlightToggled toggled = NightlightToggled.newBuilder()
                .setDeviceId("deviceId2")
                .setCustomerLocationId(customerLocationId)
                .setNightlightOn(true)
                .build();

        customerLocation.toggleNightlight(ToggleNightlightCommand.newBuilder()
                        .setDeviceId("deviceId2")
                        .build()
                , context);

        Mockito.verify(context).emit(toggled);
        customerLocation.nightlightToggled(toggled); // Simulate event callback to drive state change.
        GetDevicesCommand command = GetDevicesCommand.newBuilder()
                .setCustomerLocationId(customerLocationId)
                .build();

        List<Device> expected = new ArrayList<Device>();
        expected.add(defaultDevice("deviceId1"));
        expected.add(defaultDevice("deviceId2").toBuilder().setNightlightOn(true).build());
        expected.add(defaultDevice("deviceId3"));
        Devices devices = customerLocation.getDevices(command, context);
        List<Device> sorted = devices.getDeviceList().stream().sorted(Comparator.comparing(Device::getDeviceId)).collect(toList());
        Assert.assertEquals(sorted, expected);
    }

    private CustomerLocation createAndAddCustomerLocation() {
        CommandContext context = Mockito.mock(CommandContext.class);
        CustomerLocation customerLocation = new CustomerLocation(customerLocationId);

        CustomerLocationAdded added = CustomerLocationAdded.newBuilder()
                .setCustomerLocationId(customerLocationId)
                .setAccessToken(accessToken)
                .build();

        customerLocation.addCustomerLocation(AddCustomerLocationCommand.newBuilder()
                .setCustomerLocationId(customerLocationId)
                .setAccessToken(accessToken)
                .build(), context);

        Mockito.verify(context).emit(added);
        customerLocation.customerLocationAdded(added); // Simulate event callback to drive state change.
        
        return customerLocation;
    }

    private Device defaultDevice(String deviceId) {
        return Device.newBuilder()
                .setDeviceId(deviceId)
                .setCustomerLocationId(customerLocationId)
                .setActivated(true).setRoom("")
                .setNightlightOn(false)
                .build();
    }
}
