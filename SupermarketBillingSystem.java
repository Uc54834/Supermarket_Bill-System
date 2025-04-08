import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SupermarketBillingSystem extends JFrame {
    private JTextField customerNameField;
    private JTextField itemNameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double totalAmount = 0.0;

    public SupermarketBillingSystem() {
        setTitle("Supermarket Billing System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Customer details panel (North)
        JPanel customerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel customerInfoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        customerInfoPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));

        customerInfoPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField();
        customerInfoPanel.add(customerNameField);

        customerInfoPanel.add(new JLabel("Date:"));
        JLabel dateLabel = new JLabel(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        customerInfoPanel.add(dateLabel);

        customerPanel.add(customerInfoPanel);
        mainPanel.add(customerPanel, BorderLayout.NORTH);

        // Items table (Center)
        String[] columnNames = {"Item Name", "Quantity", "Unit Price (LKR)", "Total (LKR)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        itemsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Item entry panel (West)
        JPanel itemEntryPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        itemEntryPanel.setBorder(BorderFactory.createTitledBorder("Add Item"));

        itemEntryPanel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField();
        itemEntryPanel.add(itemNameField);

        itemEntryPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        itemEntryPanel.add(quantityField);

        itemEntryPanel.add(new JLabel("Unit Price (LKR):"));
        priceField = new JTextField();
        itemEntryPanel.add(priceField);

        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(new AddItemListener());
        itemEntryPanel.add(addButton);

        JButton removeButton = new JButton("Remove Item");
        removeButton.addActionListener(new RemoveItemListener());
        itemEntryPanel.add(removeButton);

        mainPanel.add(itemEntryPanel, BorderLayout.WEST);

        // Bottom panel with total and buttons (South)
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("Total Amount (LKR):"));
        totalLabel = new JLabel("0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton calculateButton = new JButton("Calculate Total");
        calculateButton.addActionListener(new CalculateTotalListener());
        buttonPanel.add(calculateButton);

        JButton printButton = new JButton("Print Bill");
        printButton.addActionListener(new PrintBillListener());
        buttonPanel.add(printButton);

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(new ClearAllListener());
        buttonPanel.add(clearButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private class AddItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String itemName = itemNameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());

                if (itemName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter item name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double itemTotal = quantity * price;
                tableModel.addRow(new Object[]{itemName, quantity, String.format("%.2f", price), String.format("%.2f", itemTotal)});

                // Clear fields after adding
                itemNameField.setText("");
                quantityField.setText("");
                priceField.setText("");

                // Focus back to item name field
                itemNameField.requestFocus();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid quantity and price", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RemoveItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = itemsTable.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(null, "Please select an item to remove", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CalculateTotalListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            totalAmount = 0.0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                double rowTotal = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
                totalAmount += rowTotal;
            }
            totalLabel.setText(String.format("%.2f", totalAmount));
        }
    }

    private class PrintBillListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No items to print", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calculate total if not already calculated
            if (totalAmount == 0.0) {
                new CalculateTotalListener().actionPerformed(null);
            }

            // Create bill content
            StringBuilder billContent = new StringBuilder();
            billContent.append("SUPERMARKET BILL\n");
            billContent.append("----------------\n");
            billContent.append("Customer: ").append(customerNameField.getText()).append("\n");
            billContent.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
            billContent.append("----------------\n");
            billContent.append(String.format("%-20s %-10s %-12s %-12s\n", "Item", "Qty", "Unit Price", "Total"));
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String item = tableModel.getValueAt(i, 0).toString();
                String qty = tableModel.getValueAt(i, 1).toString();
                String unitPrice = tableModel.getValueAt(i, 2).toString();
                String total = tableModel.getValueAt(i, 3).toString();
                billContent.append(String.format("%-20s %-10s %-12s %-12s\n", item, qty, unitPrice, total));
            }
            
            billContent.append("----------------\n");
            billContent.append(String.format("%-42s %-12s\n", "TOTAL:", String.format("%.2f", totalAmount)));
            billContent.append("----------------\n");
            billContent.append("Thank you for shopping with us!");

            // Show bill in a dialog
            JTextArea billTextArea = new JTextArea(billContent.toString());
            billTextArea.setEditable(false);
            billTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JOptionPane.showMessageDialog(null, new JScrollPane(billTextArea), "Bill Receipt", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class ClearAllListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tableModel.setRowCount(0);
            customerNameField.setText("");
            totalAmount = 0.0;
            totalLabel.setText("0.00");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SupermarketBillingSystem().setVisible(true);
        });
    }
}