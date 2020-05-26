package utils;

import java.awt.*;

import javax.swing.*;

public class ImageLabel extends JLabel {
	public ImageLabel(ImageIcon icon){
        super.setIcon(icon);
    }
    private AlphaComposite cmp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1);
    private float alpha;

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        if (isVisible())   paintImmediately(getBounds());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setComposite(cmp.derive(alpha));
        super.paintComponent(g2d);

    }
}
