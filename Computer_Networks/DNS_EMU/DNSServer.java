package DNS_EMU;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class DNSServer {

  private static final Map<String, String> domainMap = new HashMap<>();
  private static final int dnsPort = 53;

  // Initialize the domain-to-IP map
  // Method to init domainMap
  public static void initStore() {
    domainMap.put("example.com", "192.168.1.1");
    domainMap.put("mydomain.com", "192.168.1.2");
    domainMap.put("test.com", "192.168.1.3");
  }

  public static void main(String[] args) {

    initStore(); // initialize

    // Essentials
    DatagramSocket socket = null;
    String defaultIp = "0.0.0.0";

    try {
      // Create a UDP socket to listen on port 5353
      socket = new DatagramSocket(dnsPort);
      System.out.println("DNS Server started on port " + dnsPort + "...");

      byte[] receiveBuffer = new byte[512];

      while (true) {
        // Receive DNS query packet
        DatagramPacket requestPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(requestPacket);

        // Extract request data
        byte[] requestData = Arrays.copyOf(requestPacket.getData(), requestPacket.getLength());
        InetAddress clientAddress = requestPacket.getAddress();
        int clientPort = requestPacket.getPort();

        // Pars the DNS query
        String domainName = parseDNSRequest(requestData);
        System.out.println("Received query for domain: " + domainName);

        // Build the response
        byte[] responseData;

        if (domainMap.containsKey(domainName)) {
          // If domain is in the hash
          String ipAddress = domainMap.get(domainName);
          responseData = buildDNSResponse(requestData, domainName, ipAddress);
          System.out.println("Responding with IP: " + ipAddress);
        } else {
          // If domain not in hash; return 0.0.0.0
          responseData = buildDNSResponse(requestData, domainName, defaultIp);
          System.out.println("Unkown domain. Responding with IP: " + defaultIp);
        } // end of domainName check

        // Send DNS response packet
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress,
            clientPort);
        socket.send(responsePacket);
        System.out.println("Sent response for domain: " + domainName);
      }
    } catch (Exception se) {
      // TODO: handle exception
      // Handling socket exception
      System.out.println("SocketException: " + se.getMessage());
      se.printStackTrace();
    } finally {
      if (socket != null && !socket.isClosed()) {
        socket.close();
      }
    } // end of try-catch
  } // end of main

  // DNS query parsere : No big deal just extracting the domain name
  // {from the question section}
  public static String parseDNSRequest(byte[] requestData) {

    StringBuilder domainName = new StringBuilder();
    int position = 12; // DNS header is 12 bytes long

    // Domain name extraction
    int length = requestData[position];
    while (length > 0) {
      for (int i = 0; i < length; i++) {
        domainName.append((char) requestData[position + 1 + i]);
      }
      position += length + 1;
      length = requestData[position];
      if (length > 0) {
        domainName.append(".");
      }
    }
    return domainName.toString();
  } // end of parseDNSRequest

  // Build DNS response : with hardcoded IP -> for simplicity
  // TODO handle DNS hashing
  public static byte[] buildDNSResponse(byte[] requestData, String domainName, String ipAddress) {

    try {
      // Response buffer
      byte[] response = new byte[512];

      // Transcribe header from request to response
      System.arraycopy(requestData, 0, response, 0, 12);

      // Set response flags
      response[2] = (byte) 0x81; // 10000001 (QR = 1, OPCODE = 0000, AA = 1)
      response[3] = (byte) 0x82; // 10000000 (No error)

      // Set question count to 1
      response[4] = requestData[4];
      response[5] = requestData[5];

      // Set answer cont to 1
      response[6] = 0;
      response[7] = 1;

      // Copy that question section from the requestData (domain name and type)
      int questionLength = requestData.length - 12;
      System.arraycopy(requestData, 12, response, 12, questionLength);

      int answerOffset = 12 + questionLength;

      // Answer name: pointer to the domain name (0xC00C)
      response[answerOffset] = (byte) 0xC0;
      response[answerOffset + 1] = 0x0C;

      // Answer type: A (host address) - 0x0001
      response[answerOffset + 2] = 0x00;
      response[answerOffset + 3] = 0x01;

      // Answer class: IN (internet) - 0x0001
      response[answerOffset + 4] = 0x00;
      response[answerOffset + 5] = 0x01;

      // TTL (Time To Live): 32 seconds - 0x00000020
      response[answerOffset + 6] = 0x00;
      response[answerOffset + 7] = 0x00;
      response[answerOffset + 8] = 0x00;
      response[answerOffset + 9] = 0x20;

      // Data length: 4 bytes (IPv4 address)
      response[answerOffset + 10] = 0x00;
      response[answerOffset + 11] = 0x04;

      // IP address: split the IP address into 4 bytes
      String[] ipParts = ipAddress.split("\\.");
      for (int i = 0; i < 4; i++) {
        response[answerOffset + 12 + i] = (byte) Integer.parseInt(ipParts[i]);
      }

      return Arrays.copyOf(response, answerOffset + 16);
    } catch (Exception e) {
      // TODO: handle exception

      e.printStackTrace();
      return null;
    }
  } // end of buildDNSResponse

} // end of the whole ensemble
