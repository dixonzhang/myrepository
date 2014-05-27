package server;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TTransportFactory;







// Generated code
import tutorial.*;
import shared.*;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class NonBlockingServer {

	public static CalculatorHandler handler;

	public static Calculator.Processor processor;

	public static void main(String[] args) {
		try {
			handler = new CalculatorHandler();
			processor = new Calculator.Processor(handler);

			Runnable simple = new Runnable() {
				public void run() {
					simple(processor);
				}
			};
			/*Runnable secure = new Runnable() {
				public void run() {
					secure(processor);
				}
			};*/

			new Thread(simple).start();
//			new Thread(secure).start();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public static void simple(Calculator.Processor processor) {
		try {
//			TServerTransport serverTransport = new TServerSocket(/*9090*/new InetSocketAddress("127.0.0.1", 8001));
			//单线程阻塞: 多个客户端访问同一方法时会阻塞，请求一个个执行
//			TServer server = new TSimpleServer(
//					new Args(serverTransport).processor(processor));
			
			//多线程阻塞：多个客户端访问同一方法并行执行，但是同一个客户端请求阻塞
//			org.apache.thrift.server.TThreadPoolServer.Args args = new org.apache.thrift.server.TThreadPoolServer.Args(serverTransport);
//			TServer server = new TThreadPoolServer(args.processor(processor));

			//多线程不阻塞：多个客户端访问同一方法并行执行，同一个客户端请求不阻塞
			TBinaryProtocol.Factory protoFactory = new TBinaryProtocol.Factory(true, true);
			TNonblockingServerTransport nbTransport = new TNonblockingServerSocket(8001);
			
			org.apache.thrift.server.TNonblockingServer.Args args = new org.apache.thrift.server.TNonblockingServer.Args(nbTransport);
			TServer server = new TNonblockingServer(args.processor(processor).protocolFactory(protoFactory));
			
			
			// Use this for a multithreaded server
			// TServer server = new TThreadPoolServer(new
			// TThreadPoolServer.Args(serverTransport).processor(processor));

			System.out.println("Starting the simple server...");
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void secure(Calculator.Processor processor) {
		try {
			/*
			 * Use TSSLTransportParameters to setup the required SSL parameters.
			 * In this example we are setting the keystore and the keystore
			 * password. Other things like algorithms, cipher suites, client
			 * auth etc can be set.
			 */
			TSSLTransportParameters params = new TSSLTransportParameters();
			// The Keystore contains the private key
			params.setKeyStore("C:/Users/Administrator/workspace/RPC/src/.keystore", "thrift", null,
					null);

			/*
			 * Use any of the TSSLTransportFactory to get a server transport
			 * with the appropriate SSL configuration. You can use the default
			 * settings if properties are set in the command line. Ex:
			 * -Djavax.net.ssl.keyStore=.keystore and
			 * -Djavax.net.ssl.keyStorePassword=thrift
			 * 
			 * Note: You need not explicitly call open(). The underlying server
			 * socket is bound on return from the factory class.
			 */
			TServerTransport serverTransport = TSSLTransportFactory
					.getServerSocket(9091, 0, null, params);
			
			TServer server = new TSimpleServer(
					new Args(serverTransport).processor(processor));

			// Use this for a multi threaded server
			// TServer server = new TThreadPoolServer(new
			// TThreadPoolServer.Args(serverTransport).processor(processor));

			System.out.println("Starting the secure server...");
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}