def socket           = new MulticastSocket(4446)
def multicastAddress = "224.225.226.227" // between 224.0.0.1 and 239.255.255.254
def group            = InetAddress.getByName(multicastAddress)
socket.joinGroup(group)

for (;;) {
    def buf = new byte[2560]
    def packet = new DatagramPacket(buf, buf.length)
    socket.receive(packet)
    def received = new String(packet.getData())
    println("Received: " + received)
}