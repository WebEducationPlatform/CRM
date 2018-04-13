package com.ewp.crm.utils.converters;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class VKConverter {

	public String parseMessage(String msg){
		String nix = msg.replaceAll("\r\n","\n");
		String br = "";
		String space = "";
		try {
			br = URLEncoder.encode("<br>","UTF-8");
			space = URLEncoder.encode(" ", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		nix = nix.replaceAll("\n", br);
		nix = nix.replaceAll(" ", space);
		return nix;
	}
	public long parseLink(String link){
		return Long.parseLong(link.replaceAll("https://vk.com/id", ""));
	}
}
