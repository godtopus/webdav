package com.webdav.util;

import io.milton.http.webdav.DisplayNameFormatter;
import io.milton.resource.PropFindableResource;

public class MyDisplayNameFormatter implements DisplayNameFormatter {
	
	@Override
	public String formatDisplayName(PropFindableResource res) {
		System.out.println(res.getName());
		return res.getName();
	}
}