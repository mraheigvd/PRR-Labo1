package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.logging.Logger;

public abstract class SimpleUDP {
    public static final Logger LOG = Logger.getLogger(SimpleUDP.class.getName());

    private int bufferSize = 1024;
    private MulticastSocket multicastSocket;
    private DatagramSocket datagramSocket;
    private InetAddress group;
    private boolean running = true;

    public SimpleUDP() {
        try {
            multicastSocket = new MulticastSocket(Protocol.SYNC_PORT);
            group = InetAddress.getByName(Protocol.SYNC_ADDRESS);
            multicastSocket.joinGroup(group);
            datagramSocket = initDatagramSocket();
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return a DatagramSocket for point to point communication
     */
    public abstract DatagramSocket initDatagramSocket();

    /**
     * start all thread to process received messages and send messages regularly (sync, delay, ...)
     */
    public abstract void start();

    /**
     * send a message on multicast
     * @param messages messages to send
     */
    protected void MCSendMsg(byte[][] messages) {

        try {
            for(byte[] msg : messages) {
                DatagramPacket packet = new DatagramPacket(msg, msg.length, group, Protocol.SYNC_PORT);
                multicastSocket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return a DatagramPacket receive on multicast
     */
    protected DatagramPacket MCReceiveMsg() {
        return receiveMsg(multicastSocket);
    }

    /**
     * send a message with DatagramSocket(point to point)
     * @param msg message to send
     * @param address address to send to
     * @param port port to use to send
     */
    protected void DGSendMsg(byte[][] messages, InetAddress address, int port) {
        try {
            for(byte[] msg : messages) {
                DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
                multicastSocket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return a DatagramPacket receive on point to point connection
     */
    protected DatagramPacket DGReceiveMsg() {
        return receiveMsg(datagramSocket);
    }

    /**
     *
     * @param packet packet to process into String
     * @return extract the data of a DatagramPacket as a String
     */
    protected static String processDatagramToString(DatagramPacket packet) {
        String temp =  new String(packet.getData());
        return temp.substring(0, temp.indexOf('\0'));
    }

    /**
     *
     * @param socket socket to use to get listen and receive a message
     * @return DatagramPacket received on the socket
     */
    private DatagramPacket receiveMsg(DatagramSocket socket) {
        byte[] msg = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(msg, msg.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // simulation of delay (ping) between master and slaves
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info(new String(packet.getData()));

        return packet;
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        running = false;
    }
}
