
package com.deardhruv.swipevolley.model;

import java.io.Serializable;

public class ImageItem implements Serializable {

	private static final long serialVersionUID = 1754973654774734018L;
	private String name, imgUrl;

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
