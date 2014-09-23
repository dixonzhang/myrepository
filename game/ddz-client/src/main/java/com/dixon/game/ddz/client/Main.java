package com.dixon.game.ddz.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class Main {
	public Session session;  
    
    protected void start()  
             {  
   
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();  
   
            String uri = "ws://localhost:8888/ddz-server/websocket/ddz";  
            System.out.println("Connecting to " + uri);  
            try {  
                session = container.connectToServer(Client.class, URI.create(uri));  
            } catch (DeploymentException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }               
   
    }  
    public static void main(String args[]){  
    	Main client = new Main();  
        client.start();  
   
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
        String input = "";  
        try {  
            do{  
                input = br.readLine();  
                System.out.println(input);
                if(!input.equals("exit"))  
                    client.session.getBasicRemote().sendText(input);
                if(input.equals("pong")){
                	byte b = 1;
                	client.session.getBasicRemote().sendPong(ByteBuffer.allocate(1).put(b));
                }
   
            }while(!input.equals("exit"));  
   
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }
}
