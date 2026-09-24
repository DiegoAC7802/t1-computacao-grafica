import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class MainClass {

    public static void main(String[] args) {

        JFrame f = new JFrame("Visualizador 3D");

        MainCanvas meuCanvas = new MainCanvas();

        f.setSize(1920, 1080);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        f.add(meuCanvas);

        f.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        f.setVisible(true);

        meuCanvas.start();
    }
}