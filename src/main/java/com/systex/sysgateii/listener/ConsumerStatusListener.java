package com.systex.sysgateii.listener;

public interface ConsumerStatusListener {

	public void consumerActive(String serverId);

	public void consumerInactive(String serverId);

	public void consumerShutdown(String serverId);

	public void consumerSendmessage(String serverId, Object eventObj);
}
