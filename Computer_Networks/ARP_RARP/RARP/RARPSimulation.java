import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class RARPEntry {
  String macAddress;
  String ipAddress;
  long timeStamp;
  static final long TTL = 3000000;

  RARPEntry(String macAddress, String ipAddress) {
    this.macAddress = macAddress;
    this.ipAddress = ipAddress;
    this.timeStamp = System.currentTimeMillis();
  }

  boolean isExpired() {
    return System.currentTimeMillis() - this.timeStamp > TTL;
  }
}

class Device {
  String ipAddress;
  String macAddress;
  Map<String, RARPEntry> rarpTable;

  Device(String ip, String mac) {
    this.ipAddress = ip;
    this.macAddress = mac;
    this.rarpTable = new HashMap<>();
  }

  void sendRARPRequest(String targetMac, Network network) {
    System.out.println("[" + this.macAddress + "] Sending RARP request for MAC: " + targetMac);
    network.processRARPRequest(this.macAddress, targetMac);
  }

  void receiveRARPRequest(String srcMac, String targetMac, Network network) {
    System.out.println("[" + this.macAddress + "] Received RARP Request from " + srcMac + " asking for " + targetMac);

    if (this.macAddress.equals(targetMac)) {
      System.out.println("[" + this.macAddress + "] I own MAC " + targetMac + ". Sending RARP reply.");
      network.processRARPReply(this.ipAddress, this.macAddress, srcMac);
    }
  }

  void receiveRARPReply(String srcIp, String srcMac) {
    System.out
        .println("[" + this.macAddress + "] Received RARP reply from MAC: " + srcMac + " with IP address: " + srcIp);
    rarpTable.put(srcMac, new RARPEntry(srcMac, srcIp));
  }

  void printRARPTable() {
    System.out.println("[" + this.macAddress + "] RARP Table:");
    rarpTable.entrySet().removeIf(entry -> entry.getValue().isExpired());
    for (RARPEntry entry : rarpTable.values()) {
      System.out.println(" " + entry.macAddress + " -> " + entry.ipAddress);
    }
  }
}

class Network {
  List<Device> devices;

  Network() {
    devices = new ArrayList<>();
  }

  void addDevice(Device device) {
    devices.add(device);
    System.out.println("Device with MAC: " + device.macAddress + " added to the network.");
  }

  void processRARPRequest(String srcMac, String targetMac) {
    for (Device device : devices) {
      if (!device.macAddress.equals(srcMac)) {
        device.receiveRARPRequest(srcMac, targetMac, this);
      }
    }
  }

  void processRARPReply(String srcIp, String srcMac, String targetMac) {
    for (Device device : devices) {
      if (device.macAddress.equals(targetMac)) {
        device.receiveRARPReply(srcIp, srcMac);
      }
    }
  }

  Device findDeviceByMac(String macAddress) {
    for (Device device : devices) {
      if (device.macAddress.equals(macAddress)) {
        return device;
      }
    }
    return null;
  }
}

public class RARPSimulation {

  public static void main(String[] args) throws InterruptedException {
    Network network = new Network();

    // Create devices and add them to the network
    Device device1 = new Device("192.168.1.1", "00:1A:2B:3C:4D:5E");
    Device device2 = new Device("192.168.1.2", "00:1A:2B:3C:4D:5F");
    Device device3 = new Device("192.168.1.3", "00:1A:2B:3C:4D:60");

    network.addDevice(device1);
    network.addDevice(device2);
    network.addDevice(device3);

    // Simulate a RARP request from device1 to resolve the MAC address to an IP
    device1.sendRARPRequest("00:1A:2B:3C:4D:5F", network);
    Thread.sleep(1000); // Simulate some network delay

    // Simulate a RARP request from device2 to resolve another MAC address
    device2.sendRARPRequest("00:1A:2B:3C:4D:60", network);
    Thread.sleep(1000); // Simulate some network delay

    // Print RARP tables for each device
    device1.printRARPTable();
    device2.printRARPTable();
    device3.printRARPTable();
  }
}
