package main;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

public class Brush {
    private static Mode currentMode = Mode.Pen;

    private static Point lastDragged = null;

    private static boolean deleteMode = false;

    static class BrushListener implements MouseInputListener, KeyListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (currentMode == Mode.Pen) {
                DrawingPane.paintPixel(e.getPoint());
            } else if (deleteMode) {
                DrawingPane.eraseShapes(e.getPoint());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            lastDragged = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (currentMode != Mode.Pen)
                return;

            Point mousePos = e.getPoint();
            if (lastDragged != null && mousePos.distanceSq(lastDragged) > 2) {  //dragged faster than event refresh-rate
                DrawingPane.paintLine(mousePos, lastDragged);
            } else {
                DrawingPane.paintPixel(mousePos);
            }
            lastDragged = mousePos;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (currentMode != Mode.Circle && currentMode != Mode.Square)
                return;

            Point drawingPaneLocation = DrawingPane.singleton.getLocationOnScreen();
            Point mousePos = MouseInfo.getPointerInfo().getLocation();
            mousePos.translate(-drawingPaneLocation.x, -drawingPaneLocation.y);

            if (e.getKeyCode() == KeyEvent.VK_F1) {
                DrawingPane.paintShape(currentMode, mousePos);
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                deleteMode = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_D)
                deleteMode = false;
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }

    public static void setMode(Mode mode) {
        currentMode = mode;
    }

    public enum Mode {
        Circle,
        Square,
        Pen
    }
}
