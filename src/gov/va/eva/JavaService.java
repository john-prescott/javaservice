package gov.va.eva;  // "http://cases.services.vetsnet.vba.va.gov/"

import java.io.IOException;
import java.util.logging.*;

import static java.lang.Thread.sleep;


/*  Comments
 */
public class JavaService {
    private static final Configuration config = new Configuration("config.txt");
    private static final Logger logger = Logger.getLogger("eVA.log");
    private JDBCService jdbc;
    private SOAPClient soap;

    public JavaService() {
        logInit();
        log("Java Service version 0.3 starts. " + config.getString("greeting"));
        this.soap = new SOAPClient(this);
        this.jdbc = new JDBCService(this);

        // Run tests
        soap.test();

        // Loop until ^C Exit
        System.out.println("Hit ^C to exit.");
        try {
            while (true) {
                sleep(config.getInt("wait")); // note to Use better Rate limit, calculation later
                jdbc.poll();
            }
        } catch (InterruptedException e) {
            ;
        }
    }

    public static void main(String[] args) {
        new JavaService();
    }

    public synchronized void receive(CaseNote note) {
        soap.sendCaseNote(note);
    }

    /* Logging is based on java lagger but may be replaced or moved - - - - - - - - - - - - - - */
    private static void logInit() {
        try {
            Handler fh = new FileHandler("eVA.log", false);  // append is true  %t/ temp %h ?
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.FINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean canLog() {
        return (config.getString("log").equalsIgnoreCase("on"));
    }

    void log(String msg) {
        if (canLog()) {
            logger.info(msg);
        }
    }
}
