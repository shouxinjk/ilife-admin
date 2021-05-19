package com.pcitech.iLife.cps.kaola;

public class ShareLinkInfo {
	private	String	originalLink	;//	原始链接
	private	String	shareLink	;//	分享链接（原始长链）
	private	String	shortLink	;//	分享短链
	public String getOriginalLink() {
		return originalLink;
	}
	public void setOriginalLink(String originalLink) {
		this.originalLink = originalLink;
	}
	public String getShareLink() {
		return shareLink;
	}
	public void setShareLink(String shareLink) {
		this.shareLink = shareLink;
	}
	public String getShortLink() {
		return shortLink;
	}
	public void setShortLink(String shortLink) {
		this.shortLink = shortLink;
	}
	
}
