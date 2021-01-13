# An akka serverless sample application for a wirelessmesh.

Customer locations are modeled, each containing a number of wirelessmesh devices. These devices may be interacted with
in order to assign them to rooms in the house, turn on their nightlights, etc. The nightlight feature is useful because
one can use an internet addressable light, lifx for example to stand in for a device in order to demo. When you use this
sample application to turn on the nightlight, you can see your bulb turn on.

Note: we can't use an actual mesh device for this (such as eero) because appropriately there is no public API to 
communicate with these due to security concerns.

## With maven, 'mvn clean install'
