import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

class Student implements Serializable {
    private String rollNo;
    private String name;
    private String department;
    private Map<Integer, List<Subject>> semesterSubjects;

    public Student(String rollNo, String name, String department) {
        this.rollNo = rollNo;
        this.name = name;
        this.department = department;
        this.semesterSubjects = new HashMap<>();
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Map<Integer, List<Subject>> getSemesterSubjects() {
        return semesterSubjects;
    }

    public void addSemesterSubjects(int semester, List<Subject> subjects) {
        semesterSubjects.put(semester, subjects);
    }
}

class Subject implements Serializable {
    private String name;
    private double marks;
    private double maxMarks;
    private double passingPercentage;
    private double percentage;
    private String grade;
    private String status;

    public Subject(String name, double marks, double maxMarks, double passingPercentage) {
        this.name = name;
        this.marks = marks;
        this.maxMarks = maxMarks;
        this.passingPercentage = passingPercentage;
        calculateResults();
    }

    private void calculateResults() {
        this.percentage = (marks / maxMarks) * 100;
        this.grade = calculateGrade();
        this.status = percentage >= passingPercentage ? "PASS" : "FAIL";
    }

    private String calculateGrade() {
        if (percentage >= 90)
            return "A+";
        else if (percentage >= 80)
            return "A";
        else if (percentage >= 70)
            return "B";
        else if (percentage >= 60)
            return "C";
        else if (percentage >= 50)
            return "D";
        else
            return "F";
    }

    public String getName() {
        return name;
    }

    public double getMarks() {
        return marks;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getGrade() {
        return grade;
    }

    public String getStatus() {
        return status;
    }
}

public class StudentGradeManagementSystem extends JFrame {
    private Map<String, Student> students;
    private JTabbedPane tabbedPane;
    private DefaultTableModel studentTableModel;
    private DefaultTableModel gradesTableModel;
    private JTable studentTable;
    private JTable gradesTable;
    private JComboBox<String> studentSelector;
    private JComboBox<Integer> semesterSelector;
    private JComboBox<String> reportStudentSelector; // Declare this at the class level

    public StudentGradeManagementSystem() {
        students = new HashMap<>();
        initializeGUI();
        loadData();
    }

