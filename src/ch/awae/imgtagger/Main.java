package ch.awae.imgtagger;

import scala.collection.immutable.List;

public class Main {

	public static void main(String[] args) {
		List<String> images = IO.listImages();
		Meta meta = IO.readMeta();
		WindowManager manager = new WindowManager();
		manager.init();
		manager.setImages(images);
		manager.setMeta(meta);
	}

}