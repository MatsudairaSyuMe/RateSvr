package com.systex.sysgateii.listener;

public interface MessageListener<T> {
	public void messageReceived(String serverId, T msg);
}
