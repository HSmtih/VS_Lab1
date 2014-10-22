package general;

public interface IConstants {

    static final int MAX_QUEUE_LEN = 10;
    
    static final int MAX_RETRIES = 5;
    static final int RETRY_TIMEOUT = 3000; // time in ms
    static final int CLIENT_TIMEOUT = 6000; // time in ms
    
    static final String SERVER_NAME = "SERVER";
    static final String LOG_FILE_PATH = "Log.txt";
}
