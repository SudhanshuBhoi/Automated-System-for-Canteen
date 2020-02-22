import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

class ManageFile extends JFrame {

  JLabel infoLabel;
  JLabel detailLabel;
  JLabel moreDetailLabel;
  JButton editCategoriesButton;
  JButton editMenuButton;
  JButton openImagesButton;
  JLabel statusLabel;
  JButton startButton;

  static final int PORT = 8070;

  ManageFile() {
    getUI();
    setActionListeners();
  }

  private void getUI() {

    infoLabel = new JLabel();
    detailLabel = new JLabel();
    moreDetailLabel = new JLabel();
    editCategoriesButton = new JButton("Edit Categories");
    editMenuButton = new JButton("Edit Menu Items");
    openImagesButton = new JButton("Open Images");
    statusLabel = new JLabel();
    startButton = new JButton("Start");

    JPanel infoPanel = new JPanel();
    infoLabel.setHorizontalAlignment(JLabel.CENTER);
    infoLabel.setText("Canteen Server - http://192.168.1.100:8070/");
    Font font = new Font("Arial", Font.BOLD, 20);
    infoLabel.setFont(font);
    detailLabel.setHorizontalAlignment(JLabel.CENTER);
    detailLabel.setText("/categories/ - List of all categories");
    font = new Font ("Arial", Font.BOLD, 15);
    detailLabel.setFont(font);
    moreDetailLabel.setHorizontalAlignment(JLabel.CENTER);
    moreDetailLabel.setText("/menu/ - List of all menu items");
    moreDetailLabel.setFont(font);
    GridLayout infoLayout = new GridLayout(3, 1);
    infoLayout.setVgap(5);
    infoPanel.setLayout(infoLayout);
    infoPanel.add(infoLabel);
    infoPanel.add(detailLabel);
    infoPanel.add(moreDetailLabel);
    infoPanel.setVisible(true);

    JPanel buttonPanel = new JPanel();
    GridLayout buttonPanelLayout = new GridLayout(1, 3);
    buttonPanelLayout.setHgap(5);
    JPanel ecb = new JPanel();
    ecb.setLayout(new FlowLayout());
    ecb.add(editCategoriesButton);
    JPanel mib = new JPanel();
    mib.setLayout(new FlowLayout());
    mib.add(editMenuButton);
    JPanel oib = new JPanel();
    oib.setLayout(new FlowLayout());
    oib.add(openImagesButton);
    buttonPanel.setLayout(buttonPanelLayout);
    buttonPanel.add(ecb);
    buttonPanel.add(mib);
    buttonPanel.add(oib);
    buttonPanel.setVisible(true);

    JPanel startPanel = new JPanel();
    statusLabel.setText("Status: Stopped");
    startPanel.setLayout(new FlowLayout());
    startPanel.add(statusLabel);
    startPanel.add(startButton);
    startPanel.setVisible(true);

    setSize(470, 240);
    setResizable(false);
    GridLayout frameLayout = new GridLayout(3, 1);
    frameLayout.setVgap(8);
    setLayout(frameLayout);
    add(infoPanel);
    add(buttonPanel);
    add(startPanel);
    setTitle("Canteen Management");
    setVisible(true);
  }

  private void setActionListeners() {

    startButton.addActionListener(new ActionListener() {
      ServerManager serverManager;
      Thread thread;
      public void actionPerformed(ActionEvent ae) {
        if (startButton.getText().equals("Start")) {
          serverManager = new ServerManager();
          thread = new Thread(serverManager);
          thread.start();
          startButton.setText("Stop");
          statusLabel.setText("Status: Running");
          startButton.setEnabled(false);
        } else if (startButton.getText().equals("Stop")) {
          serverManager.stopRunning();
          startButton.setText("Start");
          statusLabel.setText("Status: Stopped");
        }
      }
    });

    editCategoriesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try{
          File file = new File(JavaHTTPServer.WEBROOT, "categories");
          Desktop.getDesktop().edit(file);
        } catch (IOException e) {
          System.err.println("IOException: " + e.getMessage());
        }
      }
    });

    editMenuButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try{
          File file = new File(JavaHTTPServer.WEBROOT, "menu");
          Desktop.getDesktop().edit(file);
        } catch (IOException e) {
          System.err.println("IOException: " + e.getMessage());
        }
      }
    });

    openImagesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try{
          File file = new File(JavaHTTPServer.WEBROOT, "images");
          Desktop.getDesktop().open(file);
        } catch (IOException e) {
          System.err.println("IOException: " + e.getMessage());
        }
      }
    });
  }

  public static void main (String[] args) {
    ManageFile mg = new ManageFile();
  }

}
