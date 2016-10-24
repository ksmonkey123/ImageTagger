package ch.awae.imgtagger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import scala.collection.immutable.List;

public class Main {

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			List<String> images = IO.listImages();
			Meta meta = IO.readMeta();
			WindowManager manager = new WindowManager();
			manager.init();
			manager.setImages(images);
			manager.setMeta(meta);
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException: " + e.getMessage());
		} catch (InstantiationException e) {
			System.out.println("InstantiationException: " + e.getMessage());
		} catch (IllegalAccessException e) {
			System.out.println("IllegalAccessException: " + e.getMessage());
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
		}
	}

}