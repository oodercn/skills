
package net.ooder.bpm.engine.util;

public class UtilTimer {

    long realStartTime;
    long startTime;
    long lastMessageTime;
    String lastMessage = null;

    /** 
     * Default constructor. Starts the timer.
     */
    public UtilTimer() {
        lastMessageTime = realStartTime = startTime = System.currentTimeMillis();
        lastMessage = "Begin";
    }

    /** 
     * Creates a string with information including the passed message, 
     * the last passed message and the time since the last call, and 
     * the time since the beginning
     * 
     * @param message A message to put into the timer String
     * @return A String with the timing information, the timer String
     */
    public String timerString(String message) {
        return timerString(message, this.getClass().getName());
    }

    /** 
     * Creates a string with information including the passed message, 
     * the last passed message and the time since the last call, and 
     * the time since the beginning
     * 
     * @param message A message to put into the timer String
     * @param module The debug/log module/thread to use, can be null for root module
     * @return A String with the timing information, the timer String
     */
    public String timerString(String message, String module) {
        // time this call to avoid it interfering with the main timer
        long tsStart = System.currentTimeMillis();

        String retString =
            "[["
                + message
                + "- total:"
                + secondsSinceStart()
                + ",since last("
                + ((lastMessage.length() > 20) ? (lastMessage.substring(0, 17) + "...") : lastMessage)
                + "):"
                + secondsSinceLast()
                + "]]";

        lastMessage = message;

        // have lastMessageTime come as late as possible to just time what happens between calls
        lastMessageTime = System.currentTimeMillis();
        // update startTime to disclude the time this call took
        startTime += (lastMessageTime - tsStart);

        return retString;
    }

    /** 
     * Returns the number of seconds since the timer started
     * 
     * @return The number of seconds since the timer started
     */
    public double secondsSinceStart() {
        return ((double)timeSinceStart()) / 1000.0;
    }

    /** 
     * Returns the number of seconds since the last time timerString was called
     * 
     * @return The number of seconds since the last time timerString was called
     */
    public double secondsSinceLast() {
        return ((double)timeSinceLast()) / 1000.0;
    }

    /** 
     * Returns the number of milliseconds since the timer started
     * 
     * @return The number of milliseconds since the timer started
     */
    public long timeSinceStart() {
        long currentTime = System.currentTimeMillis();

        return currentTime - startTime;
    }

    /** 
     * Returns the number of milliseconds since the last time timerString was called
     * 
     * @return The number of milliseconds since the last time timerString was called
     */
    public long timeSinceLast() {
        long currentTime = System.currentTimeMillis();

        return currentTime - lastMessageTime;
    }

    /** 
     * Creates a string with information including the passed message, 
     * the time since the last call, and the time since the beginning.  
     * This version allows an integer level to be specified to improve 
     * readability of the output.
     * 
     * @param level Integer specifying how many levels to indent the 
     * timer string so the output can be more easily read through 
     * nested method calls.
     * @param message A message to put into the timer String
     * @return A String with the timing information, the timer String
     */
    public String timerString(int level, String message) {
        // String retString =  "[[" + message + ": seconds since start: " + secondsSinceStart() + ",since last(" + lastMessage + "):" + secondsSinceLast() + "]]";

        StringBuffer retStringBuf = new StringBuffer();

        for (int i = 0; i < level; i++) {
            retStringBuf.append("| ");
        }
        retStringBuf.append("(");

        String timeSinceStartStr = String.valueOf(timeSinceStart());

        // int spacecount = 5 - timeSinceStartStr.length();
        // for (int i=0; i < spacecount; i++) { retStringBuf.append(' '); }
        retStringBuf.append(timeSinceStartStr + ",");

        String timeSinceLastStr = String.valueOf(timeSinceLast());

        // spacecount = 4 - timeSinceLastStr.length();
        // for (int i=0; i < spacecount; i++) { retStringBuf.append(' '); }
        retStringBuf.append(timeSinceLastStr);

        retStringBuf.append(")");
        int spacecount = 12 + (2 * level) - retStringBuf.length();

        for (int i = 0; i < spacecount; i++) {
            retStringBuf.append(' ');
        }
        retStringBuf.append(message);

        // lastMessageTime = (new Date()).getTime();
        lastMessageTime = System.currentTimeMillis();
        // lastMessage = message;

        String retString = retStringBuf.toString();

        return retString;
    }

}

