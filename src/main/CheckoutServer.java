package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class represents a server than handles reservation requests.
 */
public class CheckoutServer {
    private int port;
    private int numThreads;
    private String resourceFileName;
    private List<String> resourceList;
    private Thread[] threads;
    private boolean exitSignaled = false;

    /*
     * Delay in ms for a thread when it finds an empty job queue.
     *
     * Note: it is not ideal to include this type of delay if you hope to
     * minimize client wait time. However, for the sake of this project, this
     * should save your machine some battery life. Feel free to modify it if
     * your machine is struggling while running the server. Values should be
     * non-negative.
     */
    private static final int SPIN_DELAY = 1;

    /*
     * TODO:
     * STUDENT INSTRUCTION:
     * Add your queue structure and any other fields you find necessary or relevant here.
     * Regardless of the type of queue you maintain, it should hold elements of type
     * RequestItem.
     */
    PriorityQueue<RequestItem> requestItemPriorityQueue = new PriorityQueue<>();


    /**
     * Constructor for the server. This sets the port and number of threads for the
     * server.
     * @param port The port number on which the server will listen.
     * @param numThreads The number of worker threads the server will maintain.
     */
    public CheckoutServer(int port, int numThreads, String resourceFileName) {
        this.port = port;
        this.numThreads = numThreads;
        this.resourceFileName = resourceFileName;
    }


    /**
     * TODO:
     * STUDENT INSTRUCTION:
     * Complete this method to add elements to the job queue in a thread-safe manner.
     * The boss thread adds elements to this queue for the workers to pick up.
     *
     * @param item The RequestItem to be added to the queue.
     * @return Returns true if item was successfully added, false otherwise.
     */
    private synchronized boolean addToQueue(RequestItem item) {
        return requestItemPriorityQueue.add(item);
    }

    /**
     * TODO:
     * STUDENT INSTRUCTION:
     * Complete this method to remove elements from the job queue in a thread-safe manner.
     * The boss thread adds elements to this queue for the workers to pick up.
     *
     * @return Returns a RequestItem from the queue or null if the queue is empty.
     */
    private synchronized RequestItem pollFromQueue() {

        return requestItemPriorityQueue.poll();
    }

    /**
     * TODO:
     * STUDENT INSTRUCTION:
     * Complete this method to remove a resource from the resource list. If the resource is available,
     * this method claims it and removes it from the list. If the resource is unavailable, this method
     * returns false.
     *
     * @param resource The requested resource in the form of a string.
     * @return True if resource was successfully claimed, false otherwise.
     */
    private synchronized boolean getResourceFromList(String resource) {

        return false;
    }

    /**
     * Initializes the worker threads. Each worker
     * @param numThreads The number of worker threads to create.
     */
    private void initThreads(int numThreads) {
        threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            Runnable task = () -> {
                RequestItem request;
                String response;

                // loop forever
                while (!exitSignaled) {
                    request = null;
                    response = null;

                    // get a job from the queue
                    request = pollFromQueue();
                    Util.log(3, "Got this request: " + request);

                    // check if queue was empty
                    if (request == null){
                        Util.log(4, "Request queue empty.");
                        try {
                            Thread.sleep(SPIN_DELAY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    // request was found, so process it
                    Util.log(1, Thread.currentThread().getName() + " is processing this request: " + request);

                    /*
                     * TODO:
                     * STUDENT INSTRUCTION:
                     * Check the request against the resource list. Respond to the client with either
                     *      "Selection confirmed. [RESOURCE] reserved."
                     * or
                     *      "Selection [RESOURCE] unavailable."
                     *
                     * Note: you should use the synchronized method getResourceFromList to ensure thread safety.
                     */

                    // check request against resource list, if resource available, claim it, else tell client not available



                    // send receipt



                    // END STUDENT WORK
                }
            };

            Thread thread = new Thread(task);
            thread.start();
            threads[i] = thread;
        }
    }

    /**
     * Waits for the worker threads to finish their jobs and joins them.
     */
    public void cleanThreads() {
        // tell threads to stop
        exitSignaled = true;
        // wait for threads to finish before exiting
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
                Util.log(3, "Thread " + i + " finished.");
            } catch (InterruptedException e) {
                Util.logError(0, e);
            }
        }
    }

    /**
     * Starts the server by initializing a server socket on this.port.
     * Listens for client requests and creates a socket for each client that
     * successfully connects.
     *
     * The server accepts messages from the client in the form of RequestItems,
     * and adds those request items to the job queue for the workers to pick up
     * and process.
     */
    public void start() {
        // handle SIGINT: add shutdown hook to clean up resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Util.log(0, "Shutting down server.");
            cleanThreads();
        }));

        ServerSocket server = null;
        Socket socket = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        RequestItem request = null;
        Context ctx = null;

        // generate resource list
        try {
            resourceList = Collections.synchronizedList(Files.readAllLines(Paths.get("resources", resourceFileName)));
            Util.log(3, "Resource list generated.");
        } catch (IOException e) {
            Util.logError(2, e);
        }

        // create and start threads
        initThreads(numThreads);

        // create server
        try {
            server = new ServerSocket(port);
            Util.log(1, "Server listening on port " + port + ".");
        } catch (Exception e) {
            Util.logError(2, e);
        }

        // loop forever
        while (true) {
            // listen for client connection
            try {
                socket = server.accept();
                Util.log(3, "A client connected.");
            } catch (Exception e) {
                Util.logError(2, e);
            }

            // wait for message from client
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                String message = (String)input.readObject();
                Util.log(4, "Received a request from the client.");


                /*
                 * TODO:
                 * STUDENT INSTRUCTION:
                 * Verify the format of the request message. If the message is malformed,
                 * don't add it to the job queue, but immediately respond to the client with the
                 * response "INVALID". Any request not of the form "[RESOURCE],[PRIORITY]" is
                 * considered malformed.
                 */

                // if message is malformed, don't add to queue



                // END STUDENT WORK



                // message is correctly formed, so generate RequestItem from message
                ctx = new Context(socket, output);
                request = new RequestItem(ctx, message.split(",")[0], Integer.parseInt(message.split(",")[1]));

                // add message to queue
                addToQueue(request);

                Util.log(3, "Added " + request + " to request queue.");
            } catch (Exception e) {
                Util.logError(2, e);
            }

        }
    }

}
