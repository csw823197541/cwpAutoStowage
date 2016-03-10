package viewFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

/**
 * Created by csw on 2016/1/18.
 */
public class CwpViewFrame extends JFrame {

	private JLabel label;	

	public CwpViewFrame() {
		initComponents();
	}

	private void initComponents() {
		this.setTitle("cwp计划结果图");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(1400, 900);
		this.setLocationRelativeTo(null);// 居中显示

		label = new JLabel();
		String imgPath = "E:/cwp_data/file9.png";
		BufferedImage image = null;
		try {
			image = ImageIO.read(new FileInputStream(imgPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage imageNew = resizeImage(image, 1400, 1000);
		Graphics g = imageNew.getGraphics();		
		g.drawImage(imageNew, 0, 0, null); // 绘制缩小后的图 
		g.dispose();
		// 输出为文件 
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(new File("E:/cwp_data/newFile9.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			ImageIO.write(imageNew, "png", fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		label.setIcon(new ImageIcon("E:/cwp_data/newFile9.png"));
//		add(label);
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.add(label, BorderLayout.CENTER);
		this.setContentPane(panel);
	}

	public BufferedImage resizeImage(BufferedImage source, int targetW,
			int targetH) {
		// targetW，targetH分别表示目标长和宽
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		// 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
		// 则将下面的if else语句注释即可
		if (sx < sy) {
			sy = sx;
			targetH = (int) (sx * source.getHeight());
		} else {
			sx = sy;
			targetW = (int) (sy * source.getWidth());
		}
		if (type == BufferedImage.TYPE_CUSTOM) { // handmade
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
					targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else
			target = new BufferedImage(targetW, targetH, type);
		Graphics2D g = target.createGraphics();
		// smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}

}
