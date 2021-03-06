import java.util.Queue;
import java.util.LinkedList;

/**
 * Encapsulates the unique attributes for a server – id and serviceTime –
 * and the methods that are necessary to handle a customer in a shop.
 * A server can service, wait, and reject customers.
 * It relies on another class to keep track of time.
 *
 * @author Low Yew Woei
 * @version CS2030 AY17/18 Sem 2 LabTwoA
 */


class Server {
  /** class field to keep track of the number of servers created. Used for ID */
  private static int Id = 0;
  /** The maximum queue length */
  protected static int customerQueueLimit = 1;
  /** the ID of the instantiated server */
  private final int serverId;

  /** stores the reference to the object of the currently served customer */
  private Customer currentCustomer;
  /** stores the reference to the object of the currently waiting customer */
  private Queue<Customer> waitingCustomers = new LinkedList<Customer>();
  
  /**
   * Create a new Server object
   * 
   */
  public Server() {
    this.serverId = Id;
    Id++;
    this.currentCustomer = null;
  }

  public void setQueueLimit(int limit) {
    Server.customerQueueLimit = limit;
  }

  /**
   * makes the server handle the customer either serving, making the customer
   * wait, or rejecting him.
   * @param customer the customer to be handled
   * @param servedTime the time the customer is handled by the server
   * @return the customer done event, if any.
   */
  public Event handleCustomer(Customer customer, double handleTime) {
    // serve customer if server is available
    Event customerDoneEvent = null;
    if (!customerBeingServed()) {
      customerDoneEvent = this.serve(customer, handleTime);

      // return a customer done event to be processed in the future
    } 
    // make customer wait
    else if (hasSpaceToWait()) {
      this.delayCustomer(customer);

    } else {
      System.out.println("Exception happened.");
    }

    return customerDoneEvent;
   
  }

  /**
   * Checks if there is a customer being served by this server.
   *
   * @return true if a customer is being served by this server; false otherwise.
   */
  public boolean customerBeingServed() {
    return this.currentCustomer != null;
  }

  /**
   * Checks if there is a customer waiting for this server.
   *
   * @return true if a customer is waiting for this server; false otherwise.
   */
  private boolean hasSpaceToWait() {
    return this.waitingCustomers.size() < customerQueueLimit;
  }

  /**
   * Checks if there are waiting customers
   * @return  true if the waiting customer list is not empty.
   */
  private boolean hasWaitingCustomers() {
    return this.waitingCustomers.peek() != null;
  }

  /**
   * Gets a customer off the waiting customer queue.
   * @return  a waiting customer.
   */
  private Customer getWaitingCustomer() {
    return this.waitingCustomers.poll();
  }
  /**
   * serve the customer and return a CustDone event.
   * @param customer the customer being served
   * @param servedTime the time the customer is served
   * @return the customer done event
   */

  private Event serve(Customer customer, double servedTime) {
    this.currentCustomer = customer;
    // update the waiting time in the Customer object
    customer.serveBegin(servedTime, this);

    double doneTime = customer.getDoneTime();
    return Simulator.createDoneEvent(doneTime, this);
   
  }
  
  /**
   * adds the customer to the server's wait queue.
   * @param customer the customer that is to be delayed
   */

  private void delayCustomer(Customer customer) {
    this.waitingCustomers.offer(customer);
    customer.waitBegin(this);
  }


  /**
   * Used to finish a customer after he has been served.
   * will serve a waiting customer if there is any.
   * @param finishTime the time the customer is finished.
   */
  public Event finishCustomer(double finishTime) {
    Event customerDoneEvent = null;
    this.currentCustomer.serveEnd(finishTime, this);

    this.currentCustomer = null;
    if (hasWaitingCustomers()) {
      customerDoneEvent = this.serve(getWaitingCustomer(), finishTime);
    }

    return customerDoneEvent;
  }
  
  @Override
  public String toString() {
    return "S" + this.serverId;
  }


}
