import javax.swing.SwingUtilities;
import ui.MainFrame;
import utils.DataManager;

public class MainApp {
    public static void main(String[] args) {
        // Tải dữ liệu trước khi khởi chạy giao diện
        DataManager.loadData();

        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
