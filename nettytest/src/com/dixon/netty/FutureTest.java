package com.dixon.netty;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureTest {
	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		Runnable task1 = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("task1 running");
					Thread.sleep(5000);
					System.out.println("task1 done");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		Callable<Integer> task2 = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("task2 running");
				Thread.sleep(10000);
				System.out.println("task2 done");
				return 100;
			}
		};
		
		
		Future<?> future1 = executor.submit(task1);
		Future<Integer> future2 = executor.submit(task2);
		
//		int i = 0;
//		while (!future1.isDone() || !future2.isDone()) {
//			try {
//				System.out.println(i++);
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
//		try {
//			System.out.println(future1.get());
			
			System.out.println("done");
			
//			System.out.println(future1.get());
//			System.out.println(future2.get());
			
			executor.shutdown();
			
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
	}
}
