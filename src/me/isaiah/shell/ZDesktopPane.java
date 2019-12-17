package me.isaiah.shell;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import me.isaiah.shell.api.JProgram;
import me.isaiah.shell.api.Notification;

public class ZDesktopPane extends JDesktopPane {

    private static final long serialVersionUID = 1L;
    public Image img;
    private JFrame f;

    public ZDesktopPane(JFrame parent) {
        super();
        this.f = parent;
        this.addMouseListener(Utils.click(e -> StartMenu.stop()));
        this.addContainerListener(new DesktopContainerListener());
    }

    public void setLAF(String className) {
        Utils.runAsync(() -> {
            try {
                UIManager.setLookAndFeel(className);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            SwingUtilities.updateComponentTreeUI(Main.f);
            Main.p.removeAll();

            Desktop.reset();
            Desktop.init();
        });
    }

    public void setBackground(Image img) {
        if (this.img == null) {
            f.addComponentListener(new ComponentAdapter() {  
                public void componentResized(ComponentEvent evt) {
                    setBackground(img);
                }
            });
        }
        this.img = img.getScaledInstance(f.getWidth(), f.getHeight(), 0);
    }

    public void add(JProgram j, int width, int height) {
        j.setIconifiable(true);
        j.setSize(width, height);
        add(j);
    }

    @Override
    public void addImpl(Component j, Object constraints, int index) {

        StartMenu.stop();

        j.setVisible(true);
        super.addImpl(j, constraints, index);
        moveToFront(j);
    }

    private class DesktopContainerListener implements ContainerListener {
        
        private boolean d = false;
        int i = 0;

        public void componentAdded(ContainerEvent event) {
            if (d) return;

            JInternalFrame j = (JInternalFrame) event.getChild();
            if (null == j.getClientProperty("dontDisplayInWindowBar")) {
                JInternalFrame jp = (JInternalFrame) j;
                if ((jp.getName() == null || !jp.getName().equalsIgnoreCase("DESKTOP_ICON")) && !jp.isIcon())
                    SystemBar.get.wb.addFrame(j);
            }
        }

        public void componentRemoved(ContainerEvent event) {
            i++;
            if (i < 3) return;

            JInternalFrame j = (JInternalFrame) event.getChild();
            if (!(j instanceof StartMenu || j instanceof Notification || j instanceof SystemBar))
                SystemBar.get.wb.removeFrame(j);
        }
    }

}