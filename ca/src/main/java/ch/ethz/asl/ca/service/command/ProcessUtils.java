package ch.ethz.asl.ca.service.command;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ProcessUtils {

    private static final Logger logger = Logger.getLogger(ProcessUtils.class);

    void runBlockingProcess(final String command) throws IOException, InterruptedException {
        logger.info(String.format("Starting blocking process: %s", command));
        Process p = Runtime.getRuntime().exec(command);
        consumeProcessStream(p);
        p.waitFor();
    }

    private void consumeProcessStream(Process process) throws IOException {
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", true);
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", true);

        errorGobbler.start();
        outputGobbler.start();
    }

    private static class StreamGobbler extends Thread {

        private static final Logger logger = Logger.getLogger(StreamGobbler.class);

        private InputStream is;

        private String streamType;

        private boolean shouldLog;

        StreamGobbler(InputStream is, String streamType, boolean shouldLog) {
            this.is = is;
            this.streamType = streamType;
            this.shouldLog = shouldLog;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (shouldLog) {
                        logger.info(streamType + ">" + line);
                    }
                }

                br.close();
                isr.close();
                is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
