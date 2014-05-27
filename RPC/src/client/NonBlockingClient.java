package client;

import java.io.IOException;

import tutorial.*;
import shared.*;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

public class NonBlockingClient {
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("Please enter 'simple' or 'secure'");
			System.exit(0);
		}

		try {
			TNonblockingSocket transport = null;
			if (args[0].contains("simple")) {
				transport = new TNonblockingSocket("localhost", 8001);
//				transport = new TNonblockingSocket("localhost", 8001);
				
//				transport.open();
//				transport.startConnect();
			}
		else {
				/**
				 * Similar to the server, you can use the parameters to setup
				 * client parameters or use the default settings. On the client
				 * side, you will need a TrustStore which contains the trusted
				 * certificate along with the public key. For this example it's
				 * a self-signed cert.
				 */
				 
				TSSLTransportParameters params = new TSSLTransportParameters();
				params.setTrustStore("../../lib/java/test/.truststore",
						"thrift", "SunX509", "JKS");
				
				/**
				 * Get a client transport instead of a server transport. The
				 * connection is opened on invocation of the factory method, no
				 * need to specifically call open()
				 */
//				transport = TSSLTransportFactory.getClientSocket("localhost",
//						9091, 0, params);
			}

			TBinaryProtocol.Factory factory = new TBinaryProtocol.Factory(true, true);
			TAsyncClientManager clientManager = new TAsyncClientManager();
//			TProtocol protocol = new TBinaryProtocol(transport);
//			Calculator.Client client = new Calculator.Client(protocol);
			Calculator.AsyncClient asyncClient = new Calculator.AsyncClient(factory , clientManager, transport);

//			perform(client);
			testSyn(asyncClient);

			transport.close();
		} catch (TException | IOException x/* | IOException x*/) {
			x.printStackTrace();
		}
	}

	private static void perform(Calculator.Client client) throws TException {
		client.ping();
		System.out.println("ping()");

		int sum = client.add(1, 1);
		System.out.println("1+1=" + sum);

		Work work = new Work();

		work.op = Operation.DIVIDE;
		work.num1 = 1;
		work.num2 = 0;
		try {
			int quotient = client.calculate(1, work);
			System.out.println("Whoa we can divide by 0");
		} catch (InvalidOperation io) {
			System.out.println("Invalid operation: " + io.why);
		}

		work.op = Operation.SUBTRACT;
		work.num1 = 15;
		work.num2 = 10;
		try {
			int diff = client.calculate(1, work);
			System.out.println("15-10=" + diff);
		} catch (InvalidOperation io) {
			System.out.println("Invalid operation: " + io.why);
		}

		SharedStruct log = client.getStruct(1);
		System.out.println("Check log: " + log.value);
	}
	
	private static void testSyn(Calculator.AsyncClient client) throws TException {
		
		int a = 1;
		int b = 1;
		client.add(a, b, new MyCallback(a, b));
		
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		client.add(++a, b, new MyCallback(a, b));
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		int a = 2;
//		int b = 1;
//		int sum = client.add(a, b);
//		System.out.println(a + "+" + b + "=" + sum);
//		int sum2 = client.add(++a, b);
//		System.out.println(a + "+" + b + "=" + sum2);
	}
	
}

class MyCallback implements AsyncMethodCallback{
	private int a;
	private int b;
	public MyCallback(int a, int b){
		this.a = a;
		this.b = b;
	}
	
	public void onComplete(Object response) {
		Calculator.AsyncClient.add_call add_call = (Calculator.AsyncClient.add_call) response;
		try {
			System.out.println(a + "+" + b +"=" + add_call.getResult());
		} catch (TException e) {
			e.printStackTrace();
		}
	};
	@Override
	public void onError(Exception exception) {
		exception.printStackTrace();
	}
}