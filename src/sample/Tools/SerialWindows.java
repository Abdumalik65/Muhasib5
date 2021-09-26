package sample.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SerialWindows {
    public static String getSerialNumber() {
        ProcessBuilder pb = new ProcessBuilder("wmic", "baseboard", "get", "serialnumber");
        Process process = null;
        String serialNumber = "";
        try {
            process = pb.start();
            process.waitFor();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()))) {
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    if (line.length() < 1 || line.startsWith("SerialNumber")) {
                        continue;
                    }
                    serialNumber = line;
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return serialNumber;
    }
}
