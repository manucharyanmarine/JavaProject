import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Stack;

public class GraphicWindow {
    private Connection connection;
    private Stack<JFrame> windowStack = new Stack<>();

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Shop");
        frame.setSize(1400, 800);
        frame.setLayout(null);

        JButton btnCreate = new JButton("Create");
        btnCreate.setBounds(50, 80, 100, 30);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(50, 120, 100, 30);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(50, 160, 100, 30);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(260, 20, 100, 30);


        JTextField searchField = new JTextField();
        searchField.setBounds(50, 20, 200, 30);



        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String query = "SELECT * FROM staff";
                    ResultSet resultSet = executeQuery(query);


                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                            model.addColumn(metaData.getColumnName(i));
                    }
                    model.addColumn("Update");
                    model.addColumn("Delete");

                    int deleteColumnId = columnCount+1;

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount+2];

                        for (int i = 1 ; i <= columnCount; i++) {
                            row[i - 1] = resultSet.getObject(i);
                        }

                        row[columnCount] = "UPDATE";
                        row[deleteColumnId] = "DELETE";

                        model.addRow(row);
                    }
                    JTable table = new JTable(model);

                    JScrollPane tableScrollPane = new JScrollPane(table);
                    tableScrollPane.setBounds(200, 80, 1100, 700);
                    Component[] components = frame.getContentPane().getComponents();
                    for (Component component : components) {
                        if (component instanceof JTextArea) {
                            frame.remove(component);
                        }
                    }
                    frame.add(tableScrollPane);
                    frame.repaint(); // Repaint the frame to reflect changes
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea resultTextArea = new JTextArea();
                resultTextArea.setBounds(200, 120, 800, 300);
                resultTextArea.setLineWrap(true);
                resultTextArea.setEditable(false);
                resultTextArea.setFont(new Font("Courier New", Font.PLAIN, 12));

                String searchText = searchField.getText();
                String query = "SELECT * FROM staff WHERE FName LIKE '%" + searchText + "%'";
                try {
                    ResultSet resultSet = executeQuery(query);
                    displayResultSetInTextArea(resultSet, resultTextArea);
                    Component[] components = frame.getContentPane().getComponents();
                    for (Component component : components) {
                        if (component instanceof JScrollPane) {
                            frame.remove(component);
                        }
                    }

                    frame.add(resultTextArea);
                    frame.repaint();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });


        JButton btnOpenWindow = new JButton("Open New Window");
        btnOpenWindow.setBounds(50, 200, 150, 30);
        btnOpenWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNewWindow();
            }
        });
        frame.add(btnOpenWindow);

        frame.add(btnCreate);
        frame.add(btnUpdate);
        frame.add(btnDelete);
        frame.add(searchField);
        frame.add(btnSearch);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        windowStack.push(frame); // Push the initial window onto the stack
    }

    private ResultSet executeQuery(String query) throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket", "root", "");

            } catch (ClassNotFoundException e) {
            System.out.println("asdas");
            System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    private void displayResultSetInTextArea(ResultSet resultSet, JTextArea textArea) throws SQLException {
        StringBuilder result = new StringBuilder();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                result.append(metaData.getColumnName(i)).append(": ").append(resultSet.getString(i)).append("\n");
            }
            result.append("\n");
        }
        textArea.setText(result.toString());
    }

    private void openNewWindow() {
        JFrame newFrame = new JFrame("New Window");
        newFrame.setSize(1100, 800);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setVisible(true);
        windowStack.peek().setVisible(false); // Hide the current window
        windowStack.push(newFrame);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, 50, 100, 30);
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        newFrame.add(btnBack);
    }

    public void goBack() {
        if (windowStack.size() > 1) {
            JFrame currentWindow = windowStack.pop();
            currentWindow.dispose();

            JFrame previousWindow = windowStack.peek();
            previousWindow.setVisible(true);
        }
    }
}
