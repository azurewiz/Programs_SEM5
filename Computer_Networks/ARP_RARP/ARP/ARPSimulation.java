import java.util.*;

// Class representing an ARP Entry in the ARP table
class ARPEntry {
  String ipAddress;
  String macAddress;
  long timeStamp;
  static final long TTL = 300000; // ARP entry TTL (5 minutes)

  ARPEntry(String ipAddress, String macAddress) {
    this.ipAddress = ipAddress;
    this.macAddress = macAddress;
    this.timeStamp = System.currentTimeMillis();
  }

  boolean isExpired() {
    return System.currentTimeMillis() - this.timeStamp > TTL;
  }
}

// Class representing a Device in the network
class Device {
  String ipAddress;
  String macAddress;
  Map<String, ARPEntry> arpTable;

  Device(String ip, String mac) {
    this.ipAddress = ip;
    this.macAddress = mac;
    this.arpTable = new HashMap<>();
  }

  // Send ARP Request to the network
  void sendARPRequest(String targetIp, Network network) {
    System.out.println("[" + this.ipAddress + "] Sending ARP request for IP: " + targetIp);
    network.processARPRequest(this.ipAddress, targetIp);
  }

  // Receive an ARP Request from another device
  void receiveARPRequest(String srcIp, String srcMac, String targetIp, Network network) {
    System.out.println("[" + this.ipAddress + "] Received ARP Request from " + srcIp + " asking for " + targetIp);

    // If this device owns the target IP, it sends an ARP reply
    if (this.ipAddress.equals(targetIp)) {
      System.out.println("[" + this.ipAddress + "] I own IP " + targetIp + ". Sending ARP reply.");
      network.processARPReply(this.ipAddress, this.macAddress, srcIp);
    }
  }

  // Receive an ARP Reply and update the ARP table
  void receiveARPReply(String srcIp, String srcMac) {
    System.out.println("[" + this.ipAddress + "] Received ARP reply from " + srcIp + " with MAC address: " + srcMac);
    arpTable.put(srcIp, new ARPEntry(srcIp, srcMac));
  }

  // Print the current ARP table for this device
  void printARPTable() {
    System.out.println("[" + this.ipAddress + "] ARP Table:");
    arpTable.entrySet().removeIf(entry -> entry.getValue().isExpired()); // Remove expired entries
    for (ARPEntry entry : arpTable.values()) {
      System.out.println("  " + entry.ipAddress + " -> " + entry.macAddress);
    }
  }
}

// Class representing a network where devices can communicate via ARP
class Network {
  List<Device> devices;

  Network() {
    devices = new ArrayList<>();
  }

  // Add a device to the network
  void addDevice(Device device) {
    devices.add(device);
    System.out.println("Device with IP: " + device.ipAddress + " added to the network.");
  }

  // Process an ARP Request (broadcast it to all devices)
  void processARPRequest(String srcIp, String targetIp) {
    for (Device device : devices) {
      if (!device.ipAddress.equals(srcIp)) {
        device.receiveARPRequest(srcIp, findDevice(srcIp).macAddress, targetIp, this);
      }
    }
  }

  // Process an ARP Reply (deliver it to the requester)
  void processARPReply(String srcIp, String srcMac, String targetIp) {
    for (Device device : devices) {
      if (device.ipAddress.equals(targetIp)) {
        device.receiveARPReply(srcIp, srcMac);
      }
    }
  }

  // Find a device by its IP address
  Device findDevice(String ipAddress) {
    for (Device device : devices) {
      if (device.ipAddress.equals(ipAddress)) {
        return device;
      }
    }
    return null;
  }
}

public class ARPSimulation {

  public static void main(String[] args) throws InterruptedException {
    Network network = new Network();

    // Create devices and add them to the network
    Device device1 = new Device("192.168.1.1", "00:1A:2B:3C:4D:5E");
    Device device2 = new Device("192.168.1.2", "00:1A:2B:3C:4D:5F");
    Device device3 = new Device("192.168.1.3", "00:1A:2B:3C:4D:60");

    network.addDevice(device1);
    network.addDevice(device2);
    network.addDevice(device3);

    // Simulate an ARP request from device1 to resolve device2's IP
    device1.sendARPRequest("192.168.1.2", network);
    Thread.sleep(1000); // Simulate some network delay

    // Simulate an ARP request from device2 to resolve device3's IP
    device2.sendARPRequest("192.168.1.3", network);
    Thread.sleep(1000); // Simulate some network delay

    // Print ARP tables for each device
    device1.printARPTable();
    device2.printARPTable();
    device3.printARPTable();
  }
}
