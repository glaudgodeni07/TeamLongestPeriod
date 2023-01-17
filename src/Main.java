import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    static class EmployeeProject {
        int empID;
        int projectID;
        Date dateFrom;
        Date dateTo;

        EmployeeProject(int empID, int projectID, Date dateFrom, Date dateTo) {
            this.empID = empID;
            this.projectID = projectID;
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        // Ask user for filename
        String filepath = getFilepath();

        // Read data from file
        List<EmployeeProject> data = readDataFromFile(filepath);

        // Create a dictionary with keys as employee IDs and values as lists of projects they have worked on
        Map<Integer, List<Integer>> empProjects = new HashMap<>();
        for (EmployeeProject ep : data) {
            if (!empProjects.containsKey(ep.empID)) {
                empProjects.put(ep.empID, new ArrayList<>());
            }
            empProjects.get(ep.empID).add(ep.projectID);
        }

        // Keep track of the couple of employees with the longest duration of working together on common projects
        int empIDAMax = -1;
        int empIDBMax = -1;
        long maxDuration = 0;
        for (int empIDA : empProjects.keySet()) {
            for (int empIDB : empProjects.keySet()) {
                if (empIDA >= empIDB) {
                    continue;
                }

                // Find common projects
                List<Integer> commonProjects = new ArrayList<>(empProjects.get(empIDA));
                commonProjects.retainAll(empProjects.get(empIDB));
                if (commonProjects.isEmpty()) {
                    continue;
                }

                // Calculate duration for which they worked together on common projects
                long duration = 0;
                for (int projectID : commonProjects) {
                    Date dateFromA = null;
                    Date dateToA = null;
                    Date dateFromB = null;
                    Date dateToB = null;
                    for (EmployeeProject ep : data) {
                        if (ep.empID == empIDA && ep.projectID == projectID) {
                            dateFromA = ep.dateFrom;
                            dateToA = ep.dateTo;
                        }
                        if (ep.empID == empIDB && ep.projectID == projectID) {
                            dateFromB = ep.dateFrom;
                            dateToB = ep.dateTo;
                        }
                    }
                    if (dateFromA == null || dateFromB == null) {
                        continue;
                    }
                    //compare the dates and take the intersection
                    if (dateToA == null) {
                        dateToA = new Date();
                    }
                    if (dateToB == null) {
                        dateToB = new Date();
                    }
                    Date start = dateFromA.compareTo(dateFromB) > 0 ? dateFromA : dateFromB;
                    Date end = dateToA.compareTo(dateToB) < 0 ? dateToA : dateToB;
                    if (start.compareTo(end) < 0) {
                        duration += (end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000);
                    }
                }

                // Update the result if the current couple has a longer duration
                if (duration > maxDuration) {
                    maxDuration = duration;
                    empIDAMax = empIDA;
                    empIDBMax = empIDB;
                }
            }
        }

        // Print the result
        System.out.println("Employee " + empIDAMax + " and Employee " + empIDBMax + " have worked together on common projects for the longest time: " + maxDuration + " days");
    }

    private static String getFilepath() {
        Scanner sc = new Scanner(System.in);
        String filePath;
        File file;

        do {
            System.out.print("Enter file path with the employee records: ");
            filePath = sc.nextLine();
            file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Invalid file path. Please try again.");
            }
        } while (!file.exists());

        // The file path is valid, you can now use the file object to read or manipulate the file
        System.out.println("File path is valid. Proceeding with file operations.");
        return filePath;
    }

    private static List<EmployeeProject> readDataFromFile(String fileName) throws IOException, ParseException {
        List<EmployeeProject> data = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            int empID = Integer.parseInt(parts[0].trim());
            int projectID = Integer.parseInt(parts[1].trim());
            Date dateFrom = dateFormat.parse(parts[2].trim());
            Date dateTo = null;
            if (!parts[3].trim().equalsIgnoreCase("NULL")) {
                dateTo = dateFormat.parse(parts[3].trim());
            }
            data.add(new EmployeeProject(empID, projectID, dateFrom, dateTo));
        }
        br.close();
        return data;
    }
}