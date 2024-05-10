import java.util.PriorityQueue;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	private enum STATE {THINKING, HUNGRY, EATING}; // Define philosopher states
	private int numPhilosophers; // Number of philosophers
	private STATE[] states; // Array to track each philosopher's state
	private boolean is_talking; // Flag to check if a philosopher is talking

	private PriorityQueue<Integer> hungryList; // Priority queue to manage hungry philosophers


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		numPhilosophers = piNumberOfPhilosophers;
		states = new STATE[numPhilosophers];

		// Initialize all philosophers as THINKING
		for(int i = 0; i < numPhilosophers; i++) {
			states[i] = STATE.THINKING;
		}
		hungryList = new PriorityQueue<>();
		is_talking = false;
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */
	// Test if a philosopher can start eating
	public synchronized void test(int position){
		try {
			while(true){
				// Check if neighboring philosophers are not eating
				if(states[(position + 1) % numPhilosophers] != STATE.EATING && states[(position + (numPhilosophers - 1)) % numPhilosophers] != STATE.EATING
						&& states[position] == STATE.HUNGRY){
					states[position] = STATE.EATING;
					break;
				}
				else {
					wait(); // Wait if unable to eat
				}
			}
		}

		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		int position = piTID - 1;
		states[position] = STATE.HUNGRY;
		hungryList.add(piTID);
		test(position); // Test if the philosopher can eat
		hungryList.remove(); // Remove philosopher from the hungry list after eating
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		int position = piTID - 1;
		states[position] = STATE.THINKING;
		notifyAll(); // Notify other philosophers waiting to pick up chopsticks
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk()
	{
		if(is_talking) {
			try {
				wait();
				requestTalk();
			}

			catch(InterruptedException e) {
				System.out.println("A philosopher is currently speaking something very useful. Please wait to philosophy");
			}
			is_talking = true;
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk()
	{
		is_talking = false;
		notifyAll(); // Notify other philosophers waiting to talk
	}
}

// EOF