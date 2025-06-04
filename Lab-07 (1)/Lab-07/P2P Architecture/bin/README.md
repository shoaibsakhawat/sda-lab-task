# P2P Chat Application

This project demonstrates a simple Peer-to-Peer (P2P) chat system in Java. Each peer can send and receive messages until "exit" is typed.

## Files
- `Peer1.java`: Listens on port 5000, sends to port 6000
- `Peer2.java`: Listens on port 6000, sends to port 5000

## Requirements
- Java JDK 8 or above

## How to Run
1. Open two terminal windows.
2. In the first terminal, compile and run Peer1:
   ```
   javac Peer1.java
   java Peer1
   ```
3. In the second terminal, compile and run Peer2:
   ```
   javac Peer2.java
   java Peer2
   ```
4. Type messages in either terminal. Type `exit` to end the chat.

## Notes
- Both peers must be running for chat to work.
- You can change the IP and port in the code to chat across different machines on the same network.
- Make sure to allow the ports (5000, 6000) through your firewall if running on different machines. 