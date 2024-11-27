import ui.MainFrame;
import utils.DataManager;

public class MainApp {
    public static void main(String[] args) {
        // Tải dữ liệu từ file
        DataManager.loadData();

        // Khởi chạy giao diện người dùng
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
