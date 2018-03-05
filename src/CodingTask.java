import java.io.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by Raphi on 02.03.2018.
 */
public class CodingTask {


    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final String INPUT_PATH = "C:\\Users\\Raphi\\Desktop\\Coding\\CodingTask\\sample1m.csv";
        final String LOG_PATH = "C:\\Users\\Raphi\\Desktop\\Coding\\CodingTask\\log.csv";
        final String ENCODING = "UTF-8";

        File inputFile = new File(INPUT_PATH);
        File logFile = new File(LOG_PATH);
        int bufferReader = 1000 * 8192;
        int bufferWriter = 1000 * 8192;

        //is true if user wants to continue the sending of mails, false if user wants to restart
        boolean cont = false;


        if(inputFile.isFile()){
            if(logFile.isFile()){
                if(checkAllLines()){
                    //is true when the user wants to proceed with the mail delivery and false if he wants to restart.
                    boolean correctInput = false;

                    //loops until user input is correct
                    do {
                        try {
                            System.out.println("It seems that there is already a log file, do you want to keep the progress and continue?");
                            Scanner in = new Scanner(System.in);
                            cont = in.nextBoolean();
                            correctInput = true;
                        }
                        catch(InputMismatchException ex){
                            System.out.println("Invalid Input. Please write \033[1mtrue\033[0m to continue or \033[1mfalse\033[0m to restart");
                        }
                    }while(!correctInput);

                    //runs based on user Input either continueMailDelivery or startMailDelivery and catches possible IOExceptions.
                    if(cont){
                        try{
                            continueMailDelivery(bufferReader, bufferWriter, INPUT_PATH, LOG_PATH, ENCODING);
                        }
                        catch(IOException ex){
                            System.out.println("IOException during continuation of mail delivery.");
                        }
                    }
                    else{
                        try{
                            startMailDelivery(bufferReader, bufferWriter, INPUT_PATH, LOG_PATH, ENCODING);
                        }
                        catch(IOException ex){
                            System.out.println("IOException during restart of mail delivery.");
                        }
                    }

                }
                else{
                    try{
                        startMailDelivery(bufferReader, bufferWriter, INPUT_PATH, LOG_PATH, ENCODING);
                    }
                    catch(IOException ex){
                        System.out.println("IOException during start of mail delivery with new input.");
                    }
                }
            }
            else{
                try{
                    startMailDelivery(bufferReader, bufferWriter, INPUT_PATH, LOG_PATH, ENCODING);
                }
                catch(IOException ex){
                    System.out.println("IOException during start of mail delivery.");
                }
            }
        }
        else{
            System.out.println("There is no such file under " + INPUT_PATH + ". Please make sure that you have entered the right path.");
        }

        final long stopTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (stopTime-startTime) + "ms");
    }

    //continues the mail delivery by skipping the number of lines, that exist in the log file, in the input file
    //bufferReader represents the buffer size of the file reader used. default: 8192
    //bufferWriter represents the buffer size of the file writer used. default: 8192
    public static void continueMailDelivery(int bufferReader, int bufferWriter, String inputPath, String logPath, String encoding) throws FileNotFoundException,IOException{
        BufferedReader brInput = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), encoding), bufferReader);
        BufferedReader brLog = new BufferedReader(new InputStreamReader(new FileInputStream(logPath), encoding), bufferReader);

        BufferedWriter writer = new BufferedWriter(new PrintWriter(new FileOutputStream(logPath, true),true),bufferWriter);

        //number of lines in the log file without last empty line
        long numOfLogLines = brLog.lines().count()-1;
        //if there is just an empty log file
        if(numOfLogLines < 0){
            numOfLogLines = 0;
        }

        brInput.lines().skip(numOfLogLines).forEach((String n) -> {
            sendmail(n.replace("\"","").split(";"), writer);
        });

        writer.close();
    }

    //starts a mail delivery by working through the input file and logging each line in the log file
    //bufferReader represents the buffer size of the file reader used. default: 8192
    //bufferWriter represents the buffer size of the file writer used. default: 8192
    public static void startMailDelivery(int bufferReader, int bufferWriter, String inputPath, String logPath, String encoding) throws FileNotFoundException,IOException{
        BufferedReader brInput = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), encoding), bufferReader);

        BufferedWriter writer = new BufferedWriter(new PrintWriter(new FileOutputStream(logPath, false),true),bufferWriter);

        brInput.lines().forEach((String n) -> {
            sendmail(n.replace("\"","").split(";"), writer);
        });

        writer.close();
    }

    /*
    //mocks sending a mail by waiting half a second and also logs the mail.
    public static void sendmail(String[] text, BufferedWriter writer){
        String mailAddress = text[0];
        String firstName = text[1];
        String lastName = text[2];

        try {
            TimeUnit.MILLISECONDS.sleep(500);
            logMail(true, writer, text);
        }
        catch (InterruptedException ex){
            System.out.println("Interrupted during mail delivery (sleep)." + text[0] + " " + text[1] + " " + text[2]);
            logMail(false, writer, text);
        }
    }
    */

    //mocks sending a mail without waiting also logs the mail.
    public static void sendmail(String[] text, BufferedWriter writer){
        String mailAddress = text[0];
        String firstName = text[1];
        String lastName = text[2];

        logMail(true, writer, text);
    }

    // logs if the mail was successfully sent and the senders first and last name using the specified writer
    public static void logMail(boolean sent, BufferedWriter writer, String[] text) {

        try{
            if(sent){
                writer.write("\u2713;" + text[1] + ";" + text[2] + "\n");
            }
            else{
                writer.write("\n\u2715;" + text[1] + ";" + text[2] + "\n");
            }
            writer.flush();
        }
        catch (IOException ex){
            System.out.println("IOException during logging of mails." + text[0] + " " + text[1] + " " + text[2]);
        }
    }

    //compares all lines in log file to lines in input file and returns true if they are the same.
    public static boolean checkAllLines(){
        return true;
    }

    //compares the first line of the log file to the first line of the input file and returns true if they are the same.
    public static boolean checkFirstLines(){
        return true;
    }
}
