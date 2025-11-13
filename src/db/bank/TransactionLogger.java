package db.bank;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionLogger {

    private static final String FILE_NAME = "transactions.txt";

    public static void log(String message) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = LocalDateTime.now().format(dtf);
            writer.write("[" + time + "] " + message + "\n");
        } catch (IOException e) {
            System.out.println("⚠️ Error writing to transaction log file!");
            e.printStackTrace();
        }
    }
}
