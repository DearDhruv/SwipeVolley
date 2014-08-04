
package com.deardhruv.swipevolley;

import java.io.Serializable;

public class ItemDetail implements Serializable {

	private static final long	serialVersionUID	= -7866770526789880505L;
	private String				name, imgUrl;

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