    private void initializeGUI() {
        setTitle("Student Grade Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Student Management", createStudentPanel());
        tabbedPane.addTab("Grade Management", createGradePanel());
        tabbedPane.addTab("Reports", createReportPanel());

        add(tabbedPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "Roll No", "Name", "Department" };
        studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField rollNoField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField deptField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Roll No:"), gbc);
        gbc.gridx = 1;
        formPanel.add(rollNoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        formPanel.add(deptField, gbc);

        JButton addButton = new JButton("Add Student");
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String rollNo = rollNoField.getText().trim();
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();

            if (rollNo.isEmpty() || name.isEmpty() || dept.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            if (students.containsKey(rollNo)) {
                JOptionPane.showMessageDialog(this, "Student with this Roll No already exists!");
                return;
            }

            Student student = new Student(rollNo, name, dept);
            students.put(rollNo, student);
            updateStudentTable();
            updateStudentSelector();

            rollNoField.setText("");
            nameField.setText("");
            deptField.setText("");
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createGradePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel selectionPanel = new JPanel(new FlowLayout());

        studentSelector = new JComboBox<>();
        semesterSelector = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8 });

        selectionPanel.add(new JLabel("Student:"));
        selectionPanel.add(studentSelector);
        selectionPanel.add(new JLabel("Semester:"));
        selectionPanel.add(semesterSelector);

        String[] columns = { "Subject", "Marks", "Max Marks", "Passing %", "Percentage", "Grade", "Status" };
        gradesTableModel = new DefaultTableModel(columns, 0);
        gradesTable = new JTable(gradesTableModel);
        JScrollPane scrollPane = new JScrollPane(gradesTable);

        JPanel addSubjectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField subjectField = new JTextField(15);
        JTextField marksField = new JTextField(15);
        JTextField maxMarksField = new JTextField(15);
        JTextField passingField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        addSubjectPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        addSubjectPanel.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addSubjectPanel.add(new JLabel("Marks:"), gbc);
        gbc.gridx = 1;
        addSubjectPanel.add(marksField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addSubjectPanel.add(new JLabel("Max Marks:"), gbc);
        gbc.gridx = 1;
        addSubjectPanel.add(maxMarksField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addSubjectPanel.add(new JLabel("Passing %:"), gbc);
        gbc.gridx = 1;
        addSubjectPanel.add(passingField, gbc);

        JButton addButton = new JButton("Add Subject");
        gbc.gridx = 1;
        gbc.gridy = 4;
        addSubjectPanel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String selectedRollNo = (String) studentSelector.getSelectedItem();
            int selectedSemester = (Integer) semesterSelector.getSelectedItem();

            if (selectedRollNo == null) {
                JOptionPane.showMessageDialog(this, "Please select a student!");
                return;
            }

            try {
                String subject = subjectField.getText().trim();
                double marks = Double.parseDouble(marksField.getText().trim());
                double maxMarks = Double.parseDouble(maxMarksField.getText().trim());
                double passing = Double.parseDouble(passingField.getText().trim());

                if (subject.isEmpty()) {
                    throw new IllegalArgumentException("Subject name cannot be empty");
                }

                Subject newSubject = new Subject(subject, marks, maxMarks, passing);
                Student student = students.get(selectedRollNo);

                List<Subject> semesterSubjects = student.getSemesterSubjects()
                        .getOrDefault(selectedSemester, new ArrayList<>());
                semesterSubjects.add(newSubject);
                student.addSemesterSubjects(selectedSemester, semesterSubjects);

                updateGradesTable(selectedRollNo, selectedSemester);

                subjectField.setText("");
                marksField.setText("");
                maxMarksField.setText("");
                passingField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        studentSelector.addActionListener(e -> {
            String selectedRollNo = (String) studentSelector.getSelectedItem();
            int selectedSemester = (Integer) semesterSelector.getSelectedItem();
            if (selectedRollNo != null) {
                updateGradesTable(selectedRollNo, selectedSemester);
            }
        });

        semesterSelector.addActionListener(e -> {
            String selectedRollNo = (String) studentSelector.getSelectedItem();
            int selectedSemester = (Integer) semesterSelector.getSelectedItem();
            if (selectedRollNo != null) {
                updateGradesTable(selectedRollNo, selectedSemester);
            }
        });

        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addSubjectPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        JPanel controlPanel = new JPanel(new FlowLayout());
        reportStudentSelector = new JComboBox<>(); // Initialize here
        JButton generateButton = new JButton("Generate Report");

        controlPanel.add(new JLabel("Student:"));
        controlPanel.add(reportStudentSelector);
        controlPanel.add(generateButton);

        generateButton.addActionListener(e -> {
            String selectedRollNo = (String) reportStudentSelector.getSelectedItem();
            if (selectedRollNo != null) {
                Student student = students.get(selectedRollNo);
                StringBuilder report = new StringBuilder();

                report.append("Student Report\n");
                report.append("=============\n\n");
                report.append(String.format("Roll No: %s\n", student.getRollNo()));
                report.append(String.format("Name: %s\n", student.getName()));
                report.append(String.format("Department: %s\n\n", student.getDepartment()));

                Map<Integer, List<Subject>> semesterSubjects = student.getSemesterSubjects();
                for (Map.Entry<Integer, List<Subject>> entry : semesterSubjects.entrySet()) {
                    report.append(String.format("Semester %d\n", entry.getKey()));
                    report.append("----------\n");

                    List<Subject> subjects = entry.getValue();
                    double semesterTotal = 0;
                    double semesterMaxTotal = 0;

                    for (Subject subject : subjects) {
                        report.append(String.format("%-20s: %6.2f/%-6.2f (%6.2f%%) - %s - %s\n",
                                subject.getName(),
                                subject.getMarks(),
                                subject.getMaxMarks(),
                                subject.getPercentage(),
                                subject.getGrade(),
                                subject.getStatus()));

                        semesterTotal += subject.getMarks();
                        semesterMaxTotal += subject.getMaxMarks();
                    }

                    double semesterPercentage = (semesterTotal / semesterMaxTotal) * 100;
                    report.append(String.format("\nSemester Total: %.2f/%.2f (%.2f%%)\n\n",
                            semesterTotal, semesterMaxTotal, semesterPercentage));
                }

                reportArea.setText(report.toString());
            }
        });

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void updateStudentTable() {
        studentTableModel.setRowCount(0);
        for (Student student : students.values()) {
            studentTableModel.addRow(new Object[] {
                    student.getRollNo(),
                    student.getName(),
                    student.getDepartment()
            });
        }
    }

    private void updateStudentSelector() {
        studentSelector.removeAllItems();
        for (String rollNo : students.keySet()) {
            studentSelector.addItem(rollNo);
        }
    }

    private void updateGradesTable(String rollNo, int semester) {
        gradesTableModel.setRowCount(0);
        Student student = students.get(rollNo);
        if (student != null) {
            List<Subject> subjects = student.getSemesterSubjects()
                    .getOrDefault(semester, new ArrayList<>());

            for (Subject subject : subjects) {
                gradesTableModel.addRow(new Object[] {
                        subject.getName(),
                        subject.getMarks(),
                        subject.getMaxMarks(),
                        subject.getPercentage(),
                        String.format("%.2f%%", subject.getPercentage()),
                        subject.getGrade(),
                        subject.getStatus()
                });
            }
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("student_data.ser"))) {
            oos.writeObject(students);
            JOptionPane.showMessageDialog(this,
                    "Data saved successfully!",
                    "Save Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving data: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("student_data.ser"))) {
            students = (Map<String, Student>) ois.readObject();
            updateStudentTable();
            updateStudentSelector();
            updateReportStudentSelector(); // Add this line to update the report student selector
        } catch (FileNotFoundException e) {
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading data: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReportStudentSelector() {
        reportStudentSelector.removeAllItems(); // Clear existing items
        for (String rollNo : students.keySet()) {
            reportStudentSelector.addItem(rollNo);
        }
    }

    private boolean isValidNumber(String input) {
        try {
            double value = Double.parseDouble(input);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveItem.addActionListener(e -> saveData());
        exitItem.addActionListener(e -> {
            saveData();
            System.exit(0);
        });

        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem exportItem = new JMenuItem("Export Reports");
        JMenuItem statisticsItem = new JMenuItem("Show Statistics");

        exportItem.addActionListener(e -> exportReports());
        statisticsItem.addActionListener(e -> showStatistics());

        toolsMenu.add(exportItem);
        toolsMenu.add(statisticsItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void exportReports() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Reports");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Student Grade Management System - Complete Report");
                writer.println("==============================================\n");

                for (Student student : students.values()) {
                    writer.println("Student Information:");
                    writer.printf("Roll No: %s%n", student.getRollNo());
                    writer.printf("Name: %s%n", student.getName());
                    writer.printf("Department: %s%n%n", student.getDepartment());

                    for (Map.Entry<Integer, List<Subject>> entry : student.getSemesterSubjects().entrySet()) {
                        writer.printf("Semester %d:%n", entry.getKey());
                        writer.println("-----------");

                        for (Subject subject : entry.getValue()) {
                            writer.printf("%-20s: %6.2f/%-6.2f (%6.2f%%) - %s - %s%n",
                                    subject.getName(),
                                    subject.getMarks(),
                                    subject.getMaxMarks(),
                                    subject.getPercentage(),
                                    subject.getGrade(),
                                    subject.getStatus());
                        }
                        writer.println();
                    }
                    writer.println("==============================================\n");
                }

                JOptionPane.showMessageDialog(this,
                        "Reports exported successfully!",
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting reports: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStatistics() {
        JDialog dialog = new JDialog(this, "Statistics", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statsArea);

        StringBuilder stats = new StringBuilder();
        stats.append("System Statistics\n");
        stats.append("=================\n\n");

        stats.append(String.format("Total Students: %d%n", students.size()));

        int totalSubjects = 0;
        int totalPasses = 0;
        double totalPercentage = 0;

        for (Student student : students.values()) {
            for (List<Subject> subjects : student.getSemesterSubjects().values()) {
                totalSubjects += subjects.size();
                for (Subject subject : subjects) {
                    if (subject.getStatus().equals("PASS")) {
                        totalPasses++;
                    }
                    totalPercentage += subject.getPercentage();
                }
            }
        }

        if (totalSubjects > 0) {
            stats.append(String.format("Total Subjects: %d%n", totalSubjects));
            stats.append(String.format("Pass Rate: %.2f%%%n",
                    (totalPasses * 100.0) / totalSubjects));
            stats.append(String.format("Average Percentage: %.2f%%%n",
                    totalPercentage / totalSubjects));
        }

        statsArea.setText(stats.toString());
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Student Grade Management System\nVersion 1.0\n\n" +
                        "A comprehensive system for managing student grades\n" +
                        "and generating reports.",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            StudentGradeManagementSystem system = new StudentGradeManagementSystem();
            system.setVisible(true);
        });
    }
}