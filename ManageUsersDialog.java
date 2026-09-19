package moviecritics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageUsersDialog extends JDialog {

    private JTable userTable;
    private DefaultTableModel tableModel;

    public ManageUsersDialog(JFrame parent) {
        super(parent, "Manage User Accounts", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        String[] columns = {"ID", "Username", "Type", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(tableModel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add User");
        JButton editBtn = new JButton("Edit User");
        JButton deleteBtn = new JButton("Delete User");
        JButton resetPassBtn = new JButton("Reset Password");
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(resetPassBtn);

        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> openAddUserDialog());
        editBtn.addActionListener(e -> openEditUserDialog());
        deleteBtn.addActionListener(e -> deleteUser());
        resetPassBtn.addActionListener(e -> resetPassword());
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT UserId, Username, UserType, Email FROM Users ORDER BY Username")) {
            while (rs.next()) {
                String type = rs.getInt("UserType") == 1 ? "Parent" : "Child";
                tableModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), type, rs.getString(4)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void openAddUserDialog() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JComboBox<String> type = new JComboBox<>(new String[]{"Parent", "Child"});
        JTextField email = new JTextField();
        Object[] fields = {"Username:", username, "Password:", password, "Type:", type, "Email:", email};
        int result = JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO Users (Username, Password, UserType, Email) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, username.getText().trim());
                ps.setString(2, new String(password.getPassword()).trim());
                ps.setInt(3, type.getSelectedIndex() == 0 ? 1 : 2);
                ps.setString(4, email.getText().trim());
                ps.executeUpdate();
                loadUsers();
                JOptionPane.showMessageDialog(this, "User added.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void openEditUserDialog() {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String currentUsername = (String) tableModel.getValueAt(row, 1);
        String currentEmail = (String) tableModel.getValueAt(row, 3);

        JTextField username = new JTextField(currentUsername);
        JTextField email = new JTextField(currentEmail);
        JComboBox<String> type = new JComboBox<>(new String[]{"Parent", "Child"});
        Object[] fields = {"Username:", username, "Type:", type, "Email:", email};
        int result = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Users SET Username=?, UserType=?, Email=? WHERE UserId=?")) {
                ps.setString(1, username.getText().trim());
                ps.setInt(2, type.getSelectedIndex() == 0 ? 1 : 2);
                ps.setString(3, email.getText().trim());
                ps.setInt(4, id);
                ps.executeUpdate();
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM Users WHERE UserId=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void resetPassword() {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        JPasswordField passField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(this, new Object[]{"New Password:", passField},
                "Reset Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newPass = new String(passField.getPassword()).trim();
            if (newPass.isEmpty()) { JOptionPane.showMessageDialog(this, "Password cannot be empty."); return; }
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE Users SET Password=? WHERE UserId=?")) {
                ps.setString(1, newPass);
                ps.setInt(2, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Password reset successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
