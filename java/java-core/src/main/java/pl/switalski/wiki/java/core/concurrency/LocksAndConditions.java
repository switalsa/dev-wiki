package pl.switalski.wiki.java.core.concurrency;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates how locks and their conditions work. Queue (backed by a linked list) has a capacity of 20 elements, for the first 50 messages it takes
 * twice as much time for consumer to process them than to the producer to deliver, therefore the queue gets full and the producer has to wait for the
 * consumer's action to complete (<code>notFull.await()</code>). The other 50 messages take twice as much time for the producer to deliver than for
 * the consumer to process, thus the queue size decreases gradually and before the end of the test consumer catches up and has to wait for the
 * producer to deliver new messages.
 * 
 * @author sensei
 */
public class LocksAndConditions {
	
	private static final int NUMBER_OF_MESSAGES = 100;
	
	private static final int QUEUE_MAX_SIZE = 20;

	private static class Queue {

		private LinkedList<Number> elements = new LinkedList<>();
		
		final Lock lock = new ReentrantLock();
		
		final Condition notFull = lock.newCondition();
		
		final Condition notEmpty = lock.newCondition();
		
		public void put(Number number) {
			lock.lock();
			pPrint("Locked for put");
			try {
				while (elements.size() == QUEUE_MAX_SIZE) {
					pPrint("Queue full, object not put, awaiting...");
					notFull.await(); // releases the lock and waits for a signal, then checks the size again
				}
				pPrint("Queue not full, adding element");
				elements.add(number);
				pPrint("Signalling not empty, queue size: " + elements.size());
				notEmpty.signal(); // signals that condition is met, but does not release the lock
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				pPrint("Put releases the lock");
				lock.unlock();
			}
		}
		
		public Number take() {
			lock.lock();
			cPrint("Locked for take");
			try {
				while (elements.size() == 0) {
					cPrint("Queue empty, object not taken, awaiting...");
					notEmpty.await(); // releases the lock and waits for a signal
				}
				cPrint("Queue not empty, taking element");
				Number number = elements.removeFirst();
				cPrint("Signalling not full, queue size: " + elements.size());
				notFull.signal(); // signals that condition is met, but does not release the lock
				return number;
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				cPrint("Take releases the lock");
				lock.unlock();
			}
		}
	}
	
	public static void main(String... args) throws Exception {
		
		final Queue queue = new Queue();

		/**
		 * Produces and deliveres
		 */
		Thread producer = new Thread() {
			
			@Override
			public void run() {
				for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
					nap(i < NUMBER_OF_MESSAGES / 2 ? 10 : 20);
					int number = i + 1;
					print("<== Produced: " + number);
					queue.put(number);
				}
			}
		};
		
		/**
		 * Consumer thread, receives messages when they are ready.
		 */
		Thread consumer = new Thread() {
			
			@Override
			public void run() {
				for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
					print("==> Consumed: " + queue.take());
					nap(i < NUMBER_OF_MESSAGES / 2 ? 20 : 10);
				}
				System.exit(0);
			}
		};
		
		consumer.start();
		producer.start();

		TimeUnit.SECONDS.sleep(10);
	}
	
	private static void nap(int milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static void print(String msg) {
		System.out.println(msg);
	}

	private static void cPrint(String msg) {
		System.out.println("- " + msg);
	}
	
	private static void pPrint(String msg) {
		System.out.println("+ " + msg);
	}
}
