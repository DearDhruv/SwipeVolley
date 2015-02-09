
package com.deardhruv.swipevolley.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class SignUtils {
	public static String apiSignRequest(Map<String, Object> paramsToSign, String apiSecret) {
		Collection<String> params = new ArrayList<String>();
		for (Map.Entry<String, Object> param : new TreeMap<String, Object>(paramsToSign).entrySet()) {
			if (param.getValue() instanceof Collection) {
				params.add(param.getKey() + "="
						+ StringUtils.join((Collection) param.getValue(), ","));
			} else {
				if (StringUtils.isNotBlank(param.getValue())) {
					params.add(param.getKey() + "=" + param.getValue().toString());
				}
			}
		}
		String to_sign = StringUtils.join(params, "&");
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unexpected exception", e);
		}
		byte[] digest = md.digest((to_sign + apiSecret).getBytes());
		return StringUtils.encodeHexString(digest);
	}

}
